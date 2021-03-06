/*
 * @version $Id: EnsoniqESQ1BankDriver.java 744 2004-10-03 17:44:17Z hayashi $
 */
package org.jsynthlib.synthdrivers.EnsoniqESQ1;

import javax.swing.JOptionPane;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

public class EnsoniqESQ1BankDriver extends AbstractBankDriver {

    public EnsoniqESQ1BankDriver()

    {
        super("Bank", "Brian Klock", 40, 4);
        sysexID = "F00F02**02";
        deviceIDoffset = 3;

        singleSysexID = "F00F02**01";
        singleSize = 0;
        bankNumbers = new String[] {
            "0-Internal" };
        patchNumbers =
                new String[] {
                        "01", "02", "03", "04", "05", "06", "07", "08", "09",
                        "10", "11", "12", "13", "14", "15", "16", "17", "18",
                        "19", "20", "21", "22", "23", "24", "25", "26", "27",
                        "28", "29", "30", "31", "32", "33", "34", "35", "36",
                        "37", "38", "39", "40" };

    }

    public int getPatchStart(int patchNum) {
        int start = (204 * patchNum);
        start += 5; // sysex header
        return start;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 0; // offset of name in patch data

        try {
            byte[] b = new byte[6];
            b[0] =
                    ((byte) (p.sysex[nameStart] + p.sysex[nameStart + 1] * 16));
            b[1] =
                    ((byte) (p.sysex[nameStart + 2] + p.sysex[nameStart + 3] * 16));
            b[2] =
                    ((byte) (p.sysex[nameStart + 4] + p.sysex[nameStart + 5] * 16));
            b[3] =
                    ((byte) (p.sysex[nameStart + 6] + p.sysex[nameStart + 7] * 16));
            b[4] =
                    ((byte) (p.sysex[nameStart + 8] + p.sysex[nameStart + 9] * 16));
            b[5] =
                    ((byte) (p.sysex[nameStart + 10] + p.sysex[nameStart + 11] * 16));
            StringBuffer s = new StringBuffer(new String(b, 0, 6, "US-ASCII"));
            return s.toString();
        } catch (Exception ex) {
            return "-";
        }

    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        byte[] namebytes = new byte[32];
        int nameStart = getPatchStart(patchNum);
        try {
            if (name.length() < 6) {
                name = name + "        ";
            }
            namebytes = name.getBytes("US-ASCII");
            p.sysex[nameStart] = ((byte) (namebytes[0] % 16));
            p.sysex[nameStart + 1] = ((byte) (namebytes[0] / 16));
            p.sysex[nameStart + 2] = ((byte) (namebytes[1] % 16));
            p.sysex[nameStart + 3] = ((byte) (namebytes[1] / 16));
            p.sysex[nameStart + 4] = ((byte) (namebytes[2] % 16));
            p.sysex[nameStart + 5] = ((byte) (namebytes[2] / 16));
            p.sysex[nameStart + 6] = ((byte) (namebytes[3] % 16));
            p.sysex[nameStart + 7] = ((byte) (namebytes[3] / 16));
            p.sysex[nameStart + 8] = ((byte) (namebytes[4] % 16));
            p.sysex[nameStart + 9] = ((byte) (namebytes[4] / 16));
            p.sysex[nameStart + 10] = ((byte) (namebytes[5] % 16));
            p.sysex[nameStart + 11] = ((byte) (namebytes[5] / 16));
        } catch (Exception e) {
        }

    }

    // protected static void calculateChecksum(Patch p,int start,int end,int
    // ofs)
    // {
    // }

    @Override
    public void calculateChecksum(Patch p) {
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            PopupHandlerProvider.get().showMessage(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.arraycopy(p.sysex, 5, bank.sysex,
                getPatchStart(patchNum), 204);
        calculateChecksum(bank);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        try {
            byte[] sysex = new byte[210];
            sysex[00] = (byte) 0xF0;
            sysex[01] = (byte) 0x0F;
            sysex[02] = (byte) 0x02;
            sysex[03] = (byte) 0x00;
            sysex[04] = (byte) 0x01;
            sysex[209] = (byte) 0xF7;
            System.arraycopy(bank.sysex, getPatchStart(patchNum),
                    sysex, 5, 204);
            Patch p = getPatchFactory().createNewPatch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in ESQ1 Bank Driver", e);
            return null;
        }
    }

    @Override
    public BankPatch createNewPatch() {
        byte[] sysex = new byte[15123];
        sysex[0] = (byte) 0xF0;
        sysex[1] = (byte) 0x40;
        sysex[2] = (byte) 0x00;
        sysex[3] = (byte) 0x21;
        sysex[4] = (byte) 0x00;
        sysex[5] = (byte) 0x04;
        sysex[6] = (byte) 0x0;
        sysex[15122] = (byte) 0xF7;
        BankPatch p = getPatchFactory().newBankPatch(sysex, this);
        for (int i = 0; i < 64; i++) {
            setPatchName(p, i, "NEWSND");
        }
        calculateChecksum(p);
        return p;
    }

}
