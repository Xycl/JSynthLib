package org.jsynthlib.device.viewcontroller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.SysexMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.service.MidiService;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of the
 * location, where a Patch should come from. More than one of each device is
 * supported, but only devices/drivers are selectable, which support the patch.
 * @author phil@muqus.com - 07/2001
 * @version $Id: SysexGetDialog.java 1182 2011-12-04 22:07:24Z chriswareham $
 */
public class SysexGetDialog extends JDialog {

    private final transient Logger log = Logger.getLogger(getClass());

    // ===== Instance variables
    /** timeout value (in milli second). */
    private long timeOut;
    /** number of received data bytes. */
    private int sysexSize = 0;
    /** queue to save Sysex Messages received. */
    private List queue;
    /** MIDI input port from which SysEX messages come. */
    private int inPort;

    private final Timer timer;
    private final JLabel myLabel;
    private final JComboBox deviceComboBox;
    private final JComboBox driverComboBox;
    private final JComboBox bankNumComboBox;
    private final JComboBox patchNumComboBox;

    private final MidiService midiService;

    // --------------------------------------------------------------------------
    // Constructor: SysexGetDialog()
    // --------------------------------------------------------------------------

    public SysexGetDialog(JFrame parent) { // , Driver driver, int bankNum, int
        // patchNum) {
        super(parent, "Get Sysex Data", true);

        JPanel dialogPanel = new JPanel(new BorderLayout(5, 5));
        // dialogPanel.setLayout(new GridLayout(0, 1));

        // myLabel = new JLabel(" ", JLabel.CENTER);
        myLabel =
                new JLabel("Please select a Patch Type to Get.", JLabel.CENTER);
        dialogPanel.add(myLabel, BorderLayout.NORTH);

        midiService = JSynthLibInjector.getInstance(MidiService.class);

        // =================================== Combo Panel
        // ======================
        // ----- Create the combo boxes
        deviceComboBox = new JComboBox();
        deviceComboBox.addActionListener(new DeviceActionListener());
        driverComboBox = new JComboBox();
        driverComboBox.addActionListener(new DriverActionListener());
        bankNumComboBox = new JComboBox();
        patchNumComboBox = new JComboBox();

        // First Populate the Device/Driver List with all Device/Driver
        // combinations except converters
        // skip 0 (Generic Device)
        DeviceManager deviceManager =
                JSynthLibInjector.getInstance(DeviceManager.class);
        for (int i = 1; i < deviceManager.deviceCount(); i++) {
            Device device = deviceManager.getDevice(i);
            for (IDriver driver : device) {
                // Skipping converters
                if (driver.isSingleDriver() || driver.isBankDriver()) {
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
        labelPanel.add(new JLabel("Bank:", JLabel.LEFT));
        labelPanel.add(new JLabel("Patch:", JLabel.LEFT));

        // ----- Layout the fields in a panel
        JPanel fieldPanel = new JPanel(new GridLayout(0, 1));
        fieldPanel.add(deviceComboBox);
        fieldPanel.add(driverComboBox);
        fieldPanel.add(bankNumComboBox);
        fieldPanel.add(patchNumComboBox);

        // ----- Create the comboPanel, labels on left, fields on right
        JPanel comboPanel = new JPanel(new BorderLayout());
        comboPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        comboPanel.add(labelPanel, BorderLayout.CENTER);
        comboPanel.add(fieldPanel, BorderLayout.EAST);
        dialogPanel.add(comboPanel, BorderLayout.CENTER);

        // =================================== Button Panel
        // =====================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton get = new JButton("Get");
        get.addActionListener(new GetActionListener());
        buttonPanel.add(get);

        JButton paste = new JButton("Paste");
        paste.addActionListener(new PasteActionListener());
        buttonPanel.add(paste);

        JButton done = new JButton("Done");
        done.addActionListener(new DoneActionListener());
        buttonPanel.add(done);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                timer.stop();
            }
        });
        buttonPanel.add(cancel);
        getRootPane().setDefaultButton(done);
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

        // ===================================== Timer
        // ==========================
        timer = new javax.swing.Timer(0, new TimerActionListener());

        // ===== Listener

        // ===== Final initialisation of dialog box
        getContentPane().add(dialogPanel);
        pack();
        // setSize(600, 200);
        Utility.centerWindow(this);
        sysexSize = 0;
    }

    // --------------------------------------------------------------------------
    // SysexGetDialog->pasteIntoSelectedFrame
    // --------------------------------------------------------------------------

    protected void pasteIntoSelectedFrame() {
        // The following lines are not needed. Alesis DM5 driver has patch of
        // size 11
        // if (sysexSize < 20)
        // return;

        IDriver driver = (IDriver) driverComboBox.getSelectedItem();
        SysexMessage[] msgs =
                (SysexMessage[]) queue.toArray(new SysexMessage[0]);
        Patch[] patarray = driver.createPatches(msgs);
        int bankNum = bankNumComboBox.getSelectedIndex(); // wirski@op.pl
        int patchNum = patchNumComboBox.getSelectedIndex(); // wirski@op.pl

        try {
            PatchBasket frame =
                    (PatchBasket) JSLDesktop.Factory.getDesktop()
                            .getSelectedFrame();
            for (int i = 0; i < patarray.length; i++) {
                frame.pastePatch(patarray[i], ((bankNum == -1) ? 0 : bankNum),
                        ((patchNum == -1) ? 0 : patchNum)); // wirski@op.pl
            }
        } catch (Exception ex) {
            PopupHandlerProvider.get().showMessage(null,
                    "Library to Receive into must be the focused Window.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --------------------------------------------------------------------------
    // InnerClass: DoneActionListener
    // --------------------------------------------------------------------------

    public class DoneActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            timer.stop();
            pasteIntoSelectedFrame();
            setVisible(false);
        }
    } // End InnerClass: DoneActionListener

    // --------------------------------------------------------------------------
    // InnerClass: DeviceActionListener
    // --------------------------------------------------------------------------

    public class DeviceActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // log.info("DeviceActionListener->actionPerformed");
            driverComboBox.removeAllItems();

            Device device = (Device) deviceComboBox.getSelectedItem();
            for (IDriver driver : device) {
                if (driver.isSingleDriver() || driver.isBankDriver()) {
                    driverComboBox.addItem(driver);
                }
            }
            driverComboBox.setEnabled(driverComboBox.getItemCount() > 1);
        }
    } // End InnerClass: DeviceActionListener

    // --------------------------------------------------------------------------
    // InnerClass: DriverActionListener
    // --------------------------------------------------------------------------

    public class DriverActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            IDriver driver = (IDriver) driverComboBox.getSelectedItem();
            // log.info("DriverActionListener->actionPerformed:" +
            // driver);
            if (driver == null) {
                return;
            }

            bankNumComboBox.removeAllItems();
            patchNumComboBox.removeAllItems();

            String bankNumbers[] = driver.getBankNumbers();
            if (bankNumbers != null && bankNumbers.length > 1) {
                for (int i = 0; i < bankNumbers.length; i++) {
                    bankNumComboBox.addItem(bankNumbers[i]);
                }
            }
            bankNumComboBox.setEnabled(bankNumComboBox.getItemCount() > 1);

            String patchNumbers[] = driver.getPatchNumbers();
            if (patchNumbers.length > 1) {
                for (int i = 0; i < patchNumbers.length; i++) {
                    patchNumComboBox.addItem(patchNumbers[i]);
                }
            }
            // N.B. Do not enable patch selection for banks
            patchNumComboBox.setEnabled(driver.isSingleDriver()
                    && patchNumComboBox.getItemCount() > 1);
        }
    } // End InnerClass: DriverActionListener

    // --------------------------------------------------------------------------
    // InnerClass: PasteActionListener
    // --------------------------------------------------------------------------

    public class PasteActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            myLabel.setText(" ");
            timer.stop();
            pasteIntoSelectedFrame();
            sysexSize = 0; // ???
        }
    } // End InnerClass: PasteActionListener

    // --------------------------------------------------------------------------
    // InnerClass: GetActionListener
    // --------------------------------------------------------------------------

    public class GetActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            IDriver driver = (IDriver) driverComboBox.getSelectedItem();
            int bankNum = bankNumComboBox.getSelectedIndex();
            int patchNum = patchNumComboBox.getSelectedIndex();
            inPort = driver.getDevice().getInPort();
            log.info("SysexGetDialog | port: " + inPort + " | bankNum: "
                    + bankNum + " | patchNum: " + patchNum);

            // ----- Start timer and request dump
            myLabel.setText("Getting sysex dump...");
            // patchsize value is similiar to expected transmission time *3
            timeOut = driver.getPatchSize();
            sysexSize = 0;
            queue = new ArrayList();
            midiService.clearSysexInputQueue(inPort); // clear MIDI input buffer
            timer.start();
            driver.requestPatchDump(bankNum, patchNum);
        }
    } // End InnerClass: GetActionListener

    // --------------------------------------------------------------------------
    // InnerClass: TimerActionListener
    // --------------------------------------------------------------------------

    private boolean isEmpty() {
        return midiService.isSysexInputQueueEmpty(inPort);
    }

    public class TimerActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            try {
                while (!isEmpty()) {
                    SysexMessage msg;
                    msg =
                            (SysexMessage) midiService.getMessage(inPort,
                                    timeOut);
                    queue.add(msg);
                    // log.info
                    // ("TimerActionListener | size more bytes: " +
                    // msg.getLength());
                    sysexSize += msg.getLength();
                    myLabel.setText(sysexSize + " Bytes Received");
                }
            } catch (Exception ex) {
                setVisible(false);
                timer.stop();
                ErrorMsg.reportError("Error", "Unable to receive Sysex", ex);
            }
        }
    } // End InnerClass: SysexGetTimer
} // End Class: SysexGetDialog
// (setq c-basic-offset 2)
