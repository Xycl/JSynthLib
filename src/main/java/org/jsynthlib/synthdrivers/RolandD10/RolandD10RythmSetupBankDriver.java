/*
 * Copyright 2006 Roger Westerlund
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
package org.jsynthlib.synthdrivers.RolandD10;

import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_RYTHM_SETUP;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.OFS_ADDRESS;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.OFS_DEVICE_ID;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_COUNT;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.RYTHM_SETUP_SIZE;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.SIZE_HEADER_DT1;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.SIZE_TRAILER;

import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.RolandD10.message.D10DataSetMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10RequestMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10TransferMessage;

public class RolandD10RythmSetupBankDriver extends AbstractBankDriver {

    private final RolandD10RythmSetupDriver rythmSetupDriver;

    /**
     * @param patchType
     * @param authors
     * @param numPatches
     * @param numColumns
     */
    public RolandD10RythmSetupBankDriver(
            RolandD10RythmSetupDriver rythmSetupDriver) {
        super("Rythm Setup Bank", "Roger Westerlund", RYTHM_SETUP_COUNT, 1);
        this.rythmSetupDriver = rythmSetupDriver;
        sysexID = "F041**1612";

        singleSysexID = "F041**1612";
        patchSize =
                2 * (SIZE_HEADER_DT1 + SIZE_TRAILER) + RYTHM_SETUP_COUNT
                        * RYTHM_SETUP_SIZE.getIntValue();
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createRythmSetupNumbers();
    }

    @Override
    public BankPatch createNewPatch() {
        D10TransferMessage message =
                new D10DataSetMessage(patchSize
                        - (SIZE_HEADER_DT1 + SIZE_TRAILER),
                        BASE_RYTHM_SETUP.getDataValue());
        BankPatch bank = getPatchFactory().newBankPatch(message.getBytes(), this);
        for (int patchNumber = 0; patchNumber < RYTHM_SETUP_COUNT; patchNumber++) {
            putPatch(bank, rythmSetupDriver.createNewPatch(), patchNumber);
        }
        return bank;
    }

    @Override
    public void requestPatchDump(int bankNumber, int patchNumber) {
        D10RequestMessage requestMessage =
                new D10RequestMessage(BASE_RYTHM_SETUP, Entity
                        .createFromIntValue(D10Constants.RYTHM_SETUP_COUNT)
                        .multiply(RYTHM_SETUP_SIZE));
        send(requestMessage.getBytes());
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        Patch patch = rythmSetupDriver.createNewPatch();
        RolandD10Support.copyPatchFromBank(patchNum, bank.sysex, patch.sysex);
        return patch;
    }

    @Override
    public void putPatch(Patch bank, Patch patch, int patchNum) {
        RolandD10Support.copyPatchToBank(patchNum, bank.sysex, patch.sysex);
    }

    @Override
    public String getPatchName(Patch bank, int patchNum) {
        return rythmSetupDriver.getPatchName(getPatch(bank, patchNum));
    }

    @Override
    public void setPatchName(Patch bank, int patchNum, String name) {
        // Patch has no name in data.
    }

}
