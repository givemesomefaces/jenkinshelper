package com.lvlifeng.jenkinshelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
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
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    public UpdateConfig getUpdateConfig() {
        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setNewGitBranchName(newGitBranchNameTextField.getText());
        updateConfig.setStringParamsMap(StringParamsParseHelper.Companion.getParamsMap(stringParameTextField.getText()));
        return updateConfig;
    }
}
