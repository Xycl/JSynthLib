/*
 * @version $Id: YamahaDX100BankDriver.java 744 2004-10-03 17:44:17Z hayashi $
 */
package org.jsynthlib.synthdrivers.YamahaDX100;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

public class YamahaDX100BankDriver extends AbstractBankDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public YamahaDX100BankDriver() {
        super("Bank", "Brian Klock", 32, 4);
        sysexID = "F043**042000";
        deviceIDoffset = 2;
        bankNumbers = new String[] {
            "0-Internal" };
        patchNumbers =
                new String[] {
                        "I01", "I02", "I03", "I04", "I05", "I06", "I07", "I08",
                        "I09", "I10", "I11", "I12", "I13", "I14", "I15", "I16",
                        "I17", "I18", "I19", "I20", "I21", "I22", "I23", "I24",
                        "I25", "I26", "I27", "I28", "I29", "I30", "I31", "I32" };
        singleSize = 101;
        singleSysexID = "F043**03005D";

    }

    public int getPatchStart(int patchNum) {
        int start = (128 * patchNum);
        start += 6; // sysex header

        return start;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchStart(patchNum);
        nameStart += 57; // offset of name in patch data
        try {
            StringBuffer s =
                    new StringBuffer(new String(p.sysex, nameStart,
                            10, "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }

    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        patchNameSize = 10;
        patchNameStart = getPatchStart(patchNum) + 57;

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
    public void calculateChecksum(Patch p) {
        calculateChecksum(p, 6, 4101, 4102);

    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            PopupHandlerProvider.get().showMessage(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        bank.sysex[getPatchStart(patchNum) + 0] =
                ((p.sysex[47 - 41])); // AR
        bank.sysex[getPatchStart(patchNum) + 1] =
                ((p.sysex[48 - 41])); // D1r
        bank.sysex[getPatchStart(patchNum) + 2] =
                ((p.sysex[49 - 41])); // D2r
        bank.sysex[getPatchStart(patchNum) + 3] =
                ((p.sysex[50 - 41])); // RR
        bank.sysex[getPatchStart(patchNum) + 4] =
                ((p.sysex[51 - 41])); // D1L
        bank.sysex[getPatchStart(patchNum) + 5] =
                ((p.sysex[52 - 41])); // LS
        bank.sysex[getPatchStart(patchNum) + 6] =
                (byte) ((p.sysex[55 - 41] * 64
                        + p.sysex[54 - 41] * 8 + p.sysex[56 - 41]));// ame
                                                                                        // ebs
                                                                                        // kvs
        bank.sysex[getPatchStart(patchNum) + 7] =
                ((p.sysex[57 - 41])); // out
        bank.sysex[getPatchStart(patchNum) + 8] =
                ((p.sysex[58 - 41])); // freq
        bank.sysex[getPatchStart(patchNum) + 9] =
                (byte) ((p.sysex[53 - 41] * 8 + p.sysex[59 - 41])); // rs
                                                                                        // dbt

        bank.sysex[getPatchStart(patchNum) + 10] =
                ((p.sysex[-41 + 47 + 13 * 1])); // AR
        bank.sysex[getPatchStart(patchNum) + 11] =
                ((p.sysex[-41 + 48 + 13 * 1])); // D1r
        bank.sysex[getPatchStart(patchNum) + 12] =
                ((p.sysex[-41 + 49 + 13 * 1])); // D2r
        bank.sysex[getPatchStart(patchNum) + 13] =
                ((p.sysex[-41 + 50 + 13 * 1])); // RR
        bank.sysex[getPatchStart(patchNum) + 14] =
                ((p.sysex[-41 + 51 + 13 * 1])); // D1L
        bank.sysex[getPatchStart(patchNum) + 15] =
                ((p.sysex[-41 + 52 + 13 * 1])); // LS
        bank.sysex[getPatchStart(patchNum) + 16] =
                (byte) ((p.sysex[-41 + 55 + 13 * 1] * 64
                        + p.sysex[-41 + 54 + 13 * 1] * 8 + p.sysex[-41 + 56 + 13 * 1]));// ame
                                                                                                            // ebs
                                                                                                            // kvs
        bank.sysex[getPatchStart(patchNum) + 17] =
                ((p.sysex[-41 + 57 + 13 * 1])); // out
        bank.sysex[getPatchStart(patchNum) + 18] =
                ((p.sysex[-41 + 58 + 13 * 1])); // freq
        bank.sysex[getPatchStart(patchNum) + 19] =
                (byte) ((p.sysex[-41 + 53 + 13 * 1] * 8 + p.sysex[-41 + 59 + 13 * 1])); // rs
                                                                                                            // dbt

        bank.sysex[getPatchStart(patchNum) + 20] =
                ((p.sysex[-41 + 47 + 13 * 2])); // AR
        bank.sysex[getPatchStart(patchNum) + 21] =
                ((p.sysex[-41 + 48 + 13 * 2])); // D1r
        bank.sysex[getPatchStart(patchNum) + 22] =
                ((p.sysex[-41 + 49 + 13 * 2])); // D2r
        bank.sysex[getPatchStart(patchNum) + 23] =
                ((p.sysex[-41 + 50 + 13 * 2])); // RR
        bank.sysex[getPatchStart(patchNum) + 24] =
                ((p.sysex[-41 + 51 + 13 * 2])); // D1L
        bank.sysex[getPatchStart(patchNum) + 25] =
                ((p.sysex[-41 + 52 + 13 * 2])); // LS
        bank.sysex[getPatchStart(patchNum) + 26] =
                (byte) ((p.sysex[-41 + 55 + 13 * 2] * 64
                        + p.sysex[-41 + 54 + 13 * 2] * 8 + p.sysex[-41 + 56 + 13 * 2]));// ame
                                                                                                            // ebs
                                                                                                            // kvs
        bank.sysex[getPatchStart(patchNum) + 27] =
                ((p.sysex[-41 + 57 + 13 * 2])); // out
        bank.sysex[getPatchStart(patchNum) + 28] =
                ((p.sysex[-41 + 58 + 13 * 2])); // freq
        bank.sysex[getPatchStart(patchNum) + 29] =
                (byte) ((p.sysex[-41 + 53 + 13 * 2] * 8 + p.sysex[-41 + 59 + 13 * 2])); // rs
                                                                                                            // dbt

        bank.sysex[getPatchStart(patchNum) + 30] =
                ((p.sysex[-41 + 47 + 13 * 3])); // AR
        bank.sysex[getPatchStart(patchNum) + 31] =
                ((p.sysex[-41 + 48 + 13 * 3])); // D1r
        bank.sysex[getPatchStart(patchNum) + 32] =
                ((p.sysex[-41 + 49 + 13 * 3])); // D2r
        bank.sysex[getPatchStart(patchNum) + 33] =
                ((p.sysex[-41 + 50 + 13 * 3])); // RR
        bank.sysex[getPatchStart(patchNum) + 34] =
                ((p.sysex[-41 + 51 + 13 * 3])); // D1L
        bank.sysex[getPatchStart(patchNum) + 35] =
                ((p.sysex[-41 + 52 + 13 * 3])); // LS
        bank.sysex[getPatchStart(patchNum) + 36] =
                (byte) ((p.sysex[-41 + 55 + 13 * 3] * 64
                        + p.sysex[-41 + 54 + 13 * 3] * 8 + p.sysex[-41 + 56 + 13 * 3]));// ame
                                                                                                            // ebs
                                                                                                            // kvs
        bank.sysex[getPatchStart(patchNum) + 37] =
                ((p.sysex[-41 + 57 + 13 * 3])); // out
        bank.sysex[getPatchStart(patchNum) + 38] =
                ((p.sysex[-41 + 58 + 13 * 3])); // freq
        bank.sysex[getPatchStart(patchNum) + 39] =
                (byte) ((p.sysex[-41 + 53 + 13 * 3] * 8 + p.sysex[-41 + 59 + 13 * 3])); // rs
                                                                                                            // dbt

        bank.sysex[getPatchStart(patchNum) + 40] =
                (byte) ((p.sysex[-41 + 105] * 64
                        + p.sysex[-41 + 100] * 8 + p.sysex[-41 + 99]));// sync
                                                                                           // fbl
                                                                                           // alg
        bank.sysex[getPatchStart(patchNum) + 41] =
                ((p.sysex[-41 + 101])); // lfs
        bank.sysex[getPatchStart(patchNum) + 42] =
                ((p.sysex[-41 + 102])); // lfd
        bank.sysex[getPatchStart(patchNum) + 43] =
                ((p.sysex[-41 + 103])); // pmd
        bank.sysex[getPatchStart(patchNum) + 44] =
                ((p.sysex[-41 + 104])); // amd
        bank.sysex[getPatchStart(patchNum) + 45] =
                (byte) ((p.sysex[-41 + 107] * 16
                        + p.sysex[-41 + 108] * 4 + p.sysex[-41 + 106]));// pms
                                                                                            // ams
                                                                                            // lfw
        bank.sysex[getPatchStart(patchNum) + 46] =
                ((p.sysex[-41 + 109])); // traspose
        bank.sysex[getPatchStart(patchNum) + 47] =
                ((p.sysex[-41 + 111])); // pbr
        bank.sysex[getPatchStart(patchNum) + 48] =
                (byte) ((p.sysex[-41 + 117] * 16
                        + p.sysex[-41 + 110] * 8
                        + p.sysex[-41 + 115] * 4
                        + p.sysex[-41 + 116] * 2 + p.sysex[-41 + 112]));// ch
                                                                                            // mo
                                                                                            // su
                                                                                            // po
                                                                                            // pm
        bank.sysex[getPatchStart(patchNum) + 49] =
                ((p.sysex[-41 + 113])); // porta time
        bank.sysex[getPatchStart(patchNum) + 50] =
                ((p.sysex[-41 + 114])); // footcontrol

        System.arraycopy(p.sysex, 118 - 41, bank.sysex,
                getPatchStart(patchNum) + 51, 22);

        calculateChecksum(bank);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        try {
            byte[] sysex = new byte[101];
            // Then create VCED Data
            sysex[00] = (byte) 0xF0;
            sysex[01] = (byte) 0x43;
            sysex[02] = (byte) 0x00;
            sysex[03] = (byte) 0x03;
            sysex[04] = (byte) 0x00;
            sysex[05] = (byte) 0x5D;

            sysex[06] =
                    ((bank.sysex[getPatchStart(patchNum) + 0])); // AR
            sysex[07] =
                    ((bank.sysex[getPatchStart(patchNum) + 1])); // d1r
            sysex[8] =
                    ((bank.sysex[getPatchStart(patchNum) + 2])); // d2r
            sysex[9] =
                    ((bank.sysex[getPatchStart(patchNum) + 3])); // rr
            sysex[10] =
                    ((bank.sysex[getPatchStart(patchNum) + 4])); // d1l
            sysex[11] =
                    ((bank.sysex[getPatchStart(patchNum) + 5])); // ls
            sysex[12] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9] & 24) / 8);// rate
                                                                                          // scaling;
            sysex[13] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6] & 56) / 8);// ebs
            sysex[14] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6] & 64) / 64);// ame
            sysex[15] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6] & 7)); // kvs
            sysex[16] =
                    ((bank.sysex[getPatchStart(patchNum) + 7])); // out
            sysex[17] =
                    ((bank.sysex[getPatchStart(patchNum) + 8])); // frs
            sysex[18] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9] & 7)); // dbt(det)

            sysex[19] =
                    ((bank.sysex[getPatchStart(patchNum) + 0 + 10])); // AR
            sysex[20] =
                    ((bank.sysex[getPatchStart(patchNum) + 1 + 10])); // d1r
            sysex[21] =
                    ((bank.sysex[getPatchStart(patchNum) + 2 + 10])); // d2r
            sysex[22] =
                    ((bank.sysex[getPatchStart(patchNum) + 3 + 10])); // rr
            sysex[23] =
                    ((bank.sysex[getPatchStart(patchNum) + 4 + 10])); // d1l
            sysex[24] =
                    ((bank.sysex[getPatchStart(patchNum) + 5 + 10])); // ls
            sysex[25] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 10] & 24) / 8);// rate
                                                                                               // scaling;
            sysex[26] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 10] & 56) / 8);// ebs
            sysex[27] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 10] & 64) / 64);// ame
            sysex[28] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 10] & 7)); // kvs
            sysex[29] =
                    ((bank.sysex[getPatchStart(patchNum) + 7 + 10])); // out
            sysex[30] =
                    ((bank.sysex[getPatchStart(patchNum) + 8 + 10])); // frs
            sysex[31] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 10] & 7)); // dbt(det)

            sysex[32] =
                    ((bank.sysex[getPatchStart(patchNum) + 0 + 20])); // AR
            sysex[33] =
                    ((bank.sysex[getPatchStart(patchNum) + 1 + 20])); // d1r
            sysex[34] =
                    ((bank.sysex[getPatchStart(patchNum) + 2 + 20])); // d2r
            sysex[35] =
                    ((bank.sysex[getPatchStart(patchNum) + 3 + 20])); // rr
            sysex[36] =
                    ((bank.sysex[getPatchStart(patchNum) + 4 + 20])); // d1l
            sysex[37] =
                    ((bank.sysex[getPatchStart(patchNum) + 5 + 20])); // ls
            sysex[38] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 20] & 24) / 8);// rate
                                                                                               // scaling;
            sysex[39] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 20] & 56) / 8);// ebs
            sysex[40] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 20] & 64) / 64);// ame
            sysex[41] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 20] & 7)); // kvs
            sysex[42] =
                    ((bank.sysex[getPatchStart(patchNum) + 7 + 20])); // out
            sysex[43] =
                    ((bank.sysex[getPatchStart(patchNum) + 8 + 20])); // frs
            sysex[44] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 20] & 7)); // dbt(det)

            sysex[45] =
                    ((bank.sysex[getPatchStart(patchNum) + 0 + 30])); // AR
            sysex[46] =
                    ((bank.sysex[getPatchStart(patchNum) + 1 + 30])); // d1r
            sysex[47] =
                    ((bank.sysex[getPatchStart(patchNum) + 2 + 30])); // d2r
            sysex[48] =
                    ((bank.sysex[getPatchStart(patchNum) + 3 + 30])); // rr
            sysex[49] =
                    ((bank.sysex[getPatchStart(patchNum) + 4 + 30])); // d1l
            sysex[50] =
                    ((bank.sysex[getPatchStart(patchNum) + 5 + 30])); // ls
            sysex[51] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 30] & 24) / 8);// rate
                                                                                               // scaling;
            sysex[52] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 30] & 56) / 8);// ebs
            sysex[53] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 30] & 64) / 64);// ame
            sysex[54] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6 + 30] & 7)); // kvs
            sysex[55] =
                    ((bank.sysex[getPatchStart(patchNum) + 7 + 30])); // out
            sysex[56] =
                    ((bank.sysex[getPatchStart(patchNum) + 8 + 30])); // frs
            sysex[57] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 9 + 30] & 7)); // dbt(det)

            sysex[58] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 40] & 7)); // algorithem
            sysex[59] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 40] & 56) / 8); // feedback
            sysex[60] =
                    ((bank.sysex[getPatchStart(patchNum) + 41])); // lfo
                                                                                   // speed
            sysex[61] =
                    ((bank.sysex[getPatchStart(patchNum) + 42])); // lfo
                                                                                   // delay
            sysex[62] =
                    ((bank.sysex[getPatchStart(patchNum) + 43])); // pmod
                                                                                   // depth
            sysex[63] =
                    ((bank.sysex[getPatchStart(patchNum) + 44])); // amod
                                                                                   // depth
            sysex[64] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 40] & 64) / 64); // sync
            sysex[65] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 45] & 3)); // lfw
            sysex[66] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 45] & 112) / 16); // pms
            sysex[67] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 45] & 12) / 4); // ams
            sysex[68] =
                    ((bank.sysex[getPatchStart(patchNum) + 46])); // transpose
            sysex[69] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 48] & 8) / 8); // polymode
                                                                                           // ***
            sysex[70] =
                    ((bank.sysex[getPatchStart(patchNum) + 47])); // pitchbendrange
            sysex[71] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 48] & 1)); // portamento
                                                                                       // mode***
            sysex[72] =
                    ((bank.sysex[getPatchStart(patchNum) + 49])); // portamento
                                                                                   // time
            sysex[73] =
                    ((bank.sysex[getPatchStart(patchNum) + 50])); // foot
                                                                                   // control
                                                                                   // volume
            sysex[74] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 48] & 4) / 4); // sustain
            sysex[75] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 48] & 2) / 2); // portamento***
            sysex[76] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 48] & 16) / 16); // chorus

            sysex[100] = (byte) 0xF7;
            System.arraycopy(bank.sysex,
                    getPatchStart(patchNum) + 51, sysex, 77, 22);
            Patch p = getPatchFactory().createNewPatch(sysex, getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Error in DX100 Bank Driver");
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public BankPatch createNewPatch() {
        byte[] sysex = new byte[4104];
        sysex[00] = (byte) 0xF0;
        sysex[01] = (byte) 0x43;
        sysex[02] = (byte) 0x00;
        sysex[03] = (byte) 0x04;
        sysex[04] = (byte) 0x20;
        sysex[05] = (byte) 0x00;
        sysex[4103] = (byte) 0xF7;

        BankPatch p = getPatchFactory().newBankPatch(sysex, this);
        for (int i = 0; i < 32; i++) {
            setPatchName(p, i, "NewPatch");
        }
        calculateChecksum(p);
        return p;
    }

}
