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
import java.awt.*;
/**
 *
 * @author peter
 */
public class YamahaCS2xLayerEditor extends PatchEditorFrame {

    public YamahaCS2xLayerEditor(Patch p) {
        super("YamahaCS2xLayerEditor",p);
        scrollPane.setLayout(new GridLayout(0,1));
        addLayerPane(p);
    }
    public void addLayerPane(Patch p) {
        JPanel layerPane = new JPanel();
        layerPane.setLayout(new GridBagLayout());
        //**
        
        ComboBoxWidget voice = new ComboBoxWidget(
                "Voice",
                p, 
                new LayerModel( //see paramModel
                    p, //the type of patch we're working with
                    11), // the offset of the parameter in the sysexmessage
                new LayerSender(p,2), // see ParamSender
                CS2x.VoiceNames[p.sysex[10]]);
        addWidget(layerPane, new ComboBoxWidget(
                "Voice bank",p,new LayerBankModel(p,10,voice), new LayerBankSender(p,0),CS2x.Banks),
                0,0,1,1,1);
        addWidget(layerPane,
            voice,
            1, // gridx
            0, // gridy
            1, // gridwidth
            1, // gridheight
            2); // slidernum //*/
        addWidget(layerPane, // the parent JComponent
                new ScrollBarWidget( // the type of SysexWidget to add
                "Volume", // label
                p, 0, 127, // patch, min, max
                0,// base value. This value is added to the actual value for display purposes
                new LayerModel(p,16),new LayerSender(p,7)),
                0, 1, 2, 1, 2); //  
        addWidget(layerPane, new ScrollBarWidget(
                "Pan", p, 0, 127, 0,new LayerModel(p,19),new LayerSender(p,10)),
                0, 2, 2, 1, 3); // 
        addWidget(layerPane, new ScrollBarWidget(
                "Cutoff", p, 1, 127, 0,new LayerModel(p,27),new LayerSender(p,18)),
                0, 3, 2, 1, 4); // Filter cutoff freq
        addWidget(layerPane, new ScrollBarWidget(
                "Res", p, 1, 127, 0,new LayerModel(p,28),new LayerSender(p,19)),
                0, 4, 2, 1, 5); //
        addWidget(layerPane, new ScrollBarWidget(
                "Chorus", p, 0, 127, 0,new LayerModel(p,22),new LayerSender(p,13)),
                0, 5, 2, 1, 6); //
        addWidget(layerPane, new ScrollBarWidget(
                "Reverb", p, 0, 127, 0,new LayerModel(p,23),new LayerSender(p,14)),
                0, 6, 2, 1, 7); //
        addWidget(layerPane, new ScrollBarWidget(
                "Vari", p, 0, 127, 0,new LayerModel(p,24),new LayerSender(p,15)),
                0, 7, 2, 1, 8); //
        addWidget(layerPane, new ScrollBarWidget(
                "AEG attack time", p, 1, 127, 0,new LayerModel(p,29),new LayerSender(p,20)),
                0, 8, 2, 1, 9); //
        addWidget(layerPane, new ScrollBarWidget(
                "AEG release time", p, 1, 127, 0,new LayerModel(p,30),new LayerSender(p,21)),
                0, 9, 2, 1, 10); //
        addWidget(layerPane, new ScrollBarWidget(
                "AEG sustain level", p, 0, 127, 0,new LayerModel(p,38),new LayerSender(p,29)),
                0, 10, 2, 1, 11); //
        addWidget (layerPane,new ComboBoxWidget(
                "receive note",p,new LayerModel(p,31),new LayerSender(p,22),new String []{"Channel","All","Mute"}),
                0, 11, 2, 1, 12);
        addWidget(layerPane, new ScrollBarWidget(
                "PEG initial level", p, 0, 127, 0,new LayerModel(p,32),new LayerSender(p,23)),
                0, 12, 2, 1, 13); //
        addWidget(layerPane, new ScrollBarWidget(
                "PEG attack time", p, 1, 127, 0,new LayerModel(p,33),new LayerSender(p,24)),
                0, 13, 2, 1, 14); //
        addWidget(layerPane, new ScrollBarWidget(
                "PEG attack level", p, 0, 127, 0,new LayerModel(p,48),new LayerSender(p,39)),
                0, 14, 2, 1, 15); //
        addWidget(layerPane, new ScrollBarWidget(
                "PEG decay time", p, 1, 127, 0,new LayerModel(p,49),new LayerSender(p,40)),
                0, 15, 2, 1, 16); //
        addWidget(layerPane, new ScrollBarWidget(
                "PEG release time", p, 1, 127, 0,new LayerModel(p,34),new LayerSender(p,25)),
                0, 16, 2, 1, 17); //
        addWidget(layerPane, new ScrollBarWidget(
                "PEG release level", p, 0, 127, 0,new LayerModel(p,35),new LayerSender(p,26)),
                0, 17, 2, 1, 18); //
        addWidget(layerPane, new ScrollBarWidget(
                "Velocity limit lo", p, 1, 127, 0,new LayerModel(p,36),new LayerSender(p,27)),
                0, 18, 2, 1, 19); //
        addWidget(layerPane, new ScrollBarWidget(
                "Velocity limit hi", p, 1, 127, 0,new LayerModel(p,37),new LayerSender(p,28)),
                0, 19, 2, 1, 20); //
        addWidget(layerPane, new ScrollBarWidget(
                "LFO Speed", p, 1, 127, 0,new LayerModel(p,39),new LayerSender(p,30)),
                0, 20, 2, 1, 21); //
        addWidget(layerPane, new ComboBoxWidget(
                "LFO Wave", p,new LayerModel(p,40),new LayerSender(p,31),new String []{"Saw","Tri","S&H","Elem"}),
                0, 21, 2, 1, 22); //
        addWidget(layerPane, new ScrollBarWidget(
                "LFO AMod Depth", p, 33, 95, 0,new LayerModel(p,41),new LayerSender(p,32)),
                0, 22, 2, 1, 23); //
        addWidget(layerPane, new ScrollBarWidget(
                "LFO PMod Depth", p, 1, 127, 0,new LayerModel(p,42),new LayerSender(p,33)),
                0, 23, 2, 1, 24); //
        addWidget(layerPane, new ScrollBarWidget(
                "LFO FModu Depth", p, 0, 127, 0,new LayerModel(p,43),new LayerSender(p,34)),
                0, 24, 2, 1, 25); //
        addWidget(layerPane, new ScrollBarWidget(
                "FEG A", p, 1, 127, 0,new LayerModel(p,44),new LayerSender(p,35)),
                0, 25, 2, 1, 26); //
        addWidget(layerPane, new ScrollBarWidget(
                "FEG D", p, 1, 127, 0,new LayerModel(p,45),new LayerSender(p,36)),
                0, 26, 2, 1, 27); //
        addWidget(layerPane, new ScrollBarWidget(
                "FEG S", p, 0, 127, 0,new LayerModel(p,46),new LayerSender(p,37)),
                0, 27, 2, 1, 28); //
        addWidget(layerPane, new ScrollBarWidget(
                "FEG R", p, 1, 127, 0,new LayerModel(p,47),new LayerSender(p,38)),
                0, 28, 2, 1, 29); //
        addWidget(layerPane, new ScrollBarWidget(
                "EQ Bass Gain", p, 1, 127, 0,new LayerModel(p,50),new LayerSender(p,41)),
                0, 29, 2, 1, 30); //
        addWidget(layerPane, new ScrollBarWidget(
                "EQ Treble Gain", p, 1, 127, 0,new LayerModel(p,51),new LayerSender(p,42)),
                0, 30, 2, 1, 31); //
        addWidget(layerPane, new ScrollBarWidget(
                "EQ Bass Freq", p, 1, 127, 0,new LayerModel(p,52),new LayerSender(p,43)),
                0, 31, 2, 1, 32); //
        addWidget(layerPane, new ScrollBarWidget(
                "EQ Treble Freq", p, 1, 127, 0,new LayerModel(p,53),new LayerSender(p,44)),
                0, 32, 2, 1, 33); //

        scrollPane.add(layerPane);
    }
    class LayerModel implements SysexWidget.IParamModel {
    /** <code>Patch</code> data. */
    protected Patch patch;
    /** Offset of the data for which this model is. */
    protected int ofs;

        public LayerModel(Patch p, int ofs) {
            this.patch = p;
            this.ofs = ofs;
        }
        public void set(int value) {
            patch.sysex[ofs] = (byte)value;
        }

        public int get() {
            return patch.sysex[ofs];
        }
        
    }
    class LayerBankModel implements SysexWidget.IParamModel {
        /** <code>Patch</code> data. */
    protected Patch patch;
    /** Offset of the data for which this model is. */
    protected int ofs;
    ComboBoxWidget voice;

        public LayerBankModel(Patch p, int ofs, ComboBoxWidget voice) {
            this.patch = p;
            this.ofs = ofs;
            this.voice = voice;
        }
        public void set(int value) {
            //NibbleSysex nibbleSysex = new NibbleSysex(((Patch)p).sysex,ofs);
            //nibbleSysex.putNibbleInt(value,2,CS2x.nibbleMultiplier);
            patch.sysex[ofs] = (byte)value;
            //voice = CS2x.VoiceNames[value];
            //is the original patch changed now?
        }

        public int get() {
            //NibbleSysex nibbleSysex = new NibbleSysex(((Patch)p).sysex,ofs);
            //return nibbleSysex.getNibbleInt(2, CS2x.nibbleMultiplier);
            return patch.sysex[ofs];
        }
    }
    class LayerSender extends SysexSender {
        Patch patch;
        byte[] sysx;
        int ofset;
        int lyr;
        public LayerSender(Patch p, int offset){
            //      0 1 2 3 4 5 6 7 8
            super("F043@@63600110**F7");
            this.patch = p;
            this.ofset = offset;
            this.sysx = patch.sysex;
            this.lyr = sysx[7];
        }
        public byte[] generate(int value) {
            byte[] syse = super.generate(value);
            int dev = syse[2];
            syse[2]=(byte)(dev+0x10);
            syse[5]=(byte)lyr;
            syse[6]=(byte)ofset;
            return syse;
        }
        
    }
    /** sending the two bytes as described in the manual doesn't seem to work.*/
    class LayerBankSender extends SysexSender {
        
        Patch patch;
        //byte[] sysx;
        int ofset;
        int lyr;
        byte[] sysx;
        public LayerBankSender(Patch p, int offset){
            //      0 1 2 3 4 5 6 7 8 9
            super("F043@@636001103F**F7"); 
            this.patch = p;
            this.ofset = offset;
            this.sysx = patch.sysex;
            this.lyr = sysx[7];
        }
        public byte[] generate(int value) {
            byte[] syse = super.generate(value);
            int dev = syse[2];
            syse[2]=(byte)(dev+0x10);
            syse[5]=(byte)lyr;
            syse[6]=(byte)ofset;
            syse[7]=(byte)63;
            return syse;
        }
    }
}
