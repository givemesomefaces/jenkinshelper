package com.lvlifeng.jenkinshelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.bean.StringParamsConfig;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Lv Lifeng
 * @date 2022-05-09 16:57
 */
public class AddStringParamsDialog extends DialogWrapper {
    private JPanel rootPanel;
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField defaultValueField;


    protected AddStringParamsDialog(@Nullable Project project, JPanel parentPanel) {
        super(project, parentPanel, false, IdeModalityType.IDE);
        init();
        this.setTitle(Bundle.message("addStringParamsDialogTitle"));

    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    public StringParamsConfig getStringParamsConfig(){
        return new StringParamsConfig(nameField.getText(), descriptionField.getText(), defaultValueField.getText());
    }
}
