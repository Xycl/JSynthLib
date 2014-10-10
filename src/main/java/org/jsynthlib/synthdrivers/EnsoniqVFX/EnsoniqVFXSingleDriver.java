package org.jsynthlib.synthdrivers.EnsoniqVFX;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Single driver for VFX. Nybble Hi 4 bytes are transmitted first.
 * @author <a href="mailto:dqueffeulou@free.fr">Denis Queffeulou</a> (created 17
 *         Sep 2002)
 * @version $Id: EnsoniqVFXSingleDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
public class EnsoniqVFXSingleDriver extends AbstractPatchDriver {
    /** size of patch without header */
    static final int PATCH_SIZE = 1060;

    /** offset without sysex header */
    static final int PATCHNAME_OFFSET = 996;

    /** number of characters in patch name */
    static final int PATCHNAME_SIZE = 11;

    /** size of header begin + end */
    static final int HEADER_SIZE = 7;

    /** size of all */
    static final int PATCH_AND_HEADER_SIZE = PATCH_SIZE + HEADER_SIZE;

    /**
     * Constructor for the EnsoniqVFXSingleDriver object
     */
    public EnsoniqVFXSingleDriver() {
        super("Single", "Denis Queffeulou");
        sysexID = "F00F0500**02";
        patchSize = PATCH_AND_HEADER_SIZE;
        patchNameStart = PATCHNAME_OFFSET;
        patchNameSize = PATCHNAME_SIZE;
        deviceIDoffset = 4;
        checksumStart = 0;
        checksumEnd = 0;
        checksumOffset = 0;
        sysexRequestDump = new SysexHandler("F0 0F 05 00 00 00 00 05 F7");
        bankNumbers = new String[] {
            "0-Internal" };// , "1-Cart A", "2-Cart B"};
        patchNumbers = new String[] {
            "Current program" };

    }

    /**
     * Gets the patchName attribute of the EnsoniqVFXSingleDriver object
     * @param ip
     *            Description of the Parameter
     * @return The patchName value
     */
    @Override
    public String getPatchName(Patch ip) {
        return getPatchName(ip.sysex, 6);
    }

    /**
     * Extract patch name from the sysex
     * @param aOffset
     *            offset of patch in sysex
     */
    static String getPatchName(byte aSysex[], int aOffset) {
        try {
            byte[] b = new byte[PATCHNAME_SIZE];
            int oOffset = PATCHNAME_OFFSET + aOffset;
            for (int i = 0; i < PATCHNAME_SIZE; i++) {
                b[i] = ((byte) ((aSysex[oOffset++] << 4) + aSysex[oOffset++]));
            }
            String oName = new String(b, 0, PATCHNAME_SIZE, "US-ASCII");
            return oName;
        } catch (Exception ex) {
            return "-";
        }
    }

    /**
     * Store the name in the patch
     * @param p
     *            the patch
     * @param name
     *            The getPatchFactory().createNewPatch Name value
     */
    @Override
    public void setPatchName(Patch p, String name) {
        setPatchName(p.sysex, name, HEADER_SIZE - 1);
    }

    /**
     * @param aOffset
     *            offset of patch in sysex
     */
    static void setPatchName(byte aSysex[], String name, int aOffset) {
        byte[] namebytes = new byte[32];
        try {
            if (name.length() < 11) {
                name = name + "           ";
            }
            namebytes = name.getBytes("US-ASCII");
            int oOffset = PATCHNAME_OFFSET + aOffset;
            for (int i = 0; i < PATCHNAME_SIZE; i++) {
                aSysex[oOffset++] = (byte) ((namebytes[i] & 0xF0) >> 4);
                aSysex[oOffset++] = (byte) (namebytes[i] & 0x0F);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Description of the Method
     * @param p
     *            Description of the Parameter
     */
    /*
     * public void choosePatch(Patch p) { storePatch(p, 0, 0); }
     */

    /**
     * Description of the Method
     * @param p
     *            Description of the Parameter
     * @param bankNum
     *            Description of the Parameter
     * @param patchNum
     *            Description of the Parameter
     */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        // TODO choose the patchnum by sysex
        sendPatch(p);
    }

    /**
     * Description of the Method
     * @param p
     *            Description of the Parameter
     */
    @Override
    public void sendPatch(Patch p) {
        sendPatchWorker(p);
        ErrorMsg.reportError(
                "Ensoniq VFX!",
                "The patch has been placed in the edit buffer\nYou must now hold the 'write' button on the VFX's\nand choose a location to store the patch.");
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        // This synth does not use a checksum
    }

    /**
     * Description of the Method
     * @return Description of the Return Value
     */
    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[PATCH_AND_HEADER_SIZE];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x0F;
        sysex[2] = (byte) 0x05;
        sysex[3] = (byte) 0x00;
        sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x02; // single patch sysex
        sysex[PATCH_AND_HEADER_SIZE - 1] = (byte) 0xF7;
        // Patch oPatch = getPatchFactory().createNewPatch(sysex, this);
        Patch oPatch = getPatchFactory().createNewPatch(sysex);
        setPatchName(oPatch.sysex, "NEWSND", HEADER_SIZE - 1);
        return oPatch;
        }

    /**
     * Description of the Method
     * @param p
     *            Description of the Parameter
     * @return Description of the Return Value
     */
    // Don't override editPatch() without implementing.
    // public JSLFrame editPatch(Patch p)
    // {
    // return null;
    // }
}
