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
import java.io.UnsupportedEncodingException;
//import core.JSLFrame;
/**
 * For complete data of a specific performance.
 * @author Peter Geirnaert
 */
public class YamahaCS2xPerformanceDriver extends Driver {
    private final YamahaCS2xCommonDriver CmnDriver; //= new YamahaCS2xCommonDriver();
    private final YamahaCS2xLayersDriver LyrsDriver; // = new YamahaCS2xLayersDriver();
    // final static SysexHandler SYS_REQ = new SysexHandler("F0 43 *device* 63 50 00 00 F7");
    
    public YamahaCS2xPerformanceDriver(YamahaCS2xCommonDriver common, YamahaCS2xLayersDriver layers) {
        super("Performance", "Peter Geirnaert");
        sysexID = "F0430*630034****00"; // The same as sysexID of YamahaCS2xCommonDriver
        deviceIDoffset = 2; 
        bankNumbers = new String[] {"Current", "Bank 1", "Bank 2"} ;
        patchNumbers = DriverUtil.generateNumbers(1,128,"Perf ##");
        patchSize = 0 ; //(117 or 115)+224 
        patchNameSize = 8;
        patchNameStart = 9;
        this.CmnDriver = common;
        //CmnDriver.setDevice(getDevice());
        this.LyrsDriver = layers;
        //LyrsDriver.setDevice(getDevice());
    }
    
    public void requestPatchDump(int bankNum, int patchNum) {
        CmnDriver.requestPatchDump(bankNum, patchNum);
        CS2x.sleep();  // wait at least 120 milliseconds .
        LyrsDriver.requestPatchDump(bankNum, patchNum);
    }
    //constructor for use with a converter, and because CmnDriver and LyrsDriver  be initialized (copy from RolandSPD11Patch2Driver
    public YamahaCS2xPerformanceDriver() {
        super("Performance", "Peter Geirnaert");
        sysexID = "F0430*630034****00"; // The same as sysexID of YamahaCS2xCommonDriver
        deviceIDoffset = 2; 
        bankNumbers = new String[] {"Curryent", "Banko 1", "Banko 2"} ;
        patchNumbers = DriverUtil.generateNumbers(1,128,"Perf ##");
        patchSize = 0 ; //(117 or 115)+224 
        patchNameSize = 8;
        patchNameStart = 9;
        this.CmnDriver = null;
        this.LyrsDriver = null;
    }
    
    public void sendPatch(Patch p) {
        byte[] lyrsSysex = new byte[224];
        if (p.sysex[6]==0x60) { //the source patch is a Current Performance patch.
            byte[] cmnSysex = new byte[117]; //create array for Current Performance Common data
            System.arraycopy(p.sysex, 0, cmnSysex, 0, 117); //copy the Common sysex data 
            Patch cmnPatch = new Patch(cmnSysex,CmnDriver); //create a Common Patch
            CmnDriver.sendPatch(cmnPatch); //send the Common Patch
            CS2x.sleep();  // wait at least 120 milliseconds .
            System.arraycopy(p.sysex, 117, lyrsSysex, 0, 224); //copy the Layers Sysex
            Patch lyrsPatch = new Patch(lyrsSysex,LyrsDriver); //create a Layers Patch
            LyrsDriver.sendPatch(lyrsPatch); //send the Layers Patch
            
        } else { //the source patch is a Bank Performance patch.
            byte[] cmnSysex = new byte[115]; //the same as above but with cmnPatch.sysex.length 115 for the Common data
            System.arraycopy(p.sysex, 0, cmnSysex, 0, 115);
            Patch cmnPatch = new Patch(cmnSysex,CmnDriver);
            CmnDriver.sendPatch(cmnPatch);
            CS2x.sleep();  // wait at least 120 milliseconds .
            System.arraycopy(p.sysex, 115, lyrsSysex, 0, 224);
            Patch lyrsPatch = new Patch(lyrsSysex,LyrsDriver);
            LyrsDriver.sendPatch(lyrsPatch);
        }
    }
    
    //sends the patch to synth in a given bank and patch number
    public void storePatch(Patch p, int bankNum, int patchNum){
        byte[] lyrsSysex = new byte[224];
        byte[] bCmnSysex = new byte[115]; //the same as above but with cmnPatch.sysex.length 115 for the Common data
        if (p.sysex[6]==0x60) { //the source patch is a Current Performance patch.
            byte[] cmnSysex = new byte[117]; //create array for Current Performance Common data
            System.arraycopy(p.sysex, 0, cmnSysex, 0, 117); //copy the Common sysex data 
            Patch cmnPatch = new Patch(cmnSysex,CmnDriver); //create a Common Patch
            CmnDriver.storePatch(cmnPatch,bankNum,patchNum); //store the Common Patch
            CS2x.sleep();  // wait at least 120 milliseconds .
            System.arraycopy(p.sysex, 117, lyrsSysex, 0, 224); //copy the Layers Sysex
            Patch lyrsPatch = new Patch(lyrsSysex,LyrsDriver); //create a Layers Patch
            LyrsDriver.storePatch(lyrsPatch,bankNum,patchNum); //store the Layers Patch
            
        } 
        else { //the source patch is a Bank Performance patch.
            System.arraycopy(p.sysex, 0, bCmnSysex, 0, 115);
            Patch cmnPatch = new Patch(bCmnSysex,CmnDriver);
            CmnDriver.storePatch(cmnPatch,bankNum,patchNum);
            CS2x.sleep();  // wait at least 120 milliseconds .
            System.arraycopy(p.sysex, 115, lyrsSysex, 0, 224);
            Patch lyrsPatch = new Patch(lyrsSysex,LyrsDriver);
            LyrsDriver.storePatch(lyrsPatch,bankNum,patchNum);
        }
    }
    public boolean supportsPatch(String patchString, byte[] sysex) {
        // check the length of Patch
        if ((sysex.length != 339) && (sysex.length != 341)) // it could be 117 or 115: a common patch only
            return false;
        return super.supportsPatch(patchString, sysex);
    }
    public void calculateChecksum(Patch p) {
        // no calculateChecksum please
    }
    protected String getPatchName(Patch p) {
        String name;
        try { 
            switch (p.sysex[6]) {
                case (byte)0x70 :
                    name = new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII") +" (B1-"+ patchNumbers[p.sysex[7]]+")";
                    return name;
                case (byte)0x78 :
                    name = new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII") +" (B2-"+ patchNumbers[p.sysex[7]]+")";
                    return name;
                default :
                    name = new String(p.sysex, patchNameStart, patchNameSize, "US-ASCII");
                    return name;
            }
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }
    /**
    public JSLFrame editPatch(Patch p)
    {
     return new CS2xPerformanceEditor((Patch)p);
    }
    */
}
