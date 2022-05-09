package com.lvlifeng.jenkinshelper.ui;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.lvlifeng.jenkinshelper.bean.BuildConfig;
import com.lvlifeng.jenkinshelper.bean.UpdateConfig;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.helper.JenkinsVersion;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.View;
import org.apache.commons.lang.StringUtils;
import org.dom4j.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private JButton stopRebuildButton;

    private AccountState ac = AccountState.Companion.getInstance();
    private static JenkinsServer jk = null;
    private Project project;
    private Map<String, View> views;
    private List<Job> selectedJobs = new ArrayList<>();
    private List<Job> allJobs = new ArrayList<>();
    private boolean rebuildFlag = false;
    private static final String XML_SUFFIX = "\n\t\t\t\t\n    \n\t\t\t\t";

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
        initJobList();
        initSelectedJobList();
        initCheckAllButton();
        initBuildAndRebuildButton();
        initUpdateButton();
        initAddParamsButton();
        initErrorLogButton();
    }

    private void initErrorLogButton() {

    }

    private void initAddParamsButton() {

    }

    private void initUpdateButton() {
        updateButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                UpdateJobDialog updateJobDialog = new UpdateJobDialog(project, rootPanel);
                updateJobDialog.show();
                if (updateJobDialog.isOK()) {
                    UpdateConfig updateConfig = updateJobDialog.getUpdateConfig();
                    doUpdate(updateConfig);
                }
            }
        });
    }

    private void doUpdate(UpdateConfig updateConfig) {
        String newGitBranchName = updateConfig.getNewGitBranchName().trim();
        String newBranchXml = "<hudson.plugins.git.BranchSpec>" + XML_SUFFIX
                + "<name>" + newGitBranchName + "</name>" + XML_SUFFIX
                + "</hudson.plugins.git.BranchSpec>";
        HashMap<String, String> stringParamsMap = updateConfig.getStringParamsMap();
        selectedJobs.stream().forEach(job -> {
            try {
                String jobXml = jk.getJobXml(job.getName());
                Document document = DocumentHelper.parseText(jobXml);
                Element rootElement = document.getRootElement();
                if (StringUtils.isNotBlank(newGitBranchName)) {
                    List<Node> nodes = rootElement.selectNodes("//hudson.plugins.git.BranchSpec");
                    if (CollectionUtil.isNotEmpty(nodes)) {
                        nodes.get(0).setDocument(DocumentHelper.parseText(newBranchXml));
                    }
                }
                if (MapUtil.isNotEmpty(stringParamsMap)) {
                    List<Node> nodes = rootElement.selectNodes("//properties/hudson.model.ParametersDefinitionProperty/parameterDefinitions/hudson.model.StringParameterDefinition");
                    if (CollectionUtil.isNotEmpty(nodes)) {
                        nodes.stream().forEach(node -> {
                            Element parent = node.getParent();
                            List<Element> elements = parent.elements();
                            if (CollectionUtil.isNotEmpty(elements)) {
                                elements.forEach(element -> {
//                                    parent.elements().get(0).elements().get(0).getName()
                                    Element stringParameterDefinition = parent.elements().get(0);
                                    if (stringParameterDefinition != null) {
                                        List<Element> childElements = stringParameterDefinition.elements();
                                        if (childElements.stream().anyMatch(child -> StringUtils.equals(child.getName(), "name") && stringParamsMap.keySet().contains(child.getData()))) {
                                            if (childElements.stream().noneMatch(child -> StringUtils.equals(child.getName(), "defaultValue"))) {
                                                String defaultValueStr = "<defaultValue>" + stringParamsMap.get(element.getName()) + "</defaultValue>";
                                                Document defaultValueDocument = null;
                                                try {
                                                    defaultValueDocument = DocumentHelper.parseText(defaultValueStr);
                                                } catch (DocumentException e) {
                                                    e.printStackTrace();
                                                }
                                                stringParameterDefinition.add(defaultValueDocument.getRootElement());
                                            } else {
                                                childElements.forEach(child -> {
                                                    if (StringUtils.equals(child.getName(), "defaultValue")) {
                                                        child.setData(stringParamsMap.get(element.getName()));
                                                    }
                                                });
                                            }
                                        }
                                    }

                                });
                            }
                            System.out.println("");
                        });
                    }
                }

                //<hudson.plugins.git.BranchSpec>
                //
                //
                //				<name>baseline_v2.4.8_20220425</name>
                //
                //
                //			</hudson.plugins.git.BranchSpec>

//                List<Element> elements = rootElement.elements("hudson.model.StringParameterDefinition");
//                Iterator<Element> elementIterator = rootElement.elementIterator("hudson.model.StringParameterDefinition");
                String xmlStr = rootElement.asXML();
                System.out.println(jobXml);
//                getElementsByTagName("hudson.plugins.git.BranchSpec")
//                InputSource is = new InputSource(new StringReader(jobXml));
//                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder builder = factory.newDocumentBuilder();
//                Document doc = builder.parse(is);
//                NodeList brancheList = doc.getElementsByTagName("hudson.plugins.git.BranchSpec");
//                for (int i = 0; i < brancheList.getLength(); i++) {
//                    Node item = brancheList.item(i);
//                }
//                for branch in branches:
//                name = branch.getElementsByTagName('name')[0]
//                name.childNodes[0].data = new_branch
//                update = True
                jk.updateJob(job.getName(), xmlStr, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initBuildAndRebuildButton() {
        stopRebuildButton.setEnabled(false);
        stopRebuildButton.setVisible(false);
        buildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                BuildJobDialog buildJobDialog = new BuildJobDialog(project, rootPanel);
                buildJobDialog.show();
                if (buildJobDialog.isOK()) {
                    BuildConfig buildConfig = buildJobDialog.getBuildConfig();
                    rebuildFlag = buildConfig.getReBuildFlag();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            do {
                                doBuild(buildConfig);
                            } while (rebuildFlag);
                        }
                    }).start();
                    if (rebuildFlag) {
                        buildButton.setEnabled(false);
                        buildButton.setVisible(false);
                        stopRebuildButton.setEnabled(true);
                        stopRebuildButton.setVisible(true);
                    }
                }
            }
        });
        stopRebuildButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                rebuildFlag = false;
                buildButton.setEnabled(true);
                buildButton.setVisible(true);
                stopRebuildButton.setEnabled(false);
                stopRebuildButton.setVisible(false);
            }
        });
    }

    private void doBuild(BuildConfig buildConfig) {
        selectedJobs.stream().forEach(job -> {
            try {
                if (buildConfig.getBuildLastFailedFlag()) {
                    JobWithDetails details = job.details();
                    if (details.getLastBuild().getNumber() != details.getLastFailedBuild().getNumber()) {
                        return;
                    }
                    if (details.getQueueItem() != null && details.getLastCompletedBuild().getNumber() == details.getLastBuild().getNumber()) {
                        return;
                    }
                }
                if (MapUtil.isNotEmpty(buildConfig.getParamesMap())) {
                    job.build(buildConfig.getParamesMap(), true);
                } else {
                    job.build(true);
                }
            } catch (IOException e) {
                e.printStackTrace();
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
            if (null != jk) {
                views = jk.getViews();
                if (MapUtil.isEmpty(views)) {
                    return;
                }
                viewList.setModel(new DefaultComboBoxModel(views.keySet().toArray()));
                viewList.setSelectedIndex(views.size() - 1);
                View view = views.get(new ArrayList<>(views.keySet()).get(views.size() - 1));
                setJobListByView(view);
            }
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
