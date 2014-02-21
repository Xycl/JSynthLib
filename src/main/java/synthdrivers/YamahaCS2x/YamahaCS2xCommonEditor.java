/*
 * Copyright 2009 Peter Geirnaert
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package synthdrivers.YamahaCS2x;

import core.*;

import javax.swing.*;

import org.jsynthlib.view.PatchEditorFrame;
import org.jsynthlib.view.widgets.ComboBoxWidget;
import org.jsynthlib.view.widgets.KnobWidget;
import org.jsynthlib.view.widgets.PatchNameWidget;
import org.jsynthlib.view.widgets.ScrollBarLookupWidget;
import org.jsynthlib.view.widgets.ScrollBarWidget;
import org.jsynthlib.view.widgets.SysexWidget;

import java.awt.*;

/**
 * @author peter
 */
public class YamahaCS2xCommonEditor extends PatchEditorFrame {

    public YamahaCS2xCommonEditor(Patch p) {
        super("Common Editor for CS2x Performance", p);
        scrollPane.setLayout(new GridBagLayout());
        gbc.weightx = 0;
        addCommonPane(p);
        // gbc.gridheight= 2;
        // gbc.gridwidth=4;
        addScen1Pane(p);
        // gbc.gridheight=2;
        // gbc.gridwidth=4;
        addScen2Pane(p);
        // gbc.gridheight=2;
        // gbc.gridwidth=4;
        addKnobsPane(p);
        // gbc.gridheight=1;
        // gbc.gridwidth=4;
        addKnob8Pane(p);
        // gbc.gridheight=8;
        // gbc.gridwidth=6;
        addArpPane(p);
        addFxTypePane(p);
    }

    // Scene 1 values for knobs 1 to 8
    public void addScen1Pane(Patch p) {
        JPanel scene1Pane = new JPanel();
        scene1Pane.setLayout(new GridBagLayout());
        addWidget(scene1Pane, new KnobWidget("1-A", p, 0, 127, -64,
                new CommonModel(p, 19), new CommonSender(p, 10)), 0, 0, 1, 1, 3);
        addWidget(scene1Pane, new KnobWidget("1-D", p, 0, 127, -64,
                new CommonModel(p, 20), new CommonSender(p, 11)), 1, 0, 1, 1, 4);
        addWidget(scene1Pane, new KnobWidget("1-R", p, 0, 127, -64,
                new CommonModel(p, 21), new CommonSender(p, 12)), 2, 0, 1, 1, 5);
        addWidget(scene1Pane, new KnobWidget("1-As1", p, 0, 127, -64,
                new CommonModel(p, 22), new CommonSender(p, 13)), 3, 0, 1, 1, 6);
        addWidget(scene1Pane, new KnobWidget("1-HPF", p, 0, 127, -64,
                new CommonModel(p, 23), new CommonSender(p, 14)), 0, 1, 1, 1, 7);
        addWidget(scene1Pane, new KnobWidget("1-LPF", p, 0, 127, -64,
                new CommonModel(p, 24), new CommonSender(p, 15)), 1, 1, 1, 1, 8);
        addWidget(scene1Pane, new KnobWidget("1-Res", p, 0, 127, -64,
                new CommonModel(p, 25), new CommonSender(p, 16)), 2, 1, 1, 1, 9);
        addWidget(scene1Pane, new KnobWidget("1-As2", p, 0, 127, -64,
                new CommonModel(p, 26), new CommonSender(p, 17)), 3, 1, 1, 1,
                10);
        gbc.gridx = 0;
        gbc.gridy = 1;
        scrollPane.add(scene1Pane, gbc);
    }// Scene 2 values for knobs 1 to 8

    public void addScen2Pane(Patch p) {
        JPanel scene2Pane = new JPanel();
        scene2Pane.setLayout(new GridBagLayout());
        addWidget(scene2Pane, new KnobWidget("2-A", p, 0, 127, -64,
                new CommonModel(p, 27), new CommonSender(p, 18)), 0, 0, 1, 1,
                11);
        addWidget(scene2Pane, new KnobWidget("2-D", p, 0, 127, -64,
                new CommonModel(p, 28), new CommonSender(p, 19)), 1, 0, 1, 1,
                12);
        addWidget(scene2Pane, new KnobWidget("2-R", p, 0, 127, -64,
                new CommonModel(p, 29), new CommonSender(p, 20)), 2, 0, 1, 1,
                13);
        addWidget(scene2Pane, new KnobWidget("2-As1", p, 0, 127, -64,
                new CommonModel(p, 30), new CommonSender(p, 21)), 3, 0, 1, 1,
                14);
        addWidget(scene2Pane, new KnobWidget("2-HPF", p, 0, 127, -64,
                new CommonModel(p, 31), new CommonSender(p, 22)), 0, 1, 1, 1,
                15);
        addWidget(scene2Pane, new KnobWidget("2-LPF", p, 0, 127, -64,
                new CommonModel(p, 32), new CommonSender(p, 23)), 1, 1, 1, 1,
                16);
        addWidget(scene2Pane, new KnobWidget("2-Res", p, 0, 127, -64,
                new CommonModel(p, 33), new CommonSender(p, 24)), 2, 1, 1, 1,
                17);
        addWidget(scene2Pane, new KnobWidget("2-As2", p, 0, 127, -64,
                new CommonModel(p, 34), new CommonSender(p, 25)), 3, 1, 1, 1,
                18);
        gbc.gridx = 1;
        gbc.gridy = 1;
        scrollPane.add(scene2Pane, gbc);
    }

    public void addKnobsPane(Patch p) {
        JPanel knobsPane = new JPanel();
        knobsPane.setLayout(new GridBagLayout());
        addWidget(knobsPane, new KnobWidget("Attack", p, 0, 127, -64,
                new CommonModel(p, 35), new CommonSender(p, 26)), 0, 0, 1, 1,
                19);
        addWidget(knobsPane, new KnobWidget("Decay", p, 0, 127, -64,
                new CommonModel(p, 36), new CommonSender(p, 27)), 1, 0, 1, 1,
                20);
        addWidget(knobsPane, new KnobWidget("Release", p, 0, 127, -64,
                new CommonModel(p, 37), new CommonSender(p, 28)), 2, 0, 1, 1,
                21);
        addWidget(knobsPane, new KnobWidget("Assign1", p, 0, 127, -64,
                new CommonModel(p, 38), new CommonSender(p, 29)), 3, 0, 1, 1,
                22);
        addWidget(knobsPane, new KnobWidget("HPF cutoff", p, 0, 127, -64,
                new CommonModel(p, 39), new CommonSender(p, 30)), 1, 1, 1, 1,
                23);
        addWidget(knobsPane, new KnobWidget("LPF cutoff", p, 0, 127, -64,
                new CommonModel(p, 40), new CommonSender(p, 31)), 0, 1, 1, 1,
                24);
        addWidget(knobsPane, new KnobWidget("Resonance", p, 0, 127, -64,
                new CommonModel(p, 41), new CommonSender(p, 32)), 2, 1, 1, 1,
                25);
        addWidget(knobsPane, new KnobWidget("Assign2", p, 0, 127, -64,
                new CommonModel(p, 42), new CommonSender(p, 33)), 3, 1, 1, 1,
                26);
        gbc.gridx = 0;
        gbc.gridy = 3;
        scrollPane.add(knobsPane, gbc);
    }

    public void addKnob8Pane(Patch p) {
        JPanel knob8Pane = new JPanel();
        knob8Pane.setLayout(new GridBagLayout());
        // knob8 param1
        addWidget(knob8Pane, new ComboBoxWidget("knob8 param1", p,
                new CommonModel(p, 43, 32, true), new CommonSender(p, 34, 32,
                        true), CS2x.Knob8aParams), 0, 0, 1, 1, 31);
        addWidget(knob8Pane, new ComboBoxWidget("param2", p, new CommonModel(p,
                45, 32, true), new CommonSender(p, 36, 32, true),
                CS2x.Knob8aParams), 0, 1, 1, 1, 32);
        addWidget(knob8Pane, new ComboBoxWidget("param3", p, new CommonModel(p,
                47, 32, true), new CommonSender(p, 38, 32, true),
                CS2x.Knob8aParams), 0, 2, 1, 1, 31);
        addWidget(knob8Pane, new ComboBoxWidget("param4", p, new CommonModel(p,
                49, 32, true), new CommonSender(p, 40, 32, true),
                CS2x.Knob8aParams), 0, 3, 1, 1, 32);
        addWidget(knob8Pane, new KnobWidget("knob8 sensi1", p, 32, 96, -64,
                new CommonModel(p, 51), new CommonSender(p, 42)), 1, 0, 1, 1,
                33);
        addWidget(knob8Pane, new KnobWidget("sensi2", p, 32, 96, -64,
                new CommonModel(p, 52), new CommonSender(p, 43)), 1, 1, 1, 1,
                34);
        addWidget(knob8Pane, new KnobWidget("sensi3", p, 32, 96, -64,
                new CommonModel(p, 53), new CommonSender(p, 44)), 1, 2, 1, 1,
                35);
        addWidget(knob8Pane, new KnobWidget("sensi4", p, 32, 96, -64,
                new CommonModel(p, 54), new CommonSender(p, 45)), 1, 3, 1, 1,
                36);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        scrollPane.add(knob8Pane, gbc);
    }

    public void addArpPane(Patch p) {
        JPanel arpPane = new JPanel();
        arpPane.setLayout(new GridBagLayout());
        addWidget(arpPane, new ComboBoxWidget("Arpeggio: ", p, new CommonModel(
                p, 60), new CommonSender(p, 51), CS2x.Arp), 0, 0, 1, 1, 27); //
        addWidget(arpPane, // the parent JComponent
                new ScrollBarLookupWidget("Tempo", p, 0, 201, 0,
                        new CommonModel(p, 56, 128, true), new CommonSender(p,
                                47, 128, true), CS2x.ArpTempos), 1, 0, 1, 1, 28); //
        addWidget(arpPane,
                new ComboBoxWidget("Type", p, new CommonModel(p, 58),
                        new CommonSender(p, 49), CS2x.ArpTypes), 2, 0, 1, 1, 29); //
        addWidget(arpPane, new ComboBoxWidget("Subdivide", p, new CommonModel(
                p, 59), new CommonSender(p, 50), CS2x.ArpSDivs), 3, 0, 1, 1, 30); //
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        scrollPane.add(arpPane, gbc);
    }

    public void addFxTypePane(Patch p) {
        JPanel fxTypePane = new JPanel();
        fxTypePane.setLayout(new GridBagLayout());
        addWidget(fxTypePane, new ComboBoxWidget("Reverb Type", p, new RTModel(
                p, 72, CS2x.ReverbTypeBytes, CS2x.ReverbTypes), new RTSender(p,
                64, CS2x.ReverbTypeBytes), CS2x.ReverbTypes), 0, 0, 1, 1, 38);
        addWidget(fxTypePane, new ComboBoxWidget("Chorus Type", p, new RTModel(
                p, 74, CS2x.ChorusTypeBytes, CS2x.ChorusTypes), new RTSender(p,
                66, CS2x.ChorusTypeBytes), CS2x.ChorusTypes), 0, 1, 1, 1, 39);
        addWidget(fxTypePane, new ComboBoxWidget("Vari Type", p, new RTModel(p,
                76, CS2x.VariTypeBytes, CS2x.VariTypes), new RTSender(p, 68,
                CS2x.VariTypeBytes), CS2x.VariTypes), 0, 2, 1, 1, 40);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        scrollPane.add(fxTypePane, gbc);
    }

    public void addCommonPane(Patch p) {
        JPanel commonPane = new JPanel();
        commonPane.setLayout(new GridBagLayout());

        // gbc.weightx = 0;
        addWidget(commonPane, new ScrollBarWidget("Volume", p, 0, 127, 0,
                new CommonModel(p, 18), new CommonSender(p, 9)), 0, 0, 2, 1, 2); //
        addWidget(commonPane, new ComboBoxWidget("Category", p,
                new CommonModel(p, 17), new CommonSender(p, 8), CS2x.Cat), 0,
                1, 1, 1, 0);
        addWidget(commonPane, new PatchNameWidget("Name", p), 1, 1, 1, 1, 0);
        addWidget(commonPane, new ComboBoxWidget("knob4 parameter", p,
                new CommonModel(p, 55), new CommonSender(p, 46),
                CS2x.Knob4Params), 0, 3, 1, 1, 37); //
        // part2
        // (missing here: Vari Param1,2,3,4,5,10 !Too much variables, find
        // somebody more crazy than me!)
        addWidget(commonPane, new KnobWidget("FC Vari Ef", p, 0, 127, -64,
                new CommonModel(p, 91), new CommonSender(p, 83)), 0, 5, 1, 1,
                41);
        addWidget(commonPane, new KnobWidget("ChorusToReverb", p, 0, 127, 0,
                new CommonModel(p, 92), new CommonSender(p, 84)), 1, 5, 1, 1,
                42);
        addWidget(commonPane, new KnobWidget("VariOnRevSend", p, 0, 127, 0,
                new CommonModel(p, 93), new CommonSender(p, 85)), 2, 5, 1, 1,
                43);
        addWidget(commonPane, new KnobWidget("VariOnChoSend", p, 0, 127, 0,
                new CommonModel(p, 94), new CommonSender(p, 86)), 3, 5, 1, 1,
                44);
        // part3
        addWidget(commonPane, new KnobWidget("MW PMod", p, 0, 127, 0,
                new CommonModel(p, 107), new CommonSender(p, 97)), 0, 6, 1, 1,
                46);
        addWidget(commonPane, new KnobWidget("MW FMod", p, 0, 127, 0,
                new CommonModel(p, 108), new CommonSender(p, 98)), 1, 6, 1, 1,
                47);
        addWidget(commonPane, new KnobWidget("MW Cutoff", p, 0, 127, -64,
                new CommonModel(p, 106), new CommonSender(p, 96)), 2, 6, 1, 1,
                45);
        addWidget(commonPane, new KnobWidget("P Bend Range", p, 40, 88, -64,
                new CommonModel(p, 109), new CommonSender(p, 99)), 3, 6, 1, 1,
                48);
        addWidget(commonPane, new KnobWidget("FC Cutoff", p, 0, 127, -64,
                new CommonModel(p, 110), new CommonSender(p, 100)), 2, 7, 1, 1,
                49);
        addWidget(commonPane, new KnobWidget("FC FMod", p, 0, 127, 0,
                new CommonModel(p, 112), new CommonSender(p, 102)), 3, 7, 1, 1,
                50);
        addWidget(commonPane, new KnobWidget("Porta Time", p, 0, 127, 0,
                new CommonModel(p, 114), new CommonSender(p, 104)), 0, 7, 1, 1,
                51);
        addWidget(commonPane, new ComboBoxWidget("Porta Switch", p,
                new CommonModel(p, 113), new CommonSender(p, 103),
                CS2x.PortamentoSwitch), 1, 7, 1, 1, 52);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        scrollPane.add(commonPane, gbc);
    }

    class CommonModel implements SysexWidget.IParamModel {
        /** <code>Patch</code> data. */
        protected Patch patch;
        /** Offset of the data for which this model is. */
        protected int ofs;
        int mltplr;
        private boolean nibbled;

        public CommonModel(Patch p, int ofs, int mltplr, boolean nibbled) {
            this.patch = p;
            this.ofs = ofs;
            this.mltplr = mltplr;
            this.nibbled = nibbled;
        }

        public CommonModel(Patch p, int ofs) {
            this(p, ofs, 1, false);
        }

        public void set(int value) {
            if (nibbled) {
                patch.sysex[ofs] = (byte) (value / mltplr);
                patch.sysex[ofs + 1] = (byte) (value % mltplr);
            } else {
                patch.sysex[ofs] = (byte) value;
            }
        }

        public int get() {
            if (nibbled) {
                int d =
                        (((patch.sysex[ofs]) * mltplr) + (patch.sysex[ofs + 1]));
                return d;
            } else {
                return patch.sysex[ofs];
            }
        }

    }

    class CommonSender extends SysexSender {
        Patch patch;
        byte[] sysx;
        int ofset;
        int lyr;
        int mltplr;
        boolean nibbled;

        public CommonSender(Patch p, int offset, int mltplr, boolean nibbled) {
            // 0 1 2 3 4 5 6 7 8
            super("F043@@63600110**F7");
            this.patch = p;
            this.ofset = offset;
            this.sysx = patch.sysex;
            this.lyr = sysx[7];
            this.nibbled = nibbled;
            this.mltplr = mltplr;
        }

        public CommonSender(Patch p, int offset) {
            this(p, offset, 1, false);
        }

        public byte[] generate(int value) {
            if (nibbled) {
                int ilyr = value / mltplr;
                int val = (value - (value % mltplr)) / mltplr;
                byte[] syse = super.generate(val);
                int dev = syse[2];
                syse[2] = (byte) (dev + 0x10);
                syse[5] = (byte) lyr;
                syse[6] = (byte) ofset;
                syse[7] = (byte) ilyr;// (value/mltplr);
                syse[8] = (byte) (value % mltplr);
                byte[] syx = new byte[10];
                System.arraycopy(syse, 0, syx, 0, 9);
                syx[9] = (byte) 0xF7;
                return syx;
            } else {
                byte[] syse = super.generate(value);
                int dev = syse[2];
                syse[2] = (byte) (dev + 0x10);
                syse[5] = (byte) lyr;
                syse[6] = (byte) ofset;
                return syse;
            }
        }
    }

    class RTModel implements SysexWidget.IParamModel {
        /** <code>Patch</code> data. */
        protected Patch patch;
        protected byte[][] arrayArray;
        protected String[] types;
        protected int ofs;

        public RTModel(Patch p, int ofs, byte[][] arrArr, String[] types) {
            this.patch = p;
            this.ofs = ofs;
            this.arrayArray = arrArr;
            this.types = types;
        }

        public void set(int value) {
            patch.sysex[ofs] = arrayArray[value][0];
            patch.sysex[ofs + 1] = arrayArray[value][1];
            // throw new UnsupportedOperationException("Not supported yet.");
        }

        public int get() {
            int t = 0;
            for (int i = 0; i < types.length; i++) {
                if ((patch.sysex[ofs] == arrayArray[i][0])
                        && (patch.sysex[ofs + 1] == arrayArray[i][1])) {
                    t = i;
                    // return t;
                } else {
                    // t=0;
                }
            }
            return t;
            // throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class RTSender extends SysexSender {
        Patch patch;
        protected byte[] sysx;
        protected byte[][] arrayArray;
        protected int adrs;

        public RTSender(Patch p, int adrs, byte[][] arrArr) {
            // 0 1 2 3 4 5 6 7 8 9 10
            super("F043@@63600040**00F7");
            this.patch = p;
            this.adrs = adrs;
            this.arrayArray = arrArr;

        }

        // todo : generalize generate method
        public byte[] generate(int value) {
            byte[] syse = super.generate(0);
            int dev = syse[2];
            syse[2] = (byte) (dev + 0x10);
            syse[6] = (byte) adrs;
            syse[7] = arrayArray[value][0];
            syse[8] = arrayArray[value][1];
            return syse;
        }
    }
}
