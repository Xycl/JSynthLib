package org.jsynthlib.synthdrivers.KorgWavestation;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver for Korg Wavestation Banks of Patches Be carefull: Untested, because I
 * only have access to a file containing some WS patches....
 * @version $Id: KorgWavestationBankPatchDriver.java 939 2005-03-03 04:05:40Z
 *          hayashi $
 * @author Gerrit Gehnen
 */
public class KorgWavestationBankPatchDriver extends AbstractBankDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public KorgWavestationBankPatchDriver() {
        super("Patch Bank", "Gerrit Gehnen", 35, 1);
        sysexID = "F042**284C";
        trimSize = 35 * 852 + 8;
        sysexRequestDump = new SysexHandler("F0 42 @@ 28 1C *bankNum* F7");

        deviceIDoffset = 0; // need our own handling of the ID-num

        bankNumbers = new String[] {
                "RAM1", "RAM2", "ROM1", "CARD", "RAM3" };
        patchNumbers =
                new String[] {
                        "01-", "02-", "03-", "04-", "05-", "06-", "07-", "08-",
                        "09-", "10-", "11-", "12-", "13-", "14-", "15-", "16-",
                        "17-", "18-", "19-", "20-", "21-", "22-", "23-", "24-",
                        "25-", "26-", "27-", "28-", "29-", "30-", "31-", "32-",
                        "33-", "34-", "35-" };

        singleSysexID = "F040**2*0004";
        singleSize = 852;

    }

    public int getPatchStart(int patchNum) {
        int start = (singleSize * patchNum);
        start += 6; // sysex header
        return start;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);

        // nameStart+=0; //offset of name in patch data
        try {
            byte[] byteBuffer = new byte[16];

            for (int i = 0; i < 16; i++) {
                byteBuffer[i] =
                        (byte) (p.sysex[nameStart + i * 2] + ((0x10) * p.sysex[nameStart
                                + i * 2 + 1]));
                if (byteBuffer[i] == 0) {
                    byteBuffer[i] = 0x20;
                }
            }

            StringBuffer s =
                    new StringBuffer(new String(byteBuffer, "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }

    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        patchNameSize = 16;
        patchNameStart = getPatchStart(patchNum);

        if (name.length() < patchNameSize) {
            name = name + "                     ";
        }
        byte[] namebytes = new byte[64];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchNameStart + i * 2] =
                        (byte) (namebytes[i] & 0x0f);
                p.sysex[patchNameStart + i * 2 + 1] =
                        (byte) (namebytes[i] / 0x10);
            }

        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        p.sysex[2] = (byte) (0x30 + getChannel() - 1);
        p.sysex[5] = (byte) bankNum;
        sendPatchWorker(p);
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int i;
        int sum = 0;

        log.info("Checksum was" + sysex[ofs]);
        for (i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[ofs] = (byte) (sum % 128);
        log.info("Checksum new is" + sysex[ofs]);

    }

    @Override
    public void calculateChecksum(Patch p) {
        calculateChecksum(p, 6, 6 + (852 * 35) - 1, 6 + (852 * 35));
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            JOptionPane.showMessageDialog(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.arraycopy(p.sysex, 8, bank.sysex,
                getPatchStart(patchNum), 131);
        calculateChecksum(bank);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        try {
            byte[] sysex = new byte[852 + 9];
            sysex[00] = (byte) 0xF0;
            sysex[01] = (byte) 0x42;
            sysex[2] = (byte) (0x30 + getChannel() - 1);
            sysex[03] = (byte) 0x28;
            sysex[04] = (byte) 0x40;
            sysex[05] = (byte) 0x00/* bankNum */;
            sysex[06] = (byte) patchNum;

            /* sysex[852+7]=checksum; */
            sysex[852 + 8] = (byte) 0xF7;
            System.arraycopy(bank.sysex, getPatchStart(patchNum),
                    sysex, 7, 852);
            Patch p = getPatchFactory().createNewPatch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in Wavestation Bank Driver", e);
            return null;
        }
    }

    @Override
    public BankPatch createNewPatch() {

        byte[] sysex = new byte[35 * 852 + 8];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x42;
        sysex[2] = (byte) (0x30 + getChannel() - 1);
        sysex[3] = (byte) 0x28;
        sysex[4] = (byte) 0x4C;
        sysex[5] = (byte) 0x00/* bankNum */;

        /* sysex[35*852+6]=checksum; */
        sysex[35 * 852 + 7] = (byte) 0xF7;
        BankPatch p = getPatchFactory().newBankPatch(sysex, this);
        for (int i = 0; i < 35; i++) {
            setPatchName(p, i, "New Patch");
        }
        calculateChecksum(p);
        return p;
    }
}
