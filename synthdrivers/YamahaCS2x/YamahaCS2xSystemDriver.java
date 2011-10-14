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
/**
 * 
 * @author peter
 */
public class YamahaCS2xSystemDriver extends Driver {
    final static SysexHandler SYS_REQ = new SysexHandler
            ("F0 43 *device* 63 50 00 00 F7");
    public YamahaCS2xSystemDriver(){
    super("System", "Peter Geirnaert");
    sysexID = "F0430*63001A500000"; // all sysex for this driver starts with this.
    deviceIDoffset = 0; 
    bankNumbers = new String[] {"System"} ;
    patchNumbers = new String[] {"CS2x"};
    patchSize = 37 ;
    patchNameSize = 0;
    checksumStart = 4;
    checksumEnd = 34;
    checksumOffset = 35;
    }
    public void requestPatchDump(int bankNum, int patchNum) {
        int dvc = 0x21 + getDeviceID()+bankNum+patchNum;
        final SysexHandler.NameValue[] nameValues = {
            new SysexHandler.NameValue("device", dvc),
            new SysexHandler.NameValue("bankNum", bankNum),
            new SysexHandler.NameValue("patchNum", patchNum)
        };
        send(SYS_REQ.toSysexMessage(getDeviceID(), nameValues));
    }
    public void sendPatch(Patch p) {
        int dvic = 0x00 + getDeviceID(); //getdeviceID() returns value 1~16 = global channel of device
        byte[] sysp = new byte[37];
        System.arraycopy(p.sysex,0,sysp,0,37);
        sysp[2]=(byte)(dvic-1);
        send(sysp);
    }
    //sends the patch to a given synth
    public void storePatch(Patch p, int bankNum, int patchNum){
        sendPatch(p);
    }
    /**
    public JSLFrame editPatch(Patch p)
    {
     return new CS2xSystemEditor((Patch)p);
    }
    */
}
