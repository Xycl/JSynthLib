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

import core.BankDriver;
import core.Driver;
import core.Patch;
import core.SysexHandler;

public class EmuProteus2BankDriver extends BankDriver {

    public static final int DEVICE_ID_OFFSET = 3;
    public static final int SINGLE_PATCH_SIZE = 265;
    public static final int PATCH_COUNT = 192;
    public static final int BANK_COUNT = 64;

    public EmuProteus2BankDriver() {
        super("Bank", "Pascal Collberg", BANK_COUNT, 4);
        bankNumbers = new String[] {
                "Preset 1", "Preset 2", "User" };
        patchNumbers = new String[PATCH_COUNT];
        for (int number = 0; number < PATCH_COUNT; number++) {
            patchNumbers[number] =
                    (number < BANK_COUNT * 2 ? "P" : "U")
                            + Integer.toString(number + 1);
        }
        this.sysexRequestDump = new SysexHandler("F0 18 04 @@ 00 7E 7F F7");
        sysexID = "F01804**00";
        singleSysexID = EmuProteus2SingleDriver.SYSEX_ID;
        singleSize = SINGLE_PATCH_SIZE;
        patchSize = BANK_COUNT * singleSize;
        deviceIDoffset = DEVICE_ID_OFFSET;
        patchNameSize = EmuProteus2SingleDriver.PATCH_NAME_SIZE;
    }

    @Override
    protected void putPatch(Patch bank, Patch single, int patchNum) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Patch getPatch(Patch bank, int patchNum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getPatchName(Patch bank, int patchNum) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void setPatchName(Patch bank, int patchNum, String name) {
        // TODO Auto-generated method stub

    }

}
