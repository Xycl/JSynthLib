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
import java.io.*;
import java.io.UnsupportedEncodingException;
import core.JSLFrame;
/**
 * YamahaCS2xCommonDriver needs to know about the type (current, bank1 or bank2) and performance number
 * @author peter
 */
public class YamahaCS2xCommonDriver extends Driver {
    final static SysexHandler SYS_REQ = new SysexHandler
            // 0  1  2  3     4         5      6  7  
            ("F0 43 20 63 *banknum* *patchnum* *part* F7");
    static private YamahaCS2xCommonDriver cInstance;
    
    public YamahaCS2xCommonDriver(){
    super("Common", "Peter Geirnaert");
    sysexID = "F0430*630034****00"; //: The hex header that sysex files of the format this driver supports will have. The program will attempt to match loaded sysex files with the sysexID of a loaded driver. It can be up to 16 bytes and have wildcards in the form of an asterix.
    deviceIDoffset = 2;//: The location of the getDeviceID in the sysex byte array (@@ or ## in SYS_REQ)
    bankNumbers = new String[] {"Current Performance","Bank1", "Bank2"} ; //: String Array holding names/numbers for all banks. Used for comboBox selection.
    patchNumbers = DriverUtil.generateNumbers(1,128,"u##"); //: String Array holding names/numbers for all patches. Used for comboBox selection.
    patchSize = 0 ; //: The size of the patch this Driver supports (or 0 for variable).
    patchNameSize = 8; //: Number of characters in the patch name. (0 if no name)
    patchNameStart = 9;
    // sysexRequestDump ; //: You don't have to use this field when you're overriding the requestPatchDump method.
    }
    static YamahaCS2xCommonDriver getInstance() {
	if (cInstance == null) {
            cInstance = new YamahaCS2xCommonDriver();
        }
        return cInstance;
	}

    public void requestPatchDump(int bankNum, int patchNum) {
        int pN = 0x00;
        int bN=0x60;
        switch (bankNum) {
            case 0: bN = 0x60; pN = 0x00;
            break;
            case 1: bN = 0x70; pN = patchNum;
            break;
            case 2: bN = 0x78; pN = patchNum;
            break;
        }
        int pT = 0x00;
        final SysexHandler.NameValue[] nameValues = {
            new SysexHandler.NameValue("patchnum", pN), //= the patchNum from comboselection and
            new SysexHandler.NameValue("banknum", bN),   //= the bankNum were chosen by the user
            new SysexHandler.NameValue("part", pT)
        };
        send(SYS_REQ.toSysexMessage(getDeviceID(), nameValues));
        CS2x.sleep();  // wait at least 120 milliseconds .
        pT = 0x40;
        final SysexHandler.NameValue[] nameValues2 = {
            new SysexHandler.NameValue("patchnum", pN), //= the patchNum from comboselection and
            new SysexHandler.NameValue("banknum", bN),   //= the bankNum were chosen by the user
            new SysexHandler.NameValue("part", pT)
        };
        send(SYS_REQ.toSysexMessage(getDeviceID(), nameValues2));
        CS2x.sleep();  // wait at least 120 milliseconds .
        pT = 0x60;
        final SysexHandler.NameValue[] nameValues3 = {
            new SysexHandler.NameValue("patchnum", pN), //= the patchNum from comboselection and
            new SysexHandler.NameValue("banknum", bN),   //= the bankNum were chosen by the user
            new SysexHandler.NameValue("part", pT)
        };
        send(SYS_REQ.toSysexMessage(getDeviceID(), nameValues3));
    }
    //public IPatch createPatch(byte[] sysex) {
        
    //}
    //**
    protected void calculateChecksum(Patch p) {
        switch (p.sysex.length) {
                case 117:
                    calculateChecksum(p,2,60,61);
                    calculateChecksum(p,65,94,95);
                    calculateChecksum(p,99,114,115);
                    break;
                case 115 :
                    calculateChecksum(p,2,60,61);
                    calculateChecksum(p,65,94,95);
                    calculateChecksum(p,99,112,113);
                    break;
        }
    }
    
    byte[] p1=new byte[63]; //buffer for message 1
    byte[] p2c=new byte[34]; //buffer for message 2 of a current performance
    byte[] p2u=new byte[32]; //buffer for message 2 of a user performance
    byte[] p3=new byte[20]; //buffer for message 3
    //sends the patch to the edit buffer (to the current performance)
    public void sendPatch(Patch p) {
        System.arraycopy(p.sysex,0,p1,0,63); //copy sysex message 1 to buffer
        p1[6]=(byte)0x60; //make it a current performance
        p1[7]=(byte)0x00;
        DriverUtil.calculateChecksum(p1,4,60,61);
        send(p1);
        CS2x.sleep();  // wait at least 120 milliseconds .
        if (p.sysex.length == 117 && p.sysex[6] == (byte)0x60) { //the source is a 'current performance' 
            System.arraycopy(p.sysex,63,p2c,0,34); //so copy part2 to p2c
            System.arraycopy(p.sysex,97,p3,0,20);//and part3 to p3
            DriverUtil.calculateChecksum(p2c,4,31,32);
            DriverUtil.calculateChecksum(p3,4,17,18);
            send(p2c); // send part 2
            CS2x.sleep();  // wait at least 120 milliseconds .
            send(p3); //send and leave the if-else switch
        }
        else if (p.sysex.length == 115) { //source patch is of type 'bank1 or bank2 common'
            System.arraycopy(p.sysex,63,p2c,0,32); //copy part2
            p2c[6]=(byte)0x60; //convert from user performance common to current performance common
            p2c[7]=(byte)0x00; //now this byte means 'common' instead of 'patchNum'
            p2c[30]=(byte)0x28;//insert vari on Layer Rev send default
            p2c[31]=(byte)0x00;//insert vari on Layer Chor send default
            p2c[33]=(byte)0xF7;//end of exclusive message
            DriverUtil.calculateChecksum(p2c,4,31,32);
            send(p2c); // send part 2
            CS2x.sleep();  // wait at least 120 milliseconds .
            System.arraycopy(p.sysex,95,p3,0,20); //copy part3
            p3[6]=(byte)0x60;
            p3[7]=(byte)0x00;
            DriverUtil.calculateChecksum(p3,4,17,18); //checksum message 3
            send(p3); // send part 3
        }
    }
    //sends the patch to a given location on the synth
    public void storePatch(Patch p, int bankNum, int patchNum){
        switch (bankNum){
            case 0: // user puts patch in current performance (=edit buffer)
                sendPatch(p);
            case 1: // user puts patch in bank 1
                System.arraycopy(p.sysex,0,p1,0,63);
                p1[6]=(byte)0x70; //bank 1
                p1[7]=(byte)patchNum;
                DriverUtil.calculateChecksum(p1,4,60,61);
                if (p.sysex.length == 117 && p.sysex[6] == (byte)0x60) { //the source is a 'current performance' 
                    System.arraycopy(p.sysex,63,p2u,0,32); //so copy only the first 32 bytes of part2!!
                    System.arraycopy(p.sysex,97,p3,0,20);//and part3 to p3, starting from byte 97
                    p2u[6]=(byte)0x70; //bank 1
                    p2u[7]=(byte)patchNum;
                    DriverUtil.calculateChecksum(p2u,4,29,30);
                }
                else if (p.sysex.length == 115) { //source patch is of type 'bank1 or bank2 common'
                    System.arraycopy(p.sysex,63,p2u,0,32); //copy part2
                    System.arraycopy(p.sysex,95,p3,0,20); //copy part3
                    p2u[6]=(byte)0x70; //bank 1
                    p2u[7]=(byte)patchNum;
                    DriverUtil.calculateChecksum(p2u,4,31,32);
                }
                p3[6]=(byte)0x70; //bank 1
                p3[7]=(byte)patchNum;
                DriverUtil.calculateChecksum(p3,4,17,18); //checksum message 3
                send(p1);
                CS2x.sleep();  // wait at least 120 milliseconds .
                send(p2u); // send part 2
                CS2x.sleep();  // wait at least 120 milliseconds
                send(p3);
        }
        
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
    public Patch createNewPatch() {
        byte[] sysex = new byte [117];
        InputStream fileIn= getClass().getResourceAsStream("InitPerfCommon.syx");
        try {
            fileIn.read(sysex);
            fileIn.close();
            return new Patch(sysex, this);
        } catch (Exception e) {
            ErrorMsg.reportError("Error","Unable to find InitPerfCommon.syx",e);
            return null;
        }
    }
    public boolean supportsPatch(String patchString, byte[] sysex) {
        // check the length of Patch
        if ((sysex.length != 117) && (sysex.length != 115))
            return false;
        return super.supportsPatch(patchString, sysex);
        
    }
    public JSLFrame editPatch(Patch p)
    {
     return new YamahaCS2xCommonEditor((Patch)p);
    }

}
