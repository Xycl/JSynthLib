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

package org.jsynthlib.synthdrivers.YamahaCS2x;

import java.io.UnsupportedEncodingException;

import javax.sound.midi.SysexMessage;

import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.service.MidiService;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author Peter Geirnaert
 */
public class YamahaCS2xBankDriver extends AbstractBankDriver {
    private final YamahaCS2xCommonDriver commonDriver;
    private final YamahaCS2xLayersDriver layersDriver;

    private final MidiService midiService;

    // private final YamahaCS2xPerformanceDriver perfDriver = new
    // YamahaCS2xPerformanceDriver();
    public YamahaCS2xBankDriver(YamahaCS2xCommonDriver commonDriver,
            YamahaCS2xLayersDriver layersDriver) {
        super("Bank", "Peter Geirnaert", 28, 4); // type, authors, numPatches,

        midiService = JSynthLibInjector.getInstance(MidiService.class);

        // numColums
        sysexID = "F043**042000";
        deviceIDoffset = 2;
        bankNumbers = new String[] {
                "User Bank 1", "User Bank 2" };

        // new String[]{"All performances"};
        patchNumbers = generateNumbers(1, CS2x.numPerfInBank, "");
        singleSize = 0; // 43392 all, 339 (or 341 if user wants to put current
                        // patch in bank)
        singleSysexID = "F0430*630034****00";
        this.commonDriver = commonDriver;
        this.layersDriver = layersDriver;
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        int i;
        // int bank=patchNum;
        for (i = 0; i < CS2x.numPerfInBank; i++) {
            commonDriver.requestPatchDump(bankNum + 1, i);
            CS2x.sleep();
            layersDriver.requestPatchDump(bankNum + 1, i);
            CS2x.sleep();
        }
    }

    // public IPatch[] createPatches(SysexMessage[] msgs){return
    // getPatchFactory().createNewPatch(new
    // byte[] sysex,getDevice());};

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        throw new UnsupportedOperationException("Not supported yet.");

    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPatchStart(int patchNum) {
        int start = (339 * patchNum);
        return start;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 9; // offset of name in patch data
        try {
            StringBuffer s =
                    new StringBuffer(new String(p.sysex, nameStart,
                            8, "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch bank, int patchNum, String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean canHoldPatch(Patch p) {
        return true; // TODO: something serious here please!!
    }

    @Override
    public Patch[] createPatches(SysexMessage[] msgs) {
        byte[] allsysex = midiService.sysexMessagesToByteArray(msgs);

        int i;
        Patch[] patarray = new Patch[CS2x.numPerfInBank];
        for (i = 0; i < CS2x.numPerfInBank; i++) {
            byte[] perf = new byte[339];
            System.arraycopy(allsysex, i * 339, perf, 0, 339);
            patarray[i] = getPatchFactory().createNewPatch(perf, getDevice());
        }
        return patarray;
    }
}
