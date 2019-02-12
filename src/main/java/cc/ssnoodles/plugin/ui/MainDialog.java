package cc.ssnoodles.plugin.ui;

import cc.ssnoodles.plugin.model.Template;
import cc.ssnoodles.plugin.services.GeneratorService;
import cc.ssnoodles.plugin.services.PersistentStateService;
import cc.ssnoodles.plugin.util.UiUtil;
import cc.ssnoodles.plugin.model.Config;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import cc.ssnoodles.db.entity.Table;
import cc.ssnoodles.db.factory.DbFactory;
import cc.ssnoodles.db.factory.DbFactoryImpl;
import cc.ssnoodles.db.template.RepositoryTemplate;
import org.jetbrains.annotations.SystemIndependent;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * @author ssnoodles
 * @version 1.0
 * Create at 2019-02-10 12:51
 */
public class MainDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JRadioButton jpaRadioButton;
    private JRadioButton dtoRadioButton;
    private JRadioButton commonRadioButton;
    private JTextField domainPackage;
    private JTextField controllerPackage;
    private JCheckBox repositoryCheckBox;
    private ButtonGroup buttonGroup = new ButtonGroup();

    private PsiElement[] psiElements;
    private Project project;
    private AnActionEvent anActionEvent;

    private PersistentStateService persistentStateService;

    public MainDialog(AnActionEvent anActionEvent) {
        this.anActionEvent = anActionEvent;

        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        persistentStateService = PersistentStateService.getInstance(project);

        UiUtil.centerDialog(this,"Db2j", 600, 300);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonGroup.add(jpaRadioButton);
        buttonGroup.add(dtoRadioButton);
        buttonGroup.add(commonRadioButton);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        Map<String, Config> config = persistentStateService.getConfig();
        if (config == null) {
            config = new HashMap<>();
        }else {
            Config historyConfig = config.get("history");
            repositoryCheckBox.setSelected(historyConfig.isRepository());
            domainPackage.setText(historyConfig.getDomainPackage());
            controllerPackage.setText(historyConfig.getControllerPackage());
            if (Template.JPA.name().equalsIgnoreCase(historyConfig.getTemplateType())) {
                jpaRadioButton.setSelected(true);
            }else if (Template.DTO.name().equalsIgnoreCase(historyConfig.getTemplateType())) {
                dtoRadioButton.setSelected(true);
            }else {
                commonRadioButton.setSelected(true);
            }

        }

        pack();
        setVisible(true);
    }

    private void onCancel() {
        dispose();
    }

    private void onOK() {
        String template = getTemplateRadio();

        Map<String, Config> config = persistentStateService.getConfig();
        if (config == null) {
            config = new HashMap<>();
        }

        Config historyConfig = new Config();
        historyConfig.setTemplateType(template);
        historyConfig.setControllerPackage(controllerPackage.getText());
        historyConfig.setDomainPackage(domainPackage.getText());
        historyConfig.setRepository(repositoryCheckBox.isSelected());
        config.put("history", historyConfig);
        persistentStateService.setConfig(config);


        GeneratorService generatorService = GeneratorService.of();
        @SystemIndependent String projectPath = project.getBasePath();
        for (PsiElement psiElement : psiElements) {
            Table table = generatorService.table((DbTable) psiElement);
            DbFactory dbFactory = new DbFactoryImpl();
            generatorService.generateEntity(table, projectPath, domainPackage.getText(), dbFactory.getTemplate(template.toLowerCase()));
            if (repositoryCheckBox.isSelected()) {
                generatorService.generateRepository(table, projectPath, domainPackage.getText(), new RepositoryTemplate());
            }
        }

        dispose();
    }

    private String getTemplateRadio() {
        String template = "JPA";
        Enumeration<AbstractButton> radioBtns = buttonGroup.getElements();
        while (radioBtns.hasMoreElements()) {
            AbstractButton btn = radioBtns.nextElement();
            if(btn.isSelected()) {
                template = btn.getText();
                break;
            }
        }
        return template;
    }
}
