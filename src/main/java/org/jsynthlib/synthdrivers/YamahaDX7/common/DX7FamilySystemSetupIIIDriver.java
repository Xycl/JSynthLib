/*
 * JSynthlib -	generic "System Setup III" Driver for Yamaha DX7 Family
 *		(used by TX802)
 * ====================================================================
 * @version $Id: DX7FamilySystemSetupIIIDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
package org.jsynthlib.synthdrivers.YamahaDX7.common;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

public class DX7FamilySystemSetupIIIDriver extends AbstractPatchDriver {
    byte[] initSysex;
    String[] dxPatchNumbers;
    String[] dxBankNumbers;

    public DX7FamilySystemSetupIIIDriver(byte[] initSysex,
            String[] dxPatchNumbers, String[] dxBankNumbers) {
        super("System Setup", "Torsten Tittmann");

        this.initSysex = initSysex;
        this.dxPatchNumbers = dxPatchNumbers;
        this.dxBankNumbers = dxBankNumbers;

        sysexID = "F0430*7E02114C4D2020383935325320";
        patchNameStart = 0; // !!!! no patchName !!!!
        patchNameSize = 0; // !!!! no patchName !!!!
        deviceIDoffset = 2;
        checksumOffset = 279;
        checksumStart = 6;
        checksumEnd = 278;
        patchNumbers = dxPatchNumbers;
        bankNumbers = dxBankNumbers;
        patchSize = 281;
        trimSize = 281;
        numSysexMsgs = 1;
        sysexRequestDump =
                new SysexHandler("F0 43 @@ 7E 4C 4D 20 20 38 39 35 32 53 20 F7");
    }

    public Patch createNewPatch() {
        return getPatchFactory().createNewPatch(initSysex, getDevice());
    }

    public JSLFrame editPatch(Patch p) {
        return new DX7FamilySystemSetupIIIEditor(getManufacturerName() + " "
                + getModelName() + " \"" + getPatchType() + "\" Editor",
                (Patch) p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }
}
