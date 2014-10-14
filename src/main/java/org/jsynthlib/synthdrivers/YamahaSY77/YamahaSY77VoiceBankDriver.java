/*
 * JSynthlib - Bank Driver for Yamaha SY77
 * ========================================
 * @version $Id$
 * @author  Vladimir Avdonin
 *
 * Copyright (C) 2011 vldmrrr@yahoo.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package org.jsynthlib.synthdrivers.YamahaSY77;

import javax.sound.midi.SysexMessage;

import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

public class YamahaSY77VoiceBankDriver extends AbstractBankDriver {
    private static final int numPatches = 16;
    private final YamahaSY77VoiceDriver mVoiceD;

    public YamahaSY77VoiceBankDriver(YamahaSY77VoiceDriver iVoiceD) {
        super("Voice bank", YamahaSY77Device.author, numPatches, 1);
        mVoiceD = iVoiceD;
        singleSysexID = YamahaSY77VoiceDriver.SYSEXID;
        sysexID = "SY77 Voice Bank";
        numSysexMsgs = 16;
        patchSize = 0;
        deviceIDoffset = YamahaSY77VoiceDriver.DEV_ID_OFS;
        bankNumbers = YamahaSY77VoiceDriver.SINGLE_VOICE_BANK_NUMBERS;
        patchNumbers = YamahaSY77VoiceDriver.SINGLE_VOICE_PATCH_NUMBERS;
    }

    @Override
    public BankPatch createNewPatch() {
        BankPatch bank = getPatchFactory().newBankPatch(this, sysexID);
        int i;
        for (i = 0; i < getNumPatches(); i++) {
            bank.putSingle(mVoiceD.createNewPatch(), i);
        }
        return bank;
    }

    @Override
    public void putPatch(Patch bank, Patch patch, int patchNum) {
        ((BankPatch) bank).putSingle(patch, patchNum);
    }

    @Override
    public void deletePatch(Patch single, int patchNum) {
        setPatchName(single, patchNum, "          ");
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        return ((BankPatch) bank).getSingle(patchNum);
    }

    @Override
    public String getPatchName(Patch bank, int patchNum) {
        return ((BankPatch) bank).getSingle(patchNum).getName();
    }

    @Override
    public void setPatchName(Patch bank, int patchNum, String name) {
        ((BankPatch) bank).getSingle(patchNum).setName(name);
    }

    @Override
    public void storePatch(Patch bank, int bankNum, int patchNum) {
        int i;
        for (i = 0; i < getNumPatches(); i++) {
            mVoiceD.storePatch(((BankPatch) bank).getSingle(i), bankNum, i);
        }
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        int i;
        for (i = 0; i < getNumPatches(); i++) {
            mVoiceD.requestPatchDump(bankNum, i);
            try {
                Thread.sleep(400);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public Patch[] createPatches(SysexMessage[] msgs) {
        BankPatch bank = getPatchFactory().newBankPatch(this, sysexID);
        int i, j;
        int n = msgs.length;
        if (n > getNumPatches()) {
            n = getNumPatches();
        }
        for (i = 0; i < n; i++) {
            bank.putSingle(mVoiceD.createPatch(msgs[i].getMessage()), i);
        }
        Patch[] res = new Patch[1];
        res[0] = bank;
        return res;
    }

    @Override
    public boolean supportsPatch(String patchString, byte[] sysex) {
        try {
            String id = new String(sysex, "ISO-8859-1");
            return id.equals(sysexID);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public java.lang.String getPatchName(Patch bank) {
        return ((BankPatch) bank).getBankName();
    }

    @Override
    public void setPatchName(Patch bank, String name) {
        ((BankPatch) bank).setBankName(name);
    }
}
