package synthdrivers.RocktronIntellifex;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.gui.desktop.JSLFrame;

import core.Driver;
import core.Patch;
import core.SysexHandler;

/**
 * Driver class for Rocktron Intellifex preset patch.
 * @author Klaus Sailer
 * @version $Id$
 */
public class RocktronIntellifexPresetDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 6;
    /** Data Size */
    private static final int SSIZE = 200;

    private static final byte[] defaultPatchData = {
            0x00, 0x00, 0x00, 0x00, 0x2E, 0x00, 0x2E, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x20, 0x00, 0x4E, 0x00, 0x45, 0x00, 0x57, 0x00, 0x20, 0x00, 0x50,
            0x00, 0x41, 0x00, 0x54, 0x00, 0x43, 0x00, 0x48, 0x00, 0x20, 0x00,
            0x20, 0x00, 0x20, 0x00, 0x79, 0x00, 0x00, 0x00, 0x3A, 0x00, 0x00,
            0x00, 0x79, 0x00, 0x01, 0x00, 0x3A, 0x00, 0x00, 0x00, 0x79, 0x00,
            0x02, 0x00, 0x3A, 0x00, 0x00, 0x00, 0x79, 0x00, 0x03, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x79, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x79, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00, 0x00, 0x79, 0x00, 0x06,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x79, 0x00, 0x07, 0x00, 0x00, 0x00,
            0x00, 0x00 };

    public RocktronIntellifexPresetDriver() {
        super("Single", "Klaus Sailer");

        sysexID = "F00000290228";

        patchSize = HSIZE + SSIZE + 2;
        patchNameStart = 116;
        patchNameSize = 13;

        checksumStart = HSIZE;
        checksumEnd = HSIZE + SSIZE;
        checksumOffset = HSIZE + SSIZE;

        patchNumbers = new String[] {
            "Preset" };
    }

    protected String getPatchName(Patch p) {
        try {
            String name = new String();
            for (int i = 0; i < patchNameSize; i++) {
                String s =
                        new String(p.sysex, patchNameStart + 2 * i, 1,
                                "US-ASCII");
                name = name.concat(s);
            }
            return name;
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    protected void setPatchName(Patch p, String name) {
        while (name.length() < patchNameSize)
            name = name + " ";

        byte[] namebytes = new byte[patchNameSize];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchNameStart + 2 * i] = namebytes[i];
                p.sysex[patchNameStart + 2 * i + 1] = 0x00;
            }
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    protected void calculateChecksum(Patch patch, int start, int end, int offset) {
        byte sum = 0;
        for (int i = start; i < end; i++) {
            sum ^= patch.sysex[i];
        }
        patch.sysex[offset] = sum;
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 2];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x00;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x29;
        sysex[4] = (byte) 0x02;
        sysex[5] = (byte) 0x28;
        System.arraycopy(defaultPatchData, 0, sysex, 6, SSIZE);
        sysex[HSIZE + SSIZE + 1] = (byte) 0xF7;

        Patch p = new Patch(sysex, this);
        setPatchName(p, " NEW PATCH ");
        calculateChecksum(p);
        return p;
    }

    public JSLFrame editPatch(Patch p) {
        return new RocktronIntellifexPresetEditor(p);
    }

}
