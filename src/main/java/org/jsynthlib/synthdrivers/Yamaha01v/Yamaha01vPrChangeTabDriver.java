/*
 * Copyright 2006 Robert Wirski
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

package org.jsynthlib.synthdrivers.Yamaha01v;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

public class Yamaha01vPrChangeTabDriver extends AbstractPatchDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 50 20 F7");

    private final transient Logger log = Logger.getLogger(getClass());

    public Yamaha01vPrChangeTabDriver() {
        super("Program Change Table", "Robert Wirski");

        sysexID = "F0430*7E010A4C4D20203842333450";

        patchSize = 146;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 2;

        checksumStart = 6;
        checksumOffset = 144;
        checksumEnd = 143;

        bankNumbers = new String[] {
            "" };
        patchNumbers = new String[] {
            "" };

    }

    /**
     * @see org.jsynthlib.device.model.AbstractPatchDriver#createNewPatch()
     */
    public Patch createNewPatch() {
        byte[] sysex = new byte[patchSize];
        Patch p;

        try {
            java.io.InputStream fileIn =
                    getClass().getResourceAsStream("01v_PrChangeTab.syx");
            fileIn.read(sysex);
            fileIn.close();

        } catch (Exception e) {
            log.warn("Unable to find 01v_PrChangeTab.syx.", e);
        }
        ;

        p = getPatchFactory().createNewPatch(sysex, this);
        return p;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "ID", getDeviceID() + 0x1F)));
    }
}
