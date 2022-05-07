package com.lvlifeng.jenkinshelper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-08 01:02
 */
public class JenkinsHelperWindow implements WindowWrapper {
    private JPanel panel1;
    private JComboBox comboBox1;
    private JTextField textField1;
    private JCheckBox selectAllCheckBox;
    private JList list1;
    private JList list2;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JComboBox comboBox2;
    private JTextArea textArea1;

    public JenkinsHelperWindow(Project project) {
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
        return null;
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

    public JPanel getJenkinsHelperPanel() {
        return panel1;
    }
}
