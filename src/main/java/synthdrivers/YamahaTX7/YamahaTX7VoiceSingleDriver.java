/*
 * JSynthlib - "Voice" Single Driver for Yamaha TX7
 * ================================================
 * @version $Id: YamahaTX7VoiceSingleDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
package synthdrivers.YamahaTX7;

import org.jsynthlib.view.desktop.JSLFrame;

import synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import synthdrivers.YamahaDX7.common.DX7FamilyVoiceSingleDriver;
import core.Patch;

public class YamahaTX7VoiceSingleDriver extends DX7FamilyVoiceSingleDriver {
    public YamahaTX7VoiceSingleDriver() {
        super(YamahaTX7VoiceConstants.INIT_VOICE,
                YamahaTX7VoiceConstants.SINGLE_VOICE_PATCH_NUMBERS,
                YamahaTX7VoiceConstants.SINGLE_VOICE_BANK_NUMBERS);
    }

    public Patch createNewPatch() {
        return super.createNewPatch();
    }

    public JSLFrame editPatch(Patch p) {
        return super.editPatch(p);
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        sendPatchWorker(p);

        if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1)
            // show Information
            YamahaTX7Strings.dxShowInformation(toString(),
                    YamahaTX7Strings.MEMORY_PROTECTION_STRING);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        setPatchNum(patchNum);

        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }
}
