/*
 * JSynthlib - "Voice" Bank Driver for DX7 Mark-I
 * =====================================================================
 * @version $Id: YamahaDX7VoiceBankDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
package org.jsynthlib.synthdrivers.YamahaDX7;

import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyXmlVoiceBankDriver;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

public class YamahaDX7VoiceBankDriver extends DX7FamilyXmlVoiceBankDriver {

    public YamahaDX7VoiceBankDriver(XmlBankDriverDefinition driverSpec) {
        super(driverSpec);
    }

    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (isSwOffMemProt()) {
            // switch off memory protection of internal voices
            YamahaDX7SysexHelper.swOffMemProt(this,
                    (byte) (getChannel() + 0x10), (byte) 0x21, (byte) 0x25);
        } else {
            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.MEMORY_PROTECTION_STRING);
            }
        }

        if (isSpbp()) {
            // make Sys Info available
            YamahaDX7SysexHelper.mkSysInfoAvail(this,
                    (byte) (getChannel() + 0x10));
            // switch back to voice mode (internal voices)
            YamahaDX7SysexHelper.chBank(this, (byte) (getChannel() + 0x10),
                    (byte) (0x25));
        } else {
            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.RECEIVE_STRING);
            }
        }

        sendPatchWorker(p);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if (isSpbp()) {
            // make Sys Info available
            YamahaDX7SysexHelper.mkSysInfoAvail(this,
                    (byte) (getChannel() + 0x10));
            // let the DX7 transmit the bank dump
            YamahaDX7SysexHelper.xmitBankDump(this,
                    (byte) (getChannel() + 0x10));
        } else {
            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.REQUEST_VOICE_STRING);
            }
        }
    }
}
