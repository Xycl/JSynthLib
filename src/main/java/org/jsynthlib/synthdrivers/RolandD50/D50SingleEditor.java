/*
 * Copyright 2013 Pascal Collberg
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
package org.jsynthlib.synthdrivers.RolandD50;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.CheckBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget.Node;
import org.jsynthlib.device.viewcontroller.widgets.PatchNameWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.RolandD10.D10Constants;

public class D50SingleEditor extends PatchEditorFrame {

    private static final Border BORDER = BorderFactory.createEtchedBorder();

    public static class Factory {
        private int deviceId;

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public D50Sender newSender(int offset) {
            D50Sender sender = new D50Sender();
            sender.setOffset(offset);
            return sender;
        }

        public D50Sender newPartialMuteSender(int offset,
                final D50PartialMuteParamModel model) {
            D50Sender sender = new D50Sender() {
                @Override
                void setMessage(int value) {
                    super.setMessage(model.getPatch().sysex[model.getOffset()]);
                }
            };
            sender.setOffset(offset);
            return sender;
        }
    }

    private final Patch patch;
    private final Factory senderFactory = new Factory();
    private final ImageIcon[] structureIcons;

    private static final String[] PCM_SAMPLES = {
            "Marmba", "Vibes", "Xylo1", "Xylo2", "Log_Bs", "Hammer", "JpnDrm",
            "Kalmba", "Pluck", "Chick", "Agogo", "3angle", "Bells", "Nails",
            "Pick", "Lpiano", "Mpiano", "Hpiano", "Harpsi", "Harp", "OrgPrc",
            "Steel", "Nylon", "Eguit1", "Eguit2", "Dirt", "P_Bass", "Pop",
            "Thump", "Uprite", "Clarnt", "Breath", "Steam", "FluteH", "FluteL",
            "Guiro", "IndFlt", "Harmo", "Lips1", "Lips2", "Trumpt", "Bones",
            "Contra", "Cello", "VioBow", "Violns", "Pizz", "Drawbr", "Horgan",
            "Lorgan", "EP_lp1", "EP_lp2", "CLAVlp", "HC_lp", "EB_lp1", "AB_lp",
            "EB_lp2", "EB_lp3", "EG_lp", "CELLlp", "VIOLlp", "Reedlp",
            "SAXlp1", "SAXlp2", "Aah_lp", "Ooh_lp", "Manlp1", "Spect1",
            "Spect2", "Spect3", "Spect4", "Spect5", "Spect6", "Spect7",
            "Manlp2", "Noise", "Loop01", "Loop02", "Loop03", "Loop04",
            "Loop05", "Loop06", "Loop07", "Loop08", "Loop09", "Loop10",
            "Loop11", "Loop12", "Loop13", "Loop14", "Loop15", "Loop16",
            "Loop17", "Loop18", "Loop19", "Loop20", "Loop21", "Loop22",
            "Loop23", "Loop24", };

    private static final String[] KEY_FOLLOW = {
            "-1", "-1/2", "-1/4", "0", "1/8", "1/4", "3/8", "1/2", "5/8",
            "3/4", "7/8", "1", "5/4", "3/2", "2" };

    private static final String[] LFO_SELECT = {
            "+1", "-1", "+2", "-2", "+3", "-3" };

    private static final String[] LFO_WAVEFORMS = {
            "TRI", "SAW", "SQU", "RND" };

    private static final String[] KEY_MODES = {
            "Whole", "Dual", "Split", "Separate", "Split-S", "Dual-S",
            "Split-US", "Split-LS", "Separate-S" };

    private static final String[] PORTA_MODES = {
            "U", "L", "UL" };
    private static final String[] CHASE_MODES = {
            "UL", "ULL", "ULU" };

    private static final String[] CHORUS_TYPES = {
            "Chorus 1", "Chorus 2", "Flanger 1", "Flanger 2",
            "Feedback Chorus", "Tremolo", "Chorus Tremolo", "Dimension" };

    private static final String[] REVERB_TYPES = {
            "Small Hall", "Medium Hall", "Large Hall", "Chapel", "Box",
            "Small Metal Room", "Small Room", "Medium Room",
            "Medium Large Room", "Large Room", "Single Delay (102ms)",
            "Cross Delay (180ms)", "Cross Delay (224ms)",
            "Cross Delay (148-296ms)", "Short Gate (200ms)",
            "Long Gate (480ms)", "Bright Hall", "Large Cave", "Steel Pan",
            "Delay (248ms)", "Delay (338ms)", "Cross Delay (157ms)",
            "Cross Delay (252ms)", "Cross Delay (274-137ms)", "Gate Reverb",
            "Reverse Gate (360ms)", "Reverse Gate (480ms)", "Slap Back",
            "Slap Back", "Slap Back", "Twisted Space", "Space" };

    protected D50SingleEditor(Patch patch) {
        super("Roland D-50 Single Patch Editor", patch);
        this.patch = patch;

        structureIcons = new ImageIcon[13];
        for (int i = 0; i < structureIcons.length; i++) {
            int temp = i + 1;
            StringBuilder sb = new StringBuilder();
            sb.append("images/structure");
            if (temp < 10) {
                sb.append("0");
            }
            sb.append(temp);
            sb.append(".png");
            structureIcons[i] =
                    new ImageIcon(D10Constants.class.getResource(sb.toString()));
        }

        senderFactory.setDeviceId(patch.sysex[2]);

        JTabbedPane patchPane = new JTabbedPane(); // the main container

        JPanel patchPanel = new JPanel(new GridBagLayout());
        patchPane.addTab("Patch", patchPanel);
        fillPatchPanel(patchPanel);

        for (int i = 1; i <= 2; i++) {
            for (int j = 1; j <= 2; j++) {
                JPanel partialPanel = new JPanel(new FlowLayout());
                StringBuilder sb = new StringBuilder();
                if (i == 1) {
                    sb.append("Upper ");
                } else {
                    sb.append("Lower ");
                }
                sb.append("Partial ").append(j);
                patchPane.addTab(sb.toString(), partialPanel);
                fillPartialPanel(partialPanel, ((i - 1) * 2) + j);
            }

            JPanel commonPanel = new JPanel(new FlowLayout());
            String paneName = "Common " + (i == 1 ? "Upper" : "Lower");
            patchPane.addTab(paneName, commonPanel);
            fillCommonPanel(commonPanel, i);
        }

        scrollPane.add(patchPane);
    }

    int getPartialBase(int partialIndex) {
        int base = (partialIndex - 1) * D50Constants.PARTIAL_SIZE;
        if (partialIndex > 2) {
            base += D50Constants.PARTIAL_SIZE;
        }
        return base;
    }

    int getCommonBase(int index) {
        return index * 3 * D50Constants.PARTIAL_SIZE
                - D50Constants.PARTIAL_SIZE;
    }

    int getPatchBase() {
        return 6 * D50Constants.PARTIAL_SIZE;
    }

    final void fillPatchPanel(JPanel patchPanel) {
        int base = getPatchBase();
        addWidget(patchPanel, new PatchNameWidget("Name", patch), 0, 0, 1, 1, 0);

        addWidget(
                patchPanel,
                new ScrollBarWidget("<html>Lower Tone<br>Key Shift</html>",
                        patch, 0, 48, -24, new ParamModel(patch, base
                                + D50Constants.PATCH_LTONE_KEYSHIFT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_LTONE_KEYSHIFT)), 0, 3, 1,
                1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("<html>Upper Tone<br>Fine Tune</html>",
                        patch, 0, 100, -50, new ParamModel(patch, base
                                + D50Constants.PATCH_UTONE_FINE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_UTONE_FINE)), 0, 4, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("<html>Lower Tone<br>Fine Tune</html>",
                        patch, 0, 100, -50, new ParamModel(patch, base
                                + D50Constants.PATCH_LTONE_FINE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_LTONE_FINE)), 0, 5, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Bender Range", patch, 0, 12, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_BENDER_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_BENDER_RANGE)), 1, 0, 1,
                1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("<html>After Touch<br>Bend Range<html>",
                        patch, 0, 24, -12, new ParamModel(patch, base
                                + D50Constants.PATCH_AT_BEND_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_AT_BEND_RANGE)), 1, 1, 1,
                1, 0);

        addWidget(
                patchPanel,
                new ScrollBarWidget("Output Mode", patch, 0, 3, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_OUTPUT_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_OUTPUT_MODE)), 1, 3, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ComboBoxWidget("Reverb Type", patch, new ParamModel(patch,
                        base + D50Constants.PATCH_REVERB_TYPE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_REVERB_TYPE), REVERB_TYPES),
                1, 5, 1, 1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Reverb Balance", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_REVERB_BALANCE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_REVERB_BALANCE)), 1, 6, 1,
                1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Total Volume", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_TOTAL_VOLUME
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_TOTAL_VOLUME)), 1, 7, 1,
                1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Tone Balance", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_TONE_BALANCE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_TONE_BALANCE)), 1, 8, 1,
                1, 0);

        addWidget(
                patchPanel,
                new ComboBoxWidget("Chase Mode", patch, new ParamModel(patch,
                        base + D50Constants.PATCH_CHASE_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_CHASE_MODE), CHASE_MODES),
                2, 0, 1, 1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Chase Level", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_CHASE_LEVEL
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_CHASE_LEVEL)), 2, 1, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Chase Time", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_CHASE_TIME
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_CHASE_TIME)), 2, 2, 1, 1,
                0);

        addWidget(
                patchPanel,
                new ComboBoxWidget("Key Mode", patch, new ParamModel(patch,
                        base + D50Constants.PATCH_KEY_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_KEY_MODE), KEY_MODES), 2,
                5, 1, 1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Split Point", patch, 0, 60, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_SPLIT_POINT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_SPLIT_POINT)), 2, 6, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ComboBoxWidget("Portamento Mode", patch, new ParamModel(
                        patch, base + D50Constants.PATCH_PORTA_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_PORTA_MODE), PORTA_MODES),
                2, 7, 1, 1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("Portamento Time", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PATCH_PORTA_TIME
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_PORTA_TIME)), 1, 2, 1, 1,
                0);
        addWidget(
                patchPanel,
                new ComboBoxWidget("Hold Mode", patch, new ParamModel(patch,
                        base + D50Constants.PATCH_HOLD_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_HOLD_MODE), PORTA_MODES),
                2, 8, 1, 1, 0);
        addWidget(
                patchPanel,
                new ScrollBarWidget("<html>Upper Tone<br>Key Shift</html>",
                        patch, 0, 48, -24, new ParamModel(patch, base
                                + D50Constants.PATCH_UTONE_KEYSHIFT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PATCH_UTONE_KEYSHIFT)), 0, 5, 1,
                1, 0);
    }

    public String bytesToHex(byte[] bytes) {
        final char[] hexArray =
                {
                        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                        'B', 'C', 'D', 'E', 'F' };
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    final void fillCommonPanel(JPanel commonPanel, int index) {
        JPanel column1 = new JPanel(new GridBagLayout());
        JPanel column2 = new JPanel(new GridBagLayout());
        JPanel column3 = new JPanel(new GridBagLayout());
        commonPanel.add(column1);
        commonPanel.add(column2);
        commonPanel.add(column3);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;

        final int base = getCommonBase(index);

        D50PatchStringSender focusListener = new D50PatchStringSender(patch, base);
        addWidget(column1, new PatchNameWidget("Name", patch, focusListener), 0, 0, 1, 1, 0);

        final JLabel structureLabel =
                new JLabel(
                        structureIcons[patch.sysex[D50Constants.SYSEX_HEADER_SIZE
                                + base + D50Constants.COMMON_STRUCTURE]]);
        column1.add(structureLabel, constraints);
        final ScrollBarWidget structWidget =
                new ScrollBarWidget("Structure", patch, 0, 6, 1,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_STRUCTURE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_STRUCTURE));
        addWidget(column1, structWidget, 0, GridBagConstraints.RELATIVE, 1, 1,
                0);
        structWidget.addEventListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                structureLabel.setIcon(structureIcons[structWidget.getValue()]);
            }
        });
        D50PartialMuteParamModel partial1MuteModel =
                new D50PartialMuteParamModel();
        partial1MuteModel.setFirst(true);
        partial1MuteModel.setPatch(patch);
        partial1MuteModel.setOffset(base + D50Constants.COMMON_PART_MUTE);
        addWidget(
                column1,
                new CheckBoxWidget("Partial 1", patch, partial1MuteModel,
                        senderFactory.newPartialMuteSender(base
                                + D50Constants.COMMON_PART_MUTE,
                                partial1MuteModel)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        D50PartialMuteParamModel partial2MuteModel =
                new D50PartialMuteParamModel();
        partial2MuteModel.setFirst(false);
        partial2MuteModel.setPatch(patch);
        partial2MuteModel.setOffset(base + D50Constants.COMMON_PART_MUTE);
        addWidget(
                column1,
                new CheckBoxWidget("Partial 2", patch, partial2MuteModel,
                        senderFactory.newPartialMuteSender(base
                                + D50Constants.COMMON_PART_MUTE,
                                partial2MuteModel)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                column1,
                new ScrollBarWidget("Partial Balance", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_PART_BALANCE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_PART_BALANCE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        JPanel pitchEnvPanel = createCommonPitchEnv(index);
        column2.add(pitchEnvPanel, constraints);

        JPanel pitchModPanel = createCommonPitchMod(index);
        column2.add(pitchModPanel, constraints);

        JPanel[] commonLfoPanels = createCommonLfo(index);
        for (JPanel jPanel : commonLfoPanels) {
            column3.add(jPanel, constraints);
        }

        JPanel lowEqPanel = new JPanel(new GridBagLayout());
        lowEqPanel
                .setBorder(BorderFactory.createTitledBorder(BORDER, "Low EQ"));

        addWidget(
                lowEqPanel,
                new ScrollBarWidget("Frequency", patch, 0, 15, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_LOW_EQ_FREQ
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_LOW_EQ_FREQ)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                lowEqPanel,
                new ScrollBarWidget("Gain", patch, 0, 24, -12, new ParamModel(
                        patch, base + D50Constants.COMMON_LOW_EQ_GAIN
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_LOW_EQ_GAIN)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        column1.add(lowEqPanel, constraints);

        JPanel highEqPanel = new JPanel(new GridBagLayout());
        highEqPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "High EQ"));

        addWidget(
                highEqPanel,
                new ScrollBarWidget("Frequency", patch, 0, 21, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_HIGH_EQ_FREQ
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_HIGH_EQ_FREQ)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                highEqPanel,
                new ScrollBarWidget("Q", patch, 0, 8, 0, new ParamModel(patch,
                        base + D50Constants.COMMON_HIGH_EQ_Q
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_HIGH_EQ_Q)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                highEqPanel,
                new ScrollBarWidget("Gain", patch, 0, 24, -12, new ParamModel(
                        patch, base + D50Constants.COMMON_HIGH_EQ_GAIN
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_HIGH_EQ_GAIN)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        column1.add(highEqPanel, constraints);

        JPanel chorusPanel = new JPanel(new GridBagLayout());
        chorusPanel.setBorder(BorderFactory
                .createTitledBorder(BORDER, "Chorus"));

        addWidget(
                chorusPanel,
                new ComboBoxWidget("Type", patch, new ParamModel(patch, base
                        + D50Constants.COMMON_CHORUS_TYPE
                        + D50Constants.SYSEX_HEADER_SIZE), senderFactory
                        .newSender(base + D50Constants.COMMON_CHORUS_TYPE),
                        CHORUS_TYPES), 0, GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                chorusPanel,
                new ScrollBarWidget("Rate", patch, 0, 100, 0, new ParamModel(
                        patch, base + D50Constants.COMMON_CHORUS_RATE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_CHORUS_RATE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                chorusPanel,
                new ScrollBarWidget("Depth", patch, 0, 100, 0, new ParamModel(
                        patch, base + D50Constants.COMMON_CHORUS_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_CHORUS_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                chorusPanel,
                new ScrollBarWidget("Balance", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_CHORUS_BALANCE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_CHORUS_BALANCE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        column1.add(chorusPanel, constraints);
    }

    final JPanel createCommonPitchEnv(int index) {
        JPanel pitchEnvPanel = new JPanel(new GridBagLayout());
        pitchEnvPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "PITCH ENV"));

        int base = getCommonBase(index);

        addWidget(
                pitchEnvPanel,
                new ScrollBarWidget("Velocity Range", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_ENV_VELO_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_ENV_VELO_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                pitchEnvPanel,
                new ScrollBarWidget("Time Keyfollow", patch, 0, 4, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_ENV_TIME_KEYF
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_ENV_TIME_KEYF)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        Node[] nodes =
                new Node[] {
                        new Node(0, 0, null, 0, 100, new ParamModel(
                                patch, base + D50Constants.COMMON_ENV_LEVEL_0
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, null, senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_LEVEL_0),
                                null, "Level 0"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.COMMON_ENV_TIME_1
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.COMMON_ENV_LEVEL_1
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_TIME_1),
                                senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_LEVEL_1),
                                "Time 1", "Level 1"),

                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.COMMON_ENV_TIME_2
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.COMMON_ENV_LEVEL_2
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_TIME_2),
                                senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_LEVEL_2),
                                "Time 2", "Level 2"),

                        new Node(
                                0,
                                100,
                                new ParamModel(patch, base
                                        + D50Constants.COMMON_ENV_TIME_3
                                        + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                100,
                                new ParamModel(patch, base
                                        + D50Constants.COMMON_ENV_SUSTAIN_LEVEL
                                        + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                false,
                                senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_TIME_3),
                                senderFactory
                                        .newSender(base
                                                + D50Constants.COMMON_ENV_SUSTAIN_LEVEL),
                                "Time 3", "Sustain Level"),

                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.COMMON_ENV_TIME_4
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.COMMON_ENV_END_LEVEL
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_TIME_4),
                                senderFactory.newSender(base
                                        + D50Constants.COMMON_ENV_END_LEVEL),
                                "Time 5", "End Level") };

        addWidget(pitchEnvPanel, new EnvelopeWidget(null, patch, nodes), 0,
                GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0);

        return pitchEnvPanel;
    }

    final JPanel createCommonPitchMod(int index) {
        int base = getCommonBase(index);
        JPanel pitchModPanel = new JPanel(new GridBagLayout());
        pitchModPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "PITCH MOD"));

        addWidget(
                pitchModPanel,
                new ScrollBarWidget("Lever", patch, 0, 5, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_P_MOD_LEVER
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_P_MOD_LEVER)),
                GridBagConstraints.RELATIVE, 0, 1, 1, 0);
        addWidget(
                pitchModPanel,
                new ScrollBarWidget("LFO Depth", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_LFO_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_LFO_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                pitchModPanel,
                new ScrollBarWidget("AfterTouch", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.COMMON_AT_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.COMMON_AT_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        return pitchModPanel;
    }

    final JPanel[] createCommonLfo(int index) {
        int base = getCommonBase(index);
        JPanel[] lfos = new JPanel[3];
        // LFO
        for (int i = 0; i < 3; i++) {
            lfos[i] = new JPanel(new GridBagLayout());
            lfos[i].setBorder(BorderFactory.createTitledBorder(BORDER, "LFO"
                    + (i + 1)));
            int lfoOffest = i * 4;

            addWidget(
                    lfos[i],
                    new ComboBoxWidget("Waveform", patch, new ParamModel(patch,
                            base + D50Constants.COMMON_LFO_WAVEFORM + lfoOffest
                                    + D50Constants.SYSEX_HEADER_SIZE),
                            senderFactory.newSender(base
                                    + D50Constants.COMMON_LFO_WAVEFORM
                                    + lfoOffest), LFO_WAVEFORMS), 0,
                    GridBagConstraints.RELATIVE, 1, 1, 0);
            addWidget(
                    lfos[i],
                    new ScrollBarWidget("Rate", patch, 0, 100, 0,
                            new ParamModel(patch, base
                                    + D50Constants.COMMON_LFO_RATE + lfoOffest
                                    + D50Constants.SYSEX_HEADER_SIZE),
                            senderFactory.newSender(base
                                    + D50Constants.COMMON_LFO_RATE + lfoOffest)),
                    0, GridBagConstraints.RELATIVE, 1, 1, 0);
            addWidget(
                    lfos[i],
                    new ScrollBarWidget("Delay Time", patch, 0, 100, 0,
                            new ParamModel(patch, base
                                    + D50Constants.COMMON_LFO_DELAY_TIME
                                    + lfoOffest
                                    + D50Constants.SYSEX_HEADER_SIZE),
                            senderFactory.newSender(base
                                    + D50Constants.COMMON_LFO_DELAY_TIME
                                    + lfoOffest)), 0,
                    GridBagConstraints.RELATIVE, 1, 1, 0);
            addWidget(
                    lfos[i],
                    new CheckBoxWidget("Sync", patch, new ParamModel(patch,
                            base + D50Constants.COMMON_LFO_SYNC + lfoOffest
                                    + D50Constants.SYSEX_HEADER_SIZE),
                            senderFactory.newSender(base
                                    + D50Constants.COMMON_LFO_SYNC + lfoOffest)),
                    0, GridBagConstraints.RELATIVE, 1, 1, 0);
        }
        return lfos;
    }

    final void fillPartialPanel(JPanel partialPanel, int partial) {
        JPanel column1 = new JPanel(new GridBagLayout());
        JPanel column2 = new JPanel(new GridBagLayout());
        JPanel column3 = new JPanel(new GridBagLayout());
        partialPanel.add(column1);
        partialPanel.add(column2);
        partialPanel.add(column3);

        JComponent wgPanel = createWGPanel(partial);
        column1.add(wgPanel);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;

        JPanel tvaPanel = createTVAPanel(partial);
        column2.add(tvaPanel, constraints);

        JPanel tvfPanel = createTVFPanel(partial);
        column3.add(tvfPanel);
    }

    final JComponent createWGPanel(int part) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        JPanel container = new JPanel(new GridBagLayout());
        JPanel wgPitchPanel = new JPanel(new GridBagLayout());
        wgPitchPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "WG PITCH"));

        int base = getPartialBase(part);

        addWidget(
                wgPitchPanel,
                new ScrollBarWidget("Pitch Coarse", patch, 0, 72, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PITCH_COARSE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PITCH_COARSE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgPitchPanel,
                new ScrollBarWidget("Pitch Fine", patch, 0, 100, -50,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PITCH_FINE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PITCH_FINE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgPitchPanel,
                new ComboBoxWidget("Pitch Keyfollow", patch, new ParamModel(
                        patch, base + D50Constants.PART_WG_PITCH_KEYFOLLOW
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PITCH_KEYFOLLOW),
                        KEY_FOLLOW), 0, GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                wgPitchPanel,
                new ScrollBarWidget("LFO Mode", patch, 0, 3, 0, new ParamModel(
                        patch, base + D50Constants.PART_WG_LFO_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_LFO_MODE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgPitchPanel,
                new ScrollBarWidget("P-Env Mode", patch, 0, 2, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_P_ENV_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_P_ENV_MODE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgPitchPanel,
                new ScrollBarWidget("Bender Mode", patch, 0, 2, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_BENDER_MODE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_BENDER_MODE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        JPanel wgWaveformPanel = new JPanel(new GridBagLayout());
        wgWaveformPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "WG WAVEFORM"));

        addWidget(
                wgWaveformPanel,
                new ScrollBarWidget("Waveform / PCM Bank", patch, 0, 1, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_WAVEFORM
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_WAVEFORM)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ComboBoxWidget("PCM Wave Number", patch, new ParamModel(
                        patch, base + D50Constants.PART_WG_PCM_WAVE_NO
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PCM_WAVE_NO),
                        PCM_SAMPLES), 0, GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ScrollBarWidget("Pulse Width", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PULSE_WIDTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PULSE_WIDTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ScrollBarWidget("PW Velocity Range", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PW_VELO_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PW_VELO_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ComboBoxWidget("PW LFO Select", patch, new ParamModel(
                        patch, base + D50Constants.PART_WG_PW_LFO_SELECT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PW_LFO_SELECT),
                        LFO_SELECT), 0, GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ScrollBarWidget("PW LFO Depth", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PW_LFO_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PW_LFO_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                wgWaveformPanel,
                new ScrollBarWidget("PW After Touch Range", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.PART_WG_PW_AT_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_WG_PW_AT_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        container.add(wgPitchPanel, constraints);
        container.add(wgWaveformPanel, constraints);
        return container;
    }

    final JPanel createTVFPanel(int part) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        JPanel container = new JPanel(new GridBagLayout());
        JPanel tvfPanel = new JPanel(new GridBagLayout());
        tvfPanel.setBorder(BorderFactory.createTitledBorder(BORDER, "TVF"));
        int base = getPartialBase(part);

        addWidget(
                tvfPanel,
                new ScrollBarWidget("Cutoff Frequency", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_CUTOFF_FREQ
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_CUTOFF_FREQ)), 0,
                GridBagConstraints.RELATIVE, 2, 1, 0);
        addWidget(
                tvfPanel,
                new ScrollBarWidget("Resonance", patch, 0, 30, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_RESONANCE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_RESONANCE)), 0,
                GridBagConstraints.RELATIVE, 2, 1, 0);
        addWidget(
                tvfPanel,
                new ComboBoxWidget("Keyfollow", patch, new ParamModel(patch,
                        base + D50Constants.PART_TVF_KEYFOLLOW
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_KEYFOLLOW), KEY_FOLLOW),
                0, GridBagConstraints.RELATIVE, 1, 2, 0);
        addWidget(
                tvfPanel,
                new ScrollBarWidget("Bias Point", patch, 0, 127, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_BIAS_POINT_DIR
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_BIAS_POINT_DIR)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvfPanel,
                new ScrollBarWidget("Bias Level", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_BIAS_LEVEL
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_BIAS_LEVEL)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                tvfPanel,
                new ComboBoxWidget("LFO Select", patch, new ParamModel(patch,
                        base + D50Constants.PART_TVF_LFO_SELECT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_LFO_SELECT), LFO_SELECT),
                0, GridBagConstraints.RELATIVE, 1, 2, 0);
        addWidget(
                tvfPanel,
                new ScrollBarWidget("LFO Depth", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_LFO_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_LFO_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvfPanel,
                new ScrollBarWidget("AfterTouch Range", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_AT_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_AT_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        JPanel tvfEnvPanel = new JPanel(new GridBagLayout());
        tvfEnvPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "TVF ENV"));

        addWidget(
                tvfEnvPanel,
                new ScrollBarWidget("Depth", patch, 0, 100, 0, new ParamModel(
                        patch, base + D50Constants.PART_TVF_ENV_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_ENV_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvfEnvPanel,
                new ScrollBarWidget("Velocity Range", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_VELO_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_ENV_VELO_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvfEnvPanel,
                new ScrollBarWidget("Depth Keyfollow", patch, 0, 4, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_DEPTH_KEYF
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_ENV_DEPTH_KEYF)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvfEnvPanel,
                new ScrollBarWidget("Time Keyfollow", patch, 0, 4, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_TIME_KEYF
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVF_ENV_TIME_KEYF)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        Node[] nodes =
                new Node[] {
                        new Node(0, 0, null, 0, 0, null, 0, false,
                                null, null, null, null),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_TIME_1
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVF_ENV_LEVEL_1
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_TIME_1),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_LEVEL_1),
                                "Time 1", "Level 1"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_TIME_2
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVF_ENV_LEVEL_2
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_TIME_2),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_LEVEL_2),
                                "Time 2", "Level 2"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_TIME_3
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVF_ENV_LEVEL_3
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_TIME_3),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_LEVEL_3),
                                "Time 3", "Level 3"),
                        new Node(
                                0,
                                100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVF_ENV_TIME_4
                                        + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                100,
                                new ParamModel(
                                        patch,
                                        base
                                                + D50Constants.PART_TVF_ENV_SUSTAIN_LEVEL
                                                + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                false,
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_TIME_4),
                                senderFactory
                                        .newSender(base
                                                + D50Constants.PART_TVF_ENV_SUSTAIN_LEVEL),
                                "Time 4", "Sustain level"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVF_ENV_TIME_5
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 1,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVF_ENV_END_LEVEL
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_TIME_5),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVF_ENV_END_LEVEL),
                                "Time 5", "End Level") };

        addWidget(tvfEnvPanel, new EnvelopeWidget(null, patch, nodes), 0,
                GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0);

        container.add(tvfPanel, constraints);
        container.add(tvfEnvPanel, constraints);
        return container;
    }

    final JPanel createTVAPanel(int part) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = GridBagConstraints.RELATIVE;
        JPanel container = new JPanel(new GridBagLayout());
        JPanel tvaPanel = new JPanel(new GridBagLayout());
        tvaPanel.setBorder(BorderFactory.createTitledBorder(BORDER, "TVA"));
        int base = getPartialBase(part);

        addWidget(
                tvaPanel,
                new ScrollBarWidget("Level", patch, 0, 100, 0, new ParamModel(
                        patch, base + D50Constants.PART_TVA_LEVEL
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_LEVEL)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvaPanel,
                new ScrollBarWidget("Velocity Range", patch, 0, 100, -50,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_VELO_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_VELO_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvaPanel,
                new ScrollBarWidget("Bias point", patch, 0, 127, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_BIAS_POINT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_BIAS_POINT)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvaPanel,
                new ScrollBarWidget("Bias level", patch, 0, 12, -12,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_BIAS_LEVEL
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_BIAS_LEVEL)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(
                tvaPanel,
                new ComboBoxWidget("LFO Select", patch, new ParamModel(patch,
                        base + D50Constants.PART_TVA_LFO_SELECT
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_LFO_SELECT), LFO_SELECT),
                0, GridBagConstraints.RELATIVE, 1, 2, 0);
        addWidget(
                tvaPanel,
                new ScrollBarWidget("LFO Depth", patch, 0, 100, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_LFO_DEPTH
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_LFO_DEPTH)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvaPanel,
                new ScrollBarWidget("AfterTouch Range", patch, 0, 14, -7,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_AT_RANGE
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_AT_RANGE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        JPanel tvaEnvPanel = new JPanel(new GridBagLayout());
        tvaEnvPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "TVA ENV"));

        Node[] nodes =
                new Node[] {
                        new Node(0, 0, null, 0, 0, null, 0, false,
                                null, null, null, null),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_TIME_1
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVA_ENV_LEVEL_1
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_TIME_1),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_LEVEL_1),
                                "Time 1", "Level 1"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_TIME_2
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVA_ENV_LEVEL_2
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_TIME_2),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_LEVEL_2),
                                "Time 2", "Level 2"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_TIME_3
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVA_ENV_LEVEL_3
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_TIME_3),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_LEVEL_3),
                                "Time 3", "Level 3"),
                        new Node(
                                0,
                                100,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVA_ENV_TIME_4
                                        + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                100,
                                new ParamModel(
                                        patch,
                                        base
                                                + D50Constants.PART_TVA_ENV_SUSTAIN_LEVEL
                                                + D50Constants.SYSEX_HEADER_SIZE),
                                0,
                                false,
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_TIME_4),
                                senderFactory
                                        .newSender(base
                                                + D50Constants.PART_TVA_ENV_SUSTAIN_LEVEL),
                                "Time 4", "Sustain level"),
                        new Node(0, 100, new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_TIME_5
                                + D50Constants.SYSEX_HEADER_SIZE), 0, 1,
                                new ParamModel(patch, base
                                        + D50Constants.PART_TVA_ENV_END_LEVEL
                                        + D50Constants.SYSEX_HEADER_SIZE), 0,
                                false, senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_TIME_5),
                                senderFactory.newSender(base
                                        + D50Constants.PART_TVA_ENV_END_LEVEL),
                                "Time 5", "End Level") };

        addWidget(
                tvaEnvPanel,
                new ScrollBarWidget("Velocity Keyfollow", patch, 0, 4, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_VELOCITY_KEYF
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_ENV_VELOCITY_KEYF)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                tvaEnvPanel,
                new ScrollBarWidget("Time Keyfollow", patch, 0, 4, 0,
                        new ParamModel(patch, base
                                + D50Constants.PART_TVA_ENV_TIME_KEYF
                                + D50Constants.SYSEX_HEADER_SIZE),
                        senderFactory.newSender(base
                                + D50Constants.PART_TVA_ENV_TIME_KEYF)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(tvaEnvPanel, new EnvelopeWidget(null, patch, nodes), 0,
                GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0);

        container.add(tvaPanel, constraints);
        container.add(tvaEnvPanel, constraints);
        return container;
    }

}
