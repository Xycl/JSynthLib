package org.jsynthlib.view.preferences;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jsynthlib.view.desktop.JSLDesktop;

import core.AppConfig;

/**
 * ConfigPanel for generic parameters.
 * @author Joe Emenaker
 * @author Hiroo Hayashi
 * @version $Id: GeneralConfigPanel.java 1157 2011-09-18 21:53:50Z frankster $
 */
public class GeneralConfigPanel extends ConfigPanel {
    {
        panelName = "General";
        nameSpace = "general";
    }

    private JComboBox cbLF;
    private JComboBox cbGS;
    private JCheckBox cbxTB;

    private static UIManager.LookAndFeelInfo[] installedLF;
    static {
        installedLF = UIManager.getInstalledLookAndFeels();
    }

    public GeneralConfigPanel(PrefsDialog parent) {
        super(parent);

        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        // Look & Feel combobox
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(new JLabel("GUI Look and Feel:"), c);

        cbLF = new JComboBox();
        for (int j = 0; j < installedLF.length; j++)
            cbLF.addItem(installedLF[j].getName());
        cbLF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setModified(true);
            }
        });
        c.gridx = 1;
        c.gridy = 0;
        p.add(cbLF, c);

        // GUI style (MDI/SDI) combobox
        c.gridx = 0;
        c.gridy++;
        c.insets = new Insets(10, 0, 0, 0);
        p.add(new JLabel("GUI Style:"), c);
        cbGS = new JComboBox(new String[] {
                "MDI (Single Window)", "SDI (Multiple Windows)", });
        cbGS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setModified(true);
            }
        });
        c.gridx = 1;
        p.add(cbGS, c);

        // Tool Bar check box
        cbxTB = new JCheckBox("Add Tool Bar on Each Frame in SDI Mode");
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 2;
        p.add(cbxTB, c);

        add(p, BorderLayout.CENTER);
    }

    void init() {
        cbLF.setSelectedItem(AppConfig.getLookAndFeel());
        cbGS.setSelectedIndex(AppConfig.getGuiStyle());
        cbxTB.setSelected(AppConfig.getToolBar());
    }

    void commitSettings() {
        if (!AppConfig.getLookAndFeel().equals(cbLF.getSelectedItem())) {
            AppConfig.setLookAndFeel((String) cbLF.getSelectedItem());
            JSLDesktop.Factory.getDesktop().updateLookAndFeel();
            ((JPanel) this).updateUI(); // wirski@op.pl
            SwingUtilities.updateComponentTreeUI(this.getRootPane()); // wirski@op.pl
        }
        if (AppConfig.getGuiStyle() != cbGS.getSelectedIndex()) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "You must exit and restart the program for your changes to take effect",
                            "Changing GUI Style",
                            JOptionPane.INFORMATION_MESSAGE);
            AppConfig.setGuiStyle(cbGS.getSelectedIndex());
        }
        if (AppConfig.getToolBar() != cbxTB.isSelected())
            AppConfig.setToolBar(cbxTB.isSelected());
    }
}
