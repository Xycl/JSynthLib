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

public class Yamaha01vRemoteUserDriver extends AbstractPatchDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 55 *patchNum* F7");

    private final transient Logger log = Logger.getLogger(getClass());

    public Yamaha01vRemoteUserDriver() {
        super("Remote(User Define)", "Robert Wirski");

        sysexID = "F0430*7E0B2A4C4D20203842333455";

        patchSize = 1458;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 2;

        checksumStart = 6;
        checksumOffset = 1456;
        checksumEnd = 1455;

        bankNumbers = new String[] {
            "" };
        patchNumbers = new String[] {
                "BANK 1", "BANK 2", "BANK 3", "BANK 4" };

    }

    /**
     * Sends a patch to a set location on a synth.
     * <p>
     * @see Patch#send(int, int)
     */
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setPatchNum(patchNum);
        p.sysex[15] = (byte) patchNum; // Location
        calculateChecksum(p);

        sendPatchWorker(p);
    }

    /**
     * @see org.jsynthlib.device.model.AbstractPatchDriver#createNewPatch()
     */
    public Patch createNewPatch() {
        byte[] sysex = new byte[patchSize];
        Patch p;

        try {
            java.io.InputStream fileIn =
                    getClass().getResourceAsStream("01v_RemoteUser.syx");
            fileIn.read(sysex);
            fileIn.close();

        } catch (Exception e) {
            log.warn("Unable to find 01v_RemoteUser.syx.", e);
        }
        ;

        p = getPatchFactory().createNewPatch(sysex, this);
        return p;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "ID", getDeviceID() + 0x1F), new SysexHandler.NameValue(
                "patchNum", patchNum)));
    }
}
