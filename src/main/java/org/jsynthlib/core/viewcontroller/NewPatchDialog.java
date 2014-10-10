package org.jsynthlib.core.viewcontroller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Utility;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.IPatch;

/**
 * Dialog to create a new Patch of the loaded Devices respective Drivers. Any
 * 'Generic' device and 'Converter' driver are skipped.
 * @author unascribed
 * @author Torsten Tittmann
 * @version $Id: NewPatchDialog.java 1182 2011-12-04 22:07:24Z chriswareham $
 */
public class NewPatchDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private final transient Logger log = Logger.getLogger(getClass());
    private final JComboBox deviceComboBox;
    private final JComboBox driverComboBox;
    private IPatch p;

    public NewPatchDialog(JFrame parent) {
        super(parent, "Create New Patch", true);

        JPanel container = new JPanel(new BorderLayout(5, 5));

        JLabel myLabel =
                new JLabel("Please select a Patch Type to Create.",
                        JLabel.CENTER);
        container.add(myLabel, BorderLayout.NORTH);

        deviceComboBox = new JComboBox();
        deviceComboBox.setName("deviceCb");
        deviceComboBox.addActionListener(new DeviceActionListener());
        driverComboBox = new JComboBox();
        driverComboBox.setName("driverCb");

        // First Populate the Device/Driver List with
        // Device/Driver. which supports the "createNewPatch" method
        // Skipping the generic device (i == 0)
        DeviceManager deviceManager = JSynthLibInjector.getInstance(DeviceManager.class);
        for (int i = 1; i < deviceManager.deviceCount(); i++) {
            Device device = deviceManager.getDevice(i);
            for (int j = 0; j < device.driverCount(); j++) {
                IDriver driver = device.getDriver(j);
             // Skipping a converter
                if ((driver.isSingleDriver() || driver.isBankDriver())
                        && driver.canCreatePatch()) {
                    deviceComboBox.addItem(device);
                    break;
                }
            }
        }
        deviceComboBox.setEnabled(deviceComboBox.getItemCount() > 1);

        // ----- Layout the labels in a panel.
        JPanel labelPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        labelPanel.add(new JLabel("Device:", JLabel.LEFT));
        labelPanel.add(new JLabel("Driver:", JLabel.LEFT));

        // ----- Layout the fields in a panel
        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(deviceComboBox);
        fieldPanel.add(driverComboBox);

        // ----- Create the comboPanel, labels on left, fields on right
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        comboPanel.add(labelPanel, BorderLayout.CENTER);
        comboPanel.add(fieldPanel, BorderLayout.EAST);
        container.add(comboPanel, BorderLayout.CENTER);

        // ----- Create "Create" button
        JPanel buttonPanel = new JPanel();
        JButton create = new JButton(" Create ");
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IDriver driver =
                        (IDriver) driverComboBox.getSelectedItem();

                p = driver.createPatch();
                if (p != null) {
                    log.info("Bingo " + driver.toString());
                } else {
                    // If a driver does not override
                    // createNewPatch method unnecessary, this
                    // error never occurs.
                    ErrorMsg.reportError("New Patch Error",
                            "The driver does not support `New Patch' function.");
                }
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(create);

        // ----- Create "Cancel" button
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        buttonPanel.add(cancel);

        getRootPane().setDefaultButton(create);

        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        pack();
        Utility.centerWindow(this);
        // } catch(Exception e) {
        // log.info(e);
        // }
    }

    public IPatch getNewPatch() {
        return p;
    }

    /**
     * Repopulate the Driver ComboBox with valid drivers after a Device change
     */
    public class DeviceActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            driverComboBox.removeAllItems();

            Device device = (Device) deviceComboBox.getSelectedItem();
            for (int i = 0; i < device.driverCount(); i++) {
                IDriver driver = device.getDriver(i);
                if ((driver.isSingleDriver() || driver.isBankDriver())
                        && driver.canCreatePatch()) {
                    driverComboBox.addItem(driver);
                }
            }
            driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
        }
    }
}
