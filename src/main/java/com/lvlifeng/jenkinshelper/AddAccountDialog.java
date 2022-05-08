package com.lvlifeng.jenkinshelper;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author Lv Lifeng
 * @date 2022-05-08 12:15
 */
public class AddAccountDialog extends DialogWrapper {


    private JTextField nickname;
    private JTextField apiUrl;
    private JTextField userName;
    private JTextField password;
    private JPanel rootPanel;
    private Jenkins jk;
    private AccountState ac = AccountState.Companion.getInstance();


    protected AddAccountDialog(@NotNull Component parent, boolean canBeParent, Jenkins jk) {
        super(parent, canBeParent);
        this.jk = jk;
        setSize(300, 150);
        init();
        initData();
    }

    private void initData() {
        if (jk != null) {
            nickname.setText(jk.getNickName());
            apiUrl.setText(jk.getApiUrl());
            userName.setText(jk.getUserName());
            password.setText(jk.getPassword());
        }
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }



    @Override
    protected @Nullable ValidationInfo doValidate() {
        if (StringUtils.isBlank(apiUrl.getText())) {
            return new ValidationInfo("Jenkins api url cannot be empty.", apiUrl);
        }
        if (!(apiUrl.getText().startsWith("http://") || apiUrl.getText().startsWith("https://"))) {
            return new ValidationInfo("Jenkins api url must be start with http:// or https://.", apiUrl);
        }
        if (StringUtils.isBlank(userName.getText())) {
            return new ValidationInfo("Jenkins username cannot be empty.", userName);
        }
        if (StringUtils.isBlank(password.getText())) {
            return new ValidationInfo("Jenkins password cannot be empty.", password);
        }
        Jenkins newjk = new Jenkins(nickname.getText(), apiUrl.getText(), userName.getText(), password.getText());
        JenkinsClient client = newjk.client(newjk);
        SystemInfo systemInfo = client.api().systemApi().systemInfo();
        if (systemInfo.server() == "-1") {
            return new ValidationInfo("Jenkins Authentication failed.");
        }
        if (null != jk) {
            ac.removeAccount(jk);
        }
        ac.addAccount(newjk);
        return null;
    }
}
