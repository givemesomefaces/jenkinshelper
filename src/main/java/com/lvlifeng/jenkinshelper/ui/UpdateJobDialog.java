package com.lvlifeng.jenkinshelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.bean.UpdateConfig;
import com.lvlifeng.jenkinshelper.helper.StringParamsParseHelper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Lv Lifeng
 * @date 2022-05-09 14:45
 */
public class UpdateJobDialog extends DialogWrapper {
    private JTextField newGitBranchNameTextField;
    private JTextField stringParameTextField;
    private JPanel rootPanel;


    protected UpdateJobDialog(@Nullable Project project, JPanel parentPanel) {
        super(project, parentPanel, false, IdeModalityType.IDE);
        init();
        this.setTitle(Bundle.message("updateDialogTitle"));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    public UpdateConfig getUpdateConfig() {
        return new UpdateConfig(newGitBranchNameTextField.getText(),
                StringParamsParseHelper.Companion.getParamsMap(stringParameTextField.getText()));
    }
}
