/*
 * DevDrvpatchSelector.java
 */

package org.jsynthlib.device.viewcontroller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jsynthlib.core.Utility;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of a Patch.
 * Only Devices, Drivers, Bank- and PatchNumbers are choosable, which are
 * supporting the Patch. Is used for Reassign..., Store... and SendTo... a
 * patch.
 * @author Torsten Tittmann
 * @version $Id: DevDrvPatchSelector.java 1182 2011-12-04 22:07:24Z chriswareham
 *          $
 */
public class DevDrvPatchSelector extends JDialog {

    // ===== Instance variables
    /** The last index in driver Combo Box. */
    private int driverNum;
    private int patchNum;
    private int bankNum;
    protected Patch p;
    private final byte[] sysex;
    private final String patchString;

    private JLabel myLabel;
    private JComboBox deviceComboBox;
    protected JComboBox driverComboBox;
    protected JComboBox bankComboBox;
    protected JComboBox patchNumComboBox;

    /**
     * Constructor without Bank/Patch ComboBox.
     * @param patch
     *            The Patch to store
     * @param wintitle
     *            String which appears as window title
     * @param action
     *            String which describe the used menu item
     */
    // for SendToDialog and reassignDialog
    public DevDrvPatchSelector(Patch patch, String wintitle, String action) {
        super(PatchEdit.getInstance(), wintitle, true);

        p = patch;
        sysex = patch.getByteArray();
        patchString = patch.getPatchHeader();
        initDialog(action, false);
    }

    /**
     * Constructor with Bank/Patch ComboBox
     * @param patch
     *            The Patch to store
     * @param patchnum
     *            The default patchNumber selected in the patch Combobox.
     * @param wintitle
     *            String which appears as window title
     * @param action
     *            String which describe the used menu item
     */
    // for SysexStoreDialog
    public DevDrvPatchSelector(Patch patch, int banknum, int patchnum,
            String wintitle, String action) {
        super(PatchEdit.getInstance(), wintitle, true);

        p = patch;
        sysex = patch.getByteArray();
        patchString = patch.getPatchHeader();
        this.patchNum = patchnum;
        this.bankNum = banknum; // wirski@op.pl
        initDialog(action, true);
    }

    private void initDialog(String action, boolean hasBPComboBox) {
        // now the panel
        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));

        myLabel =
                new JLabel("Please select a Location to \"" + action + "\".",
                        JLabel.CENTER);
        dialogPanel.add(myLabel, BorderLayout.NORTH);

        // =================================== Combo Panel
        // ==================================
        // ----- Create the combo boxes
        deviceComboBox = new JComboBox();
        deviceComboBox.setName("deviceCb");
        deviceComboBox.addActionListener(new DeviceActionListener());
        driverComboBox = new JComboBox();
        driverComboBox.setName("driverCb");
        if (hasBPComboBox) {
            driverComboBox.addActionListener(new DriverActionListener());
            bankComboBox = new JComboBox();
            bankComboBox.setName("bankCb");
            patchNumComboBox = new JComboBox();
            patchNumComboBox.setName("patchNumCb");
        }

        // ----- Populate the combo boxes only with devices, which supports the
        // patch
        int nDriver = 0;
        DeviceManager deviceManager =
                JSynthLibInjector.getInstance(DeviceManager.class);
        for (int i = 0; i < deviceManager.deviceCount(); i++) {
            Device device = deviceManager.getDevice(i);
            boolean newDevice = true;
            int m = 0;
            for (IDriver driver : device) {
                if ((driver.isSingleDriver() || driver.isBankDriver())
                        && (driver.supportsPatch(patchString, sysex))) {
                    if (newDevice) { // only one entry for each supporting
                        // device
                        deviceComboBox.addItem(device);
                        newDevice = false;
                    }
                    if (p.getDriver() == driver) { // default is the driver
                        // associated with patch
                        driverNum = m;
                        deviceComboBox.setSelectedIndex(deviceComboBox
                                .getItemCount() - 1); // invoke
                        // DeviceActionListener
                    }
                    nDriver++;
                    m++;
                }
            } // driver loop
        } // device loop
        deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

        // ----- Layout the labels in a panel.
        JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        labelPanel.add(new JLabel("Device:", JLabel.LEFT));
        labelPanel.add(new JLabel("Driver:", JLabel.LEFT));
        if (hasBPComboBox) {
            labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
            labelPanel.add(new JLabel("Patch:", JLabel.LEFT));
        }

        // ----- Layout the fields in a panel
        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(deviceComboBox);
        fieldPanel.add(driverComboBox);
        if (hasBPComboBox) {
            fieldPanel.add(bankComboBox);
            fieldPanel.add(patchNumComboBox);
        }

        // ----- Create the comboPanel, labels on left, fields on right
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        comboPanel.add(labelPanel, BorderLayout.CENTER);
        comboPanel.add(fieldPanel, BorderLayout.EAST);
        dialogPanel.add(comboPanel, BorderLayout.CENTER);

        // =================================== Button Panel
        // ==================================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton doit = new JButton(action);
        doit.addActionListener(new DoitActionListener());
        buttonPanel.add(doit);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        buttonPanel.add(cancel);
        getRootPane().setDefaultButton(doit);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===== Final initialisation of dialog box
        getContentPane().add(dialogPanel);
        pack();
        Utility.centerWindow(this);

        if (nDriver > 0) {
            setVisible(true);
        } else {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "Oops, No driver was found, which support this patch! Nothing will happen",
                            "Error while \"" + action + "\" a patch",
                            JOptionPane.WARNING_MESSAGE);
            dispose();
        }
    }

    protected void doit() {
    }

    /**
     * Makes the actual work after pressing the 'Store' button
     */
    private class DoitActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            doit();
        }
    }

    /**
     * Repopulate the Driver ComboBox with valid drivers after a Device change
     */
    private class DeviceActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            driverComboBox.removeAllItems();

            Device device = (Device) deviceComboBox.getSelectedItem();
            int nDriver = 0;
            for (IDriver driver : device) {
                if ((driver.isSingleDriver() || driver.isBankDriver())
                        && driver.supportsPatch(patchString, sysex)) {
                    driverComboBox.addItem(driver);
                    nDriver++;
                }
            }
            // the original driver is the default
            // When a different device is selected, driverNum can be out of
            // range.
            driverComboBox.setSelectedIndex(Math.min(driverNum, nDriver - 1));
            driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
        }
    }

    /**
     * Repopulate the Bank/Patch ComboBox with valid entries after a
     * Device/Driver change
     */
    private class DriverActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {

            IDriver driver = (IDriver) driverComboBox.getSelectedItem();
            bankComboBox.removeAllItems();
            patchNumComboBox.removeAllItems();

            if (driver != null) {
                // populate bank combo box
                String[] bankNumbers = driver.getBankNumbers();
                if (bankNumbers != null && bankNumbers.length > 1) {
                    for (int i = 0; i < bankNumbers.length; i++) {
                        bankComboBox.addItem(bankNumbers[i]);
                    }
                    bankComboBox.setSelectedIndex(Math.min(bankNum, // wirski@op.pl
                            bankComboBox.getItemCount() - 1));
                }
                if (driver.isSingleDriver()) {
                    // populate patch number combo box
                    IPatchDriver patchDriver = (IPatchDriver) driver;
                    String[] patchNumbers = getPatchNumbers(patchDriver);
                    if (patchNumbers.length > 1) {
                        for (int i = 0; i < patchNumbers.length; i++) {
                            patchNumComboBox.addItem(patchNumbers[i]);
                        }
                        patchNumComboBox.setSelectedIndex(Math.min(patchNum,
                                patchNumComboBox.getItemCount() - 1));
                    }
                }
            }
            bankComboBox.setEnabled(bankComboBox.getItemCount() > 1);
            // N.B. Do not enable patch selection for banks
            patchNumComboBox.setEnabled(patchNumComboBox.getItemCount() > 1);
        }
    }

    /**
     * This method returns the list of patch numbers, which may change according
     * to the dialog type (some have patch locations to which you can send but
     * not store)
     */
    protected String[] getPatchNumbers(IPatchDriver driver) {
        return driver.getPatchNumbers();
    }
}
