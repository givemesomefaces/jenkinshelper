package com.lvlifeng.jenkinshelper;

import com.cdancy.jenkins.rest.JenkinsClient;
import com.cdancy.jenkins.rest.domain.job.JobList;
import com.cdancy.jenkins.rest.domain.system.SystemInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.ui.WindowWrapper;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Lv Lifeng
 * @date 2022-05-08 01:02
 */
public class JenkinsHelperWindow implements WindowWrapper {
    private JPanel rootPanel;
    private JComboBox viewList;
    private JTextField textField1;
    private JCheckBox selectAllCheckBox;
    private JList jobList;
    private JList selectedJobList;
    private JButton buildButton;
    private JButton updateButton;
    private JButton addParamsButton;
    private JButton errorLogButton;
    private JButton stopRebuildButton;
    private JComboBox accountList;
    private JTextArea logTextarea;
    private JLabel selectedLable;
    private JButton accountButton;
    private JLabel errorInfoLable;

    private AccountState ac = AccountState.Companion.getInstance();
    private static JenkinsClient jk = null;
    private Project project;


    public JenkinsHelperWindow(Project project) {
        this.project = project;
        initUi();
//        Jenkins jenkins = new Jenkins("cms", "http://jenkins-cms.test.rabbitpre.com/", "admin", "admin@123");
//        accountList.addItem(jenkins);
//        ac.addAccount(jenkins);
//        jk = new Jenkins().client(jenkins);
//        SystemInfo systemInfo = jk.api().systemApi().systemInfo();
//        assertTrue(systemInfo.jenkinsVersion().equals("1.642.4"));

    }

    private void initUi() {
        initAccount();
    }

    private void initAccount() {
        accountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new AccountDialog(project, rootPanel).show();
            }
        });
        accountList.setModel(new DefaultComboBoxModel(ac.getJks().toArray()));
        accountList.setSelectedIndex(-1);
        accountList.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                JTextField textField = (JTextField) e.getSource();
                String text = textField.getText();
                accountList.setModel(new DefaultComboBoxModel(ac.getJks().toArray()));
                textField.setText(text);
                accountList.showPopup();
            }
        });
        accountList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                Jenkins jenkins = (Jenkins) e.getItem();
                JenkinsClient jenkinsClient = new Jenkins().client(jenkins);
                SystemInfo systemInfo = jenkinsClient.api().systemApi().systemInfo();
                if (systemInfo.server() == "-1") {
                    errorInfoLable.setText("Authentication failed!");
                } else {
                    jk = jenkinsClient;
                    errorInfoLable.setText("");
                    initViewList();
                    initJobList();
                }
            }
        });
    }

    private void initJobList() {
        JobList jobList = jk.;
        System.out.println("aaa");

    }

    private void initViewList() {
        
    }

    @Override
    public void show() {

    }

    @Override
    public @Nullable Project getProject() {
        return null;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return rootPanel;
    }

    @Override
    public @NotNull Mode getMode() {
        return null;
    }

    @Override
    public @NotNull Window getWindow() {
        return null;
    }

    @Override
    public void setTitle(@Nullable String title) {

    }

    @Override
    public void setImages(@Nullable List<? extends Image> images) {

    }

    @Override
    public void close() {

    }

    @Override
    public void dispose() {

    }
}
