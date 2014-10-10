package org.jsynthlib.synthdrivers.KawaiK4;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Bank driver for KAWAI K4/K4r voice patch.
 * @version $Id: KawaiK4BankDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
public class KawaiK4BankDriver extends AbstractBankDriver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 131;
    /** the number of single patches in a bank patch. */
    private static final int NS = 64;

    private static final SysexHandler SYS_REQ = new SysexHandler(
            "F0 40 @@ 01 00 04 *bankNum* 00 F7");

    public KawaiK4BankDriver() {
        super("Bank", "Brian Klock", NS, 4);

        sysexID = "F040**210004**00";
        deviceIDoffset = 2;
        bankNumbers = new String[] {
                "0-Internal", "1-External" };
        patchNumbers = new String[16 * 4];
        System.arraycopy(generateNumbers(1, 16, "A-##"), 0,
                patchNumbers, 0, 16);
        System.arraycopy(generateNumbers(1, 16, "B-##"), 0,
                patchNumbers, 16, 16);
        System.arraycopy(generateNumbers(1, 16, "C-##"), 0,
                patchNumbers, 32, 16);
        System.arraycopy(generateNumbers(1, 16, "D-##"), 0,
                patchNumbers, 48, 16);

        singleSysexID = "F040**2*0004";
        singleSize = HSIZE + SSIZE + 1;
        // To distinguish from the Effect bank, which has the same sysexID
        patchSize = HSIZE + SSIZE * NS + 1;
    }

    public int getPatchStart(int patchNum) {
        return HSIZE + (SSIZE * patchNum);
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 0; // offset of name in patch data
        try {
            StringBuffer s =
                    new StringBuffer(new String(p.sysex, nameStart, 10,
                            "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        patchNameSize = 10;
        patchNameStart = getPatchStart(patchNum);

        if (name.length() < patchNameSize) {
            name = name + "            ";
        }
        byte[] namebytes = new byte[64];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchNameStart + i] = namebytes[i];
            }

        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int i;
        int sum = 0;

        for (i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sum += 0xA5;
        sysex[ofs] = (byte) (sum % 128);
        // sysex[ofs]=(byte)(sysex[ofs]^127);
        // sysex[ofs]=(byte)(sysex[ofs]+1);
    }

    @Override
    public void calculateChecksum(Patch p) {
        for (int i = 0; i < NS; i++) {
            calculateChecksum(p, HSIZE + (i * SSIZE), HSIZE + (i * SSIZE)
                    + SSIZE - 2, HSIZE + (i * SSIZE) + SSIZE - 1);
        }
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            JOptionPane.showMessageDialog(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.arraycopy(p.sysex, HSIZE, bank.sysex, getPatchStart(patchNum),
                SSIZE);
        calculateChecksum(bank);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x40;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x20;
        sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x04;
        sysex[6] = (byte) 0x00;
        sysex[7] = /* (byte)0x00+ */(byte) patchNum;
        sysex[HSIZE + SSIZE] = (byte) 0xF7;
        System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, HSIZE,
                SSIZE);
        try {
            // pass Single Driver !!!FIXIT!!!
            Patch p = getPatchFactory().createNewPatch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in K4 Bank Driver", e);
            return null;
        }
    }

    @Override
    public BankPatch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE * NS + 1];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x40;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x21;
        sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x04;
        sysex[6] = (byte) 0x0;
        sysex[7] = 0;
        sysex[HSIZE + SSIZE * NS] = (byte) 0xF7;
        BankPatch p = getPatchFactory().newBankPatch(sysex, this);
        for (int i = 0; i < NS; i++) {
            setPatchName(p, i, "New Patch");
        }
        calculateChecksum(p);
        return p;
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "bankNum", bankNum << 1)));
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
        p.sysex[3] = (byte) 0x21;
        p.sysex[6] = (byte) (bankNum << 1);
        p.sysex[7] = (byte) 0x0;
        sendPatchWorker(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }
}
