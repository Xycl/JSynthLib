package org.jsynthlib.synthdrivers.KorgWavestation;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver for Korg Wavestation Wave Sequences Be carefull: Untested, because I
 * only have access to a file containing some WS patches....
 * @version $Id: KorgWavestationWaveSequenceDriver.java 939 2005-03-03 04:05:40Z
 *          hayashi $
 * @author Gerrit Gehnen
 */
public class KorgWavestationWaveSequenceDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public KorgWavestationWaveSequenceDriver() {
        super("Wave Sequence", "Gerrit Gehnen");
        sysexID = "F0423*2854";
        sysexRequestDump = new SysexHandler("F0 42 @@ 28 0C *bankNum* F7");

        trimSize = 17576;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 0;
        checksumStart = 6;
        checksumEnd = 17573;
        checksumOffset = 17574;
        bankNumbers = new String[] {
                "RAM1", "RAM2", "ROM1", "CARD", "RAM3" };
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        p.sysex[2] = (byte) (0x30 + getChannel() - 1);
        p.sysex[05] = (byte) bankNum;

        try {
            send(p.sysex);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    @Override
    public void sendPatch(Patch p) {
        p.sysex[2] = (byte) (0x30 + getChannel() - 1); // the only
                                                                 // thing to do
                                                                 // is to set
                                                                 // the byte to
                                                                 // 3n (n =
                                                                 // channel)

        try {
            send(p.sysex);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[17576];
        sysex[00] = (byte) 0xF0;
        sysex[01] = (byte) 0x42;
        sysex[2] = (byte) (0x30 + getChannel() - 1);
        sysex[03] = (byte) 0x28;
        sysex[04] = (byte) 0x54;
        sysex[05] = (byte) 0x00/* bankNum */;

        /* sysex[17574]=checksum; */
        sysex[17575] = (byte) 0xF7;

        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, "New Patch");
        calculateChecksum(p);
        return p;
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int i;
        int sum = 0;

        log.debug("Checksum was" + sysex[ofs]);
        for (i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[ofs] = (byte) (sum % 128);
        log.debug("Checksum new is" + sysex[ofs]);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(getChannel(), bankNum));
    }
}
