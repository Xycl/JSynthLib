/*
 * JSynthlib - "Voice" Single Driver for Yamaha DX7 Mark-I
 * =======================================================
 * @version $Id: YamahaDX7VoiceSingleDriver.java 662 2004-08-13 03:08:21Z hayashi $
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

import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyXmlVoicePatchDriver;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class YamahaDX7VoiceSingleDriver extends DX7FamilyXmlVoicePatchDriver {

    public YamahaDX7VoiceSingleDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec);
    }

    @Override
    public Patch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void sendPatch(Patch p) {
        if (isSwOffMemProt()) {
            // switch off memory protection of internal voices
            YamahaDX7SysexHelper.swOffMemProt(this,
                    (byte) (getChannel() + 0x10), (byte) (0x21), (byte) (0x25));
        }

        if (isSpbp()) {
            // make Sys Info available
            YamahaDX7SysexHelper.mkSysInfoAvail(this,
                    (byte) (getChannel() + 0x10));
        }

        sendPatchWorker(p);
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (isSwOffMemProt()) {
            // switch off memory protection of internal/cartridge voices
            YamahaDX7SysexHelper.swOffMemProt(this,
                    (byte) (getChannel() + 0x10), (byte) (bankNum + 0x21),
                    (byte) (bankNum + 0x25));
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
            // place patch in the edit buffer
            sendPatchWorker(p);

            // internal memory or RAM cartridge?
            YamahaDX7SysexHelper.chBank(this, (byte) (getChannel() + 0x10),
                    (byte) (bankNum + 0x25));
            // start storing ... (depress Store button)
            send(YamahaDX7SysexHelper.depressStore
                    .toSysexMessage(getChannel() + 0x10));
            // put patch in the patch number
            YamahaDX7SysexHelper.chPatch(this, (byte) (getChannel() + 0x10),
                    (byte) (patchNum));
            // ... finish storing (release Store button)
            send(YamahaDX7SysexHelper.releaseStore
                    .toSysexMessage(getChannel() + 0x10));
        } else {
            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.RECEIVE_STRING);
            }

            sendPatchWorker(p);

            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.STORE_SINGLE_VOICE_STRING);
            }
        }
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if (isSpbp()) {
            // make Sys Info available
            YamahaDX7SysexHelper.mkSysInfoAvail(this,
                    (byte) (getChannel() + 0x10));
            // internal memory or cartridge?
            YamahaDX7SysexHelper.chBank(this, (byte) (getChannel() + 0x10),
                    (byte) (bankNum + 0x25));
            // which patch do you want
            YamahaDX7SysexHelper.chPatch(this, (byte) (getChannel() + 0x10),
                    (byte) (patchNum));
        } else {
            if (isTipsMsg()) {
                // show Information
                YamahaDX7Strings.dxShowInformation(toString(),
                        YamahaDX7Strings.REQUEST_VOICE_STRING);
            }
        }
    }

    // @Override
    // public JSLFrame editPatch(Patch p) {
    // if (getSwOffMemProtFlag()) {
    // // switch off memory protection of internal/cartridge voices
    // YamahaDX7SysexHelper.swOffMemProt(this,
    // (byte) (getChannel() + 0x10), (byte) (0x21), (byte) (0x25));
    // } else {
    // if (getTipsMsgFlag()) {
    // // show Information
    // YamahaDX7Strings.dxShowInformation(toString(),
    // YamahaDX7Strings.MEMORY_PROTECTION_STRING);
    // }
    // }
    //
    // if (isSpbp()) {
    // // make Sys Info available
    // YamahaDX7SysexHelper.mkSysInfoAvail(this,
    // (byte) (getChannel() + 0x10));
    // } else {
    // if (getTipsMsgFlag()) {
    // // show Information
    // YamahaDX7Strings.dxShowInformation(toString(),
    // YamahaDX7Strings.RECEIVE_STRING);
    // }
    // }
    //
    // return super.editPatch(p);
    // }

}
