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

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class TCElectronicGMajorBankDriver extends AbstractBankDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 00 20 1F 00 48 45 *bankNum* *patchNum* F7");

    private final TCElectronicGMajorSingleDriver singleDriver;

    public TCElectronicGMajorBankDriver(
            TCElectronicGMajorSingleDriver singleDriver) {
        super("Bank", "Ton Holsink <a.j.m.holsink@chello.nl>>",
                TCElectronicGMajorConst.NUM_PATCH,
                TCElectronicGMajorConst.NUM_COLUMNS);

        this.singleDriver = singleDriver;
        patchNameSize = TCElectronicGMajorConst.NAME_SIZE;
        bankNumbers = new String[] {
                "Factory", "User" };
        patchNumbers = new String[TCElectronicGMajorConst.NUM_PATCH];
        System.arraycopy(generateNumbers(1,
                TCElectronicGMajorConst.NUM_PATCH, "##"), 0, patchNumbers, 0,
                TCElectronicGMajorConst.NUM_PATCH);
        patchSize =
                TCElectronicGMajorConst.SINGLE_SIZE
                        * TCElectronicGMajorConst.NUM_PATCH;

        sysexID = "F000201F004820";
        singleSysexID = "F000201F004820";
        singleSize = TCElectronicGMajorConst.SINGLE_SIZE;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameOfst =
                singleSize * patchNum + TCElectronicGMajorConst.NAME_OFFSET;
        try {
            return new String(p.sysex, nameOfst, patchNameSize,
                    "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            return "---";
        }
    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        int nameOfst =
                singleSize * patchNum + TCElectronicGMajorConst.NAME_OFFSET;
        byte[] namebytes = name.getBytes();
        for (int i = 0; i < patchNameSize; i++) {
            p.sysex[nameOfst + i] = namebytes[i];
        }
    }

    @Override
    public void calculateChecksum(Patch p) {
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            singleDriver.calculateChecksum(p.sysex, (singleSize * i)
                    + TCElectronicGMajorConst.CHECKSUMSTART, (singleSize * i)
                    + TCElectronicGMajorConst.CHECKSUMEND, (singleSize * i)
                    + TCElectronicGMajorConst.CHECKSUMOFFSET);
        }
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        System.arraycopy(p.sysex, 0, bank.sysex, singleSize
                * patchNum, singleSize);
        singleDriver.calculateChecksum(bank.sysex, (singleSize * patchNum)
                + TCElectronicGMajorConst.CHECKSUMSTART,
                (singleSize * patchNum) + TCElectronicGMajorConst.CHECKSUMEND,
                (singleSize * patchNum)
                        + TCElectronicGMajorConst.CHECKSUMOFFSET);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex = new byte[singleSize];
        System.arraycopy(bank.sysex, singleSize * patchNum, sysex, 0,
                singleSize);

        return getPatchFactory().createNewPatch(sysex, singleDriver);
    }

    @Override
    public BankPatch createNewPatch() {
        byte[] sysex = new byte[singleSize * TCElectronicGMajorConst.NUM_PATCH];
        BankPatch bank = getPatchFactory().newBankPatch(sysex, this);
        Patch p = singleDriver.createNewPatch();
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            putPatch(bank, p, i);
        }
        return bank;
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            singleDriver.requestPatchDump(bankNum, i);
            try {
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (bankNum == 0) {
            JOptionPane
                    .showMessageDialog(
                            PatchEdit.getInstance(),
                            "You cannot store patches in the factory bank.\n\nPlease try the user bank...",
                            "Store Patch", JOptionPane.WARNING_MESSAGE);
            return;
        }

        byte[] sysex = new byte[singleSize];
        Patch tmpPatch = getPatchFactory().createNewPatch(sysex, singleDriver);
        for (int i = 0; i < TCElectronicGMajorConst.NUM_PATCH; i++) {
            System.arraycopy(p.sysex, singleSize * i, tmpPatch.sysex,
                    0, singleSize);

            // TODO: CREATE FACTORYBANK=0 AND USERBANK=1 CONSTANTS
            tmpPatch.sysex[7] =
                    (byte) TCElectronicGMajorUtil.calcBankNum(1, i);
            tmpPatch.sysex[8] =
                    (byte) TCElectronicGMajorUtil.calcPatchNum(1, i);
            sendPatchWorker(tmpPatch);
            // TODO: SLEEPTIME IN FILE OR MENU OPTION IF I KEEP THIS STORE
            // METHOD
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
            }
        }
    }
}
