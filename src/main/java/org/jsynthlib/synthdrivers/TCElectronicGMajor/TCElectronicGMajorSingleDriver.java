/*
 * Copyright 2005 Ton Holsink
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

package org.jsynthlib.synthdrivers.TCElectronicGMajor;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

/**
 * Single patch driver for TC Electronic G-Major
 */
public class TCElectronicGMajorSingleDriver extends AbstractPatchDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 00 20 1F 00 48 45 *bankNum* *patchNum* F7");

    private final transient Logger log = Logger.getLogger(getClass());

    public TCElectronicGMajorSingleDriver() {
        super("Single", "Ton Holsink <a.j.m.holsink@chello.nl>");
        sysexID = "F000201F004820";

        patchSize = TCElectronicGMajorConst.SINGLE_SIZE;
        patchNameStart = TCElectronicGMajorConst.NAME_OFFSET;
        patchNameSize = TCElectronicGMajorConst.NAME_SIZE;
        deviceIDoffset = 4;
        checksumStart = TCElectronicGMajorConst.CHECKSUMSTART;
        checksumEnd = TCElectronicGMajorConst.CHECKSUMEND;
        checksumOffset = TCElectronicGMajorConst.CHECKSUMOFFSET;
        bankNumbers = new String[] {
                "Factory", "User" };
        patchNumbers = new String[TCElectronicGMajorConst.NUM_PATCH];
        System.arraycopy(
                generateNumbers(1, TCElectronicGMajorConst.NUM_PATCH, "##"), 0,
                patchNumbers, 0, TCElectronicGMajorConst.NUM_PATCH);
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (bankNum == 0) {
            JOptionPane
                    .showMessageDialog(
                            PatchEdit.getInstance(),
                            "You cannot store a patch in the factory bank.\n\nPlease try the user bank...",
                            "Store Patch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        setPatchNum(patchNum);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        p.sysex[7] =
                (byte) TCElectronicGMajorUtil.calcBankNum(bankNum, patchNum);
        p.sysex[8] =
                (byte) TCElectronicGMajorUtil.calcPatchNum(bankNum, patchNum);
        sendPatchWorker(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        setPatchNum(patchNum);
    }

    @Override
    public void sendPatch(Patch p) {
        p.sysex[7] = (byte) 0x00;
        p.sysex[8] = (byte) 0x00;
        sendPatchWorker(p);
    }

    @Override
    public void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int offset) {
        sysex[offset] = TCElectronicGMajorUtil.calcChecksum(sysex, start, end);
    }

    @Override
    public Patch createNewPatch() {
        return (Patch) getPatchFactory().createNewPatch(this,
                TCElectronicGMajorConst.PATCHFILENAME,
                TCElectronicGMajorConst.SINGLE_SIZE);
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new TCElectronicGMajorSingleEditor(p);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        log.debug("BANKNUM: " + bankNum + "PATCHNUM: " + patchNum);
        send(SYS_REQ.toSysexMessage(
                getChannel(),
                new SysexHandler.NameValue("bankNum", TCElectronicGMajorUtil
                        .calcBankNum(bankNum, patchNum)),
                new SysexHandler.NameValue("patchNum", TCElectronicGMajorUtil
                        .calcPatchNum(bankNum, patchNum))));
    }

}
