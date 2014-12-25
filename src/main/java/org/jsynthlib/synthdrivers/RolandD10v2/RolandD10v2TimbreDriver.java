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
package org.jsynthlib.synthdrivers.RolandD10v2;

import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_TIMBRE_MEMORY;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_TIMBRE_TEMP_AREA;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_WRITE_REQUEST;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.TIMBRE_SIZE;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.TIMBRE_TONE_GROUP;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.TIMBRE_TONE_NUMBER;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.TIMBRE_WRITE_REQUEST;

import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.RolandD10.Entity;
import org.jsynthlib.synthdrivers.RolandD10.message.D10DataSetMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10RequestMessage;
import org.jsynthlib.synthdrivers.RolandD10.message.D10TransferMessage;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class RolandD10v2TimbreDriver extends XMLSingleDriver {

    /**
     * @param driverSpec
     */
    public RolandD10v2TimbreDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec);
    }

    // public RolandD10TimbreDriver() {
    // super("Timbre", "Roger Westerlund");
    // sysexID = "F041**1612";
    //
    // patchSize = SIZE_HEADER_DT1 + TIMBRE_SIZE.getIntValue() + SIZE_TRAILER;
    // deviceIDoffset = OFS_DEVICE_ID;
    // checksumOffset = patchSize - SIZE_TRAILER;
    // checksumStart = OFS_ADDRESS;
    // checksumEnd = checksumOffset - 1;
    // bankNumbers = new String[] {};
    // patchNumbers = RolandD10Support.createPatchNumbers();
    // }

    @Override
    public String getPatchName(Patch patch) {

        // Patch has no name in data so we generate a name.
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);

        byte toneGroup = message.getData(TIMBRE_TONE_GROUP.getIntValue());
        byte toneNumber = message.getData(TIMBRE_TONE_NUMBER.getIntValue());

        return "Tone " + "abir".substring(toneGroup, toneGroup + 1)
                + Integer.toString(toneNumber / 8 + 1)
                + Integer.toString(toneNumber % 8 + 1);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        D10RequestMessage request =
                new D10RequestMessage(Entity.createFromIntValue(patchNum)
                        .multiply(TIMBRE_SIZE).add(BASE_TIMBRE_MEMORY),
                        TIMBRE_SIZE);
        send(request.getBytes());
    }

    @Override
    public Patch createNewPatch() {
        D10TransferMessage message =
                new D10DataSetMessage(TIMBRE_SIZE, Entity.ZERO);
        Patch patch = getPatchFactory().createNewPatch(message.getBytes(), this);
        return patch;
    }

    @Override
    public void sendPatch(Patch patch) {
        D10DataSetMessage message = new D10DataSetMessage(patch.sysex);
        message.setAddress(BASE_TIMBRE_TEMP_AREA);
        send(message.getBytes());
    }

    @Override
    public void storePatch(Patch patch, int bankNum, int patchNum) {
        sendPatch(patch);

        D10DataSetMessage message =
                new D10DataSetMessage(2, BASE_WRITE_REQUEST.add(
                        TIMBRE_WRITE_REQUEST).getDataValue());
        message.setData(0, (byte) patchNum);
        message.setData(1, (byte) 0);
        send(message.getBytes());
    }
}
