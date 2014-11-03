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
package org.jsynthlib.synthdrivers.EmuProteus2;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class EmuProteus2SingleDriver extends XMLSingleDriver {

    public EmuProteus2SingleDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec);
    }

    public static final String SYSEX_ID = "F01804**01";
    public static final int PATCH_NAME_SIZE = 12;
    public static final byte[] SYSEX_HEADER = {
            (byte) 0xF0, 0x18, 0x04, 0x00, 0x01 };
    private static final byte[] DEFAULT_NAME_ARRAY = {
            0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20,
            0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00, 0x20, 0x00,
            0x20, 0x00, };

//    public EmuProteus2SingleDriver() {
//        super("Single", "Pascal Collberg");
//        patchSize = EmuProteus2BankDriver.SINGLE_PATCH_SIZE;
//        sysexID = SYSEX_ID;
//        patchNameStart = 6;
//        patchNameSize = PATCH_NAME_SIZE;
//        deviceIDoffset = EmuProteus2BankDriver.DEVICE_ID_OFFSET;
//    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {

    }

//    @Override
//    public JSLFrame editPatch(Patch p) {
//        return new EmuProteus2SingleEditor(p);
//    }

    @Override
    public void sendPatch(Patch p) {
        sendPatchWorker(p);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[checksumOffset] = (byte) (sum % 128);
    }

    @Override
    public String getPatchName(Patch ip) {
        if (patchNameSize == 0) {
            return ("-");
        }
        try {
            StringBuffer s =
                    new StringBuffer(new String(ip.sysex,
                            patchNameStart, patchNameSize * 2 - 1, "US-ASCII"));
            return s.toString().trim();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, String name) {
        if (patchNameSize == 0) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "The Driver for this patch does not support Patch Name Editing.",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            // Reset the name
            System.arraycopy(DEFAULT_NAME_ARRAY, 0, p.sysex, patchNameStart,
                    DEFAULT_NAME_ARRAY.length);

            // Set the name. Every second byte shall be 0x00
            byte[] namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < namebytes.length; i++) {
                p.sysex[patchNameStart + (i * 2)] = namebytes[i];
            }

        } catch (UnsupportedEncodingException ex) {
            return;
        }
        calculateChecksum(p);
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[EmuProteus2BankDriver.SINGLE_PATCH_SIZE];
        System.arraycopy(SYSEX_HEADER, 0, sysex, 0, SYSEX_HEADER.length);
        sysex[sysex.length - 1] = (byte) 0xF7;
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, "New Patch");
        calculateChecksum(p);
        return p;
    }
}
