/*
 * JSynthlib -	generic "Fractional Scaling" Single Driver for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
 * =============================================================================
 * @version $Id: DX7FamilyFractionalScalingSingleDriver.java 662 2004-08-13 03:08:21Z hayashi $
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

public class DX7FamilyFractionalScalingSingleDriver extends AbstractPatchDriver {
    byte[] initSysex;
    String[] dxPatchNumbers;
    String[] dxBankNumbers;

    public DX7FamilyFractionalScalingSingleDriver(byte[] initSysex,
            String[] dxPatchNumbers, String[] dxBankNumbers) {
        super("Single Fractional Scaling", "Torsten Tittmann");

        this.initSysex = initSysex;
        this.dxPatchNumbers = dxPatchNumbers;
        this.dxBankNumbers = dxBankNumbers;

        sysexID = "f0430*7e03764c4d2020464b53594520";
        sysexRequestDump =
                new SysexHandler(
                        "f0 43 @@ 7e 4c 4d 20 20  46 4b 53 59 45 20 f7");
        patchNameStart = 0; // !!!! no patchName !!!!
        patchNameSize = 0; // !!!! no patchName !!!!
        deviceIDoffset = 2;
        checksumOffset = 508;
        checksumStart = 6;
        checksumEnd = 507;
        bankNumbers = dxBankNumbers;
        patchNumbers = dxPatchNumbers;
        patchSize = 510;
        trimSize = 510;
        numSysexMsgs = 1;
    }

    public Patch createNewPatch() {
        return getPatchFactory().createNewPatch(initSysex, this);
    }

    public JSLFrame editPatch(Patch p) {
        return new DX7FamilyFractionalScalingEditor(getManufacturerName() + " "
                + getModelName() + " \"" + getPatchType() + "\" Editor",
                (Patch) p);
    }
}
