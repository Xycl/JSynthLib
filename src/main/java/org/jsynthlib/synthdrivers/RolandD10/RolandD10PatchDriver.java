/*
 * Copyright 2003 Roger Westerlund
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
/*
 * Created on 2006 aug 9 23:52:11
 *
 * Copyright (C) Roger Westerlund 2005, All rights reserved
 */
package org.jsynthlib.synthdrivers.RolandD10;

import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_PATCH_MEMORY;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_PATCH_TEMP_AREA;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_WRITE_REQUEST;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.OFS_ADDRESS;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.OFS_DEVICE_ID;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.PATCH_NAME;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.PATCH_NAME_SIZE;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.PATCH_SIZE;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.PATCH_WRITE_REQUEST;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.SIZE_HEADER_DT1;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.SIZE_TRAILER;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.RolandD10.message.D10DataSetMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10RequestMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10TransferMessage;

public class RolandD10PatchDriver extends AbstractPatchDriver {

    public RolandD10PatchDriver() {
        super("Patch", "Roger Westerlund");
        sysexID = "F041**1612";

        patchSize = SIZE_HEADER_DT1 + PATCH_SIZE.getIntValue() + SIZE_TRAILER;
        deviceIDoffset = OFS_DEVICE_ID;
        checksumOffset = patchSize - SIZE_TRAILER;
        checksumStart = OFS_ADDRESS;
        checksumEnd = checksumOffset - 1;
        patchNameStart = SIZE_HEADER_DT1 + PATCH_NAME.getIntValue();
        patchNameSize = PATCH_NAME_SIZE.getIntValue();

        bankNumbers = new String[] {};
        patchNumbers = RolandD10Support.createPatchNumbers();
    }

    protected Patch createNewPatch() {
        D10TransferMessage message =
                new D10DataSetMessage(PATCH_SIZE, Entity.ZERO);
        Patch patch = getPatchFactory().createNewPatch(message.getBytes(), this);
        setPatchName(patch, "New Patch");
        calculateChecksum(patch);
        return patch;
    }

    public void requestPatchDump(int bankNum, int patchNumber) {
        D10RequestMessage message =
                new D10RequestMessage(Entity.createFromIntValue(patchNumber)
                        .multiply(PATCH_SIZE).add(BASE_PATCH_MEMORY),
                        PATCH_SIZE);
        send(message.getBytes());
    }

    public void sendPatch(Patch patch) {
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);
        message.setAddress(BASE_PATCH_TEMP_AREA);
        send(message.getBytes());
    }

    public void storePatch(Patch patch, int bankNum, int patchNumber) {
        sendPatch(patch);

        D10DataSetMessage message =
                new D10DataSetMessage(2, BASE_WRITE_REQUEST.add(
                        PATCH_WRITE_REQUEST).getDataValue());
        message.setData(0, (byte) patchNumber);
        message.setData(1, (byte) 0);
        send(message.getBytes());
    }

    public JSLFrame editPatch(Patch patch) {
        return new RolandD10PatchEditor(patch);
    }

    public String getPatchName(Patch patch) {
        return RolandD10Support.trimName(super.getPatchName(patch));
    }
}
