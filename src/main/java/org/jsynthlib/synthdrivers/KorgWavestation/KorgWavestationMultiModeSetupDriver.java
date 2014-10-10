package org.jsynthlib.synthdrivers.KorgWavestation;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver for Korg Wavestation MultiMode Setup. Be carefull: This driver is
 * untested, because I only have acces to a file containing WS patches....
 * @author Gerrit Gehnen
 * @version $Id: KorgWavestationMultiModeSetupDriver.java 939 2005-03-03
 *          04:05:40Z hayashi $
 */
public class KorgWavestationMultiModeSetupDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public KorgWavestationMultiModeSetupDriver() {
        super("Multi Mode Setup", "Gerrit Gehnen");
        sysexID = "F0423*2855";
        sysexRequestDump = new SysexHandler("F0 42 @@ 28 06 F7");

        trimSize = 2761;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 0;
        checksumStart = 5;
        checksumEnd = 2758;
        checksumOffset = 2759;

    }

    /**
     */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        // int patchValue = patchNum;
        // int bankValue = 0;
        calculateChecksum(p);

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }

        p.sysex[2] = (byte) (0x30 + getChannel() - 1);
        try {
            send(p.sysex);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        /*
         * try {Thread.sleep (100); } catch (Exception e) {}
         */
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
        byte[] sysex = new byte[2761];
        sysex[00] = (byte) 0xF0;
        sysex[01] = (byte) 0x42;
        sysex[2] = (byte) (0x30 + getChannel() - 1);
        sysex[03] = (byte) 0x28;
        sysex[04] = (byte) 0x55;

        /* sysex[2759]=checksum; */
        sysex[2760] = (byte) 0xF7;

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
        send(sysexRequestDump.toSysexMessage(getChannel(), 0));
    }
}
