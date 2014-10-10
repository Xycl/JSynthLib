package org.jsynthlib.midi.viewcontroller;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.preferences.ConfigPanel;
import org.jsynthlib.core.viewcontroller.preferences.PrefsDialog;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiLoopbackService;

/**
 * The panel that configures the MIDI layer. Taken out of PrefsDialog.
 * @author Joe Emenaker
 * @author Hiroo Hayashi
 * @version $Id: MidiConfigPanel.java 1079 2007-09-19 22:50:29Z billzwicky $
 */
public class MidiConfigPanel extends ConfigPanel {

    private static final long serialVersionUID = 1L;

    private final transient Logger log = Logger.getLogger(getClass());

    /** CheckBox for MIDI */
    private final JCheckBox cbxEnMidi;
    /** ComboBox for MIDI Out port. */
    private final JComboBox cbOut;
    /** ComboBox for MIDI In port. */
    private final JComboBox cbIn;
    /** ComboBox for MIDI In port for Master Controller. */
    private final JComboBox cbMC;
    /** CheckBox for Master Controller. */
    private final JCheckBox cbxEnMC;
    /** button for loop-back test. */
    private final JButton testButton;
    /** spinner for MIDI output buffer size. */
    private final JSpinner spBufSize;
    /** spinner for MIDI output delay time. */
    private final JSpinner spDelay;

    private final MidiSettings midiSettings;
    private final MidiLoopbackService midiLoopbackService;

    private final DeviceManager deviceManager;

    public MidiConfigPanel(PrefsDialog parent) {
        super(parent);

        midiSettings = JSynthLibInjector.getInstance(MidiSettings.class);
        midiLoopbackService =
                JSynthLibInjector.getInstance(MidiLoopbackService.class);
        deviceManager = JSynthLibInjector.getInstance(DeviceManager.class);

        panelName = "MIDI";
        nameSpace = "midi";
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        cbxEnMidi = new JCheckBox("Enable MIDI Interface");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        p.add(cbxEnMidi);

        c.fill = GridBagConstraints.HORIZONTAL;

        // Output Port/Input Port selection
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(10, 0, 0, 0);
        p.add(new JLabel("Output Port:"), c);
        cbOut = new JComboBox(midiSettings.getOutputNames()); // wirski@op.pl
        cbOut.setName("cbOut");
        c.gridx = 1;
        c.gridwidth = 2;
        p.add(cbOut, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 0, 0);
        p.add(new JLabel("Input Port:"), c);
        cbIn = new JComboBox(midiSettings.getInputNames()); // wirski@op.pl
        cbIn.setName("cbIn");
        c.gridx = 1;
        c.gridwidth = 2;
        p.add(cbIn, c);

        // master controller selection
        cbxEnMC = new JCheckBox("Enable Master Controller Input Port");
        cbxEnMC.setToolTipText("If enabled MIDI messages from Master Input Port are sent to Output Port.");
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 3;
        p.add(cbxEnMC, c);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        JLabel label = new JLabel("Master Input Port:");
        label.setToolTipText("MIDI notes from this port are echoed to the output MIDI port.");
        p.add(label, c);
        c.gridx = 1;
        c.gridwidth = 2;
        cbMC = new JComboBox(midiSettings.getInputNames()); // wirski@op.pl
        p.add(cbMC, c);

        // MIDI output buffer size and delay
        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.insets = new Insets(10, 0, 0, 0);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.WEST;
        label = new JLabel("MIDI Output Buffer Size:");
        label.setToolTipText("MIDI message size."
                + " If zero, whole MIDI message is passed to lower MIDI driver."
                + " Normally set to zero.");
        p.add(label, c);
        c.gridx = 1;
        spBufSize = new JSpinner(new SpinnerNumberModel(0, 0, 512, 16));
        p.add(spBufSize, c);
        c.gridx = 2;
        p.add(new JLabel(" (byte)"), c);

        c.gridx = 0;
        c.gridy++;
        c.insets = new Insets(0, 0, 0, 0);
        label = new JLabel("MIDI Output Delay:");
        label.setToolTipText("delay after every MIDI message output transfer.");
        p.add(label, c);
        c.gridx = 1;
        spDelay = new JSpinner(new SpinnerNumberModel(0, 0, 500, 10));
        p.add(spDelay, c);
        c.gridx = 2;
        p.add(new JLabel(" (msec)"), c);

        // MIDI loopback test
        c.gridx = 0;
        c.gridy++;
        c.insets = new Insets(10, 0, 0, 0);
        p.add(new JLabel("MIDI Loopback Test:"), c);
        c.gridx = 1;
        testButton = new JButton("Run...");
        p.add(testButton, c);

        add(p, BorderLayout.CENTER);

        // add actionListeners
        cbxEnMidi.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnable(cbxEnMidi.isSelected());
                setModified(true);
            }
        });

        cbxEnMC.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbMC.setEnabled(cbxEnMC.isSelected());
                setModified(true);
            }
        });

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModified(true);
            }
        };
        cbOut.addActionListener(al);
        cbIn.addActionListener(al);
        cbMC.addActionListener(al);

        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                midiLoopbackService.runLoopbackTest(cbIn.getSelectedIndex(),
                        cbOut.getSelectedIndex());

                // ErrorMsg.reportError("Error", "Data Compare Error:"
                // + "\nreceived data: " + midiMessageToString(inmsg)
                // + "\nexpected data: " + midiMessageToString(msg));
            }
        });
    }

    @Override
    protected void init() {
        cbxEnMidi.setSelected(midiSettings.getMidiEnable());
        cbxEnMC.setSelected(midiSettings.getMasterInEnable());

        try {
            if (cbOut.getItemCount() > 0) {
                cbOut.setSelectedIndex(midiSettings.getInitPortOut());
            }
            if (cbIn.getItemCount() > 0) {
                cbIn.setSelectedIndex(midiSettings.getInitPortIn());
            }
            if (cbMC.getItemCount() > 0) {
                cbMC.setSelectedIndex(midiSettings.getMasterController());
            }
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
        }

        spBufSize.setValue(new Integer(midiSettings.getMidiOutBufSize()));
        spDelay.setValue(new Integer(midiSettings.getMidiOutDelay()));

        // disable MIDI when either MIDI input or MIDI output is unavailable.
        cbxEnMidi.setEnabled(midiSettings.isOutputAvailable()
                || midiSettings.isInputAvailable());
        setEnable(midiSettings.getMidiEnable());
    }

    /**
     * enable/disable widgets according to the various settings..
     */
    private void setEnable(boolean midiEn) {
        cbxEnMC.setEnabled(midiEn && midiSettings.isOutputAvailable()
                && midiSettings.isInputAvailable());
        testButton.setEnabled(midiEn && midiSettings.isOutputAvailable()
                && midiSettings.isInputAvailable());

        cbOut.setEnabled(midiEn && midiSettings.isOutputAvailable());
        cbIn.setEnabled(midiEn && midiSettings.isInputAvailable());
        cbMC.setEnabled(midiEn && cbxEnMC.isSelected());
        spBufSize.setEnabled(midiEn && midiSettings.isOutputAvailable());
        spDelay.setEnabled(midiEn && midiSettings.isOutputAvailable());
    }

    @Override
    protected void commitSettings() {
        if (cbxEnMidi.isSelected()) {
            midiSettings.setMidiEnable(true);
            midiSettings.setMasterController(cbMC.getSelectedIndex());
            midiSettings.setMasterInEnable(cbxEnMC.isSelected());
            midiSettings.setMidiOutBufSize(((Integer) spBufSize.getValue())
                    .intValue());
            midiSettings.setMidiOutDelay(((Integer) spDelay.getValue())
                    .intValue());

            int out = cbOut.getSelectedIndex();
            int in = cbIn.getSelectedIndex();
            midiSettings.setInitPortOut(out);
            midiSettings.setInitPortIn(in);
            if (!midiSettings.getMultiMIDI()) {
                // change MIDI ports of all Devices
                for (int i = 0; i < deviceManager.deviceCount(); i++) {
                    String outputName = midiSettings.getOutputName(out);
                    deviceManager.getDevice(i).setOutPortName(outputName);
                    deviceManager.getDevice(i).setInPort(in);
                }
            }
        } else {
            midiSettings.setMidiEnable(false);
        }
        setModified(false);
    }
}
