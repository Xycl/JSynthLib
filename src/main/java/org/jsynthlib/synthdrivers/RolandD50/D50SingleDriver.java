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
package org.jsynthlib.synthdrivers.RolandD50;

import javax.swing.JOptionPane;

import org.jsynthlib.device.model.XMLPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;

/**
 * Single Voice Patch Driver for Roland D-50
 */
public class D50SingleDriver extends XMLPatchDriver {

//    public D50SingleDriver() {
//        super("Single", "Pascal Collberg");
//        patchSize = D50Constants.PATCH_SIZE;
//        sysexID = "F041**1412";
//        patchNameStart = 392;
//        patchNameSize = 18;
//        deviceIDoffset = 2;
//        checksumStart = D50Constants.SYSEX_HEADER_SIZE;
//        checksumEnd = D50Constants.PATCH_SIZE - D50Constants.SYSEX_FOOTER_SIZE;
//        checksumOffset = D50Constants.PATCH_SIZE - 2;
//        bankNumbers = new String[] {
//                "0-Internal", "1-External" };
//        patchNumbers = new String[8 * 8];
//    }
//
//    @Override
//    public JSLFrame editPatch(Patch p) {
//        return new D50SingleEditor(p);
//    }


    public D50SingleDriver(XmlPatchDriverSpec xmlDriverSpec) {
        super(xmlDriverSpec);
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.getString("D50SingleDriver.0")); //$NON-NLS-1$
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), sb.toString(),
                Messages.getString("D50SingleDriver.1"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append(Messages.getString("D50SingleDriver.2")); //$NON-NLS-1$
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), sb.toString(),
                Messages.getString("D50SingleDriver.3"), JOptionPane.WARNING_MESSAGE); //$NON-NLS-1$
    }

    @Override
    public void sendPatch(Patch p) {
        // Set address to temp area
        p.sysex[5] = 0x0;
        p.sysex[6] = 0x0;
        p.sysex[7] = 0x0;

        sendPatchWorker(p);
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[getPatchSize()];
        System.arraycopy(D50Constants.SYSEX_HEADER, 0, sysex, 0,
                D50Constants.SYSEX_HEADER_SIZE);
        sysex[sysex.length - 1] = (byte) 0xF7;
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, Messages.getString("D50SingleDriver.4")); //$NON-NLS-1$
        calculateChecksum(p);
        return p;
    }

    @Override
    public void setPatchName(Patch p, String name) {
        D50StringHandler.setName(p, name, getPatchNameStart(),
                getPatchNameSize());
        calculateChecksum(p);
    }

    @Override
    public String getPatchName(Patch p) {
        return D50StringHandler.getName(p, getPatchNameStart(),
                getPatchNameSize());
    }
}
