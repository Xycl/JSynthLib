/*
 * Copyright 2004 Peter Hageus
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

package org.jsynthlib.synthdrivers.AlesisDMPro;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

public class AlesisDMProEffectDriver extends AbstractPatchDriver {

    public AlesisDMProEffectDriver() {
        super("Effect", "Peter Hageus (peter.hageus@comhem.se)");
        sysexID = "F000000E1906*";
        sysexRequestDump = new SysexHandler("F0 00 00 0E 19 07 *patchNum* F7");

        patchSize = 36;
        patchNameStart = 0;
        patchNameSize = 0;

        bankNumbers = new String[] {
            "Internal bank" };
        patchNumbers = new String[64];

        for (int i = 0; i < 64; i++) {
            patchNumbers[i] = "Effect/Kit " + i;
        }

    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        p.sysex[6] = (byte) patchNum;
        sendPatchWorker(p);
        setPatchNum(patchNum);
    }

    @Override
    public void sendPatch(Patch p) {
        // DM Pro editbuffer is named 64
        p.sysex[6] = 64;
        sendPatchWorker(p);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
    }

    @Override
    public Patch createNewPatch() {

        byte[] sysex = new byte[36];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x00;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x0E;
        sysex[4] = (byte) 0x19;
        sysex[5] = (byte) 0x06;
        sysex[6] = (byte) 0x64;

        for (int i = 7; i < 34; i++) {
            sysex[i] = 0;
        }

        sysex[35] = (byte) 0xF7;

        return getPatchFactory().createNewPatch(sysex, this);
        // setPatchName(p,"New Effect");
        // calculateChecksum(p);
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new AlesisDMProEffectEditor(p);
    }

}
