/*
 * JSynthlib - "Micro Tuning" Bank Driver for Yamaha DX7-II
 * =====================================================================
 * @version $Id: YamahaDX7IIMicroTuningBankDriver.java 662 2004-08-13 03:08:21Z hayashi $
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7II;

import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyMicroTuningBankDriver;

public class YamahaDX7IIMicroTuningBankDriver extends
        DX7FamilyMicroTuningBankDriver {
    public YamahaDX7IIMicroTuningBankDriver() {
        super(
                YamahaDX7IIMicroTuningConstants.INIT_MICRO_TUNING,
                YamahaDX7IIMicroTuningConstants.BANK_MICRO_TUNING_PATCH_NUMBERS,
                YamahaDX7IIMicroTuningConstants.BANK_MICRO_TUNING_BANK_NUMBERS);
    }

    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
            // show Information
            YamahaDX7IIStrings.dxShowInformation(toString(),
                    YamahaDX7IIStrings.MICRO_TUNING_CARTRIDGE_STRING);
        }

        if ((((DX7FamilyDevice) (getDevice())).getSwOffMemProtFlag() & 0x01) == 1) {
            // switch off memory protection (internal+cartridge!)
            YamahaDX7IISysexHelpers.swOffMemProt(this,
                    (byte) (getChannel() + 0x10), (byte) 0);
        } else {
            if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
                // show Information
                YamahaDX7IIStrings.dxShowInformation(toString(),
                        YamahaDX7IIStrings.MEMORY_PROTECTION_STRING);
            }
        }

        sendPatchWorker(p);
    };

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
            // show Information
            YamahaDX7IIStrings.dxShowInformation(toString(),
                    YamahaDX7IIStrings.MICRO_TUNING_CARTRIDGE_STRING);
        }

        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }

}
