/**
 * Single Editor for TC Electronics M350.
 * @version $Id: KawaiK4SingleEditor.java 859 2005-01-30 22:15:49Z hayashi $
 */
package org.jsynthlib.synthdrivers.TCElectronicM350;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.KnobWidget;
import org.jsynthlib.device.viewcontroller.widgets.PatchNameWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;

class TCElectronicM350SingleEditor extends PatchEditorFrame {

    private static final String[] EffectsNames = new String[] {
            "Off", "Comp", "Hard Comp", "De-Esser", "Smooth Chorus",
            "Lush Chorus", "Inst. Flanger", "Tremelo", "Vintage Phaser",
            "Smooth Phaser", "Delay Slapback", "Delay Pingpong", "Soft Delay",
            "Triplets Delay", "Studio Delay", "Dynamic Delay" };

    private static final String[] ReverbNames = new String[] {
            "Off", "TC Classic Hall", "Cathedral", "Vocal Reverb",
            "Live Vocal", "Hall Acoustic", "Drum Ambience", "Drum Room",
            "Ambience", "Living Room", "Nearfield", "Damped Room",
            "Silver Plate", "Gold Plate", "Spring Vintage", "Live Stage" };

    public TCElectronicM350SingleEditor(Patch patch) {
        super("TC Electronic M350 Single Editor", patch);

        //

        gbc.weightx = 5;
        JPanel cmnPane = new JPanel();
        cmnPane.setLayout(new GridBagLayout());
        gbc.weightx = 0;

        addWidget(cmnPane, new ComboBoxWidget("Delay|Effects", patch, 0,
                new M350Model(patch, 3), new M350Sender(3), EffectsNames), 0,
                0, 3, 1, 4);

        addWidget(cmnPane, new PatchNameWidget(" Name  ", patch), 3, 0, 5, 1, 0);

        addWidget(cmnPane, new ComboBoxWidget("Reverb", patch, 0,
                new M350Model(patch, 6), new M350Sender(6), ReverbNames), 8, 0,
                3, 1, 7);

        gbc.weightx = 1;
        addWidget(cmnPane, new KnobWidget("Input Gain", patch, 0, 127, 0,
                new M350Model(patch, 0), new M350Sender(0)), 0, 1, 1, 1, 1);

        addWidget(cmnPane, new KnobWidget("Mix Ratio", patch, 0, 127, 0,
                new M350Model(patch, 1), new M350Sender(1)), 1, 1, 1, 1, 2);

        addWidget(cmnPane, new KnobWidget("Effect Bal", patch, 0, 127, 0,
                new M350Model(patch, 2), new M350Sender(2)), 2, 1, 1, 1, 3);

        addWidget(cmnPane, new KnobWidget("Delay/Timing", patch, 0, 127, 0,
                new M350Model(patch, 4), new M350Sender(4)), 3, 1, 1, 1, 5);

        addWidget(cmnPane, new KnobWidget("Feedback/Depth", patch, 0, 127, 0,
                new M350Model(patch, 5), new M350Sender(5)), 4, 1, 1, 1, 6);

        addWidget(cmnPane, new KnobWidget("Predelay", patch, 0, 127, 0,
                new M350Model(patch, 7), new M350Sender(7)), 9, 1, 1, 1, 8);

        addWidget(cmnPane, new KnobWidget("Decay Time", patch, 0, 127, 0,
                new M350Model(patch, 8), new M350Sender(8)), 10, 1, 1, 1, 9);

        addWidget(cmnPane, new KnobWidget("Colour Filter", patch, 0, 127, 0,
                new M350Model(patch, 9), new M350Sender(9)), 11, 1, 1, 1, 10);

        addWidget(cmnPane, new ScrollBarWidget("Tap (ms)", patch, 0, 16383, 0,
                new M350Model(patch, 13), new M350Sender(13)), 0, 2, 11, 1, 11);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 5;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        cmnPane.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.RAISED), "M350",
                TitledBorder.CENTER, TitledBorder.CENTER));
        scrollPane.add(cmnPane, gbc);

        pack();
    }

}