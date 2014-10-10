package org.jsynthlib.synthdrivers.RocktronIntellifex;

import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver class for Rocktron Intellifex S82 patch.
 * @author Klaus Sailer
 * @version $Id$
 */
public class RocktronIntellifexS82Driver extends AbstractPatchDriver {
    /** Header Size */
    private static final int HSIZE = 6;
    /** Data Size */
    private static final int SSIZE = 12;

    private static final byte[] defaultPatchData = {
            0x17, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10,
            0x00 };

    public RocktronIntellifexS82Driver() {
        super("Misc Data (82)", "Klaus Sailer");

        sysexID = "F0000029022B";

        patchSize = HSIZE + SSIZE + 2;

        checksumStart = HSIZE;
        checksumEnd = HSIZE + SSIZE;
        checksumOffset = HSIZE + SSIZE;

        patchNumbers = new String[] {
            "MiscData" };
    }

    @Override
    public String getPatchName(Patch p) {
        return "(MiscData)";
    }

    @Override
    public void setPatchName(Patch p, String name) {
        return;
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int offset) {
        byte sum = 0;
        for (int i = start; i < end; i++) {
            sum ^= sysex[i];
        }
        sysex[offset] = sum;
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 2];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x00;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x29;
        sysex[4] = (byte) 0x02;
        sysex[5] = (byte) 0x2B;
        System.arraycopy(defaultPatchData, 0, sysex, 6, SSIZE);
        sysex[HSIZE + SSIZE + 1] = (byte) 0xF7;

        Patch p = getPatchFactory().createNewPatch(sysex, this);
        calculateChecksum(p);
        return p;
    }

}
