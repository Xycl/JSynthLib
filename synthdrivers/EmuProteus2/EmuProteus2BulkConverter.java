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

import core.Converter;
import core.Patch;



public class EmuProteus2BulkConverter extends Converter {

    public EmuProteus2BulkConverter() {
        super("Bulk Dump Converter", "Pascal Collberg");
        patchSize = EmuProteus2BankDriver.SINGLE_PATCH_SIZE * EmuProteus2BankDriver.BANK_COUNT;
        sysexID = EmuProteus2SingleDriver.SYSEX_ID;
    }



    @Override
    public Patch[] extractPatch(Patch p) {
        Patch[] patches = new Patch[EmuProteus2BankDriver.BANK_COUNT];
        byte[] byteArray = p.getByteArray();
        for(int i = 0; i < EmuProteus2BankDriver.BANK_COUNT; i++) {
            byte[] buf = new byte[EmuProteus2BankDriver.SINGLE_PATCH_SIZE];
            System.arraycopy(byteArray, i * EmuProteus2BankDriver.SINGLE_PATCH_SIZE, buf, 0,
                             EmuProteus2BankDriver.SINGLE_PATCH_SIZE);
            patches[i] = new Patch(buf);
        }
        return patches;
    }

}
