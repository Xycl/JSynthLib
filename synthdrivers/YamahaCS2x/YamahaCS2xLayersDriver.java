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
//import core.JSLFrame;
//import java.io.UnsupportedEncodingException;
/**
 * Driver to send/request all 4 layers of a performance
 * @author Peter Geirnaert
 */
public class YamahaCS2xLayersDriver extends Driver {
    final static SysexHandler SYS_REQ = new SysexHandler
            // 0  1  2  3     4         5      6  7  
            ("F0 43 20 63 *banknum* *patchnum* 00 F7");
    static private YamahaCS2xLayersDriver mInstance;
    public YamahaCS2xLayersDriver(){
    super("Layers", "Peter Geirnaert");
    sysexID = "F0430*63002D****00"; //: The hex header that sysex files of the format this driver supports will have. The program will attempt to match loaded sysex files with the sysexID of a loaded driver. It can be up to 16 bytes and have wildcards in the form of an asterix.
    deviceIDoffset = 2;//was 0: The location of the deviceID in the sysex byte array (@@ or ## in SYS_REQ)
    bankNumbers = new String[] {"Current Performance","Bank1", "Bank2"} ; //: String Array holding names/numbers for all banks. Used for comboBox selection.
    patchNumbers = DriverUtil.generateNumbers(1,128,"Layers ##"); //: String Array holding names/numbers for all patches. Used for comboBox selection.
    patchSize = 56*4 ; //: The size of the patch this Driver supports (or 0 for variable).
    patchNameSize = 0; //: Number of characters in the patch name. (0 if no name)
    // sysexRequestDump ; //: You don't have to use this field when you're overriding the requestPatchDump method.
    }
    static YamahaCS2xLayersDriver getInstance() {
	if (mInstance == null) {
            mInstance = new YamahaCS2xLayersDriver();
        }
        return mInstance;
	}
    public void requestPatchDump(int bankNum, int patchNum) {
        requestPatchDump(bankNum, patchNum, getDeviceID());
    }
    public void requestPatchDump(int bankNum, int patchNum, int deviceID) {
        int i;
        int Bn;
        int Pn;
        switch (bankNum) {
            case 0: //to request curr perf layers
                Bn=0x60; Pn=0x00;
                for (i=1;i<5;i++){
                    final SysexHandler.NameValue[] nameValues = {
                        new SysexHandler.NameValue("patchnum", Pn+i), 
                        new SysexHandler.NameValue("banknum", Bn)
                    };
                    send(SYS_REQ.toSysexMessage(deviceID, nameValues));
                    CS2x.sleep();  // wait at least 120 milliseconds .
                }
                break;
            case 1: //to request layers bank1 perf # patchnum
                Bn=0x71;
                for (i=0;i<4;i++){
                    final SysexHandler.NameValue[] nameValues = {
                        new SysexHandler.NameValue("patchnum", patchNum), 
                        new SysexHandler.NameValue("banknum", Bn+i)
                    };
                    send(SYS_REQ.toSysexMessage(deviceID, nameValues));
                    CS2x.sleep();  // wait at least 120 milliseconds .
                }
                break;
            case 2: // to request layers bank2 perf # patchnum
                Bn=0x79;
                for (i=0;i<4;i++){
                    final SysexHandler.NameValue[] nameValues = {
                        new SysexHandler.NameValue("patchnum", patchNum), 
                        new SysexHandler.NameValue("banknum", Bn+i)
                    };
                    send(SYS_REQ.toSysexMessage(deviceID, nameValues));
                    CS2x.sleep();  // wait at least 120 milliseconds .
                }
                break;
        }
    }
    //when a patch depends on multiple sysexmessages, this should be overridden.
    protected void calculateChecksum(Patch p) {
        calculateChecksum(p,4,53,54);
        calculateChecksum(p,60,109,110);
        calculateChecksum(p,116,165,166);
        calculateChecksum(p,172,221,222);
    }
    byte[] cpLayer = new byte[56]; // buffer for one layer
    //sends the patch to the edit buffer (to the current performance)
    public void sendPatch(Patch p) {
        int i;
        for (i=0;i<4;i++){
            System.arraycopy(p.sysex,(i*56),cpLayer,0,56);
            cpLayer[6]=(byte)0x60; //convert to current perf layer
            cpLayer[7]=(byte)(i+1); //convert to current perf layer
            DriverUtil.calculateChecksum(cpLayer,4,53,54);
            send(cpLayer);
            CS2x.sleep();  // wait at least 120 milliseconds .
        }
    }
    //sends the patch to a given location on the synth
    public void storePatch(Patch p, int bankNum, int patchNum){
        int i;
        switch (bankNum){
            case 0:
                sendPatch(p); 
                break;
            case 1:
                for (i=0;i<4;i++){
                    System.arraycopy(p.sysex,(i*56),cpLayer,0,56);
                    cpLayer[6]=(byte)(0x71+i);
                    cpLayer[7]=(byte)patchNum;
                    DriverUtil.calculateChecksum(cpLayer,4,53,54);
                    send(cpLayer);
                    CS2x.sleep();  // wait at least 120 milliseconds .
                }
                break; 
            case 2:
                for (i=0;i<4;i++){
                    System.arraycopy(p.sysex,(i*56),cpLayer,0,56);
                    cpLayer[6]=(byte)(0x78+i);
                    cpLayer[7]=(byte)patchNum;
                    DriverUtil.calculateChecksum(cpLayer,4,53,54);
                    send(cpLayer);
                    CS2x.sleep();  // wait at least 120 milliseconds .
                }
                break;
        }
    }
    
    /**
     * Gets the name of the patch from the sysex. If the patch uses
     * some weird format or encoding, this needs to be overidden in
     * the particular driver.
     * @see Patch#getName()
     */
    protected String getPatchName(Patch p) {
        int bnk=0;
        int perf=500;
        boolean bank;
        switch (p.sysex[6]) {
            case (byte)0x60 :
                bnk = 0;
                break;
            case (byte)0x71 :
                bnk = 1;
                perf = p.sysex[7];
                break;
            case (byte)0x79 :
                bnk = 2;
                perf = p.sysex[7];
                break;
            }
        if (perf < 499) {
            return  patchNumbers[perf] + " of " + bankNumbers[bnk];
        } else {
            return bankNumbers[bnk];
        }
        
    }
    
}
