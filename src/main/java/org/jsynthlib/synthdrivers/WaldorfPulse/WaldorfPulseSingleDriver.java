/*
 *  WaldorfPulseSingleDriver.java
 *
 *  Copyright (c) Scott Shedden, 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package org.jsynthlib.synthdrivers.WaldorfPulse;

import java.io.File;
import java.io.FileInputStream;
import java.text.NumberFormat;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

public class WaldorfPulseSingleDriver extends AbstractPatchDriver {
    private final String[] userPatchNumbers;

    public WaldorfPulseSingleDriver() {
        super("Single", "Scott Shedden");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(2);

        sysexID = "F03E0B******";
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 3;
        checksumStart = 6;
        checksumEnd = 74;
        checksumOffset = 75;
        patchNumbers = new String[100];
        for (int i = 0; i <= 98; i++) {
            patchNumbers[i] = nf.format(i + 1);
        }
        patchNumbers[99] = "rn";
        userPatchNumbers = new String[40];
        for (int i = 0; i <= 39; i++) {
            userPatchNumbers[i] = nf.format(i + 1);
        }
        patchSize = 77;
        sysexRequestDump = new SysexHandler("F0 3E 0B 00 40 *patchNum* F7");
    }

    @Override
    protected void setBankNum(int bankNum) {
    }

    @Override
    protected void setPatchNum(int patchNum) {
        super.setPatchNum(patchNum);
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[77];
        try {
            FileInputStream f =
                    new FileInputStream(new File(
                            "synthdrivers/WaldorfPulse/pulse_default.syx"));
            f.read(sysex);
            f.close();
        } catch (Exception e) {
            // Fallback on hardcoded patch if default is absent
            System.arraycopy(WaldorfPulseInitPatch.initPatch, 0, sysex, 0, 77);
        }
        sysex[3] = 0; // Device ID
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        calculateChecksum(p);
        return p;
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setPatchNum(patchNum);
        p.sysex[4] = (byte) 1;
        p.sysex[5] = (byte) patchNum;
        super.sendPatch(p);
    }

    @Override
    public void sendPatch(Patch p) {
        p.sysex[4] = (byte) 0;
        super.sendPatch(p);
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new WaldorfPulseSingleEditor(p);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[ofs] = (byte) (sum & 0x7f);
    }

    @Override
    public String[] getPatchNumbersForStore() {
        return userPatchNumbers;
    }
}
