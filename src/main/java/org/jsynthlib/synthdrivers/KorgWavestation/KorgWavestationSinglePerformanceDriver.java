package org.jsynthlib.synthdrivers.KorgWavestation;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver for Korg Wavestation Single Performances Be carefull: Untested,
 * because I only have access to a file containing some WS patches....
 * @author Gerrit Gehnen
 * @version $Id: KorgWavestationSinglePerformanceDriver.java 939 2005-03-03
 *          04:05:40Z hayashi $
 */
public class KorgWavestationSinglePerformanceDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public KorgWavestationSinglePerformanceDriver() {
        super("Single Performance", "Gerrit Gehnen");
        sysexID = "F0423*2849";
        sysexRequestDump =
                new SysexHandler("F0 42 @@ 28 19 *bankNum* *patchNum* F7");

        trimSize = 1085;
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 0;
        checksumStart = 7;
        checksumEnd = 1082;
        checksumOffset = 1083;
        bankNumbers = new String[] {
                "RAM1", "RAM2", "ROM1", "CARD", "RAM3" };
        patchNumbers =
                new String[] {
                        "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-",
                        "09-", "10-", "11-", "12-", "13-", "14-", "15-", "16-",
                        "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-",
                        "25-", "26-", "27-", "28-", "29-", "30-", "31-", "32-",
                        "33-", "34-", "35-", "36-", "37-", "38-", "39-", "40-",
                        "41-", "42-", "43-", "44-", "45-", "46-", "47-", "48-",
                        "49-", "50-" };

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
        try {
            send(p.sysex);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        // Send a write request to store the patch in eprom

        byte[] sysex = new byte[8];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x42;
        sysex[2] = (byte) (0x30 + getChannel() - 1);
        sysex[3] = (byte) 0x28;
        sysex[4] = (byte) 0x1A; // Performance write request
        sysex[5] = (byte) (bankNum);
        sysex[6] = (byte) (patchNum);
        sysex[7] = (byte) 0xF7;
        try {
            send(sysex);
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
        byte[] sysex = new byte[1085];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x42;
        sysex[2] = (byte) (0x30 + getChannel() - 1);
        sysex[3] = (byte) 0x28;
        sysex[4] = (byte) 0x49;
        sysex[5] = (byte) 0; /* bankNum */
        sysex[6] = (byte) 0; /* patchNum */
        sysex[1084] = (byte) 0xF7;
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
    public void setPatchNum(int patchNum) {
        try {
            send(0xC0 + (getChannel() - 1), patchNum);
        } catch (Exception e) {
        }
        ;
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        SysexHandler.NameValue nv[] = new SysexHandler.NameValue[2];
        nv[0] = new SysexHandler.NameValue("bankNum", bankNum);
        nv[1] = new SysexHandler.NameValue("patchNum", patchNum);
        send(sysexRequestDump.toSysexMessage(getChannel(), nv));
    }
}
