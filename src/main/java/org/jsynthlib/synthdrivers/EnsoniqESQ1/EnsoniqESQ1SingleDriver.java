/*
 * @version $Id: EnsoniqESQ1SingleDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
package org.jsynthlib.synthdrivers.EnsoniqESQ1;

import org.jsynthlib.device.model.XMLPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;

public class EnsoniqESQ1SingleDriver extends XMLPatchDriver {

    // public EnsoniqESQ1SingleDriver() {
    // super("Single", "Brian Klock");
    // sysexID = "F00F02**01";
    // patchSize = 0;
    // patchNameStart = 0;
    // patchNameSize = 10;
    // deviceIDoffset = 3;
    // checksumStart = 0;
    // checksumEnd = 0;
    // checksumOffset = 0;
    // bankNumbers = new String[] {
    // "0-Internal", "1-Cart A", "2-Cart B" };
    // patchNumbers =
    // new String[] {
    // "01", "02", "03", "04", "05", "06", "07", "08", "09",
    // "10", "11", "12", "13", "14", "15", "16", "17", "18",
    // "19", "20", "21", "22", "23", "24", "25", "26", "27",
    // "28", "29", "30", "31", "32", "33", "34", "35", "36",
    // "37", "38", "39", "40" };
    //
    // }

    public EnsoniqESQ1SingleDriver(XmlPatchDriverSpec driverSpec) {
        super(driverSpec);
    }

    @Override
    public String getPatchName(Patch ip) {
        Patch p = ip;
        try {
            byte[] b = new byte[8];
            b[0] = ((byte) (p.sysex[5] + p.sysex[6] * 16));
            b[1] = ((byte) (p.sysex[7] + p.sysex[8] * 16));
            b[2] = ((byte) (p.sysex[9] + p.sysex[10] * 16));
            b[3] = ((byte) (p.sysex[11] + p.sysex[12] * 16));
            b[4] = ((byte) (p.sysex[13] + p.sysex[14] * 16));
            b[5] = ((byte) (p.sysex[15] + p.sysex[16] * 16));
            StringBuffer s = new StringBuffer(new String(b, 0, 6, "US-ASCII"));
            return s.toString();
        } catch (Exception ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, String name) {
        byte[] namebytes = new byte[32];
        try {
            if (name.length() < 6) {
                name = name + "        ";
            }
            namebytes = name.getBytes("US-ASCII");
            p.sysex[5] = ((byte) (namebytes[0] % 16));
            p.sysex[6] = ((byte) (namebytes[0] / 16));
            p.sysex[7] = ((byte) (namebytes[1] % 16));
            p.sysex[8] = ((byte) (namebytes[1] / 16));
            p.sysex[9] = ((byte) (namebytes[2] % 16));
            p.sysex[10] = ((byte) (namebytes[2] / 16));
            p.sysex[11] = ((byte) (namebytes[3] % 16));
            p.sysex[12] = ((byte) (namebytes[3] / 16));
            p.sysex[13] = ((byte) (namebytes[4] % 16));
            p.sysex[14] = ((byte) (namebytes[4] / 16));
            p.sysex[15] = ((byte) (namebytes[5] % 16));
            p.sysex[16] = ((byte) (namebytes[5] / 16));
        } catch (Exception e) {
        }
    }

    /*
     * public void choosePatch (Patch p) {storePatch(p,0,0);}
     */

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        sendPatchWorker(p);

        // Send Bank press down
        send(new byte[] {
                (byte) 0xF0, 0x0F, 0x02, 0x00, 0x0E, (byte) (0x26 + bankNum),
                (byte) 0xF7 });

        // Send Patch press
        send(new byte[] {
                (byte) 0xF0, 0x0F, 0x02, 0x00, 0x0E, (byte) (0x2A + patchNum),
                (byte) (0x5D + patchNum), (byte) 0xF7 });

        // Send Bank press up
        send(new byte[] {
                (byte) 0xF0, 0x0F, 0x02, 0x00, 0x0E, (byte) (0x59 + bankNum),
                (byte) 0xF7 });
        // ErrorMsg.reportError(
        // "Ensoniq ESQ!",
        // "The patch has been placed in the edit buffer\nYou must now hold the 'write' button 'exit' on the ESQ1's\nand choose a location to store the patch.");
    }

    @Override
    public void sendPatch(Patch p) {
        sendPatchWorker(p);

        // Send Exit button press
        send(new byte[] {
                (byte) 0xF0, 0x0F, 0x02, 0x00, 0x0E, 0x2F, 0x62, (byte) 0xF7 });
        // ErrorMsg.reportError(
        // "Ensoniq ESQ!",
        // "You must now hit 'exit' on the ESQ1's\nfront panel before you can\nsend another patch.");
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        // This synth does not use a checksum
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[210];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x0F;
        sysex[2] = (byte) 0x02;
        sysex[3] = (byte) 0x00;
        sysex[4] = (byte) 0x01;
        sysex[209] = (byte) 0xF7;
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, "NEWSND");
        calculateChecksum(p);
        return p;
    }

}