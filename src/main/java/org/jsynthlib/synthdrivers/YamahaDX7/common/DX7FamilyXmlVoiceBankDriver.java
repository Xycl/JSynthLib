package org.jsynthlib.synthdrivers.YamahaDX7.common;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlBankDriverSpecDocument.XmlBankDriverSpec;

public class DX7FamilyXmlVoiceBankDriver extends XMLBankDriver {

    private boolean spbp;
    private boolean swOffMemProt;
    private boolean tipsMsg;

    private int dxSysexHeaderSize;
    private int dxPatchNameOffset;
    private int dxSinglePackedSize;

    public DX7FamilyXmlVoiceBankDriver(XmlBankDriverSpec driverSpec) {
        super(driverSpec);
    }

    public int getPatchStart(int patchNum) {
        return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
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

        // ***** OPERATOR 6 *****
        bank.sysex[getPatchStart(patchNum) + 0] =
                ((p.sysex[6 + 0])); // EG Rate 1
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 1] =
                ((p.sysex[6 + 1])); // EG Rate 2
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 2] =
                ((p.sysex[6 + 2])); // EG Rate 3
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 3] =
                ((p.sysex[6 + 3])); // EG Rate 4
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 4] =
                ((p.sysex[6 + 4])); // EG Level 1
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 5] =
                ((p.sysex[6 + 5])); // EG Level 2
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 6] =
                ((p.sysex[6 + 6])); // EG Level 3
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 7] =
                ((p.sysex[6 + 7])); // EG Level 4
                                                     // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 8] =
                ((p.sysex[6 + 8])); // Kbd Level Scale Break
                                                     // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 9] =
                ((p.sysex[6 + 9])); // Kbd Level Scale Left
                                                     // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 10] =
                ((p.sysex[6 + 10])); // Kbd Level Scale Right
                                                      // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 11] =
                (byte) ((p.sysex[6 + 12] * 4 + p.sysex[6 + 11])); // Kbd
                                                                                      // Level
                                                                                      // Scale
                                                                                      // Right
                                                                                      // Curve
                                                                                      // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 12] =
                (byte) ((p.sysex[6 + 20] * 8 + p.sysex[6 + 13])); // Osc
                                                                                      // Detune
                                                                                      // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 13] =
                (byte) ((p.sysex[6 + 15] * 4 + p.sysex[6 + 14])); // Key
                                                                                      // Velocity
                                                                                      // Sensitivity
                                                                                      // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 14] =
                ((p.sysex[6 + 16])); // Operator Output Level
                                                      // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 15] =
                (byte) ((p.sysex[6 + 18] * 2 + p.sysex[6 + 17])); // Osc
                                                                                      // Frequency
                                                                                      // Coarse
                                                                                      // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 16] =
                ((p.sysex[6 + 19])); // Osc Frequency Fine
                                                      // .........(0-99)

        // ***** OPERATOR 5 *****
        bank.sysex[getPatchStart(patchNum) + 17] =
                ((p.sysex[6 + 21])); // EG Rate 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 18] =
                ((p.sysex[6 + 22])); // EG Rate 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 19] =
                ((p.sysex[6 + 23])); // EG Rate 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 20] =
                ((p.sysex[6 + 24])); // EG Rate 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 21] =
                ((p.sysex[6 + 25])); // EG Level 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 22] =
                ((p.sysex[6 + 26])); // EG Level 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 23] =
                ((p.sysex[6 + 27])); // EG Level 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 24] =
                ((p.sysex[6 + 28])); // EG Level 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 25] =
                ((p.sysex[6 + 29])); // Kbd Level Scale Break
                                                      // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 26] =
                ((p.sysex[6 + 30])); // Kbd Level Scale Left
                                                      // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 27] =
                ((p.sysex[6 + 31])); // Kbd Level Scale Right
                                                      // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 28] =
                (byte) ((p.sysex[6 + 33] * 4 + p.sysex[6 + 32])); // Kbd
                                                                                      // Level
                                                                                      // Scale
                                                                                      // Right
                                                                                      // Curve
                                                                                      // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 29] =
                (byte) ((p.sysex[6 + 41] * 8 + p.sysex[6 + 34])); // Osc
                                                                                      // Detune
                                                                                      // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 30] =
                (byte) ((p.sysex[6 + 36] * 4 + p.sysex[6 + 35])); // Key
                                                                                      // Velocity
                                                                                      // Sensitivity
                                                                                      // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 31] =
                ((p.sysex[6 + 37])); // Operator Output Level
                                                      // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 32] =
                (byte) ((p.sysex[6 + 39] * 2 + p.sysex[6 + 38])); // Osc
                                                                                      // Frequency
                                                                                      // Coarse
                                                                                      // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 33] =
                ((p.sysex[6 + 40])); // Osc Frequency Fine
                                                      // .........(0-99)

        // ***** OPERATOR 4 *****
        bank.sysex[getPatchStart(patchNum) + 34] =
                ((p.sysex[6 + 42])); // EG Rate 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 35] =
                ((p.sysex[6 + 43])); // EG Rate 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 36] =
                ((p.sysex[6 + 44])); // EG Rate 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 37] =
                ((p.sysex[6 + 45])); // EG Rate 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 38] =
                ((p.sysex[6 + 46])); // EG Level 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 39] =
                ((p.sysex[6 + 47])); // EG Level 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 40] =
                ((p.sysex[6 + 48])); // EG Level 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 41] =
                ((p.sysex[6 + 49])); // EG Level 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 42] =
                ((p.sysex[6 + 50])); // Kbd Level Scale Break
                                                      // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 43] =
                ((p.sysex[6 + 51])); // Kbd Level Scale Left
                                                      // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 44] =
                ((p.sysex[6 + 52])); // Kbd Level Scale Right
                                                      // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 45] =
                (byte) ((p.sysex[6 + 54] * 4 + p.sysex[6 + 53])); // Kbd
                                                                                      // Level
                                                                                      // Scale
                                                                                      // Right
                                                                                      // Curve
                                                                                      // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 46] =
                (byte) ((p.sysex[6 + 62] * 8 + p.sysex[6 + 55])); // Osc
                                                                                      // Detune
                                                                                      // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 47] =
                (byte) ((p.sysex[6 + 57] * 4 + p.sysex[6 + 56])); // Key
                                                                                      // Velocity
                                                                                      // Sensitivity
                                                                                      // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 48] =
                ((p.sysex[6 + 58])); // Operator Output Level
                                                      // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 49] =
                (byte) ((p.sysex[6 + 60] * 2 + p.sysex[6 + 59])); // Osc
                                                                                      // Frequency
                                                                                      // Coarse
                                                                                      // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 50] =
                ((p.sysex[6 + 61])); // Osc Frequency Fine
                                                      // .........(0-99)

        // ***** OPERATOR 3 *****
        bank.sysex[getPatchStart(patchNum) + 51] =
                ((p.sysex[6 + 63])); // EG Rate 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 52] =
                ((p.sysex[6 + 64])); // EG Rate 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 53] =
                ((p.sysex[6 + 65])); // EG Rate 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 54] =
                ((p.sysex[6 + 66])); // EG Rate 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 55] =
                ((p.sysex[6 + 67])); // EG Level 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 56] =
                ((p.sysex[6 + 68])); // EG Level 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 57] =
                ((p.sysex[6 + 69])); // EG Level 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 58] =
                ((p.sysex[6 + 70])); // EG Level 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 59] =
                ((p.sysex[6 + 71])); // Kbd Level Scale Break
                                                      // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 60] =
                ((p.sysex[6 + 72])); // Kbd Level Scale Left
                                                      // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 61] =
                ((p.sysex[6 + 73])); // Kbd Level Scale Right
                                                      // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 62] =
                (byte) ((p.sysex[6 + 75] * 4 + p.sysex[6 + 74])); // Kbd
                                                                                      // Level
                                                                                      // Scale
                                                                                      // Right
                                                                                      // Curve
                                                                                      // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 63] =
                (byte) ((p.sysex[6 + 83] * 8 + p.sysex[6 + 76])); // Osc
                                                                                      // Detune
                                                                                      // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 64] =
                (byte) ((p.sysex[6 + 78] * 4 + p.sysex[6 + 77])); // Key
                                                                                      // Velocity
                                                                                      // Sensitivity
                                                                                      // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 65] =
                ((p.sysex[6 + 79])); // Operator Output Level
                                                      // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 66] =
                (byte) ((p.sysex[6 + 81] * 2 + p.sysex[6 + 80])); // Osc
                                                                                      // Frequency
                                                                                      // Coarse
                                                                                      // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 67] =
                ((p.sysex[6 + 82])); // Osc Frequency Fine
                                                      // .........(0-99)

        // ***** OPERATOR 2 *****
        bank.sysex[getPatchStart(patchNum) + 68] =
                ((p.sysex[6 + 84])); // EG Rate 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 69] =
                ((p.sysex[6 + 85])); // EG Rate 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 70] =
                ((p.sysex[6 + 86])); // EG Rate 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 71] =
                ((p.sysex[6 + 87])); // EG Rate 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 72] =
                ((p.sysex[6 + 88])); // EG Level 1
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 73] =
                ((p.sysex[6 + 89])); // EG Level 2
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 74] =
                ((p.sysex[6 + 90])); // EG Level 3
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 75] =
                ((p.sysex[6 + 91])); // EG Level 4
                                                      // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 76] =
                ((p.sysex[6 + 92])); // Kbd Level Scale Break
                                                      // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 77] =
                ((p.sysex[6 + 93])); // Kbd Level Scale Left
                                                      // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 78] =
                ((p.sysex[6 + 94])); // Kbd Level Scale Right
                                                      // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 79] =
                (byte) ((p.sysex[6 + 96] * 4 + p.sysex[6 + 95])); // Kbd
                                                                                      // Level
                                                                                      // Scale
                                                                                      // Right
                                                                                      // Curve
                                                                                      // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 80] =
                (byte) ((p.sysex[6 + 104] * 8 + p.sysex[6 + 97])); // Osc
                                                                                       // Detune
                                                                                       // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 81] =
                (byte) ((p.sysex[6 + 99] * 4 + p.sysex[6 + 98])); // Key
                                                                                      // Velocity
                                                                                      // Sensitivity
                                                                                      // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 82] =
                ((p.sysex[6 + 100])); // Operator Output Level
                                                       // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 83] =
                (byte) ((p.sysex[6 + 102] * 2 + p.sysex[6 + 101])); // Osc
                                                                                        // Frequency
                                                                                        // Coarse
                                                                                        // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 84] =
                ((p.sysex[6 + 103])); // Osc Frequency Fine
                                                       // .........(0-99)

        // ***** OPERATOR 1 *****
        bank.sysex[getPatchStart(patchNum) + 85] =
                ((p.sysex[6 + 105])); // EG Rate 1
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 86] =
                ((p.sysex[6 + 106])); // EG Rate 2
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 87] =
                ((p.sysex[6 + 107])); // EG Rate 3
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 88] =
                ((p.sysex[6 + 108])); // EG Rate 4
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 89] =
                ((p.sysex[6 + 109])); // EG Level 1
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 90] =
                ((p.sysex[6 + 110])); // EG Level 2
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 91] =
                ((p.sysex[6 + 111])); // EG Level 3
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 92] =
                ((p.sysex[6 + 112])); // EG Level 4
                                                       // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 93] =
                ((p.sysex[6 + 113])); // Kbd Level Scale Break
                                                       // Point (0-99)
        bank.sysex[getPatchStart(patchNum) + 94] =
                ((p.sysex[6 + 114])); // Kbd Level Scale Left
                                                       // Depth .(0-99)
        bank.sysex[getPatchStart(patchNum) + 95] =
                ((p.sysex[6 + 115])); // Kbd Level Scale Right
                                                       // Depth (0-99)
        bank.sysex[getPatchStart(patchNum) + 96] =
                (byte) ((p.sysex[6 + 117] * 4 + p.sysex[6 + 116])); // Kbd
                                                                                        // Level
                                                                                        // Scale
                                                                                        // Right
                                                                                        // Curve
                                                                                        // .(0-3)
        // | Left Curve ...............(0-3)
        bank.sysex[getPatchStart(patchNum) + 97] =
                (byte) ((p.sysex[6 + 125] * 8 + p.sysex[6 + 118])); // Osc
                                                                                        // Detune
                                                                                        // .................(0-14)
        // | Kbd Rate Scaling .........(0-7)
        bank.sysex[getPatchStart(patchNum) + 98] =
                (byte) ((p.sysex[6 + 120] * 4 + p.sysex[6 + 119])); // Key
                                                                                        // Velocity
                                                                                        // Sensitivity
                                                                                        // ....(0-7)
        // | Mod Sensitivity Amplitude (0-3)
        bank.sysex[getPatchStart(patchNum) + 99] =
                ((p.sysex[6 + 121])); // Operator Output Level
                                                       // ......(0-99)
        bank.sysex[getPatchStart(patchNum) + 100] =
                (byte) ((p.sysex[6 + 123] * 2 + p.sysex[6 + 122])); // Osc
                                                                                        // Frequency
                                                                                        // Coarse
                                                                                        // .......(0-31)
        // | Osc Mode .................(0-1)
        bank.sysex[getPatchStart(patchNum) + 101] =
                ((p.sysex[6 + 124])); // Osc Frequency Fine
                                                       // .........(0-99)

        // ***** other Parameters *****
        bank.sysex[getPatchStart(patchNum) + 102] =
                ((p.sysex[6 + 126])); // Pitch EG Rate 1
                                                       // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 103] =
                ((p.sysex[6 + 127])); // Pitch EG Rate 2
                                                       // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 104] =
                ((p.sysex[6 + 128])); // Pitch EG Rate 3
                                                       // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 105] =
                ((p.sysex[6 + 129])); // Pitch EG Rate 4
                                                       // ............(0-99)
        bank.sysex[getPatchStart(patchNum) + 106] =
                ((p.sysex[6 + 130])); // Pitch EG Level 1
                                                       // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 107] =
                ((p.sysex[6 + 131])); // Pitch EG Level 2
                                                       // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 108] =
                ((p.sysex[6 + 132])); // Pitch EG Level 3
                                                       // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 109] =
                ((p.sysex[6 + 133])); // Pitch EG Level 4
                                                       // ...........(0-99)
        bank.sysex[getPatchStart(patchNum) + 110] =
                ((p.sysex[6 + 134])); // Algorithmic Select
                                                       // .........(0-31)
        bank.sysex[getPatchStart(patchNum) + 111] =
                (byte) ((p.sysex[6 + 136] * 8 + p.sysex[6 + 135])); // Oscillator
                                                                                        // Sync
                                                                                        // .............(0-1)|
                                                                                        // Feedback
                                                                                        // (0-7)
        bank.sysex[getPatchStart(patchNum) + 112] =
                ((p.sysex[6 + 137])); // LFO Speed
                                                       // ..................(0-99)
        bank.sysex[getPatchStart(patchNum) + 113] =
                ((p.sysex[6 + 138])); // LFO Delay
                                                       // ..................(0-99)
        bank.sysex[getPatchStart(patchNum) + 114] =
                ((p.sysex[6 + 139])); // LFO PMD
                                                       // ....................(0-99)
        bank.sysex[getPatchStart(patchNum) + 115] =
                ((p.sysex[6 + 140])); // LFO AMD
                                                       // ....................(0-99)
        bank.sysex[getPatchStart(patchNum) + 116] =
                (byte) ((p.sysex[6 + 143] * 16
                        + p.sysex[6 + 142] * 2 + p.sysex[6 + 141]));
        // LFO Mod Sensitivity Pitch ...(0-7)
        // | LFO Wave (0-5)| LFO Sync (0-1)
        bank.sysex[getPatchStart(patchNum) + 117] =
                ((p.sysex[6 + 144])); // Transpose
                                                       // ..................(0-48)
        bank.sysex[getPatchStart(patchNum) + 118] =
                ((p.sysex[6 + 145])); // Voice name 1
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 119] =
                ((p.sysex[6 + 146])); // Voice name 2
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 120] =
                ((p.sysex[6 + 147])); // Voice name 3
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 121] =
                ((p.sysex[6 + 148])); // Voice name 4
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 122] =
                ((p.sysex[6 + 149])); // Voice name 5
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 123] =
                ((p.sysex[6 + 150])); // Voice name 6
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 124] =
                ((p.sysex[6 + 151])); // Voice name 7
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 125] =
                ((p.sysex[6 + 152])); // Voice name 8
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 126] =
                ((p.sysex[6 + 153])); // Voice name 9
                                                       // ...............ASCII
        bank.sysex[getPatchStart(patchNum) + 127] =
                ((p.sysex[6 + 154])); // Voice name 10
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

            // ***** OPERATOR 6 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 0] = ((bank.sysex[getPatchStart(patchNum) + 0]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 1] = ((bank.sysex[getPatchStart(patchNum) + 1]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 2] = ((bank.sysex[getPatchStart(patchNum) + 2]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 3] = ((bank.sysex[getPatchStart(patchNum) + 3]));
            // EG Level 1 .................(0-99)
            sysex[6 + 4] = ((bank.sysex[getPatchStart(patchNum) + 4]));
            // EG Level 2 .................(0-99)
            sysex[6 + 5] = ((bank.sysex[getPatchStart(patchNum) + 5]));
            // EG Level 3 .................(0-99)
            sysex[6 + 6] = ((bank.sysex[getPatchStart(patchNum) + 6]));
            // EG Level 4 .................(0-99)
            sysex[6 + 7] = ((bank.sysex[getPatchStart(patchNum) + 7]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 8] = ((bank.sysex[getPatchStart(patchNum) + 8]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 9] = ((bank.sysex[getPatchStart(patchNum) + 9]));

            sysex[6 + 10] = ((bank.sysex[getPatchStart(patchNum) + 10]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 11] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 11] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 12] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 11] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 13] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 12] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 14] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 13] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 15] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 13] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 16] = ((bank.sysex[getPatchStart(patchNum) + 14]));
            // Osc Mode ....................(0-1)
            sysex[6 + 17] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 15] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 18] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 15] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 19] = ((bank.sysex[getPatchStart(patchNum) + 16]));
            // Osc Detune .................(0-14)
            sysex[6 + 20] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 12] & 120) / 8);

            // ***** OPERATOR 5 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 21] = ((bank.sysex[getPatchStart(patchNum) + 17]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 22] = ((bank.sysex[getPatchStart(patchNum) + 18]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 23] = ((bank.sysex[getPatchStart(patchNum) + 19]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 24] = ((bank.sysex[getPatchStart(patchNum) + 20]));
            // EG Level 1 .................(0-99)
            sysex[6 + 25] = ((bank.sysex[getPatchStart(patchNum) + 21]));
            // EG Level 2 .................(0-99)
            sysex[6 + 26] = ((bank.sysex[getPatchStart(patchNum) + 22]));
            // EG Level 3 .................(0-99)
            sysex[6 + 27] = ((bank.sysex[getPatchStart(patchNum) + 23]));
            // EG Level 4 .................(0-99)
            sysex[6 + 28] = ((bank.sysex[getPatchStart(patchNum) + 24]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 29] = ((bank.sysex[getPatchStart(patchNum) + 25]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 30] = ((bank.sysex[getPatchStart(patchNum) + 26]));
            // Kbd Level Scale Right Depth (0-99)
            sysex[6 + 31] = ((bank.sysex[getPatchStart(patchNum) + 27]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 32] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 28] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 33] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 28] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 34] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 29] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 35] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 30] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 36] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 30] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 37] = ((bank.sysex[getPatchStart(patchNum) + 31]));
            // Osc Mode ....................(0-1)
            sysex[6 + 38] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 32] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 39] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 32] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 40] = ((bank.sysex[getPatchStart(patchNum) + 33]));
            // Osc Detune .................(0-14)
            sysex[6 + 41] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 29] & 120) / 8);

            // ***** OPERATOR 4 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 42] = ((bank.sysex[getPatchStart(patchNum) + 34]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 43] = ((bank.sysex[getPatchStart(patchNum) + 35]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 44] = ((bank.sysex[getPatchStart(patchNum) + 36]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 45] = ((bank.sysex[getPatchStart(patchNum) + 37]));
            // EG Level 1 .................(0-99)
            sysex[6 + 46] = ((bank.sysex[getPatchStart(patchNum) + 38]));
            // EG Level 2 .................(0-99)
            sysex[6 + 47] = ((bank.sysex[getPatchStart(patchNum) + 39]));
            // EG Level 3 .................(0-99)
            sysex[6 + 48] = ((bank.sysex[getPatchStart(patchNum) + 40]));
            // EG Level 4 .................(0-99)
            sysex[6 + 49] = ((bank.sysex[getPatchStart(patchNum) + 41]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 50] = ((bank.sysex[getPatchStart(patchNum) + 42]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 51] = ((bank.sysex[getPatchStart(patchNum) + 43]));
            // Kbd Level Scale Right Depth (0-99)
            sysex[6 + 52] = ((bank.sysex[getPatchStart(patchNum) + 44]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 53] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 45] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 54] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 45] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 55] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 46] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 56] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 47] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 57] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 47] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 58] = ((bank.sysex[getPatchStart(patchNum) + 48]));
            // Osc Mode ....................(0-1)
            sysex[6 + 59] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 49] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 60] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 49] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 61] = ((bank.sysex[getPatchStart(patchNum) + 50]));
            // Osc Detune .................(0-14)
            sysex[6 + 62] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 46] & 120) / 8);

            // ***** OPERATOR 3 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 63] = ((bank.sysex[getPatchStart(patchNum) + 51]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 64] = ((bank.sysex[getPatchStart(patchNum) + 52]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 65] = ((bank.sysex[getPatchStart(patchNum) + 53]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 66] = ((bank.sysex[getPatchStart(patchNum) + 54]));
            // EG Level 1 .................(0-99)
            sysex[6 + 67] = ((bank.sysex[getPatchStart(patchNum) + 55]));
            // EG Level 2 .................(0-99)
            sysex[6 + 68] = ((bank.sysex[getPatchStart(patchNum) + 56]));
            // EG Level 3 .................(0-99)
            sysex[6 + 69] = ((bank.sysex[getPatchStart(patchNum) + 57]));
            // EG Level 4 .................(0-99)
            sysex[6 + 70] = ((bank.sysex[getPatchStart(patchNum) + 58]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 71] = ((bank.sysex[getPatchStart(patchNum) + 59]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 72] = ((bank.sysex[getPatchStart(patchNum) + 60]));
            // Kbd Level Scale Right Depth (0-99)
            sysex[6 + 73] = ((bank.sysex[getPatchStart(patchNum) + 61]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 74] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 62] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 75] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 62] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 76] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 63] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 77] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 64] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 78] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 64] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 79] = ((bank.sysex[getPatchStart(patchNum) + 65]));
            // Osc Mode ....................(0-1)
            sysex[6 + 80] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 66] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 81] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 66] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 82] = ((bank.sysex[getPatchStart(patchNum) + 67]));
            // Osc Detune .................(0-14)
            sysex[6 + 83] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 63] & 120) / 8);

            // ***** OPERATOR 2 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 84] = ((bank.sysex[getPatchStart(patchNum) + 68]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 85] = ((bank.sysex[getPatchStart(patchNum) + 69]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 86] = ((bank.sysex[getPatchStart(patchNum) + 70]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 87] = ((bank.sysex[getPatchStart(patchNum) + 71]));
            // EG Level 1 .................(0-99)
            sysex[6 + 88] = ((bank.sysex[getPatchStart(patchNum) + 72]));
            // EG Level 2 .................(0-99)
            sysex[6 + 89] = ((bank.sysex[getPatchStart(patchNum) + 73]));
            // EG Level 3 .................(0-99)
            sysex[6 + 90] = ((bank.sysex[getPatchStart(patchNum) + 74]));
            // EG Level 4 .................(0-99)
            sysex[6 + 91] = ((bank.sysex[getPatchStart(patchNum) + 75]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 92] = ((bank.sysex[getPatchStart(patchNum) + 76]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 93] = ((bank.sysex[getPatchStart(patchNum) + 77]));
            // Kbd Level Scale Right Depth (0-99)
            sysex[6 + 94] = ((bank.sysex[getPatchStart(patchNum) + 78]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 95] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 79] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 96] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 79] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 97] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 80] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 98] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 81] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 99] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 81] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 100] = ((bank.sysex[getPatchStart(patchNum) + 82]));
            // Osc Mode ....................(0-1)
            sysex[6 + 101] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 83] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 102] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 83] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 103] = ((bank.sysex[getPatchStart(patchNum) + 84]));
            // Osc Detune .................(0-14)
            sysex[6 + 104] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 80] & 120) / 8);

            // ***** OPERATOR 1 *****
            // EG Rate 1 .................(0-99)
            sysex[6 + 105] = ((bank.sysex[getPatchStart(patchNum) + 85]));
            // EG Rate 2 .................(0-99)
            sysex[6 + 106] = ((bank.sysex[getPatchStart(patchNum) + 86]));
            // EG Rate 3 .................(0-99)
            sysex[6 + 107] = ((bank.sysex[getPatchStart(patchNum) + 87]));
            // EG Rate 4 .................(0-99)
            sysex[6 + 108] = ((bank.sysex[getPatchStart(patchNum) + 88]));
            // EG Level 1 .................(0-99)
            sysex[6 + 109] = ((bank.sysex[getPatchStart(patchNum) + 89]));
            // EG Level 2 .................(0-99)
            sysex[6 + 110] = ((bank.sysex[getPatchStart(patchNum) + 90]));
            // EG Level 3 .................(0-99)
            sysex[6 + 111] = ((bank.sysex[getPatchStart(patchNum) + 91]));
            // EG Level 4 .................(0-99)
            sysex[6 + 112] = ((bank.sysex[getPatchStart(patchNum) + 92]));
            // Kbd Level Scale Break Point (0-99)
            sysex[6 + 113] = ((bank.sysex[getPatchStart(patchNum) + 93]));
            // Kbd Level Scale Left Depth .(0-99)
            sysex[6 + 114] = ((bank.sysex[getPatchStart(patchNum) + 94]));
            // Kbd Level Scale Right Depth (0-99)
            sysex[6 + 115] = ((bank.sysex[getPatchStart(patchNum) + 95]));
            // Kbd Level Scale Left Curve ..(0-3)
            sysex[6 + 116] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 96] & 3));
            // Kbd Level Scale Right Curve .(0-3)
            sysex[6 + 117] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 96] & 12) / 4);
            // Kbd Rate Scaling ............(0-7)
            sysex[6 + 118] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 97] & 7));
            // Mod Sensitivity Amplitude ...(0-3)
            sysex[6 + 119] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 98] & 3));
            // Key Velocity Sensitivity ....(0-7)
            sysex[6 + 120] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 98] & 28) / 4);
            // Operator Output Level ......(0-99)
            sysex[6 + 121] = ((bank.sysex[getPatchStart(patchNum) + 99]));
            // Osc Mode ....................(0-1)
            sysex[6 + 122] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 100] & 1));
            // Osc Frequency Coarse .......(0-31)
            sysex[6 + 123] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 100] & 62) / 2);
            // Osc Frequency Fine .........(0-99)
            sysex[6 + 124] = ((bank.sysex[getPatchStart(patchNum) + 101]));
            // Osc Detune .................(0-14)
            sysex[6 + 125] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 97] & 120) / 8);

            // ***** other Parameters *****
            // Pitch EG Rate 1 ............(0-99)
            sysex[6 + 126] = ((bank.sysex[getPatchStart(patchNum) + 102]));
            // Pitch EG Rate 2 ............(0-99)
            sysex[6 + 127] = ((bank.sysex[getPatchStart(patchNum) + 103]));
            // Pitch EG Rate 3 ............(0-99)
            sysex[6 + 128] = ((bank.sysex[getPatchStart(patchNum) + 104]));
            // Pitch EG Rate 4 ............(0-99)
            sysex[6 + 129] = ((bank.sysex[getPatchStart(patchNum) + 105]));
            // Pitch EG Level 1 ...........(0-99)
            sysex[6 + 130] = ((bank.sysex[getPatchStart(patchNum) + 106]));
            // Pitch EG Level 2 ...........(0-99)
            sysex[6 + 131] = ((bank.sysex[getPatchStart(patchNum) + 107]));
            // Pitch EG Level 3 ...........(0-99)
            sysex[6 + 132] = ((bank.sysex[getPatchStart(patchNum) + 108]));
            // Pitch EG Level 4 ...........(0-99)
            sysex[6 + 133] = ((bank.sysex[getPatchStart(patchNum) + 109]));
            // Algorithmic Select .........(0-31)
            sysex[6 + 134] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 110] & 31));
            // Feedback ....................(0-7)
            sysex[6 + 135] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 111] & 7));
            // Oscillator Sync .............(0-1)
            sysex[6 + 136] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 111] & 8) / 8);
            // LFO Speed ..................(0-99)
            sysex[6 + 137] = ((bank.sysex[getPatchStart(patchNum) + 112]));
            // LFO Delay ..................(0-99)
            sysex[6 + 138] = ((bank.sysex[getPatchStart(patchNum) + 113]));
            // LFO PMD ....................(0-99)
            sysex[6 + 139] = ((bank.sysex[getPatchStart(patchNum) + 114]));
            // LFO AMD ....................(0-99)
            sysex[6 + 140] = ((bank.sysex[getPatchStart(patchNum) + 115]));
            // LFO Sync ....................(0-1)
            sysex[6 + 141] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 116] & 1));
            // LFO Wave ....................(0-5)
            sysex[6 + 142] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 116] & 14) / 2);
            // LFO Mod Sensitivity Pitch ...(0-7)
            sysex[6 + 143] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 116] & 112) / 16);
            // Transpose ................. (0-48)
            sysex[6 + 144] = ((bank.sysex[getPatchStart(patchNum) + 117]));
            // Voice name 1 .............. ASCII
            sysex[6 + 145] = ((bank.sysex[getPatchStart(patchNum) + 118]));
            // Voice name 2 .............. ASCII
            sysex[6 + 146] = ((bank.sysex[getPatchStart(patchNum) + 119]));
            // Voice name 3 .............. ASCII
            sysex[6 + 147] = ((bank.sysex[getPatchStart(patchNum) + 120]));
            // Voice name 4 .............. ASCII
            sysex[6 + 148] = ((bank.sysex[getPatchStart(patchNum) + 121]));
            // Voice name 5 .............. ASCII
            sysex[6 + 149] = ((bank.sysex[getPatchStart(patchNum) + 122]));
            // Voice name 6 .............. ASCII
            sysex[6 + 150] = ((bank.sysex[getPatchStart(patchNum) + 123]));
            // Voice name 7 .............. ASCII
            sysex[6 + 151] = ((bank.sysex[getPatchStart(patchNum) + 124]));
            // Voice name 8 .............. ASCII
            sysex[6 + 152] = ((bank.sysex[getPatchStart(patchNum) + 125]));
            // Voice name 9 .............. ASCII
            sysex[6 + 153] = ((bank.sysex[getPatchStart(patchNum) + 126]));
            // Voice name 10 .............. ASCII
            sysex[6 + 154] = ((bank.sysex[getPatchStart(patchNum) + 127]));

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
