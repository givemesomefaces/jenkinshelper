package com.lvlifeng.jenkinshelper.ui;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.bean.*;
import com.lvlifeng.jenkinshelper.helper.JobBuildHelper;
import com.lvlifeng.jenkinshelper.helper.JobConfigHelper;
import com.lvlifeng.jenkinshelper.helper.ProgressHelper;
import com.lvlifeng.jenkinshelper.helper.WindowHelper;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.View;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.*;


/**
 * @author Lv Lifeng
 * @date 2022-05-08 01:02
 */
public class JenkinsHelperWindow implements WindowWrapper {
    private JPanel rootPanel;
    private JComboBox viewList;
    private JTextField searchField;
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
    private JButton stopBuildButton;

    private AccountState ac = AccountState.Companion.getInstance();
    private static JenkinsServer jk = null;
    private Project project;
    private Map<String, View> views;
    private Set<Job> selectedJobs = new HashSet<>();
    private List<Job> allJobs = new ArrayList<>();
    private List<Job> filterJobs = new ArrayList<>();
    private boolean rebuildFlag = false;
    private List<Jenkins> oldAc = Lists.newArrayList();
    private static final List<String> ERROR_LOG_KEYWORDS = Lists.newArrayList(
            "[error]",
            "as failure",
            "finished: failure"
    );

    public JenkinsHelperWindow(Project project) {
        this.project = project;
        initUi();

    }

    private void initUi() {
        initAccount();
        initViewListAndJobList();
        initSelectedJobList();
        initSearch();
        initCheckAllButton();
        initBuildAndRebuildButton();
        initUpdateButton();
        initAddParamsButton();
        initErrorLogButton();
    }

    private void initSearch() {
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                JTextField s = (JTextField) e.getComponent();
                initJobList(filterJob(s.getText()));
            }
        });
    }

    private void initErrorLogButton() {
        errorLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        errorLogButton.setEnabled(false);
                        getErrorLog();
                        errorLogButton.setEnabled(true);
                    }
                }).start();
            }
        });
        logTextarea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == 3) {
                    JBPopupFactory.getInstance()
                            .createListPopup(new Jpopup(Lists.newArrayList(Bundle.message("clearLog")),
                                    logTextarea))
                            .show(new RelativePoint(logTextarea,
                                    new Point((int) logTextarea.getMousePosition().getX(),
                                            (int) logTextarea.getMousePosition().getY())));
                }
            }
        });
    }

    private void getErrorLog() {
        log(Bundle.message("startQueryErrorLogs"));
        try {
            for (Job job : selectedJobs) {
                StringBuilder errorLogs = new StringBuilder();
                Build lastFailedBuild = jk.getJob(job.getName()).getLastFailedBuild();
                if (lastFailedBuild == null || lastFailedBuild.getNumber() == -1) {
                    continue;
                }
//                if (!StringUtils.equals(lastFailedBuild.getUrl(), job.getUrl())) {
//                    errorLogs.append(Bundle.message("urlErrorMsg")).append(Bundle.message("enter"));
//                }
                String consoleOutputText = lastFailedBuild.details().getConsoleOutputText();
                if (StringUtils.isBlank(consoleOutputText)) {
                    continue;
                }
                errorLogs.append(Bundle.message("jobName", job.getName())).append(Bundle.message("newLine"));
                String[] msg = consoleOutputText.split(Bundle.message("newLine"));
                Arrays.asList(msg).forEach(m -> {
                    if (ERROR_LOG_KEYWORDS.stream().anyMatch(o -> m.toLowerCase().contains(o))) {
                        errorLogs.append(m).append(Bundle.message("newLine"));
                    }
                });
                errorLogs.append(Bundle.message("newLine"));
                if (StringUtils.isNotBlank(errorLogs.toString())) {
                    log(errorLogs.toString());
                }
            }
        } catch (UnknownHostException hostException) {
            log(hostException.getMessage() + ". " + Bundle.message("urlErrorMsg"));
        } catch (IOException ioException) {
            log(ioException.getMessage());
        }
        log(Bundle.message("endQueryErrorLogs"));
    }

    private void initAddParamsButton() {
        addParamsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                AddStringParamsDialog addStringParamsDialog = new AddStringParamsDialog(project, rootPanel);
                addStringParamsDialog.show();
                if (addStringParamsDialog.isOK()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            addParamsButton.setEnabled(false);
                            doAddStringParams(addStringParamsDialog.getStringParamsConfig());
                            addParamsButton.setEnabled(true);
                        }
                    }).start();
                }
            }
        });
    }

    private void doAddStringParams(StringParamsConfig config) {
        log(Bundle.message("startAddStringParam", selectedJobs.size(), JSONUtil.toJsonStr(config)));
        selectedJobs.stream().forEach(job -> {
            try {
                JobConfigHelper.Companion.addParams(config, jk, job);
                log(Bundle.message("addParamsSuccess", job.getName()));
            } catch (Exception e) {
                log(Bundle.message("addParamsError", job.getName(), e.getMessage()));
            }
        });
        log(Bundle.message("endAddStringParam", selectedJobs.size(), JSONUtil.toJsonStr(config)));

    }

    private void initUpdateButton() {
        updateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                UpdateJobDialog updateJobDialog = new UpdateJobDialog(project, rootPanel);
                updateJobDialog.show();
                if (updateJobDialog.isOK()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateButton.setEnabled(false);
                            doUpdate(updateJobDialog.getUpdateConfig());
                            updateButton.setEnabled(true);
                        }
                    }).start();
                }
            }
        });
    }

    private void doUpdate(UpdateConfig updateConfig) {
        log(Bundle.message("startUpdate", selectedJobs.size(), JSONUtil.toJsonStr(updateConfig)));
        selectedJobs.stream().forEach(job -> {
            try {
                JobConfigHelper.Companion.updateJobConfig(updateConfig, jk, job);
                log(Bundle.message("updateSuccess", job.getName()));
            } catch (Exception e) {
                log(Bundle.message("updateError", job.getName(), e.getMessage()));
            }

        });
        log(Bundle.message("endUpdate", selectedJobs.size(), JSONUtil.toJsonStr(updateConfig)));
    }

    private void initBuildAndRebuildButton() {
        stopBuildButton.setEnabled(false);
        stopBuildButton.setVisible(false);
        buildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                BuildJobDialog buildJobDialog = new BuildJobDialog(project, rootPanel);
                buildJobDialog.show();
                if (buildJobDialog.isOK()) {
                    BuildConfig buildConfig = buildJobDialog.getBuildConfig();
                    rebuildFlag = buildConfig.getReBuildFlag();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            do {
                                log(Bundle.message("startBuild", JSONUtil.toJsonStr(buildConfig)));
                                try {
                                    doBuild(buildConfig);
                                } catch (Exception exception) {
                                    log(exception.getMessage());
                                    break;
                                }
                            } while (rebuildFlag);
                            log(Bundle.message("endBuild", JSONUtil.toJsonStr(buildConfig)));
                        }
                    }).start();
                    if (rebuildFlag) {
                        buildButton.setEnabled(false);
                        buildButton.setVisible(false);
                        stopBuildButton.setEnabled(true);
                        stopBuildButton.setVisible(true);
                    }
                }
            }
        });
        stopBuildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                rebuildFlag = false;
                buildButton.setEnabled(true);
                buildButton.setVisible(true);
                stopBuildButton.setEnabled(false);
                stopBuildButton.setVisible(false);
            }
        });
    }

    private void doBuild(BuildConfig buildConfig) throws Exception {
        selectedJobs.stream().forEach(job -> {
            try {
                JobBuildHelper.Companion.build(buildConfig, job);
                log(Bundle.message("buildSuccess", job.getName()));
            } catch (Exception e) {
                log(Bundle.message("buildError", job.getName(), e.getMessage()));
            }
        });
        if (rebuildFlag) {
            try {
                Thread.sleep(buildConfig.getReBuildTime() * 60 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initCheckAllButton() {
        selectAllCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                JCheckBox checkbox = (JCheckBox) e.getComponent();
                if (checkbox.isSelected()) {
                    selectedJobs.addAll(filterJobs);
                    jobList.addSelectionInterval(0, filterJobs.size());
                } else {
                    selectedJobs.removeAll(filterJobs);
                    jobList.clearSelection();
                }
                initSelectedJobList();
            }
        });
    }

    private void initJobList(List<Job> jobs) {
        jobList.setListData(jobs.stream().map(Job::getName).toArray());
        jobList.setSelectionModel(new DefaultListSelectionModel() {
            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (super.isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                    selectedJobs.remove(filterJobs.get(index0));
                } else {
                    super.addSelectionInterval(index0, index1);
                    selectedJobs.add(filterJobs.get(index0));
                }
                initSelectedJobList();
            }
        });
        if (CollectionUtil.isNotEmpty(selectedJobs)) {
            jobList.setSelectedIndices(selectedJobs.stream()
                    .map(o -> jobs.indexOf(o))
                    .mapToInt(Integer::valueOf)
                    .toArray());
        } else {
            jobList.clearSelection();
        }
    }

    private List<Job> filterJob(String searchWord) {
        return filterJobs = WindowHelper.Companion.filterJob(searchWord, allJobs);
    }

    private void initSelectedJobList() {
        setButtonStatusBySelectedJobs();
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
                int index = allJobs.indexOf(new ArrayList<>(selectedJobs).get(index0));
                jobList.removeSelectionInterval(index, index);
                selectedJobs.remove(new ArrayList<>(selectedJobs).get(index0));
                initSelectedJobList();
            }
        });
    }

    private void setButtonStatusBySelectedJobs() {
        if (CollectionUtil.isEmpty(selectedJobs)) {
            buildButton.setEnabled(false);
            updateButton.setEnabled(false);
            addParamsButton.setEnabled(false);
            errorLogButton.setEnabled(false);
        } else {
            buildButton.setEnabled(true);
            updateButton.setEnabled(true);
            addParamsButton.setEnabled(true);
            errorLogButton.setEnabled(true);
        }
    }

    private void initAccount() {
        accountButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getButton() != 1) {
                    return;
                }
                oldAc = Lists.newArrayList(ac.getJks());
                AccountDialog accountDialog = new AccountDialog(project, rootPanel);
                accountDialog.show();
                if (accountDialog.isOK() && !ac.getJks().contains(accountList.getSelectedItem())) {
                    resetAccountList();
                } else {
                    if (CollectionUtil.isNotEmpty(ac.getJks())
                            && oldAc.size() != ac.getJks().size()) {
                        List<Jenkins> addAc = CollectionUtil.subtractToList(ac.getJks(), oldAc);
                        addAc.stream().forEach(o -> accountList.addItem(o));
                        List<Jenkins> deleteAc = CollectionUtil.subtractToList(oldAc, ac.getJks());
                        deleteAc.stream().forEach(o -> accountList.removeItem(o));
                    } else {
                        resetAccountList();
                        initJobList(Lists.newArrayList());
                        initSelectedJobList();
                        clearSearch();
                    }
                }
            }
        });
        resetAccountList();

    }

    private void clearSearch() {
        searchField.setText("");
    }

    private void resetAccountList() {
        if (CollectionUtil.isEmpty(ac.getJks())) {
            accountList.setEnabled(false);
            accountList.setVisible(false);
        } else {
            accountList.setEnabled(true);
            accountList.setVisible(true);
        }
        List<Jenkins> accounts = Lists.newArrayList(AccountState.Companion.getDefaultAc());
        accounts.addAll(ac.getJks());
        accountList.setModel(new DefaultComboBoxModel(accounts.toArray()));
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
                ProgressHelper.Companion.show(project,
                        Bundle.message("switchAccount"),
                        Bundle.message("checkAccount"),
                        Bundle.message("checkAccountEnd"),
                        false,
                        () -> {
                            Jenkins jenkins = (Jenkins) e.getItem();
                            if (Objects.equals(AccountState.Companion.getDefaultAc(), jenkins)) {
                                return null;
                            }
                            accountList.removeItem(AccountState.Companion.getDefaultAc());
                            AccountStatus accountStatus = Jenkins.Companion.validAndGet(jenkins);
                            if (!accountStatus.getStatus()) {
                                errorInfoLable.setText(Bundle.message("authenticationFailed"));
                            } else {
                                jk = accountStatus.getJenkinsServer();
                                errorInfoLable.setText("");
                                initViewListAndJobList();
                            }
                            return null;
                        });
            }
        });
        initViewListAndJobList();
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
            View view = null;
            if (null != jk && MapUtil.isNotEmpty(views = jk.getViews())) {
                view = views.get(new ArrayList<>(views.keySet()).get(0));
            } else {
                views = MapUtil.empty();
            }
            viewList.setModel(new DefaultComboBoxModel(views.keySet().toArray()));
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
        selectedJobs = new HashSet<>();
        selectAllCheckBox.setSelected(false);
        initSelectedJobList();
        initJobList(filterJob(null));
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

    private void log(String log) {
        logTextarea.append(DateUtil.now() + "  " + log + Bundle.message("newLine"));
        logTextarea.setCaretPosition(logTextarea.getText().length());
    }
}
