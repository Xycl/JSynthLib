package org.jsynthlib.core.viewcontroller.preferences;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jsynthlib.core.Constants;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.domain.MidiSettings;

/**
 * Config panel for Fader Input setting.
 * @author Joe Emenaker
 * @author Hiroo Hayashi
 * @version $Id: FaderBoxConfigPanel.java 950 2005-03-06 03:58:11Z hayashi $
 */
class FaderBoxConfigPanel extends ConfigPanel {

    private static final long serialVersionUID = 1L;

    {
        panelName = "Fader Box";
        nameSpace = "faderbox";
    }

    private final JPanel faderPanel;
    private final JComboBox cbFdr;
    private final JCheckBox enabledBox;
    private final JList lstSl;
    private final JComboBox cbControl;
    private final JComboBox cbChannel;

    /* keep current values */
    private int slider = 0;
    private final int[] control = new int[Constants.NUM_FADERS];
    private final int[] channel = new int[Constants.NUM_FADERS];
    private final MidiSettings midiSettings;

    FaderBoxConfigPanel(PrefsDialog parent) {
        super(parent);
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        // Fader Port selection
        enabledBox = new JCheckBox("Enable Fader Input Port");
        enabledBox
                .setToolTipText("Sliders and buttons are controlled by Control Change MIDI message.");
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        p.add(enabledBox, c);
        enabledBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setContainerEnabled(faderPanel, enabledBox.isSelected());
                setModified(true);
            }
        });

        midiSettings = JSynthLibInjector.getInstance(MidiSettings.class);

        // create own Fader/Slider Panel
        faderPanel = new JPanel(new BorderLayout(5, 5));
        // upper side
        cbFdr = new JComboBox(midiSettings.getInputMidiDeviceInfo());
        cbFdr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setModified(true);
            }
        });
        c.gridy++;
        c.gridwidth = GridBagConstraints.REMAINDER;
        faderPanel.add(cbFdr, BorderLayout.NORTH);

        // Init Slider JList
        // 0 : active slider
        // 1-16 : fader 1-16
        // 17-30 : button 1-14
        // 31 : button 15 : prev fader bank
        // 32 : button 16 : next fader bank
        String[] n = new String[Constants.NUM_FADERS];
        n[0] = "Active Slider";
        for (int i = 1; i <= 16; i++) {
            // slider : 1 to 16
            n[i] = "Slider #" + i;
        }
        for (int i = 1; i <= 14; i++) {
            // button : 1 to 14
            n[i + 16] = "Button #" + i;
        }
        n[31] = "Button #15: Prev Slider Bank";
        n[32] = "Button #16: Next Slider Bank";
        lstSl = new JList(n);
        lstSl.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                slider = lstSl.getSelectedIndex();
                cbControl.setSelectedIndex(control[slider]);
                cbChannel.setSelectedIndex(channel[slider]);
                // setModified(true); // don't do that
            }
        });
        lstSl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // MIDI Control Change Message controll # (0-119)
        // 120-127 are reserved for Channel Mode Messages
        String[] cc1 = new String[121];
        for (int i = 0; i < 120; i++) {
            cc1[i] = "" + i;
        }
        cc1[120] = "Off";
        cbControl = new JComboBox(cc1);
        cbControl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                control[slider] = cbControl.getSelectedIndex();
                setModified(true);
            }
        });

        String[] cc2 = new String[17]; // channel
        for (int i = 0; i < 16; i++) {
            cc2[i] = "" + (i + 1);
        }
        cc2[16] = "Off";
        cbChannel = new JComboBox(cc2);
        cbChannel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                channel[slider] = cbChannel.getSelectedIndex();
                setModified(true);
            }
        });

        // left side
        JScrollPane scroll = new JScrollPane(lstSl);
        faderPanel.add(scroll, BorderLayout.WEST);
        // right side
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new GridBagLayout());
        GridBagConstraints dc = new GridBagConstraints();
        dc.anchor = GridBagConstraints.EAST;
        dc.gridx = 0;
        dc.gridy = 1;
        dc.gridwidth = 2;
        dataPanel.add(new JLabel("MIDI Channel Number: "), dc);
        dc.gridx = 0;
        dc.gridy = 2;
        dc.gridwidth = 2;
        dataPanel.add(new JLabel("MIDI Control Number: "), dc);
        dc.gridx = 2;
        dc.gridy = 1;
        dc.gridwidth = 2;
        dataPanel.add(cbChannel, dc);
        dc.gridx = 2;
        dc.gridy = 2;
        dc.gridwidth = 2;
        dataPanel.add(cbControl, dc);
        dc.gridx = 2;
        dc.gridy = 5;
        dc.gridwidth = 5;
        dc.fill = GridBagConstraints.BOTH;
        dataPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        faderPanel.add(dataPanel, BorderLayout.CENTER);
        // lower side
        JPanel buttonPanel = new JPanel();
        JButton b0 = new JButton("Reset");
        JButton b1 = new JButton("Peavey PC1600x Preset");
        JButton b2 = new JButton("Kawai K5000 Knobs Preset");
        b0.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetSliders();
                setModified(true);
            }
        });
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presetPC1600x();
                setModified(true);
            }
        });
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                presetKawaiK5000();
                setModified(true);
            }
        });

        buttonPanel.add(b0);
        buttonPanel.add(b1);
        buttonPanel.add(b2);

        faderPanel.add(buttonPanel, BorderLayout.SOUTH);

        c.gridx = 0;
        c.gridy++;
        c.gridwidth = 1;
        c.gridheight = 1;
        p.add(faderPanel, c);
        add(p, BorderLayout.CENTER);
    }

    @Override
    protected void init() {
        try {
            if (midiSettings.isInputAvailable()) {
                // index 0 may be unavailable
                cbFdr.setSelectedIndex(getAppConfig().getFaderPort());
            }
        } catch (IllegalArgumentException e) {
            cbFdr.setSelectedIndex(0);
        }

        // copy into the temporary array.
        for (int i = 0; i < Constants.NUM_FADERS; i++) {
            control[i] = getAppConfig().getFaderControl(i);
            channel[i] = getAppConfig().getFaderChannel(i);
        }
        lstSl.setSelectedIndex(slider);
        cbControl.setSelectedIndex(control[slider]);
        cbChannel.setSelectedIndex(channel[slider]);

        enabledBox.setSelected(getAppConfig().getFaderEnable());
        enabledBox.setEnabled(midiSettings.getMidiEnable());

        setContainerEnabled(faderPanel,
                enabledBox.isEnabled() && enabledBox.isSelected());
    }

    @Override
    protected void commitSettings() {
        getAppConfig().setFaderEnable(enabledBox.isSelected());
        getAppConfig().setFaderPort(cbFdr.getSelectedIndex());
        for (int i = 0; i < Constants.NUM_FADERS; i++) {
            getAppConfig().setFaderControl(i, control[i]);
            getAppConfig().setFaderChannel(i, channel[i]);
        }
        setModified(false);
    }

    /*
     * Private methods
     */
    private void resetSliders() { // disable all
        for (int i = 0; i < Constants.NUM_FADERS; i++) {
            channel[i] = 0;
            control[i] = 120;
        }
        cbControl.setSelectedIndex(control[slider]);
        cbChannel.setSelectedIndex(channel[slider]);
    }

    private void presetPC1600x() {
        channel[0] = 16;
        control[0] = 120;
        for (int i = 1; i < 17; i++) {
            channel[i] = i - 1;
            control[i] = 24;
        }
        for (int i = 17; i < Constants.NUM_FADERS; i++) {
            channel[i] = i - 17;
            control[i] = 25;
        }
        cbChannel.setSelectedIndex(channel[slider]);
        cbControl.setSelectedIndex(control[slider]);
    }

    private void presetKawaiK5000() {
        channel[0] = 0;
        control[0] = 1;
        for (int i = 1; i < 17; i++) {
            channel[i] = 0;
        }
        for (int i = 17; i < Constants.NUM_FADERS; i++) {
            channel[i] = 16;
            control[i] = 120;
        }
        control[1] = 16;
        control[2] = 18;
        control[3] = 74;
        control[4] = 73;
        control[5] = 17;
        control[6] = 19;
        control[7] = 77;
        control[8] = 78;
        control[9] = 71;
        control[10] = 75;
        control[11] = 76;
        control[12] = 72;
        control[13] = 80;
        control[14] = 81;
        control[15] = 82;
        control[16] = 83;

        cbControl.setSelectedIndex(control[slider]);
        cbChannel.setSelectedIndex(channel[slider]);
    }
}
