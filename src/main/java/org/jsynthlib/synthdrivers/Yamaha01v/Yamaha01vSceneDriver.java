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

public class Yamaha01vSceneDriver extends AbstractPatchDriver {

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 43 *ID* 7E 4C 4D 20 20 38 42 33 34 4D *patchNum* F7");

    private final transient Logger log = Logger.getLogger(getClass());

    public Yamaha01vSceneDriver() {
        super("Scene", "Robert Wirski");

        sysexID = "F0430*7E10004C4D2020384233344D";

        patchSize = 2056;
        patchNameStart = 18;
        patchNameSize = 8;
        deviceIDoffset = 2;

        checksumStart = 6;
        checksumOffset = 2054;
        checksumEnd = 2053;

        bankNumbers = new String[] {
            "" };

        patchNumbers = new String[101];
        System.arraycopy(generateNumbers(0, 99, "00"), 0,
                patchNumbers, 0, 100);
        patchNumbers[100] = new String("Edit Buffer");

    }

    /**
     * Sends a patch to a set location on a synth.
     * <p>
     * @see Patch#send(int, int)
     */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (patchNum == 100) {
            patchNum = 127;
        }
        setPatchNum(patchNum);
        setBankNum(0);
        p.sysex[15] = (byte) patchNum; // Location
        calculateChecksum(p);

        sendPatchWorker(p);
    }

    /**
     * Sends a patch to the synth's edit buffer.
     * <p>
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    @Override
    public void sendPatch(Patch p) {
        p.sysex[15] = (byte) 127; // Location (use Edit Buffer)
        calculateChecksum(p);

        sendPatchWorker(p);
    }

    /**
     * @see org.jsynthlib.device.model.AbstractPatchDriver#createNewPatch()
     */
    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[patchSize];
        Patch p;

        try {
            java.io.InputStream fileIn =
                    getClass().getResourceAsStream("01v_Scene.syx");
            fileIn.read(sysex);
            fileIn.close();

        } catch (Exception e) {
            log.warn("Unable to find 01v_Scene.syx.", e);
        }
        ;

        p = getPatchFactory().createNewPatch(sysex, this);
        return p;
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if (patchNum == 100) {
            patchNum = 127;
        }
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "ID", getDeviceID() + 0x1F), new SysexHandler.NameValue(
                "patchNum", patchNum)));
    }
}
