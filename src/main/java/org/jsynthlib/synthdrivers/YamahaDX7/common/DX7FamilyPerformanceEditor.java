/*
 * JSynthlib -	generic "Performance" Editor for Yamaha DX7 Family
 * (used by DX1, DX5, DX7 MKI, TX7, TX816)
 * ===============================================================
 * @version $Id: DX7FamilyPerformanceEditor.java 1106 2011-09-03 17:04:25Z frankster $
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7.common;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jsynthlib.device.model.ISender;
import org.jsynthlib.device.model.ParamModel;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.widgets.ComboBoxWidget;
import org.jsynthlib.device.viewcontroller.widgets.PatchNameWidget;
import org.jsynthlib.device.viewcontroller.widgets.ScrollBarWidget;
import org.jsynthlib.patch.model.impl.Patch;

public class DX7FamilyPerformanceEditor extends PatchEditorFrame {
    private static final String[] OnOffName = new String[] {
            "Off", "On" };

    private static final String[] SourceSelectName = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "13", "14", "15", "16" };

    private static final String[] PolyMonoName = new String[] {
            "Poly", "Mono" };

    private static final String[] VoiceSelectName = new String[] {
            "Voice A", "Voice B" };

    private static final String[] DualModeDetuneName = new String[] {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",
            "13", "14", "15" };

    private static final String[] SplitPointName = new String[] {
            "A-1", "A#-1", "B-1", "C0", "C#0", "D0", "D#0", "E0", "F0", "F#0",
            "G0", "G#0", "A0", "A#0", "B0", "C1", "C#1", "D1", "D#1", "E1",
            "F1", "F#1", "G1", "G#1", "A1", "A#1", "B1", "C2", "C#2", "D2",
            "D#2", "E2", "F2", "F#2", "G2", "G#2", "A2", "A#2", "B2", "C3",
            "C#3", "D3", "D#3", "E3", "F3", "F#3", "G3", "G#3", "A3", "A#3",
            "B3", "C4", "C#4", "D4", "D#4", "E4", "F4", "F#4", "G4", "G#4",
            "A4", "A#4", "B4", "C5", "C#5", "D5", "D#5", "E5", "F5", "F#5",
            "G5", "G#5", "A5", "A#5", "B5", "C6", "C#6", "D6", "D#6", "E6",
            "F6", "F#6", "G6", "G#6", "A6", "A#6", "B6", "C7", "C#7", "D7",
            "D#7", "E7", "F7", "F#7", "G7", "G#7", "A7", "A#7", "B6" };

    private static final String[] PerformanceKeyShiftName = new String[] {
            "C1", "C#1", "D1", "D#1", "E1", "F1", "F#1", "G1", "G#1", "A1",
            "A#1", "B1", "C2", "C#2", "D2", "D#2", "E2", "F2", "F#2", "G2",
            "G#2", "A2", "A#2", "B2", "C3", "C#3", "D3", "D#3", "E3", "F3",
            "F#3", "G3", "G#3", "A3", "A#3", "B3", "C4", "C#4", "D4", "D#4",
            "E4", "F4", "F#4", "G4", "G#4", "A4", "A#4", "B4", "C5" };

    private static final String[] VoiceNumberName = new String[] {
            " 1", " 2", " 3", " 4", " 5", " 6", " 7", " 8", " 9", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22",
            "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33",
            "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44",
            "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55",
            "56", "57", "58", "59", "60", "61", "62", "63", "64" };

    private static final String[] KeyAssignName = new String[] {
            "Single", "Dual", "Split" };

    private static final String[] PitchBendRangeName = new String[] {
            "Off", //
            "perfect First (Unison)", // Prime
            "minor Second", // kleine Sekunde
            "major Second", // grosse Sekunde
            "minor Third", // kleine Terz
            "major Third", // grosse Terz
            "perfect Fourth", // Quarte
            "perfect Fifth", // Quinte bzw. Tritonus
            "minor Sixth", // kleine Sexte
            "major Sixth", // grosse Sexte
            "minor Seventh", // kleine Septine
            "major Seventh", // grosse Septine
            "Octave" // Oktave
    };

    private static final String[] PitchBendStepName = new String[] {
            "Continuous",
            "\u00B11 Octave	  - 12 Steps", // 00B1 is the plus/minus sign
            "\u00B11 Octave	  -  6 Steps", "\u00B11 Octave	  -  4 Steps",
            "\u00B11 Octave	  -  3 Steps", "\u00B11 minor Seventh -  2 Steps",
            "\u00B11 Octave	  -  2 Steps", "\u00B11 Fifth	  -  1 Step ",
            "\u00B11 major Fifth   -  1 Step ", "\u00B11 Sixth	  -  1 Step ",
            "\u00B11 minor Seventh -  1 Step ",
            "\u00B11 major Seventh -  1 Step ", "\u00B11 Octave	  -  1 Step " };

    private static final String[] PortGlissName = new String[] {
            "Portamento", "Glissando" };

    private static final String[] PortModeName = new String[] {
            "Retain/Fingered", "Follow/Full Time" };

    private static final String[] AssignName = new String[] {
            "Off", "Pitch", "Amplitude", "Pitch + Amplitude", "EG Bias",
            "Pitch + EG Bias", "Pitch + Amplitude",
            "Pitch + Amplitude + EG Bias" };

    public DX7FamilyPerformanceEditor(String name, Patch patch) {
        super(name, patch);

        buildEditor(patch);
    }

    protected void buildEditor(Patch patch) {

        /*
         * Performance Parameter - Common settings
         */

        JPanel cmnPane = new JPanel();
        cmnPane.setLayout(new GridBagLayout());
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;

        addWidget(cmnPane, new PatchNameWidget("Performance Name (30 Char.)",
                patch), 0, 0, 9, 1, 1);

        if (!isDX7(patch)) {

            addWidget(cmnPane,
                    new ComboBoxWidget("Key Assign Mode", patch,
                            new ParamModel(patch, 6 + 60),
                            newPerformanceSender(patch, 30), KeyAssignName), 0,
                    1, 3, 1, 2);
            addWidget(cmnPane,
                    new ComboBoxWidget("Voice Memory Select Flag", patch,
                            new ParamModel(patch, 6 + 61),
                            newPerformanceSender(patch, 31), VoiceSelectName),
                    3, 1, 3, 1, 3);

            if (isDX1(patch) || isDX5(patch)) {
                addWidget(cmnPane, new ComboBoxWidget("Dual Mode Detune",
                        patch, new ParamModel(patch, 6 + 62),
                        newPerformanceSender(patch, 32), DualModeDetuneName),
                        0, 2, 3, 1, 4);
                addWidget(
                        cmnPane,
                        new ComboBoxWidget("Split Point", patch,
                                new ParamModel(patch, 6 + 63),
                                newPerformanceSender(patch, 33), SplitPointName),
                        3, 2, 3, 1, 5);
            }
        }

        addWidget(cmnPane, new ComboBoxWidget("Corresponding Voice?", patch,
                new ParamModel(patch, 6 + 0), null, VoiceNumberName), 0, 3, 3,
                1, 6);
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.gridwidth = 6;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.EAST;
        cmnPane.add(new JLabel(
                "(Attention! This is an undocumented parameter!)"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        cmnPane.setBorder(new TitledBorder(
                new EtchedBorder(EtchedBorder.RAISED), "Common",
                TitledBorder.CENTER, TitledBorder.CENTER));
        scrollPane.add(cmnPane, gbc);

        /*
         * Voice A+B Parameter
         */
        JTabbedPane voicePane = new JTabbedPane();

        for (int i = 0; i <= 1; i++) {
            JPanel panel = new JPanel();
            panel.setLayout(new GridBagLayout());

            if (i == 0) {
                voicePane.addTab("Voice A", panel);
            } else {
                voicePane.addTab("Voice B", panel);
            }

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1;
            panel.add(new JLabel(" "), gbc);
            if (isDX1(patch) || isDX5(patch)) {
                addWidget(panel, new ComboBoxWidget("Source Select", patch,
                        new ParamModel(patch, 6 + (i * 30) + 1),
                        newPerformanceSender(patch, 1), SourceSelectName), 3,
                        0, 6, 1, 10);
                addWidget(panel, new ComboBoxWidget("Performance Key Shift",
                        patch, new ParamModel(patch, 6 + (i * 30) + 29),
                        newPerformanceSender(patch, 29),
                        PerformanceKeyShiftName), 9, 0, 6, 1, 13);
            }

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            addWidget(panel, new ComboBoxWidget("Key Mode Assign", patch,
                    new ParamModel(patch, 6 + (i * 30) + 2),
                    newPerformanceSender(patch, 2), PolyMonoName), 3, 1, 6, 1,
                    12);
            if (!isDX7(patch)) {
                addWidget(panel, new ScrollBarWidget("Output Attenuator",
                        patch, 0, 7, 0,
                        new ParamModel(patch, 6 + (i * 30) + 26),
                        newPerformanceSender(patch, 26)), 9, 1, 6, 1, 11);
            }

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("Pitch Bend: "), gbc);
            addWidget(panel, new ComboBoxWidget("Range", patch, new ParamModel(
                    patch, 6 + (i * 30) + 3), newPerformanceSender(patch, 3),
                    PitchBendRangeName), 3, 3, 6, 1, 14);
            addWidget(panel, new ComboBoxWidget("Step", patch, new ParamModel(
                    patch, 6 + (i * 30) + 4), newPerformanceSender(patch, 4),
                    PitchBendStepName), 9, 3, 6, 1, 15);

            gbc.gridx = 0;
            gbc.gridy = 4;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("Portamento: "), gbc);
            addWidget(panel, new ComboBoxWidget("Portamento/Glissando", patch,
                    new ParamModel(patch, 6 + (i * 30) + 6),
                    newPerformanceSender(patch, 6), PortGlissName), 3, 5, 6, 1,
                    16);
            if (isDX1(patch) || isDX5(patch)) {
                addWidget(panel, new ComboBoxWidget(
                        "Portamento Pedal and Knop Assign (*)", patch,
                        new ParamModel(patch, 6 + (i * 30) + 8),
                        newPerformanceSender(patch, 8), OnOffName), 9, 5, 6, 1,
                        17);
            }
            addWidget(panel, new ComboBoxWidget("Mode (Poly/Mono)", patch,
                    new ParamModel(patch, 6 + (i * 30) + 7),
                    newPerformanceSender(patch, 7), PortModeName), 3, 6, 6, 1,
                    18);
            addWidget(panel, new ScrollBarWidget("Time", patch, 0, 99, 0,
                    new ParamModel(patch, 6 + (i * 30) + 5),
                    newPerformanceSender(patch, 5)), 9, 6, 6, 1, 19);

            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 9;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("Modulation Wheel: "), gbc);
            addWidget(panel, new ComboBoxWidget("Assign", patch,
                    new ParamModel(patch, 6 + (i * 30) + 10),
                    newPerformanceSender(patch, 10), AssignName), 3, 9, 6, 1,
                    20);
            addWidget(panel, new ScrollBarWidget("Sensitivity", patch, 0, 15,
                    0, new ParamModel(patch, 6 + (i * 30) + 9),
                    newPerformanceSender(patch, 9)), 9, 9, 6, 1, 21);

            gbc.gridx = 0;
            gbc.gridy = 10;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 11;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("Foot Controller: "), gbc);
            addWidget(panel, new ComboBoxWidget("Assign", patch,
                    new ParamModel(patch, 6 + (i * 30) + 12),
                    newPerformanceSender(patch, 12), AssignName), 3, 11, 6, 1,
                    22);
            addWidget(panel, new ScrollBarWidget("Sensitivity", patch, 0, 15,
                    0, new ParamModel(patch, 6 + (i * 30) + 11),
                    newPerformanceSender(patch, 11)), 9, 11, 6, 1, 23);

            gbc.gridx = 0;
            gbc.gridy = 12;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 13;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("After Touch: "), gbc);
            addWidget(panel, new ComboBoxWidget("Assign", patch,
                    new ParamModel(patch, 6 + (i * 30) + 14),
                    newPerformanceSender(patch, 14), AssignName), 3, 13, 6, 1,
                    24);
            addWidget(panel, new ScrollBarWidget("Sensitivity", patch, 0, 15,
                    0, new ParamModel(patch, 6 + (i * 30) + 13),
                    newPerformanceSender(patch, 13)), 9, 13, 6, 1, 25);

            gbc.gridx = 0;
            gbc.gridy = 14;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            panel.add(new JLabel(" "), gbc);
            gbc.gridx = 0;
            gbc.gridy = 15;
            gbc.gridwidth = 3;
            gbc.gridheight = 1;
            panel.add(new JLabel("Breath Controller: "), gbc);
            addWidget(panel, new ComboBoxWidget("Assign", patch,
                    new ParamModel(patch, 6 + (i * 30) + 16),
                    newPerformanceSender(patch, 16), AssignName), 3, 15, 6, 1,
                    26);
            addWidget(panel, new ScrollBarWidget("Sensitivity", patch, 0, 15,
                    0, new ParamModel(patch, 6 + (i * 30) + 15),
                    newPerformanceSender(patch, 15)), 9, 15, 6, 1, 27);

            if (isDX7(patch)) {
                i = 1;
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 3;
        gbc.gridheight = 13;
        scrollPane.add(voicePane, gbc);
        pack();
    }

    ISender newPerformanceSender(Patch p, int param) {
        PerformanceSender sender = new PerformanceSender();
        sender.setParameter(param);
        sender.setPatch(p);
        return sender;
    }

    private boolean isDX1(Patch p) {
        return p.getDevice().getModelName().equals("DX1");
    }

    private boolean isDX5(Patch p) {
        return p.getDevice().getModelName().equals("DX5");
    }

    boolean isDX7(Patch p) {
        return p.getDevice().getModelName().equals("DX7");
    }

    private boolean isTX7(Patch p) {
        return p.getDevice().getModelName().equals("TX7");
    }

    private boolean isTX816(Patch p) {
        return p.getDevice().getModelName().equals("TX816");
    }

}
