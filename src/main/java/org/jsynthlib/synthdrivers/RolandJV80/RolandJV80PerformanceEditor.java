/*
 * Copyright 2004 Sander Brandenburg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.synthdrivers.RolandJV80;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.ParamModel;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.CheckBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.PatchNameWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author Sander Brandenburg
 * @version $Id: RolandJV80PerformanceEditor.java 895 2005-02-07 05:50:06Z
 *          hayashi $
 */
public class RolandJV80PerformanceEditor extends PatchEditorFrame {

    private final boolean isJV80;
    private NumberGenerator numberGenerator;
    private RolandJV80PatchBank INTERNAL;
    private RolandJV80PatchBank CARD;
    private RolandJV80PatchBank PRESET_A;
    private RolandJV80PatchBank PRESET_B;

    RolandJV80PerformanceEditor(Patch p, NumberGenerator numberGenerator) {
        super("Roland JV-880 Performance Editor", p);
        this.numberGenerator = numberGenerator;

        isJV80 = ((RolandJV80PerformanceDriver) p.getDriver()).isJV80;

        ((RolandJV80Device) p.getDevice()).getPerformanceDriver()
                .setPerformanceNum(p.sysex, -1, 0);

        INTERNAL =
                new RolandJV80PatchBank("Internal", 0,
                        numberGenerator.generateNumbers(1, 64, "I-00"));

        CARD =
                new RolandJV80PatchBank("Card", 64,
                        numberGenerator.generateNumbers(1, 64, "C-00"));

        PRESET_A =
                new RolandJV80PatchBank("Preset A", 128, new String[] {
                        "A01: A.Piano 1", "A02: A.Piano 2",
                        "A03: Mellow Piano", "A04: Pop Piano 1",
                        "A05: Pop Piano 2", "A06: Pop Piano 3",
                        "A07: MIDIed Grand", "A08: Country Bar",
                        "A09: Glist EPiano", "A10: MIDI EPiano",
                        "A11: SA Rhodes", "A12: Dig Rhodes 1",
                        "A13: Dig Rhodes 2", "A14: Stiky Rhodes",
                        "A15: Guitr Rhodes", "A16: Nylon Rhodes",
                        "A17: Clav 1", "A18: Clav 2", "A19: Marimba",
                        "A20: Marimba SW", "A21: Warm Vibe", "A22: Vibe",
                        "A23: Wave Bells", "A24: Vibro Bell",
                        "A25: Pipe Organ 1", "A26: Pipe Organ 2",
                        "A27: Pipe Organ 3", "A28: E.Organ 1",
                        "A29: E.Organ 2", "A30: Jazz Organ 1",
                        "A31: Jazz Organ 2", "A32: Metal Organ",
                        "A33: Nylon Gtr 1", "A34: Flanged Nyln",
                        "A35: Steel Guitar", "A36: PickedGuitar",
                        "A37: 12 Strings", "A38: Velo Harmnix",
                        "A39: Nylon+Steel", "A40: SwitchOnMute",
                        "A41: JC Strat", "A42: Stratus", "A43: Syn Strat",
                        "A44: Pop Strat", "A45: Clean Strat", "A46: Funk Gtr",
                        "A47: Syn Guitar", "A48: Overdrive", "A49: Fretless",
                        "A50: St Fretless", "A51: Woody Bass 1",
                        "A52: Woody Bass 2", "A53: Analog Bs 1",
                        "A54: House Bass", "A55: Hip Bass",
                        "A56: RockOut Bass", "A57: Slap Bass",
                        "A58: Thumpin Bass", "A59: Pick Bass",
                        "A60: Wonder Bass", "A61: Yowza Bass",
                        "A62: Rubber Bs 1", "A63: Rubber Bs 2",
                        "A64: Stereoww Bs", });

        PRESET_B =
                new RolandJV80PatchBank("Preset B", 192, new String[] {
                        "B01: Pizzicato", "B02: Real Pizz", "B03: Harp",
                        "B04: SoarinString", "B05: Warm Strings",
                        "B06: Marcato", "B07: St Strings", "B08: Orch Strings",
                        "B09: Slow Strings", "B10: Velo Strings",
                        "B11: BrightStrngs", "B12: TremoloStrng",
                        "B13: Orch Stab 1", "B14: Brite Stab",
                        "B15: JP-8 Strings", "B16: String Synth",
                        "B17: Wire Strings", "B18: New Age Vox",
                        "B19: Arasian Morn", "B20: Beauty Vox",
                        "B21: Vento Voxx", "B22: Pvox Oooze",
                        "B23: Glass Voices", "B24: Space Ahh", "B25: Trumpet",
                        "B26: Trombone", "B27: Harmon Mute1",
                        "B28: Harmon Mute2", "B29: TeaJay Brass",
                        "B30: Brass Sect 1", "B31: Brass Sect 2",
                        "B32: Brass Swell", "B33: Brass Combo",
                        "B34: Stab Brass", "B35: Soft Brass",
                        "B36: Horn Brass", "B37: French Horn",
                        "B38: AltoLead Sax", "B39: Alto Sax",
                        "B40: Tenor Sax 1", "B41: Tenor Sax 2",
                        "B42: Sax Section", "B43: Sax Tp Tb",
                        "B44: FlutePiccolo", "B45: Flute mod", "B46: Ocarina",
                        "B47: OverblownPan", "B48: Air Lead",
                        "B49: Steel Drum", "B50: Log Drum", "B51: Box Lead",
                        "B52: Soft Lead", "B53: Whistle", "B54: Square Lead",
                        "B55: Touch Lead", "B56: NightShade",
                        "B57: Pizza Hutt", "B58: EP+Exp Pad", "B59: JP-8 Pad",
                        "B60: Puff", "B61: SpaciosSweep", "B62: Big n Beefy",
                        "B63: RevCymBend", "B64: Analog Seq" });

        buildEditor(p);
    }

    void buildEditor(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        scrollPane.add(buildCommon(patch), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        scrollPane.add(buildReserve(patch), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        scrollPane.add(buildChorus(patch), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        scrollPane.add(buildReverb(patch), gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        scrollPane.add(buildParts(patch), gbc);

        pack();
    }

    JPanel buildChorus(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "Chorus", TitledBorder.CENTER, TitledBorder.CENTER));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        panel.add(new JLabel("Chorus Type"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Chorus Level"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Chorus Depth"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Chorus Rate"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Chorus Feedback"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Chorus Output"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridy++;
        panel.add(new ComboBoxWidget(null, patch, new JVModel(patch, -1, 0x11),
                new JVSender(-1, 0x11), new String[] {
                        "CHORUS1", "CHORUS2", "CHORUS3" }), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x12), new JVSender(-1, 0x12)), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x13), new JVSender(-1, 0x13)), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x14), new JVSender(-1, 0x14)), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x15), new JVSender(-1, 0x15)), gbc);
        gbc.gridy++;
        panel.add(new ComboBoxWidget(null, patch, new JVModel(patch, -1, 0x11),
                new JVSender(-1, 0x11), new String[] {
                        "MIX", "REV" }), gbc);

        return panel;
    }

    JPanel buildReverb(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "Reverb", TitledBorder.CENTER, TitledBorder.CENTER));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridy++;
        panel.add(new JLabel("Reverb Type"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Reverb Level"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Reverb Time"), gbc);
        gbc.gridy++;
        panel.add(new JLabel("Delay Feedback"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridy++;
        panel.add(new ComboBoxWidget(null, patch, new JVModel(patch, -1, 0x0D),
                new JVSender(-1, 0x0D), new String[] {
                        "ROOM1", "ROOM2", "STAGE1", "HALL1", "HALL2", "DELAY",
                        "PAN-DLY" }), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x0E), new JVSender(-1, 0x0E)), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x0F), new JVSender(-1, 0x0F)), gbc);
        gbc.gridy++;
        panel.add(new ScrollBarWidget(null, patch, 0, 127, 0, new JVModel(
                patch, -1, 0x10), new JVSender(-1, 0x10)), gbc);

        return panel;
    }

    JPanel buildCommon(Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "Common", TitledBorder.CENTER, TitledBorder.CENTER));

        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Patch Name"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new PatchNameWidget(null, patch), gbc);

        if (isJV80) {
            gbc.gridx = 0;
            gbc.gridy = 1;
            panel.add(new JLabel("Key Mode"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 1;
            panel.add(new ComboBoxWidget(null, patch, new JVModel(patch, -1,
                    0x0C), new JVSender(-1, 0x0C), new String[] {
                    "LAYER", "ZONE", "SINGLE" }), gbc);
        }
        return panel;
    }

    JPanel buildReserve(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "Reserve", TitledBorder.CENTER, TitledBorder.CENTER));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 8;
        final JLabel poolLabel = new JLabel();
        panel.add(poolLabel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy = 1;

        final JSpinner spinners[] = new JSpinner[8];
        for (int i = 0; i < 8; i++) {
            spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 28, 1));

            final int ctr = i;
            spinners[i].addChangeListener(new ChangeListener() {
                final JVModel jm = new JVModel(patch, -1, 0x17 + ctr);
                final JVSender jsender = new JVSender(-1, 0x17 + ctr);

                {
                    spinners[ctr].setValue(new Integer(jm.get()));
                    if (ctr == 7)
                        stateChanged(new ChangeEvent(spinners[ctr]));
                }

                public void stateChanged(ChangeEvent e) {
                    // if change caused the sum of all spinners to be over 28
                    // revert that change
                    int sum = 0;
                    for (int i = 0; i < spinners.length; i++) {
                        JSpinner js = spinners[i];
                        sum += ((Integer) js.getValue()).intValue();
                    }
                    JSpinner js = (JSpinner) e.getSource();
                    int value = ((Integer) js.getValue()).intValue();
                    if (sum > 28) {
                        // new change not accepted: sum over 28 voices
                        // reset old setting
                        sum -= value;
                        js.setValue(new Integer(jm.get()));
                    } else {
                        jm.set(value);
                        jsender.send(patch.getDriver(), value);
                    }

                    poolLabel.setText("Left: " + Math.max((28 - sum), 0));
                }
            });

            gbc.gridx = i;
            gbc.gridy = 2;
            panel.add(spinners[i], gbc);

            gbc.gridy = 3;
            panel.add(new JLabel("Part " + (i + 1)), gbc);
        }

        return panel;
    }

    public String[] getBanks(Device dev) {
        if (Boolean.valueOf(
                RolandJV80Device.getPref(dev, RolandJV80Device.PREF_DATA))
                .booleanValue()) {
            return new String[] {
                    INTERNAL.getName(), CARD.getName(), PRESET_A.getName(),
                    PRESET_B.getName() };
        }

        return new String[] {
                INTERNAL.getName(), PRESET_A.getName(), PRESET_B.getName() };
    }

    JPanel buildParts(final Patch patch) {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
                "Performance Parts", TitledBorder.CENTER, TitledBorder.CENTER));

        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;

        JTabbedPane jtp = new JTabbedPane();
        panel.add(jtp, gbc);
        // make 4 tabs of 2 parts each
        for (int tabnr = 0; tabnr < 4; tabnr++) {
            JPanel part = new JPanel(new GridBagLayout());
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;
            gbc.weighty = 0;
            gbc.insets.left = gbc.insets.right = 4;
            gbc.insets.top = gbc.insets.bottom = 1;

            gbc.gridy++;
            part.add(new JLabel("Receive Switch"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Receive Channel"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Patch Bank"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Patch Number"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Part Level"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Part Pan"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Part Coarse Tune"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Part Fine Tune"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Reverb Switch"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Chorus Switch"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Receive Program Change"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Receive Volume"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Receive Hold-1"), gbc);
            gbc.gridy++;
            part.add(new JLabel("Output Select"), gbc);

            gbc.anchor = GridBagConstraints.CENTER;

            for (int i = 0; i < 2; i++) {
                gbc.gridx = 1 + i;
                gbc.gridy = 0;
                int partnr = tabnr * 2 + i;

                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x15), new JVSender(partnr, 0x15)), gbc);
                gbc.gridy++;
                part.add(new ComboBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x16), new JVSender(partnr, 0x16),
                        numberGenerator.generateNumbers(1, 16, "0")), gbc);

                final JComboBox bank =
                        new JComboBox(getBanks(patch.getDevice()));
                final JComboBox patches = new JComboBox();
                final int ctr = partnr;
                ActionListener al = new ActionListener() {
                    final JV2Sender js = new JV2Sender(ctr, 0x17);
                    final JV2Model jm = new JV2Model(patch, ctr, 0x17);
                    {
                        int patchnum = jm.get();
                        RolandJV80PatchBank pb =
                                RolandJV80PatchBank
                                        .getBankByPatchNumber(patchnum);
                        // we don't have the data card, although the patch is on
                        // it
                        if (pb == null) {
                            bank.setSelectedIndex(0);
                            patches.setSelectedIndex(0);
                        } else {
                            bank.setSelectedItem(pb.getName());
                            patches.setModel(new DefaultComboBoxModel(pb
                                    .getPatches()));
                            patches.setSelectedIndex(patchnum - pb.getOffset());
                        }
                    }

                    public void actionPerformed(ActionEvent e) {
                        String bankstr = (String) bank.getSelectedItem();
                        RolandJV80PatchBank pb =
                                RolandJV80PatchBank.getBank(bankstr);

                        if (e.getSource() == bank) {
                            int pidx = patches.getSelectedIndex();
                            patches.setModel(new DefaultComboBoxModel(pb
                                    .getPatches()));
                            if (pidx < patches.getItemCount())
                                patches.setSelectedIndex(pidx);
                        }
                        int value = pb.getOffset() + patches.getSelectedIndex();
                        jm.set(value);
                        js.send(patch.getDriver(), value);
                    }
                };
                bank.addActionListener(al);
                patches.addActionListener(al);
                gbc.gridy++;
                part.add(bank, gbc);
                gbc.gridy++;
                part.add(patches, gbc);

                gbc.gridy++;
                part.add(new ScrollBarWidget(null, patch, 0, 127, 0,
                        new JVModel(patch, partnr, 0x19), new JVSender(partnr,
                                0x19)), gbc);
                gbc.gridy++;
                part.add(new ScrollBarWidget(null, patch, 0, 127, -64,
                        new JVModel(patch, partnr, 0x1A), new JVSender(partnr,
                                0x1A)), gbc);
                gbc.gridy++;
                part.add(new ScrollBarWidget(null, patch, 16, 112, -64,
                        new JVModel(patch, partnr, 0x1B), new JVSender(partnr,
                                0x1B)), gbc);
                gbc.gridy++;
                part.add(new ScrollBarWidget(null, patch, 14, 114, -64,
                        new JVModel(patch, partnr, 0x1C), new JVSender(partnr,
                                0x1C)), gbc);
                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x1D), new JVSender(partnr, 0x1D)), gbc);
                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x1E), new JVSender(partnr, 0x1E)), gbc);
                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x1F), new JVSender(partnr, 0x1F)), gbc);
                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x20), new JVSender(partnr, 0x20)), gbc);
                gbc.gridy++;
                part.add(new CheckBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x21), new JVSender(partnr, 0x21)), gbc);
                gbc.gridy++;
                part.add(new ComboBoxWidget(null, patch, new JVModel(patch,
                        partnr, 0x22), new JVSender(partnr, 0x22),
                        new String[] {
                                "MN", "SB", "PAT" }), gbc);
            }
            jtp.add("Part " + (2 * tabnr + 1) + "-" + (2 * tabnr + 2), part);
        }

        return panel;
    }

    // sends to temporary performance
    class JVSender extends AbstractJV80SysexSender {
        byte addr3;
        byte addr4;

        // retrieve default from patch
        public JVSender(int part, int msg_offset) {
            super("F041@@461200000000**00F7", 1);
            addr3 = 0x10;
            if (part > 0)
                addr3 += 8 + part;
            addr4 = (byte) msg_offset;
        }

        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            return data;
        }
    }

    class JVModel extends ParamModel {
        final static int DATA_OFFSET = 9;

        JVModel(Patch p, int part, int msg_offset) {
            super(p, DATA_OFFSET + msg_offset);
            if (part >= 0)
                offset +=
                        ((RolandJV80PerformanceDriver) p.getDriver()).performancePartOffsets[part];
        }
    }

    class JV2Model extends JVModel {
        JV2Model(Patch p, int tone, int msg_offset) {
            super(p, tone, msg_offset);
        }

        public int get() {
            return (patch.sysex[offset] << 4) + (patch.sysex[offset + 1]);
        }

        public void set(int value) {
            patch.sysex[offset] = (byte) (value >> 4);
            patch.sysex[offset + 1] = (byte) (value & 0x0F);
        }
    }

    class JV2Sender extends AbstractJV80SysexSender {
        byte addr3;
        byte addr4;

        // retrieve default from patch
        public JV2Sender(int part, int msg_offset) {
            super("F041@@461200001000****00F7", 2);
            addr3 = 0x10;
            if (part > 0)
                addr3 += 8 + part;
            addr4 = (byte) msg_offset;
        }

        protected byte[] generate(int value) {
            byte[] data = super.generate(value);
            data[JV80Constants.ADDR3_IDX] = addr3;
            data[JV80Constants.ADDR4_IDX] = addr4;
            data[JV80Constants.ADDR4_IDX + 1] = (byte) (value >> 4);
            data[JV80Constants.ADDR4_IDX + 2] = (byte) (value & 0x0F);
            return data;
        }
    }

}
