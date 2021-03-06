package org.jsynthlib.synthdrivers.KawaiK4;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver Set Patch Driver for Kawai K4.
 * @author Gerrit Gehnen
 * @version $Id: KawaiK4DrumsetDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
public class KawaiK4DrumsetDriver extends AbstractPatchDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 682;

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 40 @@ 00 00 04 *bankNum* 20 F7");

    public KawaiK4DrumsetDriver() {
        super("Drumset", "Gerrit Gehnen");
        sysexID = "F040**2*0004**20";
        patchSize = HSIZE + SSIZE + 1;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 2;
        bankNumbers = new String[] {
                "0-Internal", "1-External" };
        patchNumbers = new String[] {
            "Drumset" };
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        p.sysex[3] = (byte) 0x20;
        p.sysex[6] = (byte) ((bankNum << 1) + 1);
        p.sysex[7] = (byte) 0x20;
        sendPatchWorker(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }

    @Override
    public void sendPatch(Patch p) {
        p.sysex[3] = (byte) 0x23;
        p.sysex[7] = (byte) 0x20;
        sendPatchWorker(p);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        // a litte strange this, but there is a checksum for each key!
        for (int i = 8; i < HSIZE + SSIZE - 1; i += 11) {
            int sum = 0;
            for (int j = i; j < i + 10; j++) {
                sum += sysex[j];
            }
            sum += 0xA5;
            sysex[i + 10] = (byte) (sum % 128);
        }
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x40;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x23;
        sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x04;
        sysex[6] = (byte) 0x01;
        sysex[7] = 0x20;

        for (int i = 0; i < 61; i++) {
            sysex[8 + 11 + 6 + i * 11] = 50;
            sysex[8 + 11 + 7 + i * 11] = 50;
        }

        sysex[HSIZE + SSIZE] = (byte) 0xF7;
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        calculateChecksum(p);
        return p;
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new KawaiK4DrumsetEditor(p);
    }

    @Override
    public String getPatchName(Patch ip) {
        return "Drumset";
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "bankNum", (bankNum << 1) + 1)));
    }
}
