/*
 * JSynthlib - "Voice" Bank Driver for Yamaha DX7s
 * ===============================================
 * @version $Id: YamahaDX7sVoiceBankDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
package org.jsynthlib.synthdrivers.YamahaDX7s;

import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyVoiceBankDriver;

public class YamahaDX7sVoiceBankDriver extends DX7FamilyVoiceBankDriver {
    public YamahaDX7sVoiceBankDriver() {
        super(YamahaDX7sVoiceConstants.INIT_VOICE,
                YamahaDX7sVoiceConstants.BANK_VOICE_PATCH_NUMBERS,
                YamahaDX7sVoiceConstants.BANK_VOICE_BANK_NUMBERS);
    }

    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if ((((DX7FamilyDevice) (getDevice())).getSwOffMemProtFlag() & 0x01) == 1) {
            // switch off memory protection (internal+cartridge!)
            YamahaDX7sSysexHelpers.swOffMemProt(this,
                    (byte) (getChannel() + 0x10), (byte) (0));
        } else {
            if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
                // show information
                YamahaDX7sStrings.dxShowInformation(toString(),
                        YamahaDX7sStrings.MEMORY_PROTECTION_STRING);
            }
        }

        // choose the desired MIDI Receive block (internal (1-32), internal
        // (33-64))
        YamahaDX7sSysexHelpers.chRcvBlock(this, (byte) (getChannel() + 0x10),
                (byte) (bankNum));

        sendPatchWorker(p);
    };

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        // choose the desired MIDI transmit block (internal (1-32), internal
        // (33-64))
        YamahaDX7sSysexHelpers.chXmitBlock(this, (byte) (getChannel() + 0x10),
                (byte) (bankNum));

        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }
}
