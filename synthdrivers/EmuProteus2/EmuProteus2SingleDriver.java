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

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import synthdrivers.RolandD50.D50Constants;
import synthdrivers.RolandD50.D50SingleEditor;
import core.Driver;
import core.JSLFrame;
import core.Patch;
import core.PatchEdit;



public class EmuProteus2SingleDriver extends Driver {

    public static final String  SYSEX_ID           = "F01804**01";
    public static final int     PATCH_NAME_SIZE    = 12;
    public static final byte[]  SYSEX_HEADER       = {
            (byte)0xF0, 0x18, 0x04, 0x00, 0x01
                                                   };
    private static final byte[] DEFAULT_NAME_ARRAY = {
            0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00,
            0x20, 0x00, 0x20, 0x00, 0x20, 0x00,
                                                   };



    public EmuProteus2SingleDriver() {
        super("Single", "Pascal Collberg");
        patchSize = EmuProteus2BankDriver.SINGLE_PATCH_SIZE;
        sysexID = SYSEX_ID;
        patchNameStart = 6;
        patchNameSize = PATCH_NAME_SIZE;
        deviceIDoffset = EmuProteus2BankDriver.DEVICE_ID_OFFSET;
    }



    public void requestPatchDump(int bankNum, int patchNum) {

    }



    protected JSLFrame editPatch(Patch p) {
        return new EmuProteus2SingleEditor(p);
    }



    public void sendPatch(Patch p) {
        sendPatchWorker(p);
    }




    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
        int sum = 0;
        for(int i = start; i <= end; i++) {
            sum += p.sysex[i];
        }
        p.sysex[checksumOffset] = (byte)(sum % 128);
    }



    public String getPatchName(Patch ip) {
        if(patchNameSize == 0)
            return ("-");
        try {
            StringBuffer s = new StringBuffer(new String(((Patch)ip).sysex, patchNameStart, patchNameSize * 2 - 1,
                    "US-ASCII"));
            return s.toString().trim();
        }
        catch(UnsupportedEncodingException ex) {
            return "-";
        }
    }



    public void setPatchName(Patch p, String name) {
        if(patchNameSize == 0) {
            JOptionPane.showMessageDialog(null, "The Driver for this patch does not support Patch Name Editing.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Reset the name
            System.arraycopy(DEFAULT_NAME_ARRAY, 0, p.sysex, patchNameStart, DEFAULT_NAME_ARRAY.length);

            // Set the name. Every second byte shall be 0x00
            byte[] namebytes = name.getBytes("US-ASCII");
            for(int i = 0; i < namebytes.length; i++)
                p.sysex[patchNameStart + (i * 2)] = namebytes[i];

        }
        catch(UnsupportedEncodingException ex) {
            return;
        }
        calculateChecksum(p);
    }



    public Patch createNewPatch() {
        byte[] sysex = new byte[EmuProteus2BankDriver.SINGLE_PATCH_SIZE];
        System.arraycopy(SYSEX_HEADER, 0, sysex, 0, SYSEX_HEADER.length);
        sysex[sysex.length - 1] = (byte)0xF7;
        Patch p = new Patch(sysex, this);
        setPatchName(p, "New Patch");
        calculateChecksum(p);
        return p;
    }
}
