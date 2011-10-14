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
/**
 * Constants for YamahaCS2x package.
 * @author Peter Geirnaert
 */
public class CS2x {
    public static void sleep() {
        try { Thread.sleep(220); 
        } catch (Exception e) { 
            ErrorMsg.reportStatus(e);
        }
    }
    static int nibbleMultiplier=64;
    static int numPerfInBank=128;
    //voice categories (23)
    static final String[] Cat = {
        "-- Undefined", "Pf Piano", "Cp Chromatic Percussion", "Or Organ", 
        "Gt Guitar", "Ba Bass", "St String/Orchestral", "En Ensemble",
        "Br Brass", "Rd Reed", "Pi Pipe", "Ld Synth Lead", 
        "Pd Synth Pad", "Fx Synth SFX", "Et Ethnic", "Pc Percussive",
        "Se Sound Effect", "Dr Drums", "Sc Synth Comping", "Vo Vocal", 
        "Co Combination", "Wv Material Wave", "Sq Sequence"
    };
    static final String[] Layers = {"Layer1", "Layer2", "Layer3", "Layer4"};
    //voice banks
    static final String[] Banks = DriverUtil.generateNumbers(0, 20, "Pre##");
    static final String[] ArpTypes = {
        "UpOct1","UpOct2", "UpOct4", "DwOct1", "DwOct2", "DwOct4", "UpDwAOct1", "UpDwAOct2",
        "UpDwAOct4", "UpDwBOct1", "UpDwBOct2", "UpDwBOct4", "RandmOct1", "RandmOct2", "RandmOct4",
        "Techno-A", "Techno-B", "Techno-C", "Techno-D", "DAHouse", "SyncopaA", "SyncopaB", "TekkEcho", 
        "SweepLine", "PulseLine", "BaseLineA", "BaseLineB", "BaseLineC", "BaseLineD", "Ocean", "X-Sweep", 
        "Lamb", "Thips", "HardCore", "Doves", "Chordal", "Orbit", "Velgruv", "MuteLine"
    };
    static final String[] Arp = {
        "off", "On+Spli", "Hold+Spli",
        "on+spli", "Off", "On","Hold"//,"on"
    };
    static final String[] ArpSDivs = {
        "3/8", "1/4", "3/16",
        "1/6", "1/8", "3/32",
        "1/12", "1/16", "1/24", "1/32"
    };
    static final String[] ReverbTypes = {
        "No Effect", //msb=0
        "Hall 1", "Hall 2", //msb=1
        "Room 1", "Room 2", "Room 3", //msb=2
        "Stage 1", "Stage 2", //msb=3
        "Plate", //msb=4
        "White Room", //msb=0x10
        "Tunnel", //msb=0x11
        "Canyon", //msb=0x12
        "Basement" //msb=0x13
    };
    static final byte [][] ReverbTypeBytes = {
        {(byte)0x00, (byte)0x00},
        {(byte)0x01, (byte)0x00},{(byte)0x01, (byte)0x01},
        {(byte)0x02, (byte)0x00},{(byte)0x02, (byte)0x01},{(byte)0x02, (byte)0x02},
        {(byte)0x03, (byte)0x00},{(byte)0x03, (byte)0x01},
        {(byte)0x04, (byte)0x00},
        {(byte)0x10, (byte)0x00},
        {(byte)0x11, (byte)0x00},
        {(byte)0x12, (byte)0x00},
        {(byte)0x13, (byte)0x00}
    };
    /**Todo put right types here */
    static final String[] ChorusTypes = {
        "No Effect", //msb=0
        "Chorus 1", "Chorus 2", "Chorus 3", "Chorus 4",//msb=41
        "Celeste 1", "Celeste 2", "Celeste 3", "Celeste 4",//msb=42
        "Flanger 1", "Flanger 2", "Flanger 3",//msb=43
        "Symphonic", //msb=44
        "Ensemble Detune", //msb=0x57
        "Phaser 1", //msb=0x48
    };
    static final byte [][] ChorusTypeBytes = {
        {(byte)0x00, (byte)0x00},
        {(byte)0x41, (byte)0x00},{(byte)0x41, (byte)0x01},{(byte)0x41, (byte)0x02},{(byte)0x41, (byte)0x08},
        {(byte)0x42, (byte)0x00},{(byte)0x42, (byte)0x01},{(byte)0x42, (byte)0x02},{(byte)0x42, (byte)0x08},
        {(byte)0x43, (byte)0x00},{(byte)0x43, (byte)0x01},{(byte)0x43, (byte)0x08},
        {(byte)0x44, (byte)0x00},
        {(byte)0x57, (byte)0x00},
        {(byte)0x48, (byte)0x00}
    };
    static final String[] VariTypes = {
        "No Effect", 
        "Hall 1", "Hall 2",  
        "Room 1", "Room 2", "Room 3",  
        "Stage 1", "Stage 2",  
        "Plate", "White Room", "Tunnel", "Canyon", "Basement", 
        "Delay L,C,R", "Delay L,R", "Echo", "Cross Delay", 
        "ER 1", "ER 2", 
        "Gate Reverb", "Reverse gate",
        "Karaoke 1", "Karaoke 2", "Karaoke 3",
        "Chorus 1", "Chorus 2", "Chorus 3", "Chorus 4", 
        "Celeste 1", "Celeste 2", "Celeste 3", "Celeste 4", 
        "Flanger 1", "Flanger 2", "Flanger 3", 
        "Symphonic", "Ensemble detune", "Ambience", "Rotary Speaker", "2Way Rotary Speaker", "Tremolo", "Auto Pan", 
        "Phaser 1", "Phaser 2", 
        "Distortion", "Comp+Distortion", 
        "Over Drive", "Amp Simulator", "3Band EQ(Mono)", "2Band EQ(Stereo)", 
        "Auto Wah(LFO)", "Auto Wah+Dist", "Auto Wah+Odrv", 
        "Touch Wah 1", "Touch Wah 2", "Touch Wah+Dist", "Touch Wah+Odrv", 
        "Pitch Change 1", "Pitch Change 2", 
        "Harmonic Enhancer", "Compressor", "Noise Gate", "Voice Cancel", "Thru"
    };
    static final byte[][] VariTypeBytes = {
        {(byte)0x00, (byte)0x00},
        {(byte)0x01, (byte)0x00}, {(byte)0x01, (byte)0x01},
        {(byte)0x02, (byte)0x00}, {(byte)0x02, (byte)0x01}, {(byte)0x02, (byte)0x02}, 
        {(byte)0x03, (byte)0x00}, {(byte)0x03, (byte)0x01}, 
        {(byte)0x04, (byte)0x00}, {(byte)0x10, (byte)0x00}, {(byte)0x11, (byte)0x00}, {(byte)0x12, (byte)0x00}, {(byte)0x13, (byte)0x00},
        {(byte)0x05, (byte)0x00}, {(byte)0x06, (byte)0x00}, {(byte)0x07, (byte)0x00}, {(byte)0x08, (byte)0x00}, 
        {(byte)0x09, (byte)0x00}, {(byte)0x09, (byte)0x01}, 
        {(byte)0x0A, (byte)0x00}, {(byte)0x0B, (byte)0x00}, 
        {(byte)0x14, (byte)0x00}, {(byte)0x14, (byte)0x01}, {(byte)0x14, (byte)0x02},  
        {(byte)0x41, (byte)0x00}, {(byte)0x41, (byte)0x01}, {(byte)0x41, (byte)0x02}, {(byte)0x41, (byte)0x08},  
        {(byte)0x42, (byte)0x00}, {(byte)0x42, (byte)0x01}, {(byte)0x42, (byte)0x02}, {(byte)0x42, (byte)0x08}, 
        {(byte)0x43, (byte)0x00}, {(byte)0x43, (byte)0x01}, {(byte)0x43, (byte)0x08},  
        {(byte)0x44, (byte)0x00}, {(byte)0x57, (byte)0x00}, {(byte)0x58, (byte)0x00}, {(byte)0x45, (byte)0x00}, {(byte)0x56, (byte)0x00}, {(byte)0x46, (byte)0x00}, {(byte)0x47, (byte)0x00},   
        {(byte)0x48, (byte)0x00}, {(byte)0x48, (byte)0x08}, 
        {(byte)0x49, (byte)0x00}, {(byte)0x49, (byte)0x01}, 
        {(byte)0x4A, (byte)0x00}, {(byte)0x4B, (byte)0x00}, {(byte)0x4C, (byte)0x00}, {(byte)0x4D, (byte)0x00}, 
        {(byte)0x4E, (byte)0x00}, {(byte)0x4E, (byte)0x01}, {(byte)0x4E, (byte)0x02}, 
        {(byte)0x52, (byte)0x00}, {(byte)0x52, (byte)0x01}, {(byte)0x52, (byte)0x02}, {(byte)0x52, (byte)0x08},   
        {(byte)0x50, (byte)0x00}, {(byte)0x50, (byte)0x01}, 
        {(byte)0x51, (byte)0x00}, {(byte)0x53, (byte)0x00}, {(byte)0x54, (byte)0x00}, {(byte)0x55, (byte)0x00}, {(byte)0x40, (byte)0x00}
        
    };
    static final String[] Knob8aParams = {
        "L1 off", "L1 Volume", "L1 NoteShift", "L1 Detune", "L1 Pan", "L1 ChorusSend", 
        "L1 ReverbSend", "L1 Pitch", "L1 VelSnsDpth", "L1 VelSnsOfst", "L1 Cutoff", "L1 Resonance", 
        "L1 AEGAtkTime", "L1 AEGDcyTime", "L1 AEGSusLvl", "L1 AEGRelTime", "L1 LFOSpeed", "L1 LFOAMod", 
        "L1 LFOPMod", "L1 LFOFMod", "L1 FEGAtkTime", "L1 FEGDcyTime", "L1 FEGSusLvl", "L1 FEGRelTime", 
        "L1 PEGInitLvl", "L1 PEGAtkTime", "L1 PEGAtkLvl", "L1 PEGDcyTime", "L1 PEGRelTime", "L1 PEGRelLvl",
        "L1 no", "L1 param", 
        "L2 off", "L2 Volume", "L2 NoteShift", "L2 Detune", "L2 Pan", "L2 ChorusSend", 
        "L2 ReverbSend", "L2 Pitch", "L2 VelSnsDpth", "L2 VelSnsOfst", "L2 Cutoff", "L2 Resonance", 
        "L2 AEGAtkTime", "L2 AEGDcyTime", "L2 AEGSusLvl", "L2 AEGRelTime", "L2 LFOSpeed", "L2 LFOAMod", 
        "L2 LFOPMod", "L2 LFOFMod", "L2 FEGAtkTime", "L2 FEGDcyTime", "L2 FEGSusLvl", "L2 FEGRelTime", 
        "L2 PEGInitLvl", "L2 PEGAtkTime", "L2 PEGAtkLvl", "L2 PEGDcyTime", "L2 PEGRelTime", "L2 PEGRelLvl",
        "L2 no", "L2 param", 
        "L3 off", "L3 Volume", "L3 NoteShift", "L3 Detune", "L3 Pan", "L3 ChorusSend", 
        "L3 ReverbSend", "L3 Pitch", "L3 VelSnsDpth", "L3 VelSnsOfst", "L3 Cutoff", "L3 Resonance", 
        "L3 AEGAtkTime", "L3 AEGDcyTime", "L3 AEGSusLvl", "L3 AEGRelTime", "L3 LFOSpeed", "L3 LFOAMod", 
        "L3 LFOPMod", "L3 LFOFMod", "L3 FEGAtkTime", "L3 FEGDcyTime", "L3 FEGSusLvl", "L3 FEGRelTime", 
        "L3 PEGInitLvl", "L3 PEGAtkTime", "L3 PEGAtkLvl", "L3 PEGDcyTime", "L3 PEGRelTime", "L3 PEGRelLvl",
        "L3 no", "L3 param", 
        "L4 off", "L4 Volume", "L4 NoteShift", "L4 Detune", "L4 Pan", "L4 ChorusSend", 
        "L4 ReverbSend", "L4 Pitch", "L4 VelSnsDpth", "L4 VelSnsOfst", "L4 Cutoff", "L4 Resonance", 
        "L4 AEGAtkTime", "L4 AEGDcyTime", "L4 AEGSusLvl", "L4 AEGRelTime", "L4 LFOSpeed", "L4 LFOAMod", 
        "L4 LFOPMod", "L4 LFOFMod", "L4 FEGAtkTime", "L4 FEGDcyTime", "L4 FEGSusLvl", "L4 FEGRelTime", 
        "L4 PEGInitLvl", "L4 PEGAtkTime", "L4 PEGAtkLvl", "L4 PEGDcyTime", "L4 PEGRelTime", "L4 PEGRelLvl",
    };
    static final String[] Knob4Params = {
        "off", "PerfLevel", "ArpgTempo", "ArpgType", "ArpgSubDiv", "MWCutoff", 
        "MWPModDpth", "MWFModDpth", "PBRange", "FCCutoff", "FCFModDpth", "FCVariDpth", 
        "PortaTime", "FEGDcyTime", "AEGDcyTime", "ChoToRev", "VariCntrl", "RevChoSend", 
        "ChorusSend", "ReverbSend", "Pan", "LFOSpeed", "LFOPMod", "VibDelay", 
        "LFOAMod", "LFOFMod", "FEGAtkTime", "FEGSusLvl", "FEGVelSens", "Pitch"
    };
    static final String[] ArpTempos = {
        "MIDI","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59",
        "60","61","62","63","64","65","66","67","68","69","70","71","72","73","74","75","76","77","78","79",
        "80","81","82","83","84","85","86","87","88","89","90","91","92","93","94","95","96","97","98","99",
        "100","101","102","103","104","105","106","107","108","109","110","111","112","113","114","115","116","117","118","119",
        "120","121","122","123","124","125","126","127","128","129","130","131","132","133","134","135","136","137","138","139",
        "140","141","142","143","144","145","146","147","148","149","150","151","152","153","154","155","156","157","158","159",
        "160","161","162","163","164","165","166","167","168","169","170","171","172","173","174","175","176","177","178","179",
        "180","181","182","183","184","185","186","187","188","189","190","191","192","193","194","195","196","197","198","199",
        "200","201","202","203","204","205","206","207","208","209","210","211","212","213","214","215","216","217","218","219",
        "220","221","222","223","224","225","226","227","228","229","230","231","232","233","234","235","236","237","238","239","240"
    };
    static final String[] PortamentoSwitch = {
        "Off All Layers", "On, Off, Off, Off", "Off, On, Off, Off", "On, On, Off, Off", "Off, Off, On, Off", "On, Off, On, Off", "Off, On, On, Off", "On, On, On, Off",
        "Off, Off, Off, On", "On, Off, Off, On", "Off, On, Off, On", "On, On, Off, On", "Off, Off, On, On", "On, Off, On, On", "Off, On, On, On", "On All Layers"
    };
    //voice names (21 banks of 128 voices)
    static final String[] Pre00 = {
        "Dr umTrx1 A", "Sq Sn*Arp A", "Sq Kirmes A", "Sq Clasic A", "Sq Seqnza A", "Sq RytFld A", "Sq B-Luva A", "Sq ObieSq A",
        "Sq Strobe A", "Sq Fly A", "Sq Vivldi A", "Sq Dorian A", "Sc Rezlne A", "Sc Todd A", "Sc Thick A", "Sc Thin A",
        "Sc CutGls A", "Sc Unvrse A", "Sc Crispy A", "Sc FatAne A", "Sc Brassy A", "Sc TheWrksA", "Sc PlsMoD6A", "Sc Minora A",
        "Sc Nble'Q A", "Sc TexSas A", "Sc Quadra A", "Sc DstArp A", "Sc Digitz A", "Sc Odysey A", "Sc Doves A", "Fx Airy A",
        "Fx Pardse A", "Fx Indies A", "Fx CSpace A", "Fx Eerie A", "Fx Ambint A", "Fx Mornng A", "Fx CSphre A", "Fx MagcPd A",
        "Fx Tintpa A", "Fx FlwrArpA", "Fx K.Scpe A", "Fx Orient A", "Fx Omnivr A", "Fx Whelez A", "Ba Baslne A", "Ba Basln2 A",
        "Ba Super A", "Ba Unison A", "Ba SQ-Bas A", "Ba 80sSynBA", "Ba Pulsbs A", "Ba SawBas A", "Ba Fsh303 A", "Ba SawnOf A",
        "Ba CS 01 A", "Ba Mogue A", "Ba LeeDa A", "Ba Howler A", "Ba KickBs A", "Ba Sub A", "Ld Wasp A", "Ld E-no A",
        
        "Ld Fifths A", "Ld TalkBx A", "Ld Micrdt A", "Ld OldMni A", "Ld NuSync A"," Ld Clangr A", "OldRso A","Ld Sync A",
        "Ld Croma A", "Ld Bg'mUp A", "Ld Human A", "Ld BigBob A", "Gt Firstr A", "Gt Sevilla A", "Pf CP80 A", "Pf Woltz1 A",
        "Pf Tina A", "Pf DX-Cls A", "Pf AmbiEp A", "Pf Hip Rds A", "Pf Hard A", "Cp BelEnd A", "Or Compct A", "Or EnsmbleA",
        "Or Gospel A", "Or Drwbrs A", "Or MissU A", "Or GlsOrgnA", "Pd AnglSt A", "Pd IceFld A", "Pd Memory A", "Pd SckWve A",
        "Pd Sprite A", "Pd Trance A", "Pd White A", "Pd AirCls A", "Pd Carpet A", "St Detrot A", "St Baroqe A", "St Octava A",
        "St Jupitr A", "St Strwmn A", "St Strynx A", "Br Jump A", "Br Bronze A", "Br Xpandr A", "Br HansUp A", "Br Prophy A",
        "Br Matrix A", "Se Union A", "Se Vulcan A", "Se WStatn A", "Se Ghost A", "Vo Choir A", "Vo Fragle A", "Co Haendl A",
        "Co WshUha A", "Co Transt A", "Dr KtB900 A", "Dr Kit909 A", "Dr Kit808 A", "Dr HipHop A", "Dr Jungly A", "Dr Elctrc A"
    };
    static final String[] Pre01 = {
        "-", "Sq Sn*Arp B", "Sq Kirmes B", "Sq Clasic B", "-", "Sq RytFld B", "Sq B-Luva B", "Sq ObieSq B",
        "Sq Strobe B", "-", "Sq Vivldi B", "Sq Dorian B", "Sc Rezlne B", "Sc Todd B", "-", "Sc Thin B",
        "Sc CutGls B", "-", "Sc Crispy B", "-", "-", "Sc TheWrksB", "Sc PlsMoD6B", "Sc Minora B",
        "Sc Nble'Q B", "Sc TexSas B", "Sc Quadra B", "Sc DstArp B", "Sc Digitz B", "Sc Odysey B", "-", "Fx Airy B",
        "Fx Pardse B", "Fx Indies B", "Fx CSpace B", "Fx Eerie B", "-", "Fx Mornng B", "Fx CSphre B", "Fx MagcPd B",
        "Fx Tintpa B", "-", "Fx K.Scpe B", "Fx Orient B", "Fx Omnivr B", "Fx Whelez B", "-", "Ba Basln2 B",
        "Ba Super B", "Ba Unison B", "Ba SQ-Bas B", "Ba 80sSynBA", "Ba Pulsbs B", "Ba SawBas B", "-", "Ba SawnOf B",
        "Ba CS 01 B", "Ba Mogue B", "Ba LeeDa B", "Ba Howler B", "Ba KickBs B", "Ba Sub B", "Ld Wasp B", "Ld E-no B",
        
        "Ld Fifths B", "Ld TalkBx B", "-", "Ld OldMni B", "Ld NuSync B"," Ld Clangr B", "OldRso B","-",
        "Ld Croma B", "Ld Bg'mUp B", "Ld Human B", "Ld BigBob B", "Gt Firstr B", "Gt Sevilla B", "-", "Pf Woltz1 B",
        "Pf Tina B", "-", "Pf AmbiEp B", "Pf Hip Rds B", "Pf Hard B", "Cp BelEnd B", "Or Compct B", "Or EnsmbleA",
        "Or Gospel B", "Or Drwbrs B", "Or MissU B", "Or GlsOrgnA", "Pd AnglSt B", "Pd IceFld B", "Pd Memory B", "Pd SckWve B",
        "Pd Sprite B", "Pd Trance B", "Pd White B", "Pd AirCls B", "Pd Carpet B", "St Detrot B", "St Baroqe B", "St Octava B",
        "St Jupitr B", "St Strwmn B", "St Strynx B", "Br Jump B", "Br Bronze B", "Br Xpandr B", "Br HansUp B", "Br Prophy B",
        "Br Matrix B", "-", "Se Vulcan B", "Se WStatn B", "Se Ghost B", "Vo Choir B", "Vo Fragle B", "Co Haendl B",
        "Co WshUha B", "Co Transt B", "Dr KtB900 B", "Dr Kit909 B", "Dr Kit808 B", "Dr HipHop B", "Dr Jungly B", "Dr Elctrc B"
    };
    static final String[] Pre02 = {
        "-", "-", " Sq Kirmes C ", "-", "-", "-", "-", "-",
        "-", "-", "-", "-", " Sc Rezlne C ", "-", "-", " Sc Thin C ",
        "-", "-", "-", "-", "-", "-", " Sc PlsMoD6C ", " Sc Minora C ",
        " Sc Nble'Q C ", " Sc TexSas C ", "-", "-", "-", " Sc Odysey C ", "-", " Fx Airy C ",
        " Fx Pardse C ", "-", " Fx CSpace C ", "-", "-", "-", " Fx CSphre C ", " Fx MagcPd C ",
        "-", "-", " Fx K.Scpe C ", "-", " Fx Omnivr C ", " Fx Whelez C ", "-", "-",
        "-", " Ba Unison C ", "-", " Ba 80sSynBC ", "-", "-", "-", "-",
        "-", "-", " Ba LeeDa C ", " Ba Howler C ", " Ba KickBs C ", " B -- ", "-", "-",
        
        "-", "Ld TalkBx C", "-", "-", "-", "-", "-", "-", 
        "-", "Ld Bg'mUp C", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "Pf HipRds C", "-", "Cp BelEnd C", "-", "-", 
        "-", "Or Drwbrs C", "-", "Or GlsOrgnC", "Pd AnglSt C", "Pd IceFld C", "Pd Memory C", "Pd SckWve C", 
        "Pd Sprite C", "-", "-", "-", "Pd Carpet C", "-", "St Baroqe C", "St Octava C", 
        "St Jupitr C", "-", "St Strynx C", "-", "-", "-", "Br HansUp C", "Br Prophy C", 
        "Br Matrix C", "-", "-", "Se WStatn C", "Se Ghost C", "-", "-", "Co Haendl C", 
        "Co WshUha C", "Co Transt C", "Dr KtB900 C", "Dr Kit9o9 C", "Dr Kit8o8 C", "Dr HipHop C", "Dr Jungly C", "Dr Elctrc C"
    };
    static final String[] Pre03 = {
        "-","-","-","-","-","-","-","-", 
        "-","-","-","-","Sc Rezlne D","-","-","Sc Thin D", 
        "-","-","-","-","-","-","Sc PlsMoD6D","-", 
        "-","Sc TexSas D","-","-","-","Sc Odysey D","-","Fx Airy D", 
        "-","-","-","-","-","-","-","Fx MagcPd D", 
        "-","-","-","-","Fx Omnivr D","-","-","-", 
        "-","-","-","Ba 80sSynBD","-","-","-","-", 
        "-","-","-","-","-","-","-","-",
        
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "Ld Bg'mUp D", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "Cp BelEnd D", "-", "-", 
        "-", "Or Drwbrs D", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "St Jupitr D", "-", "-", "-", "-", "-", "Br HansUp D", "Br Prophy D", 
        "Br Matrix D", "-", "-", "-", "Se Ghost D", "-", "-", "-", 
        "Co WshUha D", "Co Transt D", "Dr KtB900 D", "Dr Kit9o9 D", "Dr Kit8o8 D", "Dr HipHop D", "Dr Jungly D", "-"
    };
    static final String[] Pre04 = {
        "Dr umTrx2 A", "Sq PanSeq A", "Sq MC-Lne1A", "Sq MC-Lne2A", "Sq Suprrp A", "Sq uidgy1 A", "Sq uidgy2 A", "Sq HardOn A", 
        "Sq Pltyps A", "Sq Cyborg A", "Sq uelchy A", "Sq uenza1 A", "Sq uenza2 A", "Sq Erased A", "Sq uareDg A", "Sq Pulse A", 
        "Co Ethno A", "Fx CfiFlt A", "Sq SprkRn A", "Fx SnCrny A", "Fx SwepRn A", "Fx BrekIt A", "Sc Syndim A", "Sc TranCS A", 
        "Sc Source A", "Sc ary", "Sc EurRal A", "Sc OwaOwa A", "Sc Xrayz A", "Pd ResoCt A", "Sc Glassy A", "Sc SynchrdA", 
        "Sc C-Hook A", "Sc raper A", "Sc Stab A", "Sc MonBas A", "Ld UniLed A", "Ld 4Poles A", "Ld Cream A", "Ld ZapLed A", 
        "Ld TheHok A", "Ld Trngle A", "Ld Fuji A", "Ld MegaHk A", "Ld Mondo A", "Ld Marion A", "Ld Seminl A", "Ld PureSn A", 
        "Ld Volfet A Ld", "Ld Empha A", "Ba Fashns A", "Ba Relaxr A", "Ba ssWire A", "Ba Wound A", "Ba Fridge A", "Ba ssSine A", 
        "Ba Saw 1 A", "Ba Saw 2 A", "Ba Plse25 A", "Ba Fuzlne A", "Ba listic A Ba", "Ba 303Wve A", "Ba Howtzr A", "Ba Polrze A",
    
        "Pf 70'sClvA", "Pf Woltz2 A", "Pf DynaRseA", "Pf Major7 A", "Pf SwetFn A", "Cp XyldyneA", "Or ganMtl A", "Or YC45D A", 
        "Or Door A", "Or ganPrc A", "Or ganRve A", "Or Celuli A", "Gt Tele A", "Gt EzaGza A", "Br Obie A", "Br Cross A", 
        "Br assTek A", "Br asHose A", "Br asFase A", "St Swpstr A", "St Vintge A", "St StrngpdA", "St Bartok A", "St Vienna A", 
        "St FltaFe A", "Pd MlkyWy A", "Pd SlvrThwA", "Pd Solinl A", "Pd Spooks A", "Pd Swell A", "Pd VS-Pad A", "Pd Amber A", 
        "Pd Aurora A", "Pd Crystl A", "Pd Haze A", "Pd FSOTkyoA", "Fx Tribal A", "Fx Plnktn A", "Fx Ryza A", "Fx Gaa'99 A", 
        "Fx Lights A", "Fx Morf A", "Fx QSpacs A", "Fx WatrTy A", "Fx Galaxy A", "Fx Triger A", "Fx Reslve A", "Et Santur A", 
        "Se Plasma A", "Se Lunar A", "Se ArpDrpsA", "Se HybriFlA", "Se BetPhl A", "Se Organx A", "Se Varint A", "Se SkyDmn A", 
        "Vo ooDooo A", "Vo xoMono A", "Vo Tehilm A", "Co EthnoSpA", "Co ldHitz A", "Co ShmStr A", "Co DistKk A", "Co EuroKt A" 
    };
    static final String[] Pre05 = {
        "-","Sq PanSeq B","-","Sq MC-Lne2B","Sq Suprrp B","-","-","Sq HardOn B",
        "Sq Pltyps B","Sq Cyborg B","Sq uelchy B","-","Sq uenza2 B","Sq Erased B","Sq uareDg B","-",
        "Co Ethno B","-","Sq SprkRn B","Fx SnCrny B","Fx SwepRn B","Fx BrekIt B","Sc Syndim B","Sc TranCS B",
        "Sc Source B","A Sc ary","Sc EurRal B","Sc OwaOwa B","Sc Xrayz B","-","Sc Glassy B","Sc SynchrdB",
        "Sc C-Hook B","-","Sc Stab B","Sc MonBas B","Ld UniLed B","Ld 4Poles B","-","Ld ZapLed B",
        "Ld TheHok B","Ld Trngle B","Ld Fuji B","Ld MegaHk B","Ld Mondo B","Ld Marion B","Ld Seminl B","Ld PureSn B",
        "Ld Volfet B","-","Ba Fashns B","-","Ba ssWire B","-","Ba Fridge B","Ba ssSine B",
        "-","-","-","-","Ba listic B","Ba 303Wve A","-","Ba Polrze B",
        
        "Pf 70'sClvB", "Pf Woltz2 B", "Pf DynaRseB", "Pf Major7 B", "Pf SwetFn B", "Cp XyldyneB", "Or ganMtl B", "Or YC45D B", 
        "Or Door B", "Or ganPrc B", "Or ganRve B", "Or Celuli B", "Gt Tele B", "Gt EzaGza B", "-", "Br Cross B", 
        "Br assTek B", "Br asHose B", "Br asFase B", "St Swpstr B", "-", "St StrngpdB", "St Bartok B", "St Vienna B", 
        "-", "Pd MlkyWy B", "Pd SlvrThwB", "Pd Solinl B", "Pd Spooks B", "Pd Swell B", "Pd VS-Pad B", "-", 
        "Pd Aurora B", "Pd Crystl B", "Pd Haze B", "Pd FSOTkyoB", "Fx Tribal B", "-", "Fx Ryza B", "Fx Gaa'99 B", 
        "Fx Lights B", "Fx Morf B", "Fx QSpacs B", "Fx WatrTy B", "Fx Galaxy B", "-", "Fx Reslve B", "Et Santur B", 
        "Se Plasma B", "Se Lunar B", "Se ArpDrpsB", "Se HybriFlB", "Se BetPhl B", "Se Organx B", "Se Varint B", "Se SkyDmn B", 
        "Vo ooDooo B", "Vo xoMono B", "Vo Tehilm B", "Co EthnoSpB", "Co ldHitz B", "Co ShmStr B", "Co DistKk B", "Co EuroKt B"
    };
    static final String[] Pre06 = {
        "-", "-", "-", "-", "Sq Suprrp C", "-", "-", "Sq HardOn C", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "Co Ethno C", "-", "-", "Fx SnCrny C", "-", "-", "Sc Syndim C", "Sc TranCS C", 
        "Sc Source C", "B Sc ary", "-", "-", "-", "-", "Sc Glassy C", "Sc SynchrdC", 
        "-", "-", "-", "-", "-", "Ld 4Poles C", "-", "-", 
        "Ld TheHok C", "-", "Ld Fuji C", "Ld MegaHk C", "-", "Ld Marion C", "Ld Seminl C", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-",
        
        "-", "-", "Pf DynaRseC", "Pf Major7 C", "Pf SwetFn C", "-", "Or ganMtl C", "-", 
        "-", "Or ganPrc C", "Or ganRve C", "-", "-", "-", "-", "Br Cross C", 
        "-", "Br asHose C", "-", "St Swpstr C", "-", "-", "-", "-", 
        "-", "Pd MlkyWy C", "-", "Pd Solinl C", "-", "-", "-", "-", 
        "Pd Aurora C", "Pd Crystl C", "-", "-", "Fx Tribal C", "-", "Fx Ryza C", "Fx Gaa'99 C", 
        "Fx Lights C", "Fx Morf C", "Fx QSpacs C", "Fx WatrTy C", "-", "-", "-", "Et Santur C", 
        "-", "Se Lunar C", "-", "Se HybriFlC", "-", "Se Organx C", "-", "-", 
        "Vo ooDooo C", "Vo xoMono C", "-", "Co EthnoSpC", "Co ldHitz C", "Co ShmStr C", "Co DistKk C", "Co EuroKt C"
    };
    static final String[] Pre07 = {
        "-", "-", "-", "-", "Sq Suprrp D", "-", "-", "Sq HardOn D", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "Co Ethno D", "-", "-", "Fx SnCrny D", "-", "-", "-", "Sc TranCS D", 
        "Sc Source D", "Sc ary D", "-", "-", "-", "-", "-", "Sc SynchrdD", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "Ld MegaHk D", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-",
        
        "-", "-", "-", "Pf Major7 D", "-", "-", "Or ganMtl D", "-", 
        "-", "Or ganPrc D", "-", "-", "-", "-", "-", "Br Cross D", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "Fx Gaa'99 D", 
        "-", "-", "Fx QSpacs D", "-", "-", "-", "-", "-", 
        "-", "Se Lunar D", "-", "-", "-", "-", "-", "-", 
        "Vo ooDooo D", "Vo xoMono D", "-", "Co EthnoSpD", "Co ldHitz D", "Co ShmStr D", "-", "Co EuroKt D"
    };
    static final String[] Pre08 = {
        "Pf Wrltza A", "Pf Radio A", "Pf DncePn A", "Cp AirHtz A", "Cp TheBig A", "Cp Vibes A", "Or Drwbrs2A", "Or PeeEss A", 
        "Or PhseOrgA", "Or DistHm A", "Or JacjJz A", "Or Hamond A", "Gt JazAmp A", "Gt Chorus A", "Ba Dubstr A", "Ba Joda-C A", 
        "Ba sPunch A", "Ba Marins A", "Ba Trad A", "Ba Yeah A", "Ba Boing A", "Ba 303Vlo A", "Ba Outlnd A", "Ba belshs A", 
        "St ObeStrgA", "St Phaser A", "St Dark A", "St UFourr A", "St Cars A", "St Arco A", "Br Behind A", "Br OldTnr A", 
        "Br GoaBrs A", "Br Punchy A", "Br Trmpts A", "Br Sectin A", "Br Soft A", "Ld ToClse A", "Ld EntrnseA", "Ld MiniQS A", 
        "Ld Babyln A", "Ld Thermn A", "Ld DstShk A", "Ld AcidLd A", "Ld BabyLd A", "Ld MogLed A", "Ld Raplnd A", "Ld CryBby A", 
        "Ld SneMng A", "Ld TheLog A", "Ld InYFce A", "Pd ChoSwp A", "Pd Synagy A", "Pd Vangls A", "Pd ClubUK A", "Pd Dolfns A", 
        "Pd Expndr A", "Pd MayTrk A", "Pd MonPad A", "Pd Nebula A", "Pd RelAnl A", "Pd Dawn A", "Pd Satrn5 A", "Fx KeslRn A",
        
        "Fx Goldlx A", "Fx Washot A", "Fx Chilin A", "Fx Scvnge A", "Fx Dr Hoo A", "Fx D-Laid A", "Fx Wisppp A", "Fx Comdwn A", 
        "Fx SpceDstA", "Fx Wowlng A", "Fx Chiled A", "Fx Touch A", "Fx DynaHt A", "Fx NoseCt A", "Fx Elctro A", "Fx Winter A", 
        "Fx Magicl A", "Fx JaWble A", "Fx CSubSb A", "Et Shaku A", "Et Koto A", "Et Bali A", "Et Ravi A", "Se Yavin A", 
        "Se SwptWy A", "Se Fitzcr A", "Se HrpGls A", "Se Inosns A", "Se Monaco A", "Se Isoltr A", "Se E-Drpz A", "Se DeadBl A", 
        "Se DblWtr A", "Se Shinng A", "Se Jungle A", "Se DevlCt A", "Se Whsprs A", "Se ColrMe A", "Sc Loaded A", "Sc ATenth A", 
        "Sc Ugly A", "Sc FMInte A", "Sc BigDgi A", "Sc Monkee A", "Sc Arpstc A", "Sc Feelme A", "Sc C-Quor A", "Sc Strinx A", 
        "Sc Busy A", "Vo You A", "Co Split A", "Co Str&Pn A", "Co Fairy A", "Co EP&StrnA", "Co Loop A", "Co Chldrn A", 
        "Co SynE.P A", "Co New808 A", "Sq uirt A", "Sq Einstn A", "Sq Estury A", "Sq Pulshn A", "Dr HipSet A", "Dr LfiAna A"
    };
    static final String[] Pre09 = {
        "Pf Wrltza B", "-", "-", "Cp AirHtz B", "Cp TheBig B", "Cp Vibes B", "Or Drwbrs2B", "Or PeeEss B", 
        "Or PhseOrgB", "Or DistHm B", "Or JacjJz B", "Or Hamond B", "-", "Gt Chorus B", "-", "Ba Joda-C B", 
        "Ba sPunch B", "Ba Marins B", "-", "Ba Yeah B", "Ba Boing B", "Ba 303Vlo B", "-", "Ba belshs B", 
        "-", "St Phaser B", "St Dark B", "St UFourr B", "St Cars B", "St Arco B", "Br Behind B", "Br OldTnr B", 
        "Br GoaBrs B", "Br Punchy B", "Br Trmpts B", "Br Sectin B", "Br Soft B", "Ld ToClse B", "Ld EntrnseB", "Ld MiniQS B", 
        "Ld Babyln B", "-", "Ld DstShk B", "Ld AcidLd B", "Ld BabyLd B", "Ld MogLed B", "Ld Raplnd B", "Ld CryBby B", 
        "Ld SneMng B", "Ld TheLog B", "Ld InYFce B", "Pd ChoSwp B", "Pd Synagy B", "Pd Vangls B", "Pd ClubUK B", "Pd Dolfns B", 
        "Pd Expndr B", "Pd MayTrk B", "Pd MonPad B", "Pd Nebula B", "Pd RelAnl B", "Pd Dawn B", "Pd Satrn5 B", "Fx KeslRn B",
        
        "Fx Goldlx B", "Fx Washot B", "Fx Chilin B", "-", "-", "Fx D-Laid B", "Fx Wisppp B", "Fx Comdwn B", 
        "Fx SpceDstB", "-", "Fx Chiled B", "Fx Touch B", "Fx DynaHt B", "-", "Fx Elctro B", "Fx Winter B", 
        "Fx Magicl B", "Fx JaWble B", "Fx CSubSb B", "Et Shaku B", "Et Koto B", "Et Bali B", "-", "Se Yavin B", 
        "Se SwptWy B", "Se Fitzcr B", "Se HrpGls B", "Se Inosns B", "Se Monaco B", "Se Isoltr B", "-", "Se DeadBl B", 
        "Se DblWtr B", "Se Shinng B", "Se Jungle B", "Se DevlCt B", "Se Whsprs B", "Se ColrMe B", "Sc Loaded B", "Sc ATenth B", 
        "Sc Ugly B", "Sc FMInte B", "Sc BigDgi B", "Sc Monkee B", "Sc Arpstc B", "Sc Feelme B", "-", "-", 
        "-", "Vo You B", "Co Split B", "Co Str&Pn B", "Co Fairy B", "Co EP&StrnB", "Co Loop B", "Co Chldrn B", 
        "Co SynE.P B", "Co New808 B", "Sq uirt B", "Sq Einstn B", "Sq Estury B", "Sq Pulshn B", "Dr HipSet B", "Dr LfiAna B"
    };
    static final String[] Pre10 = {
        "-", "-", "-", "-", "-", "-", "Or Drwbrs2C", "Or PeeEss C", 
        "-", "-", "-", "Or Hamond C", "-", "Gt Chorus C", "-", "-", 
        "Ba sPunch C", "-", "-", "Ba Yeah C", "-", "-", "-", "Ba belshs C", 
        "-", "-", "St Dark C", "St UFourr C", "St Cars C", "St Arco C", "-", "Br OldTnr C", 
        "-", "-", "Br Trmpts C", "Br Sectin C", "Br Soft C", "Ld ToClse C", "-", "-", 
        "Ld Babyln C", "-", "Ld DstShk C", "-", "Ld BabyLd C", "-", "-", "-", 
        "-", "-", "Ld InYFce C", "-", "-", "-", "Pd ClubUK C", "-", 
        "Pd Expndr C", "-", "Pd MonPad C", "Pd Nebula C", "Pd RelAnl C", "Pd Dawn B", "-", "Fx KeslRn C",
        
        "Fx Goldlx C", "Fx Washot C", "-", "-", "-", "-", "-", "-", 
        "Fx SpceDstC", "-", "-", "Fx Touch C", "Fx DynaHt C", "-", "Fx Elctro C", "Fx Winter C", 
        "Fx Magicl C", "-", "-", "Et Shaku C", "-", "-", "-", "Se Yavin C", 
        "-", "-", "Se HrpGls C", "-", "Se Monaco C", "-", "-", "-", 
        "-", "-", "Se Jungle C", "Se DevlCt C", "Se Whsprs C", "-", "-", "-", 
        "-", "-", "Sc BigDgi C", "-", "Sc Arpstc C", "Sc Feelme C", "-", "-", 
        "-", "-", "Co Split C", "Co Str&Pn C", "-", "-", "Co Loop C", "Co Chldrn C", 
        "-", "Co New808 C", "-", "Sq Einstn C", "-", "-", "Dr HipSet C", "-"
    };
    static final String[] Pre11 = {
        "-", "-", "-", "-", "-", "-", "Or Drwbrs2D", "Or PeeEss D", 
        "-", "-", "-", "-", "-", "Gt Chorus D", "-", "-", 
        "-", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "St Arco D", "-", "-", 
        "-", "-", "-", "Br Sectin D", "Br Soft D", "-", "-", "-", 
        "-", "-", "Ld DstShk D", "-", "-", "-", "-", "-", 
        "-", "-", "-", "-", "-", "-", "Pd ClubUK D", "-", 
        "-", "-", "Pd MonPad D", "-", "-", "-", "-", "Fx KeslRn D",
        
        "-", "Fx Washot D", "-", "-", "-", "-", "-", "-", 
        "-", "-", "-", "Fx Touch D", "-", "-", "Fx Elctro D", "Fx Winter D", 
        "Fx Magicl D", "-", "-", "-", "-", "-", "-", "-", 
        "-", "-", "Se HrpGls D", "-", "-", "-", "-", "-", 
        "-", "-", "Se Jungle D", "-", "Se Whsprs D", "-", "-", "-", 
        "-", "-", "-", "-", "Sc Arpstc D", "Sc Feelme D", "-", "-", 
        "-", "-", "Co Split D", "-", "-", "-", "Co Loop D", "-", 
        "-", "Co New808 D", "-", "Sq Einstn D", "-", "-", "Dr HipSet D", "-"
    };
    static final String[] Pre12 = {
        "Dr TechKit", "Dr ElctrKit", "Dr JnglKit", "Dr HipHpKit", "Dr 8o8Kit", "Dr 9o9Kit", "Dr Kiks", "Dr TeckFx", "Dr Chop091", "Dr Chop095", "Dr Chop096", "Dr Chop102", "Dr Chop103", "Dr Chop107", "Dr Chop110", "Dr Chop113", "Dr Chop134", "Dr Chop135", "Dr Chop136", "Dr Chop139", "Dr Chop144", "Dr Chop160", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-",//1~64
        "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-"//65~128:
        
    };
    static final String[] Pre13 = {
        "Sq MinstryA", "Sq I'mRedyA", "Sq TekScenA", "Sq Boodoo A", "Sq RoulettA", "Sq LongDayA", "Sq Walrus A", "Sq Sync303A", "Sq MDMA A", "Sq ChordalA", "Sq HardMrfA", "Ld Faaaat A", "Ld TekkHokA", "Ld TechLedA", "Ld Europe A", "Ld RaveLinA", "Ld HouseY!A", "Ld Earth A", "Ld CS15 A", "Ld MiniSawA", "Ld BluGrenA", "Ld RapLineA", "Ld TransinA", "Ld DJ Zap A", "Ld XChoir A", "Ld HppysynA", "Ld TekuitrA", "Ld Goa A", "Ld Noiz+RzA", "Ld VoclGymA", "Ld Astral A", "Ld SharpLdA", "Ld VanDrivA", "Ld BohkyohA", "Ld PizzsynA", "Ld InsomniA", "Ld SynStelA", "Ba Trancy A", "Ba Taurus A", "Ba Punchy!A", "Ba 101BassA", "Ba 3o3WaveA", "Ba OctaBssA", "Ba Harmo A", "Ba Ramp A", "Ba FatSnapA", "Ba CoastalA", "Ba Squash A", "Ba Cosmic A", "Ba FuzzLinA", "Ba GigabssA", "Ba SubBs A", "Ba Lately A", "Ba ElectrnA", "Ba StrollrA", "Ba Syko A", "Ba Bomber A", "Ba Warp A", "Ba Rider A", "Ba HousOrgA", "Ba Monty A", "Ba GutRncHA", "Ba DropBssA", "Ba Bad AssA",//1~64
        "Sc PopCompA", "Sc Analog A", "Sc LipstckA", "Sc MegaClvA", "Sc DigiWahA", "Sc SynChrdA", "Sc CHook A", "Sc Larynx A", "Sc Scary A", "Sc AtkDistA", "Sc WahDcy A", "Sc BitcompA", "Sc TortureA", "Sc M25 A", "Sc BigTuneA", "Pd Nevada A", "Pd Ocean A", "Pd RedWineA", "Pd FatSwepA", "Pd Shadow A", "Pd DreampdA", "Pd FlutePdA", "Pd HitoLo A", "Pd Venus A", "Pd BannersA", "Pd MetroplA", "Pd WavePadA", "Pd Awaken A", "Pd 4AD A", "Pd TherminA", "Pd Pan A", "Pd Athens A", "Pd AbandonA", "Pd BellTllA", "Pd AirBlwrA", "Fx EmeraldA", "Fx EtherelA", "Fx AutoSawA", "Fx Birds A", "Fx GeomtryA", "Fx DifusinA", "Fx Fx BellA", "Fx Beauty A", "Fx Ghosts A", "Fx BreathyA", "Fx TranspdA", "Fx SweepPdA", "Fx HlyMthrA", "Fx SparkleA", "Fx Kiseki A", "Fx DespairA", "Fx BeBach A", "Fx MoreIceA", "Fx ChemiclA", "Fx AyersRkA", "Fx BlueBokA", "Fx MoonwedA", "Fx Alobar A", "Fx KemstryA", "Fx Abyss A", "Fx DandeliA", "Fx OrchHtsA", "Fx AcidHtsA", "Fx FunkyHtA"//65~128:
    };
    static final String[] Pre14 = {
        "Sq MinstryB", "Sq I'mRedyB", "Sq TekScenB", "Sq Boodoo B", "Sq RoulettB", "Sq LongDayB", "Sq Walrus B", "Sq Sync303B", "Sq MDMA B", "Sq ChordalB", "-", "Ld Faaaat B", "Ld TekkHokB", "-", "Ld Europe B", "Ld RaveLinB", "Ld HouseY!B", "Ld Earth B", "-", "-", "-", "-", "-", "Ld DJ Zap B", "Ld XChoir B", "Ld HppysynB", "Ld TekuitrB", "Ld Goa B", "Ld Noiz+RzB", "Ld VoclGymB", "Ld Astral B", "Ld SharpLdB", "-", "Ld BohkyohB", "Ld PizzsynB", "Ld InsomniB", "Ld SynStelB", "Ba Trancy B", "Ba Taurus B", "Ba Punchy!B", "-", "-", "Ba OctaBssB", "Ba Harmo B", "Ba Ramp B", "Ba FatSnapB", "Ba CoastalB", "Ba Squash B", "Ba Cosmic B", "-", "Ba GigabssB", "Ba SubBs B", "Ba Lately B", "Ba ElectrnB", "Ba StrollrB", "Ba Syko B", "Ba Bomber B", "Ba Warp B", "Ba Rider B", "Ba HousOrgB", "-", "Ba GutRncHB", "Ba DropBssB", "-",//1~64
        "Sc PopCompB", "Sc Analog B", "-", "Sc MegaClvB", "-", "Sc SynChrdB", "Sc CHook B", "Sc Larynx B", "Sc Scary B", "Sc AtkDistB", "Sc WahDcy B", "Sc BitcompB", "Sc TortureB", "Sc M25 B", "Sc BigTuneB", "Pd Nevada B", "Pd Ocean B", "Pd RedWineB", "Pd FatSwepB", "-", "Pd DreampdB", "Pd FlutePdB", "Pd HitoLo B", "Pd Venus B", "Pd BannersB", "Pd MetroplB", "Pd WavePadB", "Pd Awaken B", "Pd 4AD B", "Pd TherminB", "Pd Pan B", "Pd Athens B", "Pd AbandonB", "Pd BellTllB", "Pd AirBlwrB", "Fx EmeraldB", "Fx EtherelB", "Fx AutoSawB", "Fx Birds B", "-", "Fx DifusinB", "Fx Fx BellB", "Fx Beauty B", "Fx Ghosts B", "Fx BreathyB", "Fx TranspdB", "Fx SweepPdB", "Fx HlyMthrB", "Fx SparkleB", "Fx Kiseki B", "Fx DespairB", "Fx BeBach B", "Fx MoreIceB", "Fx ChemiclB", "Fx AyersRkB", "Fx BlueBokB", "-", "Fx Alobar B", "Fx KemstryB", "Fx Abyss B", "Fx DandeliB", "Fx OrchHtsB", "Fx AcidHtsB", "-"//65~128:
    };
    static final String[] Pre15 = {
        "Sq MinstryC", "Sq I'mRedyC", "-", "-", "Sq RoulettC", "Sq LongDayC", "Sq Walrus C", "-", "Sq MDMA C", "-", "-", "-", "Ld TekkHokC", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "Ld DJ Zap C", "Ld XChoir C", "-", "Ld TekuitrC", "Ld Goa C", "-", "Ld VoclGymC", "Ld Astral C", "Ld SharpLdC", "-", "Ld BohkyohC", "Ld PizzsynC", "-", "-", "-", "-", "Ba Punchy!C", "-", "-", "-", "-", "-", "Ba FatSnapC", "Ba CoastalC", "Ba Squash C", "Ba Cosmic C", "-", "-", "-", "-", "-", "-", "-", "Ba Bomber C", "-", "-", "-", "-", "-", "Ba DropBssC", "-",//1~64
        "Sc PopCompC", "Sc Analog C", "-", "-", "-", "Sc SynChrdC", "-", "Sc Larynx C", "Sc Scary C", "Sc AtkDistC", "Sc WahDcy C", "Sc BitcompC", "Sc TortureC", "Sc M25 C", "Sc BigTuneC", "Pd Nevada C", "Pd Ocean C", "Pd RedWineC", "-", "-", "Pd DreampdC", "Pd FlutePdC", "Pd HitoLo C", "Pd Venus C", "Pd BannersC", "-", "Pd WavePadC", "Pd Awaken C", "Pd 4AD C", "Pd TherminC", "Pd Pan C", "Pd Athens C", "Pd AbandonC", "Pd BellTllC", "-", "-", "Fx EtherelC", "Fx AutoSawC", "Fx Birds C", "-", "-", "Fx Fx BellC", "Fx Beauty", "-", "Fx BreathyC", "Fx TranspdC", "Fx SweepPdC", "-", "Fx SparkleC", "Fx Kiseki C", "Fx DespairC", "-", "Fx MoreIceC", "-", "-", "Fx BlueBokC", "-", "-", "-", "Fx Abyss C", "-", "Fx OrchHtsC", "Fx AcidHtsC", "-"//65~128:
    };
    static final String[] Pre16 = {
        "Sq MinstryD", "-", "-", "-", "-", "Sq LongDayD", "-", "-", "Sq MDMA D", "-", "-", "-", "Ld TekkHokD", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "Ld Goa D", "-", "-", "Ld Astral D", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "Ba CoastalD", "-", "Ba Cosmic D", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-",//1~64
        "-", "-", "-", "-", "-", "Sc SynChrdD", "-", "-", "Sc Scary D", "Sc AtkDistD", "Sc WahDcy D", "-", "-", "-", "Sc BigTuneD", "-", "Pd Ocean D", "Pd RedWineD", "-", "-", "Pd DreampdD", "-", "Pd HitoLo D", "Pd Venus D", "-", "-", "-", "-", "-", "-", "Pd Pan D", "-", "Pd AbandonD", "-", "-", "-", "Fx EtherelD", "Fx AutoSawD", "Fx Birds D", "-", "-", "-", "C", "-", "Fx BreathyD", "Fx TranspdD", "Fx SweepPdD", "-", "-", "Fx Kiseki D", "Fx DespairD", "-", "Fx MoreIceD", "-", "-", "Fx BlueBokD", "-", "-", "-", "Fx Abyss D", "-", "Fx OrchHtsD", "Fx AcidHtsD", "-"//65~128:
    };
    static final String[] Pre17 = {
        "Pf ConcertA", "Pf Wired A", "Pf CP99 A", "Pf LoFi A", "Pf Grnd/EPA", "Pf Pno&StrA", "Pf BottledA", "Pf Old OneA", "Pf ChorsEPA", "Pf DX EP1 A", "Pf WurltzrA", "Pf WurliolA", "Pf ClavintA", "Pf RezClavA", "Or Jimmy A", "Or EmersonA", "Or Full B A", "Or GospelBA", "Or Swish A", "Or Full A", "Or Ranks A", "Or SuperVxA", "Or Spyral A", "Or DeepHozA", "Or ThaDorzA", "St Stryng A", "St SlStrgsA", "St Drama A", "St DarkStrA", "St Sweet A", "St ObieStrA", "St JupitrsA", "St Simple A", "Br HornSctA", "Br Jump! A", "Br FatBrssA", "Br Syn&BrsA", "Br CS WarmA", "Br AnaswllA", "Br OBSX A", "Br FatAnneA", "Br Odyssy A", "Rd BlozHrpA", "Gt Steel A", "Gt RondStlA", "Gt ChoStrtA", "Gt RokNstyA", "Gt Stack A", "Ba FretlssA", "Ba 6StringA", "Ba Jaco A", "Ba Slap A", "Ba UprightA", "Ba BoogiOnA", "Ba DXSuperA", "Ba Kickit A", "Cp KasimodA", "Cp Lounge A", "Cp BellZeeA", "Cp DoraDorA", "Et Masala A", "Et NuTriblA", "Se EmergnyA", "Se AirForcA",//1~64
        "Se TeckFX A", "Se Bad ManA", "Se SpceWrpA", "Se Lego A", "Se MagnetoA", "Se CyberSEA", "Se RtmScrcA", "Se SeaOrglA", "Se AtlantsA", "Se PlnktnsA", "Se 13FridyA", "Se ScienceA", "Se Sewer A", "Se Swamp A", "Se Radium A", "Se HipNsysA", "Se Cryton A", "Se TubChokA", "Se Dark SEA", "Se Hammer A", "Sq Patty A", "Sq ReflectA", "Sq DrumatcA", "Sq 7thRndmA", "Sq X2-SwepA", "Sq TranSeqA", "Sq AcidossA", "Sq MadKeefA", "Sq FirewrxA", "Sq Engels A", "Sq N.Y. 99A", "Sq SuprArpA", "Sq Hard OnA", "Sq Kirmes1A", "Sq ClasiclA", "Sq DRUG A", "Sq X-SweepA", "Sq Nexxus A", "Sq KatarssA", "Sq Rave A", "Sq AN VeloA", "Sq Memes A", "Fx Beauty D", "Sq MoondotA", "Co EuroKitA", "Co WishU'rA", "Co DanceTcA", "Co DigPhasA", "Co Ready A", "Co BecomngA", "Co RockMkrA", "Co TechRckA", "Co Vibe-zmA", "Co NewFunkA", "Co FunkyBrA", "Co OrchstrA", "Co Mr.MuteA", "Co WWinds A", "Dr 9o9 KitA", "Dr 8o8 KitA", "Dr 4 by 4 A", "Dr ElecKitA", "Dr B900KitA", "Dr SessionA"//65~128:
    };
    static final String[] Pre18 = {
        "-", "-", "-", "Pf LoFi B", "Pf Grnd/EPB", "Pf Pno&StrB", "Pf BottledB", "Pf Old OneB", "Pf ChorsEPB", "Pf DX EP1 B", "-", "Pf WurliolB", "-", "Pf RezClavB", "-", "Or EmersonB", "Or Full B B", "Or GospelBB", "Or Swish B", "Or Full B", "Or Ranks B", "Or SuperVxB", "Or Spyral B", "-", "Or ThaDorzB", "St Stryng B", "St SlStrgsB", "St Drama B", "St DarkStrB", "St Sweet B", "St ObieStrB", "St JupitrsB", "St Simple B", "Br HornSctB", "Br Jump! B", "Br FatBrssB", "Br Syn&BrsB", "Br CS WarmB", "Br AnaswllB", "Br OBSX B", "-", "Br Odyssy B", "Rd BlozHrpB", "Gt Steel B", "-", "Gt ChoStrtB", "Gt RokNstyB", "Gt Stack B", "Ba FretlssB", "Ba 6StringB", "Ba Jaco B", "Ba Slap B", "-", "Ba BoogiOnB", "-", "Ba Kickit B", "Cp KasimodB", "Cp Lounge B", "Cp BellZeeB", "Cp DoraDorB", "-", "Et NuTriblB", "Se EmergnyB", "Se AirForcB",//1~64
        "-", "Se Bad ManB", "Se SpceWrpB", "Se Lego B", "Se MagnetoB", "Se CyberSEB", "-", "Se SeaOrglB", "Se AtlantsB", "Se PlnktnsB", "Se 13FridyB", "Se ScienceB", "Se Sewer B", "Se Swamp B", "Se Radium B", "Se HipNsysB", "Se Cryton B", "Se TubChokB", "Se Dark SEB", "Se Hammer B", "Sq Patty B", "Sq ReflectB", "Sq DrumatcB", "Sq 7thRndmB", "Sq X2-SwepB", "Sq TranSeqB", "Sq AcidossB", "Sq MadKeefB", "Sq FirewrxB", "Sq Engels B", "Sq N.Y. 99B", "Sq SuprArpB", "Sq Hard OnB", "Sq Kirmes1B", "Sq ClasiclB", "Sq DRUG B", "Sq X-SweepB", "Sq Nexxus B", "-", "Sq Rave B", "-", "Sq Memes B", "Sq Messa A", "Sq MoondotB", "Co EuroKitB", "Co WishU'rB", "Co DanceTcB", "Co DigPhasB", "Co Ready B", "Co BecomngB", "Co RockMkrB", "Co TechRckB", "Co Vibe-zmB", "Co NewFunkB", "Co FunkyBrB", "Co OrchstrB", "Co Mr.MuteB", "Co WWinds B", "Dr 9o9 KitB", "Dr 8o8 KitB", "Dr 4 by 4 B", "Dr ElecKitB", "Dr B900KitB", "Dr SessionB"//65~128:
    };
    static final String[] Pre19 = {
        "-", "-", "-", "-", "Pf Grnd/EPC", "Pf Pno&StrC", "-", "-", "Pf ChorsEPC", "-", "-", "-", "-", "-", "-", "Or EmersonC", "Or Full B C", "Or GospelBC", "-", "Or Full C", "Or Ranks C", "Or SuperVxC", "Or Spyral C", "-", "Or ThaDorzC", "-", "-", "St Drama C", "St DarkStrC", "-", "St ObieStrC", "St JupitrsC", "-", "Br HornSctC", "-", "Br FatBrssC", "Br Syn&BrsC", "-", "Br AnaswllC", "-", "-", "Br Odyssy C", "-", "-", "-", "-", "Gt RokNstyC", "Gt Stack C", "-", "-", "-", "-", "-", "-", "-", "Ba Kickit C", "-", "Cp Lounge C", "Cp BellZeeC", "Cp DoraDorC", "-", "Et NuTriblC", "Se EmergnyC", "Se AirForcC",//1~64
        "-", "Se Bad ManC", "Se SpceWrpC", "Se Lego C", "-", "Se CyberSEC", "-", "-", "Se AtlantsC", "-", "Se 13FridyC", "-", "-", "Se Swamp C", "Se Radium C", "Se HipNsysC", "-", "-", "Se Dark SEC", "Se Hammer C", "Sq Patty C", "Sq ReflectC", "Sq DrumatcC", "-", "-", "Sq TranSeqC", "Sq AcidossC", "Sq MadKeefC", "Sq FirewrxC", "-", "-", "Sq SuprArpC", "Sq Hard OnC", "Sq Kirmes1C", "-", "Sq DRUG C", "-", "Sq Nexxus C", "-", "Sq Rave C", "-", "Sq Memes C", "Sq Messa B", "-", "Co EuroKitC", "Co WishU'rC", "Co DanceTcC", "Co DigPhasC", "Co Ready C", "Co BecomngC", "Co RockMkrC", "Co TechRckC", "Co Vibe-zmC", "Co NewFunkC", "Co FunkyBrC", "Co OrchstrC", "Co Mr.MuteC", "Co WWinds C", "Dr 9o9 KitC", "Dr 8o8 KitC", "Dr 4 by 4 C", "Dr ElecKitC", "Dr B900KitC", "-"//65~128:
    };
    static final String[] Pre20 = {
        "-", "-", "-", "-", "-", "Pf Pno&StrD", "-", "-", "-", "-", "-", "-", "-", "-", "-", "Or EmersonD", "Or Full B D", "Or GospelBD", "-", "Or Full D", "Or Ranks D", "Or SuperVxD", "-", "-", "Or ThaDorzD", "-", "-", "-", "-", "-", "-", "St JupitrsD", "-", "Br HornSctD", "-", "Br FatBrssD", "Br Syn&BrsD", "-", "Br AnaswllD", "-", "-", "Br Odyssy D", "-", "-", "-", "-", "Gt RokNstyD", "Gt Stack D", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "Cp BellZeeD", "-", "-", "Et NuTriblD", "Se EmergnyD", "-",//1~64
        "-", "Se Bad ManD", "Se SpceWrpD", "Se Lego D", "-", "Se CyberSED", "-", "-", "Se AtlantsD", "-", "Se 13FridyD", "-", "-", "Se Swamp D", "-", "-", "-", "-", "-", "Se Hammer D", "Sq Patty D", "Sq ReflectD", "-", "-", "-", "-", "Sq AcidossD", "Sq MadKeefD", "Sq FirewrxD", "-", "-", "Sq SuprArpD", "Sq Hard OnD", "-", "-", "-", "-", "-", "-", "Sq Rave D", "-", "-", "-", "-", "Co EuroKitD", "Co WishU'rD", "Co DanceTcD", "Co DigPhasD", "Co Ready D", "Co BecomngD", "Co RockMkrD", "Co TechRckD", "Co Vibe-zmD", "-", "Co FunkyBrD", "Co OrchstrD", "-", "Co WWinds D", "Dr 9o9 KitD", "Dr 8o8 KitD", "Dr 4 by 4 D", "Dr ElecKitD", "Dr B900KitD", "-"//65~128:
    };
    static final String[][] VoiceNames = {Pre00, Pre01, Pre02, Pre03, Pre04, Pre05, Pre06, Pre07, Pre08, Pre09, Pre10, Pre11, Pre12, Pre13, Pre14, Pre15, Pre16, Pre17, Pre18, Pre19, Pre20}; 

}
