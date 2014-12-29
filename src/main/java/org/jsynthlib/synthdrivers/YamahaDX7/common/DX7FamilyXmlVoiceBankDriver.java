package org.jsynthlib.synthdrivers.YamahaDX7.common;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

public class DX7FamilyXmlVoiceBankDriver extends XMLBankDriver {

    private boolean spbp;
    private boolean swOffMemProt;
    private boolean tipsMsg;

    private int dxSysexHeaderSize;
    private int dxPatchNameOffset;
    private int dxSinglePackedSize;

    public DX7FamilyXmlVoiceBankDriver(XmlBankDriverDefinition driverSpec) {
        super(driverSpec);
    }

    public int getPatchStart(int patchNum) {
        return dxSinglePackedSize * patchNum + dxSysexHeaderSize;
    }

    public int getPatchNameStart(int patchNum) {
        return getPatchStart(patchNum) + dxPatchNameOffset;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchNameStart(patchNum);

        try {
            StringBuffer s =
                    new StringBuffer(new String(p.sysex, nameStart,
                            getPatchNameSize(), "US-ASCII"));
            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        int nameStart = getPatchNameStart(patchNum);

        while (name.length() < getPatchNameSize()) {
            name = name + " ";
        }

        byte[] namebytes = new byte[getPatchNameSize()];

        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < getPatchNameSize(); i++) {
                p.sysex[nameStart + i] = namebytes[i];
            }

        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    /*
     * puts a patch into the bank, converting it as needed
     */
    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            DX7FamilyStrings.dxShowError(toString(),
                    "This type of patch does not fit in to this type of bank.");
            return;
        }

        // Transform Voice Data to Bulk Dump Packed Format

        for (int i = 0; i < 6; i++) {
            int bankOpOffset = i * 17;
            int patchOpOffset = i * 21;
            // ***** OPERATOR 6 *****
            bank.sysex[getPatchStart(patchNum) + 0 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 0 + patchOpOffset]; // EG Rate 1
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 1 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 1 + patchOpOffset]; // EG Rate 2
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 2 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 2 + patchOpOffset]; // EG Rate 3
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 3 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 3 + patchOpOffset]; // EG Rate 4
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 4 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 4 + patchOpOffset]; // EG Level
            // 1
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 5 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 5 + patchOpOffset]; // EG Level
            // 2
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + dxSysexHeaderSize
                       + bankOpOffset] =
                       p.sysex[dxSysexHeaderSize + 6 + patchOpOffset]; // EG Level
            // 3
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 7 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 7 + patchOpOffset]; // EG Level
            // 4
            // .................(0-99)
            bank.sysex[getPatchStart(patchNum) + 8 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 8 + patchOpOffset]; // Kbd Level
            // Scale
            // Break
            // Point (0-99)
            bank.sysex[getPatchStart(patchNum) + 9 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 9 + patchOpOffset]; // Kbd Level
            // Scale
            // Left
            // Depth .(0-99)
            bank.sysex[getPatchStart(patchNum) + 10 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 10 + patchOpOffset]; // Kbd
            // Level
            // Scale
            // Right
            // Depth (0-99)
            bank.sysex[getPatchStart(patchNum) + 11 + bankOpOffset] =
                    (byte) (p.sysex[dxSysexHeaderSize + 12 + patchOpOffset] * 4 + p.sysex[dxSysexHeaderSize
                                                                                          + 11 + patchOpOffset]); // Kbd
            // Level
            // Scale
            // Right
            // Curve
            // .(0-3)
            // | Left Curve ...............(0-3)
            bank.sysex[getPatchStart(patchNum) + 12 + bankOpOffset] =
                    (byte) (p.sysex[dxSysexHeaderSize + 20 + patchOpOffset] * 8 + p.sysex[dxSysexHeaderSize
                                                                                          + 13 + patchOpOffset]); // Osc
            // Detune
            // .................(0-14)
            // | Kbd Rate Scaling .........(0-7)
            bank.sysex[getPatchStart(patchNum) + 13 + bankOpOffset] =
                    (byte) (p.sysex[dxSysexHeaderSize + 15 + patchOpOffset] * 4 + p.sysex[dxSysexHeaderSize
                                                                                          + 14 + patchOpOffset]); // Key
            // Velocity
            // Sensitivity
            // ....(0-7)
            // | Mod Sensitivity Amplitude (0-3)
            bank.sysex[getPatchStart(patchNum) + 14 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 16 + patchOpOffset]; // Operator
            // Output
            // Level
            // ......(0-99)
            bank.sysex[getPatchStart(patchNum) + 15 + bankOpOffset] =
                    (byte) (p.sysex[dxSysexHeaderSize + 18 + patchOpOffset] * 2 + p.sysex[dxSysexHeaderSize
                                                                                          + 17 + patchOpOffset]); // Osc
            // Frequency
            // Coarse
            // .......(0-31)
            // | Osc Mode .................(0-1)
            bank.sysex[getPatchStart(patchNum) + 16 + bankOpOffset] =
                    p.sysex[dxSysexHeaderSize + 19 + patchOpOffset]; // Osc
            // Frequency
            // Fine
            // .........(0-99)
        }

        // ***** other Parameters *****
        bank.sysex[getPatchStart(patchNum) + 102] =
                p.sysex[dxSysexHeaderSize + 126]; // Pitch
        // EG Rate
        // 1
        // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 103] =
                p.sysex[dxSysexHeaderSize + 127]; // Pitch
        // EG Rate
        // 2
        // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 104] =
                p.sysex[dxSysexHeaderSize + 128]; // Pitch
        // EG Rate
        // 3
        // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 105] =
                p.sysex[dxSysexHeaderSize + 129]; // Pitch
        // EG Rate
        // 4
        // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 106] =
                p.sysex[dxSysexHeaderSize + 130]; // Pitch
        // EG
        // Level 1
        // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 107] =
                p.sysex[dxSysexHeaderSize + 131]; // Pitch
        // EG
        // Level 2
        // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 108] =
                p.sysex[dxSysexHeaderSize + 132]; // Pitch
        // EG
        // Level 3
        // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 109] =
                p.sysex[dxSysexHeaderSize + 133]; // Pitch
        // EG
        // Level 4
        // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 110] =
                p.sysex[dxSysexHeaderSize + 134]; // Algorithmic
        // Select
        // .........(0-31)
        bank.sysex[getPatchStart(patchNum) + 111] =
                (byte) (p.sysex[dxSysexHeaderSize + 136] * 8 + p.sysex[dxSysexHeaderSize + 135]); // Oscillator
        // Sync
        // .............(0-1)|
        // Feedback
        // (0-7)
        bank.sysex[getPatchStart(patchNum) + 112] =
                p.sysex[dxSysexHeaderSize + 137]; // LFO
        // Speed
        // ..................(0-99)
        bank.sysex[getPatchStart(patchNum) + 113] =
                p.sysex[dxSysexHeaderSize + 138]; // LFO
        // Delay
        // ..................(0-99)
        bank.sysex[getPatchStart(patchNum) + 114] =
                p.sysex[dxSysexHeaderSize + 139]; // LFO PMD
        // ....................(0-99)
        bank.sysex[getPatchStart(patchNum) + 115] =
                p.sysex[dxSysexHeaderSize + 140]; // LFO AMD
        // ....................(0-99)
        bank.sysex[getPatchStart(patchNum) + 116] =
                (byte) (p.sysex[dxSysexHeaderSize + 143] * 16
                        + p.sysex[dxSysexHeaderSize + 142] * 2 + p.sysex[dxSysexHeaderSize + 141]);
        // LFO Mod Sensitivity Pitch ...(0-7)
        // | LFO Wave (0-5)| LFO Sync (0-1)
        bank.sysex[getPatchStart(patchNum) + 117] =
                p.sysex[dxSysexHeaderSize + 144]; // Transpose
        // ..................(0-48)
        bank.sysex[getPatchStart(patchNum) + 118] =
                p.sysex[dxSysexHeaderSize + 145]; // Voice
        // name 1
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 119] =
                p.sysex[dxSysexHeaderSize + 146]; // Voice
        // name 2
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 120] =
                p.sysex[dxSysexHeaderSize + 147]; // Voice
        // name 3
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 121] =
                p.sysex[dxSysexHeaderSize + 148]; // Voice
        // name 4
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 122] =
                p.sysex[dxSysexHeaderSize + 149]; // Voice
        // name 5
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 123] =
                p.sysex[dxSysexHeaderSize + 150]; // Voice
        // name 6
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 124] =
                p.sysex[dxSysexHeaderSize + 151]; // Voice
        // name 7
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 125] =
                p.sysex[dxSysexHeaderSize + 152]; // Voice
        // name 8
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 126] =
                p.sysex[dxSysexHeaderSize + 153]; // Voice
        // name 9
        // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 127] =
                p.sysex[dxSysexHeaderSize + 154]; // Voice
        // name 10
        // ...............ASCII

        calculateChecksum(bank);
    }

    /*
     * Gets a patch from the bank, converting it as needed
     */
    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        try {
            byte[] sysex = new byte[singleSize];

            // transform bulk-dump-packed-format to voice data
            sysex[0] = (byte) 0xF0;
            sysex[1] = (byte) 0x43;
            sysex[2] = (byte) 0x00;
            sysex[3] = (byte) 0x00;
            sysex[4] = (byte) 0x01;
            sysex[5] = (byte) 0x1B;

            for (int i = 0; i < 6; i++) {
                int bankOpOffset = i * 17;
                int patchOpOffset = i * 21;

                // EG Rate 1 .................(0-99)
                sysex[dxSysexHeaderSize + 0 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 0 + bankOpOffset];
                // EG Rate 2 .................(0-99)
                sysex[dxSysexHeaderSize + 1 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 1 + bankOpOffset];
                // EG Rate 3 .................(0-99)
                sysex[dxSysexHeaderSize + 2 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 2 + bankOpOffset];
                // EG Rate 4 .................(0-99)
                sysex[dxSysexHeaderSize + 3 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 3 + bankOpOffset];
                // EG Level 1 .................(0-99)
                sysex[dxSysexHeaderSize + 4 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 4 + bankOpOffset];
                // EG Level 2 .................(0-99)
                sysex[dxSysexHeaderSize + 5 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 5 + bankOpOffset];
                // EG Level 3 .................(0-99)
                sysex[dxSysexHeaderSize + 6 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 6 + bankOpOffset];
                // EG Level 4 .................(0-99)
                sysex[dxSysexHeaderSize + 7 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 7 + bankOpOffset];
                // Kbd Level Scale Break Point (0-99)
                sysex[dxSysexHeaderSize + 8 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 8 + bankOpOffset];
                // Kbd Level Scale Left Depth .(0-99)
                sysex[dxSysexHeaderSize + 9 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 9 + bankOpOffset];

                sysex[dxSysexHeaderSize + 10 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 10 + bankOpOffset];
                // Kbd Level Scale Left Curve ..(0-3)
                sysex[dxSysexHeaderSize + 11 + patchOpOffset] =
                        (byte) (bank.sysex[getPatchStart(patchNum) + 11
                                           + bankOpOffset] & 3);

                // Kbd Level Scale Right Curve .(0-3)
                sysex[dxSysexHeaderSize + 12 + patchOpOffset] =
                        (byte) ((bank.sysex[getPatchStart(patchNum) + 11
                                            + bankOpOffset] & 12) / 4);
                // Kbd Rate Scaling ............(0-7)
                sysex[dxSysexHeaderSize + 13 + patchOpOffset] =
                        (byte) (bank.sysex[getPatchStart(patchNum) + 12
                                           + bankOpOffset] & 7);
                // Mod Sensitivity Amplitude ...(0-3)
                sysex[dxSysexHeaderSize + 14 + patchOpOffset] =
                        (byte) (bank.sysex[getPatchStart(patchNum) + 13
                                           + bankOpOffset] & 3);
                // Key Velocity Sensitivity ....(0-7)
                sysex[dxSysexHeaderSize + 15 + patchOpOffset] =
                        (byte) ((bank.sysex[getPatchStart(patchNum) + 13
                                            + bankOpOffset] & 28) / 4);
                // Operator Output Level ......(0-99)
                sysex[dxSysexHeaderSize + 16 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 14 + bankOpOffset];
                // Osc Mode ....................(0-1)
                sysex[dxSysexHeaderSize + 17 + patchOpOffset] =
                        (byte) (bank.sysex[getPatchStart(patchNum) + 15
                                           + bankOpOffset] & 1);
                // Osc Frequency Coarse .......(0-31)
                sysex[dxSysexHeaderSize + 18 + patchOpOffset] =
                        (byte) ((bank.sysex[getPatchStart(patchNum) + 15
                                            + bankOpOffset] & 62) / 2);
                // Osc Frequency Fine .........(0-99)
                sysex[dxSysexHeaderSize + 19 + patchOpOffset] =
                        bank.sysex[getPatchStart(patchNum) + 16 + bankOpOffset];
                // Osc Detune .................(0-14)
                sysex[dxSysexHeaderSize + 20 + patchOpOffset] =
                        (byte) ((bank.sysex[getPatchStart(patchNum) + 12
                                            + bankOpOffset] & 120) / 8);
            }

            // ***** other Parameters *****
            // Pitch EG Rate 1 ............(0-99)
            sysex[dxSysexHeaderSize + 126] =
                    bank.sysex[getPatchStart(patchNum) + 102];
            // Pitch EG Rate 2 ............(0-99)
            sysex[dxSysexHeaderSize + 127] =
                    bank.sysex[getPatchStart(patchNum) + 103];
            // Pitch EG Rate 3 ............(0-99)
            sysex[dxSysexHeaderSize + 128] =
                    bank.sysex[getPatchStart(patchNum) + 104];
            // Pitch EG Rate 4 ............(0-99)
            sysex[dxSysexHeaderSize + 129] =
                    bank.sysex[getPatchStart(patchNum) + 105];
            // Pitch EG Level 1 ...........(0-99)
            sysex[dxSysexHeaderSize + 130] =
                    bank.sysex[getPatchStart(patchNum) + 106];
            // Pitch EG Level 2 ...........(0-99)
            sysex[dxSysexHeaderSize + 131] =
                    bank.sysex[getPatchStart(patchNum) + 107];
            // Pitch EG Level 3 ...........(0-99)
            sysex[dxSysexHeaderSize + 132] =
                    bank.sysex[getPatchStart(patchNum) + 108];
            // Pitch EG Level 4 ...........(0-99)
            sysex[dxSysexHeaderSize + 133] =
                    bank.sysex[getPatchStart(patchNum) + 109];
            // Algorithmic Select .........(0-31)
            sysex[dxSysexHeaderSize + 134] =
                    (byte) (bank.sysex[getPatchStart(patchNum) + 110] & 31);
            // Feedback ....................(0-7)
            sysex[dxSysexHeaderSize + 135] =
                    (byte) (bank.sysex[getPatchStart(patchNum) + 111] & 7);
            // Oscillator Sync .............(0-1)
            sysex[dxSysexHeaderSize + 136] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 111] & 8) / 8);
            // LFO Speed ..................(0-99)
            sysex[dxSysexHeaderSize + 137] =
                    bank.sysex[getPatchStart(patchNum) + 112];
            // LFO Delay ..................(0-99)
            sysex[dxSysexHeaderSize + 138] =
                    bank.sysex[getPatchStart(patchNum) + 113];
            // LFO PMD ....................(0-99)
            sysex[dxSysexHeaderSize + 139] =
                    bank.sysex[getPatchStart(patchNum) + 114];
            // LFO AMD ....................(0-99)
            sysex[dxSysexHeaderSize + 140] =
                    bank.sysex[getPatchStart(patchNum) + 115];
            // LFO Sync ....................(0-1)
            sysex[dxSysexHeaderSize + 141] =
                    (byte) (bank.sysex[getPatchStart(patchNum) + 116] & 1);
            // LFO Wave ....................(0-5)
            sysex[dxSysexHeaderSize + 142] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 116] & 14) / 2);
            // LFO Mod Sensitivity Pitch ...(0-7)
            sysex[dxSysexHeaderSize + 143] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 116] & 112) / 16);
            // Transpose ................. (0-48)
            sysex[dxSysexHeaderSize + 144] =
                    bank.sysex[getPatchStart(patchNum) + 117];
            // Voice name 1 .............. ASCII
            sysex[dxSysexHeaderSize + 145] =
                    bank.sysex[getPatchStart(patchNum) + 118];
            // Voice name 2 .............. ASCII
            sysex[dxSysexHeaderSize + 146] =
                    bank.sysex[getPatchStart(patchNum) + 119];
            // Voice name 3 .............. ASCII
            sysex[dxSysexHeaderSize + 147] =
                    bank.sysex[getPatchStart(patchNum) + 120];
            // Voice name 4 .............. ASCII
            sysex[dxSysexHeaderSize + 148] =
                    bank.sysex[getPatchStart(patchNum) + 121];
            // Voice name 5 .............. ASCII
            sysex[dxSysexHeaderSize + 149] =
                    bank.sysex[getPatchStart(patchNum) + 122];
            // Voice name 6 .............. ASCII
            sysex[dxSysexHeaderSize + 150] =
                    bank.sysex[getPatchStart(patchNum) + 123];
            // Voice name 7 .............. ASCII
            sysex[dxSysexHeaderSize + 151] =
                    bank.sysex[getPatchStart(patchNum) + 124];
            // Voice name 8 .............. ASCII
            sysex[dxSysexHeaderSize + 152] =
                    bank.sysex[getPatchStart(patchNum) + 125];
            // Voice name 9 .............. ASCII
            sysex[dxSysexHeaderSize + 153] =
                    bank.sysex[getPatchStart(patchNum) + 126];
            // Voice name 10 .............. ASCII
            sysex[dxSysexHeaderSize + 154] =
                    bank.sysex[getPatchStart(patchNum) + 127];

            sysex[singleSize - 1] = (byte) 0xF7;

            Patch p = getPatchFactory().createNewPatch(sysex, getDevice()); // single
            // sysex
            p.calculateChecksum();

            return p;
        } catch (Exception e) {
            ErrorMsg.reportError(getManufacturerName() + " " + getModelName(),
                    "Error in " + toString(), e);
            return null;
        }
    }

    /*
     * create a bank with 32 "init voice"-patches
     */
    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    public boolean isSpbp() {
        return spbp;
    }

    public void setSpbp(boolean spbp) {
        this.spbp = spbp;
    }

    public boolean isSwOffMemProt() {
        return swOffMemProt;
    }

    public void setSwOffMemProt(boolean swOffMemProt) {
        this.swOffMemProt = swOffMemProt;
    }

    public boolean isTipsMsg() {
        return tipsMsg;
    }

    public void setTipsMsg(boolean tipsMsg) {
        this.tipsMsg = tipsMsg;
    }

    public int getDxSysexHeaderSize() {
        return dxSysexHeaderSize;
    }

    public void setDxSysexHeaderSize(int dxSysexHeaderSize) {
        this.dxSysexHeaderSize = dxSysexHeaderSize;
    }

    public int getDxPatchNameOffset() {
        return dxPatchNameOffset;
    }

    public void setDxPatchNameOffset(int dxPatchNameOffset) {
        this.dxPatchNameOffset = dxPatchNameOffset;
    }

    public int getDxSinglePackedSize() {
        return dxSinglePackedSize;
    }

    public void setDxSinglePackedSize(int dxSinglePackedSize) {
        this.dxSinglePackedSize = dxSinglePackedSize;
    }
}
