package com.lvlifeng.jenkinshelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.bean.BuildConfig;
import com.lvlifeng.jenkinshelper.helper.StringParamsParseHelper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lv Lifeng
 * @date 2022-05-09 10:20
 */
public class BuildJobDialog extends DialogWrapper {
    private JTextField buildParams;
    private JCheckBox rebuildCheckBox;
    private JSpinner rebuildTimeField;
    private JCheckBox buildLastFailedCheckBox;
    private JPanel rootPanel;

    private boolean rebuildFlag = false;
    private Map<String, String> paramsMap = new HashMap<>();
    private Integer rebuildTime;
    private boolean buildLastFailed = false;

    protected BuildJobDialog(@Nullable Project project, JPanel parentPanel) {
        super(project, parentPanel, false, IdeModalityType.IDE);
        this.setTitle(Bundle.message("buildDialogTitle"));
        init();
        initRebuildCheckBox();
    }

    private void initRebuildCheckBox() {
        rebuildCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JCheckBox checkbox = (JCheckBox) e.getComponent();
                rebuildFlag = checkbox.isSelected();
            }
        });
        buildLastFailedCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                JCheckBox checkbox = (JCheckBox) e.getComponent();
                rebuildFlag = checkbox.isSelected();
            }
        });
    }

    private void setUp() {
        paramsMap = StringParamsParseHelper.Companion.getParamsMap(buildParams.getText());
        rebuildTime = (Integer) rebuildTimeField.getValue();
    }


    public BuildConfig getBuildConfig() {
        return new BuildConfig(buildLastFailed, rebuildFlag, rebuildTime, paramsMap);
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        setUp();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }
}
