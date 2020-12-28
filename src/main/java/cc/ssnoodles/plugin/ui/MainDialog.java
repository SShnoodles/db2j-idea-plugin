package cc.ssnoodles.plugin.ui;

import cc.ssnoodles.db.entity.Table;
import cc.ssnoodles.db.factory.DbFactory;
import cc.ssnoodles.db.factory.DbFactoryImpl;
import cc.ssnoodles.db.util.FileUtil;
import cc.ssnoodles.db.util.StringUtil;
import cc.ssnoodles.plugin.model.Config;
import cc.ssnoodles.plugin.model.Template;
import cc.ssnoodles.plugin.services.GeneratorService;
import cc.ssnoodles.plugin.services.PersistentStateService;
import cc.ssnoodles.plugin.util.UiUtil;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiElement;

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
    private TextFieldWithBrowseButton domainPackage;
    private TextFieldWithBrowseButton controllerPackage;
    private JCheckBox repositoryCheckBox;
    private JCheckBox controllerCheckBox;
    private JTextField author;
    private JCheckBox overwriteFilesCheckBox;
    private JTextField singleTableRename;
    private JComboBox<String> version;
    private final ButtonGroup buttonGroup = new ButtonGroup();

    private PsiElement[] psiElements;
    private Project project;
    private AnActionEvent anActionEvent;

    private PersistentStateService persistentStateService;

    public MainDialog(AnActionEvent anActionEvent, boolean isSingleTable) {
        this.anActionEvent = anActionEvent;

        this.project = anActionEvent.getData(PlatformDataKeys.PROJECT);
        this.psiElements = anActionEvent.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        persistentStateService = PersistentStateService.getInstance(project);

        UiUtil.centerDialog(this,"Db2j", 650, 350);

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setButtonGroup();
        setVersion();
        setPackageListener();
        setControllerListener();

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
            author.setText(historyConfig.getAuthor());
            controllerCheckBox.setSelected(historyConfig.isController());
            overwriteFilesCheckBox.setSelected(historyConfig.isOverwriteFiles());
            if (Template.JPA.name().equalsIgnoreCase(historyConfig.getTemplateType())) {
                jpaRadioButton.setSelected(true);
                version.setSelectedIndex(historyConfig.getVersion());
            }else if (Template.DTO.name().equalsIgnoreCase(historyConfig.getTemplateType())) {
                dtoRadioButton.setSelected(true);
                version.setSelectedIndex(-1);
            }else {
                commonRadioButton.setSelected(true);
                version.setSelectedIndex(-1);
            }
            if (isSingleTable) {
                singleTableRename.setEnabled(true);
            }else {
                singleTableRename.setEnabled(false);
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
        historyConfig.setAuthor(author.getText());
        historyConfig.setController(controllerCheckBox.isSelected());
        historyConfig.setOverwriteFiles(overwriteFilesCheckBox.isSelected());
        historyConfig.setVersion(version.getSelectedIndex());
        config.put("history", historyConfig);
        persistentStateService.setConfig(config);

        cc.ssnoodles.db.entity.Config properties = FileUtil.PROPERTIES;
        if (!StringUtil.isEmpty(author.getText())) {
            properties.setAuthor(author.getText());
        }

        GeneratorService generatorService = GeneratorService.of();
        for (PsiElement psiElement : psiElements) {
            Table table = generatorService.table((DbTable) psiElement);
            DbFactory dbFactory = new DbFactoryImpl();
            generatorService.generateEntity(table, domainPackage.getText(), dbFactory.getTemplate(template.toLowerCase()), overwriteFilesCheckBox.isSelected(), singleTableRename.getText());
            if (repositoryCheckBox.isSelected()) {
                generatorService.generateRepository(version.getSelectedIndex(), table, domainPackage.getText(), overwriteFilesCheckBox.isSelected(), singleTableRename.getText());
            }
            if (controllerCheckBox.isSelected()) {
                generatorService.generateController(version.getSelectedIndex(), table, controllerPackage.getText(), overwriteFilesCheckBox.isSelected(), singleTableRename.getText());
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

    private void setVersion() {
        version.addItem("0: JpaRepository");
        version.addItem("1: TypedRepository");
        version.setSelectedIndex(-1);
    }

    private void setPackageListener() {
        domainPackage.setEditable(false);
        domainPackage.setText(project.getBasePath());
        domainPackage.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
        controllerPackage.setEditable(false);
        controllerPackage.setText(project.getBasePath());
        controllerPackage.addBrowseFolderListener(null, null, project, FileChooserDescriptorFactory.createSingleFolderDescriptor());
    }

    private void setButtonGroup() {
        buttonGroup.add(jpaRadioButton);
        buttonGroup.add(dtoRadioButton);
        buttonGroup.add(commonRadioButton);
    }

    private void setControllerListener() {
        controllerCheckBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED) {
                version.setEnabled(true);
            } else {
                version.setSelectedIndex(0);
                version.setEnabled(false);
            }
        });
    }
}
