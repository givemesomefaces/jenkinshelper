package com.lvlifeng.jenkinshelper.ui;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.CharUtil;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.JenkinsVersion;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * @author Lv Lifeng
 * @date 2022-05-08 12:15
 */
public class AddAccountDialog extends DialogWrapper {


    private JTextField nickname;
    private JTextField apiUrl;
    private JTextField userName;
    private JPasswordField password;
    private JPanel rootPanel;
    private Jenkins jk;
    private Component jenkinsHelperComponent;


    protected AddAccountDialog(@NotNull Component parent, Component jenkinsHelperComponent, boolean canBeParent, Jenkins jk) {
        super(parent, canBeParent);
        this.setTitle(Bundle.message("addAccountDialogTitle"));
        this.jk = jk;
        this.jenkinsHelperComponent = jenkinsHelperComponent;
        init();
        initData();
    }

    private void initData() {
        if (jk != null) {
            nickname.setText(jk.getNickName());
            apiUrl.setText(jk.getApiUrl());
            userName.setText(jk.getUserName());
            password.setText(jk.getCpassword());
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }


    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (StringUtils.isBlank(apiUrl.getText())) {
            return new ValidationInfo(Bundle.message("apiUrlEmptyValid"), apiUrl);
        }
        if (!(apiUrl.getText().startsWith(Bundle.message("http"))
                || apiUrl.getText().startsWith(Bundle.message("https")))) {
            return new ValidationInfo(Bundle.message("apiUrlValid"), apiUrl);
        }
        if (StringUtils.isBlank(userName.getText())) {
            return new ValidationInfo(Bundle.message("userNameValid"), userName);
        }
        if (CollectionUtil.isEmpty(Arrays.asList(password.getPassword()))) {
            return new ValidationInfo(Bundle.message("passwordValid"), password);
        }
        boolean valid = AccountState.Companion.addAccount(new Jenkins(StringUtils.isBlank(nickname.getText()) ? apiUrl.getText() : nickname.getText(),
                        apiUrl.getText(),
                        userName.getText(),
                        new String(password.getPassword())),
                jk);
        if (!valid) {
            return new ValidationInfo(Bundle.message("authenticationFailed"));
        }
        JPanel jenkinsHelperJpanel = (JPanel) jenkinsHelperComponent;
        JPanel head = (JPanel) jenkinsHelperJpanel.getComponent(1);
        JPanel accountListJpanel = (JPanel) head.getComponent(1);
        JComboBox accountListComboBox = (JComboBox) accountListJpanel.getComponent(2);
        accountListComboBox.repaint();
        accountListComboBox.revalidate();
        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
    }
}
