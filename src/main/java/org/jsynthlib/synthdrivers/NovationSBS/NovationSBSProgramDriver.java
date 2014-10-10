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

package org.jsynthlib.synthdrivers.NovationSBS;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;

public class NovationSBSProgramDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public NovationSBSProgramDriver() {
        super("Program", "Robert Wirski");

        sysexID = "F0002029010422";

        patchSize = 116;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 0;

        bankNumbers = new String[] {
            "" };

        patchNumbers = new String[] {
            "Edit Buffer" };
    }

    /**
     * Sends a patch to a set location on a synth.
     * <p>
     * @see Patch#send(int, int)
     */
    public void storePatch(Patch p, int bankNum, int patchNum) {
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
                    getClass().getResourceAsStream("SBS_ProgramInit.syx");
            fileIn.read(sysex);
            fileIn.close();

        } catch (Exception e) {
            log.warn("Unable to find SBS_ProgramInit.syx.");
        }
        ;

        p = getPatchFactory().createNewPatch(sysex, this);
        return p;
    }

    public void calculateChecksum(Patch p) {
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        JOptionPane
                .showMessageDialog(
                        null,
                        "Press the SELECT/MODE button on the device until the SAVE LED is lit.\n"
                                + "If the display do not show SS,\npress '-/SAVE TYPE' buton to get it.\n"
                                + "Press '+/SAVE' button and OK in this dialog.",
                        "Novation SBS Manual Dump",
                        JOptionPane.INFORMATION_MESSAGE);
    }
}
