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

import core.JSLFrame;
/**
 * For a single layer from the current performance
 * @author Peter Geirnaert
 */
public class YamahaCS2xLayerDriver extends Driver {
    final static SysexHandler SYS_REQ = new SysexHandler
            // 0  1  2  3     4         5      6  7  
            ("F0 43 20 63 60 *patchnum* 00 F7");
    public YamahaCS2xLayerDriver() {
        super("Single Layer","Peter Geirnaert");
        //          0 1 2 3 4 5 6 7 8 ... 55
        sysexID = "F043**63002D****00"; // all sysex for this driver starts with this.
        deviceIDoffset = 2; 
        bankNumbers = new String[] {"Current Performance"} ;
        patchNumbers = new String[] {"Layer 1","Layer 2","Layer 3","Layer 4"};
        patchSize = 56 ;
        patchNameSize = 0;
        checksumStart = 4;
        checksumEnd = 53;
        checksumOffset = 54;
    }
    public void requestPatchDump(int bankNum, int patchNum) {
        int device=0x20+getDeviceID();
        final SysexHandler.NameValue[] nameValues = {
            new SysexHandler.NameValue("patchnum", patchNum+1)
        };
        send(SYS_REQ.toSysexMessage(device, nameValues));
    }
    public void storePatch(Patch p,int bankNum, int patchNum) {
        byte[] sysex = new byte[56];
        System.arraycopy(p.sysex,0,sysex,0,56);
        sysex[2]=(byte)(getDeviceID()-1);
        sysex[7]=(byte)(patchNum+1);
        DriverUtil.calculateChecksum(sysex, 4, 53, 54);
        send(sysex);
    }
    //**
    public void sendPatch(Patch p) {
        byte[] sysex = new byte[56];
        System.arraycopy(p.sysex,0,sysex,0,56);
        sysex[2]=(byte)(getDeviceID()-1);
        send(sysex);
        
    }
    // */
    protected String getPatchName(Patch p) {
        return patchNumbers[p.sysex[7]-1];
    }
    //**
    public JSLFrame editPatch(Patch p)
    {
     return new YamahaCS2xLayerEditor((Patch)p);
    }
    //*/
}
