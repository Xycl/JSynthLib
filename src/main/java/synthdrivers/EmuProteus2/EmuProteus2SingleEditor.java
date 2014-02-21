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
package synthdrivers.EmuProteus2;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.AbstractTableModel;

import org.jsynthlib.view.PatchEditorFrame;
import org.jsynthlib.view.widgets.CheckBoxWidget;
import org.jsynthlib.view.widgets.ComboBoxWidget;
import org.jsynthlib.view.widgets.EnvelopeWidget;
import org.jsynthlib.view.widgets.PatchNameWidget;
import org.jsynthlib.view.widgets.ScrollBarWidget;
import org.jsynthlib.view.widgets.SysexWidget;

import synthdrivers.EmuProteus2.EmuProteus2Sender.Factory;
import core.ParamModel;
import core.Patch;

public class EmuProteus2SingleEditor extends PatchEditorFrame {

    private static final Border BORDER = BorderFactory.createEtchedBorder();
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(
            0, 3, 3, 3);

    private static final String[] LFO_SHAPES = {
            "Random", "Triangle", "Sine", "Sawtooth", "Square", };

    private static final String[] CF_MODES = {
            "Off", "Xfade", "Xswitch" };

    private static final String[] CF_DIRS = {
            "Pri->Sec", "Sec->Pri" };

    private static final String[] KEY_VEL_SOURCES = {
            "Number", "Velocity" };

    private static final String[] KEY_VEL_DESTINATIONS = {
            "Off", "Pitch", "Primary Pitch", "Secondary Pitch", "Volume",
            "Primary Volume", "Secondary Volume", "Attack", "Primary Attack",
            "Secondary Attack", "Decay", "Primary Decay", "Secondary Decay",
            "Release", "Primary Release", "Secondary Release", "Crossfade",
            "LFO 1 Amount", "LFO 1 Rate", "LFO 2 Amount", "LFO 2 Rate",
            "Auxiliary Envelope Amount", "Auxiliary Envelope Attack",
            "Auxiliary Envelope Decay", "Auxiliary Envelope Release",
            "Sample Start", "Primary Sample Start", "Secondary Sample Start",
            "Pan", "Primary Pan", "Secondary Pan", "Tone", "Primary Tone",
            "Secondary Tone" };

    private static final String[] REALTIME_SOURCES = {
            "Pitch Wheel", "MIDI Control A", "MIDI Control B",
            "MIDI Control C", "MIDI Control D", "Mono Pressure",
            "Polyphonic Pressure", "LFO 1", "LFO 2", "Auxiliary Envelope" };

    private static final String[] REALTIME_DESTINATIONS = {
            "Off", "Pitch", "Primary Pitch", "Secondary Pitch", "Volume",
            "Primary Volume", "Secondary Volume", "Attack", "Primary Attack",
            "Secondary Attack", "Decay", "Primary Decay", "Secondary Decay",
            "Release", "Primary Release", "Secondary Release", "Crossfade",
            "LFO 1 Amount", "LFO 1 Rate", "LFO 2 Amount", "LFO 2 Rate",
            "Auxiliary Envelope Amount", "Auxiliary Envelope Attack",
            "Auxiliary Envelope Decay", "Auxiliary Envelope Release" };

    private static final String[] VELOCITY_CURVES = {
            "OFF", "Curve 1", "Curve 2", "Curve 3", "Curve 4", "Global" };

    private static final String[] SUBMIX = {
            "Main", "Sub 1", "Sub 2" };

    private static final int INSTRUMENT_OFFSET = 18;
    private static final int LFO_OFFSET = 5;

    private static final String[] KEYS = {
            "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    private static final String[] OCTAVES = {
            "-2", "-1", " 0", " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8" };

    private static String[] generateKeysArray() {
        String[] result = new String[128];
        int i = 0;
        for (String octave : OCTAVES) {
            for (String key : KEYS) {
                result[i] = key + octave;
                i++;
                if (i == result.length) {
                    return result;
                }
            }
        }
        throw new IllegalStateException("Bad execution");
    }

    private static final String[] OCTAVE_KEYS = generateKeysArray();

    private Patch patch;
    private Factory senderFactory;

    protected EmuProteus2SingleEditor(Patch patch) {
        super("Emu Proteus 2 Single Patch Editor", patch);
        this.patch = patch;
        this.senderFactory = EmuProteus2Sender.FACTORY;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;

        JPanel leftPanel = new JPanel(new GridBagLayout());

        leftPanel.add(createGlobalPanel(), gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        leftPanel.add(createInstrumentPanel(1), gbc);
        leftPanel.add(createInstrumentPanel(2), gbc);

        JPanel rightPanel = new JPanel(new GridBagLayout());

        gbc.gridx = 1;
        gbc.gridy = GridBagConstraints.RELATIVE;
        rightPanel.add(createLfoPanel(1), gbc);
        rightPanel.add(createLfoPanel(2), gbc);
        rightPanel.add(createCrossfaderPanel(), gbc);
        rightPanel.add(createAuxEnvPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        leftPanel.add(createLinksPanel(), gbc);

        gbc.gridx = GridBagConstraints.RELATIVE;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        scrollPane.add(leftPanel, gbc);
        scrollPane.add(rightPanel, gbc);
    }

    final JPanel createGlobalPanel() {
        JPanel global = new JPanel(new GridBagLayout());

        addWidget(global, new PatchNameWidget("Name", patch), 0, 0, 1, 1, 0);

        addWidget(global, new ScrollBarWidget("Pressure", patch, -128, 127, 0,
                new EmuParamModel(patch, EmuProteus2Constants.PRESSURE),
                senderFactory.newSender(EmuProteus2Constants.PRESSURE)),
                GridBagConstraints.RELATIVE, 0, 1, 1, 0);

        addWidget(
                global,
                new ComboBoxWidget("Curve", patch, new EmuParamModel(patch,
                        EmuProteus2Constants.CURVE), senderFactory
                        .newSender(EmuProteus2Constants.CURVE), VELOCITY_CURVES),
                GridBagConstraints.RELATIVE, 1, 1, 1, 0);

        addWidget(global, new ComboBoxWidget("Submix", patch,
                new EmuParamModel(patch, EmuProteus2Constants.SUBMIX),
                senderFactory.newSender(EmuProteus2Constants.SUBMIX), SUBMIX),
                GridBagConstraints.RELATIVE, 1, 1, 1, 0);
        return global;
    }

    final JPanel createInstrumentPanel(int instrIndex) {
        JPanel instr = new JPanel(new GridBagLayout());
        instr.setBorder(BorderFactory.createTitledBorder(BORDER, "Instrument "
                + instrIndex));

        int base = (instrIndex - 1) * INSTRUMENT_OFFSET;
        addWidget(
                instr,
                new ComboBoxWidget("Instrument", patch,
                        new EmuInstrumentParamModel(patch, base
                                + EmuProteus2Constants.INSTRUMENT),
                        senderFactory.newInstrumentSender(base
                                + EmuProteus2Constants.INSTRUMENT),
                        EmuInstrumentParamModel.getInstruments()), 0, 1, 3, 1,
                0);
        addWidget(
                instr,
                new ScrollBarWidget("Sample Start Offset", patch, 0, 127, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.SAMPLE_START_OFFSET),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.SAMPLE_START_OFFSET)),
                0, GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(
                instr,
                new ScrollBarWidget("Tuning Coarse", patch, -36, 36, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.TUNING_COARSE),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.TUNING_COARSE)), 0,
                GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(
                instr,
                new ScrollBarWidget("Tuning Fine", patch, -64, 64, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.TUNING_FINE),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.TUNING_FINE)), 0,
                GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(instr, new ScrollBarWidget("Volume", patch, 0, 127, 0,
                new EmuParamModel(patch, base + EmuProteus2Constants.VOLUME),
                senderFactory.newSender(base + EmuProteus2Constants.VOLUME)),
                0, GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(instr, new ScrollBarWidget("Pan", patch, -7, 7, 0,
                new EmuParamModel(patch, base + EmuProteus2Constants.PAN),
                senderFactory.newSender(base + EmuProteus2Constants.PAN)), 0,
                GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(instr, new ComboBoxWidget("Low Key", patch,
                new EmuParamModel(patch, base + EmuProteus2Constants.LOW_KEY),
                senderFactory.newSender(base + EmuProteus2Constants.LOW_KEY),
                OCTAVE_KEYS), 0, GridBagConstraints.RELATIVE, 3, 1, 0);
        addWidget(instr, new ComboBoxWidget("High Key", patch,
                new EmuParamModel(patch, base + EmuProteus2Constants.HIGH_KEY),
                senderFactory.newSender(base + EmuProteus2Constants.HIGH_KEY),
                OCTAVE_KEYS), 0, GridBagConstraints.RELATIVE, 3, 1, 0);

        EnvelopeWidget.Node[] nodes =
                new EnvelopeWidget.Node[] {
                        new EnvelopeWidget.Node(0, 99, new EmuParamModel(patch,
                                base + EmuProteus2Constants.ENV_DELAY), 0, 0,
                                null, 0, false, senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_DELAY),
                                null, "Delay", null),
                        new EnvelopeWidget.Node(0, 99, new EmuParamModel(patch,
                                base + EmuProteus2Constants.ENV_ATTACK), 100,
                                100, null, 0, false,
                                senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_ATTACK),
                                null, "Attack", null),
                        new EnvelopeWidget.Node(0, 99, new EmuParamModel(patch,
                                base + EmuProteus2Constants.ENV_HOLD), 100,
                                100, null, 0, false,
                                senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_HOLD), null,
                                "Hold", null),
                        new EnvelopeWidget.Node(0, 99, new EmuParamModel(patch,
                                base + EmuProteus2Constants.ENV_DECAY), 100,
                                100, null, 0, false,
                                senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_DECAY),
                                null, "Decay", null),
                        new EnvelopeWidget.Node(0, 0, null, 0, 99,
                                new EmuParamModel(patch, base
                                        + EmuProteus2Constants.ENV_SUSTAIN), 0,
                                false, null, senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_SUSTAIN),
                                null, "Sustain"),
                        new EnvelopeWidget.Node(0, 99, new EmuParamModel(patch,
                                base + EmuProteus2Constants.ENV_RELEASE), 0, 0,
                                null, 0, false, senderFactory.newSender(base
                                        + EmuProteus2Constants.ENV_RELEASE),
                                null, "Release", null) };

        addWidget(instr, new CheckBoxWidget("Envelope", patch,
                new EmuParamModel(patch, base + EmuProteus2Constants.ENV_ON),
                senderFactory.newSender(base + EmuProteus2Constants.ENV_ON)),
                0, GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                instr,
                new CheckBoxWidget("Solo", patch, new EmuParamModel(patch, base
                        + EmuProteus2Constants.SOLO), senderFactory
                        .newSender(base + EmuProteus2Constants.SOLO)), 1,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(instr, new CheckBoxWidget("Reverse", patch,
                new EmuParamModel(patch, base + EmuProteus2Constants.REVERSE),
                senderFactory.newSender(base + EmuProteus2Constants.REVERSE)),
                2, GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(instr, new ScrollBarWidget("Chorus", patch, 0, 15, 0,
                new EmuParamModel(patch, base + EmuProteus2Constants.CHORUS),
                senderFactory.newSender(base + EmuProteus2Constants.CHORUS)),
                0, GridBagConstraints.RELATIVE, 3, 1, 0);

        addWidget(instr, new EnvelopeWidget(null, patch, nodes), 0,
                GridBagConstraints.RELATIVE, 3, 1, 0);
        return instr;
    }

    final JPanel createLfoPanel(int lfoIndex) {
        JPanel lfoPanel = new JPanel(new GridBagLayout());
        lfoPanel.setBorder(BorderFactory.createTitledBorder(BORDER, "LFO "
                + lfoIndex));
        int base = (lfoIndex - 1) * LFO_OFFSET;
        addWidget(
                lfoPanel,
                new ComboBoxWidget("Shape", patch, new EmuParamModel(patch,
                        base + EmuProteus2Constants.LFO_SHAPE), senderFactory
                        .newSender(base + EmuProteus2Constants.LFO_SHAPE),
                        LFO_SHAPES), 0, 1, 1, 1, 0);
        addWidget(
                lfoPanel,
                new ScrollBarWidget("Frequency", patch, 0, 127, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.LFO_FREQUENCY),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.LFO_FREQUENCY)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                lfoPanel,
                new ScrollBarWidget("Delay", patch, 0, 127, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.LFO_DELAY),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.LFO_DELAY)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                lfoPanel,
                new ScrollBarWidget("Variation", patch, 0, 127, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.LFO_VARIATION),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.LFO_VARIATION)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(
                lfoPanel,
                new ScrollBarWidget("Amount", patch, -128, 127, 0,
                        new EmuParamModel(patch, base
                                + EmuProteus2Constants.LFO_AMOUNT),
                        senderFactory.newSender(base
                                + EmuProteus2Constants.LFO_AMOUNT)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        return lfoPanel;
    }

    final JPanel createCrossfaderPanel() {
        JPanel cfPanel = new JPanel(new GridBagLayout());
        cfPanel.setBorder(BorderFactory.createTitledBorder(BORDER, "Crossfade"));

        addWidget(
                cfPanel,
                new ComboBoxWidget("Mode", patch, new EmuParamModel(patch,
                        EmuProteus2Constants.CF_MODE), senderFactory
                        .newSender(EmuProteus2Constants.CF_MODE), CF_MODES), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(cfPanel, new ComboBoxWidget("Direction", patch,
                new EmuParamModel(patch, EmuProteus2Constants.CF_DIRECTIONS),
                senderFactory.newSender(EmuProteus2Constants.CF_DIRECTIONS),
                CF_DIRS), 0, GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(cfPanel, new ScrollBarWidget("Balance", patch, 0, 127, 0,
                new EmuParamModel(patch, EmuProteus2Constants.CF_BALANCE),
                senderFactory.newSender(EmuProteus2Constants.CF_BALANCE)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(cfPanel, new ScrollBarWidget("Amount", patch, 0, 255, 0,
                new EmuParamModel(patch, EmuProteus2Constants.CF_AMOUNT),
                senderFactory.newSender(EmuProteus2Constants.CF_AMOUNT)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);
        addWidget(cfPanel, new ComboBoxWidget("Switch Point", patch,
                new EmuParamModel(patch, EmuProteus2Constants.SWITCH_POINT),
                senderFactory.newSender(EmuProteus2Constants.SWITCH_POINT),
                OCTAVE_KEYS), 0, GridBagConstraints.RELATIVE, 1, 1, 0);

        return cfPanel;
    }

    final JPanel createLinksPanel() {
        JPanel linksPanel = new JPanel(new GridBagLayout());
        linksPanel.setBorder(BorderFactory.createTitledBorder(BORDER, "Links"));

        // Used only to be able to call addWidget. The widgets are instead
        // displayed in the table.
        JPanel temp = new JPanel();

        final int rows = 5;
        final int cols = 4;
        final SysexWidget[][] widgets = new SysexWidget[rows - 1][cols - 1];
        String[] columnNames = {
                "", "Preset", "Low Key", "High Key" };
        String[] rowNames = {
                "Current", "Link 1", "Link 2", "Link 3" };
        for (int i = 0; i < rows; i++) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = i;
            gbc.gridheight = 1;
            gbc.gridwidth = 1;
            if (i == 0) {
                for (int j = 0; j < cols; j++) {
                    gbc.gridx = j;
                    JLabel label = new JLabel(columnNames[j]);
                    label.setBorder(EMPTY_BORDER);
                    linksPanel.add(label, gbc);
                }
            } else {
                int base = (i - 1);

                gbc.gridx = 0;
                JLabel label = new JLabel(rowNames[i - 1]);
                label.setBorder(EMPTY_BORDER);
                linksPanel.add(label, gbc);

                if (i > 1) {
                    ScrollBarWidget link =
                            new ScrollBarWidget(null, patch, 0, 383, 0,
                                    new EmuParamModel(patch, base
                                            + EmuProteus2Constants.LINK),
                                    senderFactory.newSender(base
                                            + EmuProteus2Constants.LINK));
                    link.setBorder(EMPTY_BORDER);
                    addWidget(temp, link, 0, 0, 0, 0, 0);
                    gbc.gridx = 1;
                    linksPanel.add(link, gbc);
                }

                ComboBoxWidget low =
                        new ComboBoxWidget(null, patch,
                                new EmuParamModel(patch, base
                                        + EmuProteus2Constants.LINK_LOW_KEY),
                                senderFactory.newSender(base
                                        + EmuProteus2Constants.LINK_LOW_KEY),
                                OCTAVE_KEYS);
                low.setBorder(EMPTY_BORDER);
                addWidget(temp, low, 0, 0, 0, 0, 0);
                gbc.gridx = 2;
                linksPanel.add(low, gbc);

                ComboBoxWidget high =
                        new ComboBoxWidget(null, patch, new EmuParamModel(
                                patch, base
                                        + EmuProteus2Constants.LINK_HIGH_KEY),
                                senderFactory.newSender(base
                                        + EmuProteus2Constants.LINK_HIGH_KEY),
                                OCTAVE_KEYS);
                high.setBorder(EMPTY_BORDER);
                addWidget(temp, high, 0, 0, 0, 0, 0);
                gbc.gridx = 3;
                linksPanel.add(high, gbc);
            }
        }

        return linksPanel;
    }

    final JPanel createAuxEnvPanel() {
        JPanel envPanel = new JPanel(new GridBagLayout());
        envPanel.setBorder(BorderFactory.createTitledBorder(BORDER,
                "Aux Envelope"));

        EnvelopeWidget.Node[] nodes =
                new EnvelopeWidget.Node[] {
                        new EnvelopeWidget.Node(
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_DELAY),
                                0,
                                0,
                                null,
                                0,
                                false,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_DELAY),
                                null, "Delay", null),
                        new EnvelopeWidget.Node(
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_ATTACK),
                                100,
                                100,
                                null,
                                0,
                                false,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_ATTACK),
                                null, "Attack", null),
                        new EnvelopeWidget.Node(
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_HOLD),
                                100,
                                100,
                                null,
                                0,
                                false,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_HOLD),
                                null, "Hold", null),
                        new EnvelopeWidget.Node(
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_DECAY),
                                100,
                                100,
                                null,
                                0,
                                false,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_DECAY),
                                null, "Decay", null),
                        new EnvelopeWidget.Node(
                                0,
                                0,
                                null,
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_SUSTAIN),
                                0,
                                false,
                                null,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_SUSTAIN),
                                null, "Sustain"),
                        new EnvelopeWidget.Node(
                                0,
                                99,
                                new EmuParamModel(patch,
                                        EmuProteus2Constants.AUX_RELEASE),
                                0,
                                0,
                                null,
                                0,
                                false,
                                senderFactory
                                        .newSender(EmuProteus2Constants.AUX_RELEASE),
                                null, "Release", null) };

        addWidget(envPanel, new ScrollBarWidget("Amount", patch, 0, 127, 0,
                new EmuParamModel(patch, EmuProteus2Constants.AUX_AMOUNT),
                senderFactory.newSender(EmuProteus2Constants.AUX_AMOUNT)), 0,
                GridBagConstraints.RELATIVE, 1, 1, 0);

        addWidget(envPanel, new EnvelopeWidget(null, patch, nodes), 0,
                GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0);

        return envPanel;
    }

    final JPanel createKeyVelCtrlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        for (int i = 0; i < 6; i++) {

        }
        return panel;
    }
}
