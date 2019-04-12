package cc.ssnoodles.plugin;

import cc.ssnoodles.plugin.ui.MainDialog;
import com.intellij.database.psi.DbTable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;

/**
 * @author ssnoodles
 * @version 1.0
 * Create at 2019-01-31 13:13
 */
public class MainAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        PsiElement[] psiElements = e.getData(LangDataKeys.PSI_ELEMENT_ARRAY);
        if (psiElements == null || psiElements.length == 0) {
            Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
            return;
        }
        for (PsiElement psiElement : psiElements) {
            if (!(psiElement instanceof DbTable)) {
                Messages.showMessageDialog("Please select one or more tables", "Notice", Messages.getInformationIcon());
                return;
            }
        }
        boolean isSingleTable = psiElements.length == 1;
        new MainDialog(e, isSingleTable);
    }
}
