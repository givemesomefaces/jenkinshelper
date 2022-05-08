package com.lvlifeng.jenkinshelper;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.JenkinsVersion;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.View;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private JComboBox accountList;
    private JTextArea logTextarea;
    private JLabel selectedLable;
    private JButton accountButton;
    private JLabel errorInfoLable;

    private AccountState ac = AccountState.Companion.getInstance();
    private static JenkinsServer jk = null;
    private Project project;
    private Map<String, View> views;
    private List<Job> selectedJobs = new ArrayList<>();
    private List<Job> allJobs = new ArrayList<>();


    public JenkinsHelperWindow(Project project) {
        this.project = project;
        initUi();
//        Jenkins jenkins = new Jenkins("cms", "http://jenkins-cms.test.rabbitpre.com/", "admin", "admin@123");
//        accountList.addItem(jenkins);
//        ac.addAccount(jenkins);
//        jk = new Jenkins().client(jenkins);
//        SystemInfo systemInfo = jk.api().systemApi().systemInfo();
//        assertTrue(systemInfo.jenkinsVersion().equals("1.642.4"));

        rootPanel.addComponentListener(new ComponentAdapter() {
        });
    }

    private void initUi() {
        initAccount();
        initJobList();
        initSelectedJobList();
        initCheckAllButton();
    }

    private void initCheckAllButton() {
        selectAllCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JCheckBox checkbox = (JCheckBox) e.getComponent();
                if (checkbox.isSelected()) {
                    selectedJobList.setListData(allJobs.stream().map(Job::getName).toArray());
                    selectedJobs = allJobs;
                } else {
                    selectedJobList.setListData(new String[]{});
                    selectedJobs = new ArrayList<>();
                }
                initSelectedJobList();
            }
        });
    }

    private void initJobList() {
        jobList.setListData(allJobs.stream().map(Job::getName).toArray());
        jobList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                    selectedJobs.remove(allJobs.get(index0));
                } else {
                    super.addSelectionInterval(index0, index1);
                    selectedJobs.add(allJobs.get(index0));
                }
                initSelectedJobList();
            }
        });
    }

    private void initSelectedJobList() {
        selectedLable.setText(selectedJobs.size() + " Selected");
        selectedJobList.setListData(selectedJobs.stream().map(Job::getName).toArray());
        selectedJobList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                } else {
                    super.addSelectionInterval(index0, index1);
                }
                int index = allJobs.indexOf(selectedJobs.get(index0));
                jobList.removeSelectionInterval(index, index);
                selectedJobs.remove(selectedJobs.get(index0));
                initSelectedJobList();
            }
        });
    }

    private void initAccount() {
        accountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new AccountDialog(project, rootPanel).show();
                resetAccountList();
            }
        });
        resetAccountList();

    }

    private void resetAccountList() {
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
                JenkinsServer server = new Jenkins().server(jenkins);
                JenkinsVersion version = server.getVersion();
                if (version.getLiteralVersion() == "-1") {
                    errorInfoLable.setText("Authentication failed!");
                } else {
                    jk = server;
                    errorInfoLable.setText("");
                    initViewListAndJobList();
                }
            }
        });
        initViewListAndJobList();
        initSelectedJobList();
        initCheckAllButton();
    }

    private void initViewListAndJobList() {
        viewList.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String viewName = e.getItem().toString();
                View view = views.get(viewName);
                setJobListByView(view);
            }
        });
        try {
            views = jk.getViews();
            if (MapUtil.isEmpty(views)) {
                return;
            }
            viewList.setModel(new DefaultComboBoxModel(views.keySet().toArray()));
            viewList.setSelectedIndex(views.size() - 1);
            View view = views.get(new ArrayList<>(views.keySet()).get(views.size() - 1));
            setJobListByView(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setJobListByView(View view) {
        if (view != null && CollectionUtil.isNotEmpty(view.getJobs())) {
            allJobs = view.getJobs();
        } else {
            allJobs = new ArrayList<>();
        }
        selectedJobs = new ArrayList<>();
        initJobList();
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
