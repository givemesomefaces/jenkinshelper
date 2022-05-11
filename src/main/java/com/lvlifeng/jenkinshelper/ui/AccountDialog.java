package com.lvlifeng.jenkinshelper.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.lvlifeng.jenkinshelper.Bundle;
import com.lvlifeng.jenkinshelper.bean.ReadOnlyTableModel;
import com.lvlifeng.jenkinshelper.icons.Icons;
import com.lvlifeng.jenkinshelper.jenkins.AccountState;
import com.lvlifeng.jenkinshelper.jenkins.Jenkins;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

/**
 * @author Lv Lifeng
 * @date 2022-05-08 11:54
 */
public class AccountDialog extends DialogWrapper {
    private JTable accountTable;
    private JButton addButton;
    private JButton deleteButton;
    private JPanel rootPanel;
    private JButton editButton;
    private AccountState ac = AccountState.Companion.getInstance();
    private Project project = null;
    private Component jenkinsHelperComponent;


    protected AccountDialog(@Nullable Project project, @Nullable Component parentComponent) {
        super(project, parentComponent, false, IdeModalityType.PROJECT);
        this.project = project;
        this.jenkinsHelperComponent = parentComponent;
        this.setTitle(Bundle.message("accountDialogTitle"));
        init();
        initButton();
        setUp();
        fill();
    }

    private void setUp() {
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new AddAccountDialog(rootPanel, jenkinsHelperComponent, false, null).show();
                fill();
            }
        });
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Jenkins jk = getSelectedAccount();
                if (jk != null) {
                    ac.removeAccount(jk);
                    fill();
                }
            }
        });
        editButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Jenkins jk = getSelectedAccount();
                if (jk == null) {
                    return;
                }
                new AddAccountDialog(rootPanel, jenkinsHelperComponent, false, jk).show();
                fill();
            }
        });
    }

    private Jenkins getSelectedAccount() {
        if(accountTable.getSelectedRow() >= 0) {
            return (Jenkins) accountTable.getValueAt(accountTable.getSelectedRow(), 0);
        }
        return null;
    }

    private void initButton() {

        addButton.setIcon(Icons.Add);
        addButton.setBorder(null);
        editButton.setIcon(Icons.Edit_grey);
        editButton.setBorder(null);
        deleteButton.setIcon(Icons.Delete_grey);
        deleteButton.setBorder(null);
    }

    private void fill() {
        accountTable.setModel(serverModel(ac.getJks()));
        accountTable.getColumnModel().getColumn(0).setMinWidth(0);
        accountTable.getColumnModel().getColumn(0).setMaxWidth(0);
        accountTable.getColumnModel().getColumn(0).setWidth(0);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.getSelectionModel().addListSelectionListener(event -> {
            editButton.setEnabled(true);
            editButton.setIcon(Icons.Edit);
            deleteButton.setEnabled(true);
            deleteButton.setIcon(Icons.Delete);
        });
        accountTable.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                editButton.setEnabled(false);
                editButton.setIcon(Icons.Edit_grey);
                deleteButton.setEnabled(false);
                deleteButton.setIcon(Icons.Delete_grey);
            }
        });
        initButton();
        jenkinsHelperComponent.getParent().repaint();
        jenkinsHelperComponent.getParent().revalidate();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    private TableModel serverModel(Collection<Jenkins> jks) {
        Object[] columnNames = {"", "NickName", "ApiUrl", "Username"};
        Object[][] data = new Object[jks.size()][columnNames.length];
        int i = 0;
        for (Jenkins jk : jks) {
            if (jk == null) {
                continue;
            }
            Object[] row = new Object[columnNames.length];
            row[0] = jk;
            row[1] = jk.getNickName();
            row[2] = jk.getApiUrl();
            row[3] = jk.getUserName();
            data[i] = row;
            i++;
        }
        return new ReadOnlyTableModel(data, columnNames);
    }
}
