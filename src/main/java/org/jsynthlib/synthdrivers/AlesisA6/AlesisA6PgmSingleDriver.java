// written by Kenneth L. Martinez
//
// @version $Id: AlesisA6PgmSingleDriver.java 677 2004-08-21 14:39:35Z hayashi $

package org.jsynthlib.synthdrivers.AlesisA6;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class AlesisA6PgmSingleDriver extends AbstractPatchDriver {
    static final String bankList[] = new String[] {
            "User", "Preset1", "Preset2", "Card 1", "Card 2", "Card 3",
            "Card 4", "Card 5", "Card 6", "Card 7", "Card 8" };
    static final String patchList[] = new String[] {
            "000", "001", "002", "003", "004", "005", "006", "007", "008",
            "009", "010", "011", "012", "013", "014", "015", "016", "017",
            "018", "019", "020", "021", "022", "023", "024", "025", "026",
            "027", "028", "029", "030", "031", "032", "033", "034", "035",
            "036", "037", "038", "039", "040", "041", "042", "043", "044",
            "045", "046", "047", "048", "049", "050", "051", "052", "053",
            "054", "055", "056", "057", "058", "059", "060", "061", "062",
            "063", "064", "065", "066", "067", "068", "069", "070", "071",
            "072", "073", "074", "075", "076", "077", "078", "079", "080",
            "081", "082", "083", "084", "085", "086", "087", "088", "089",
            "090", "091", "092", "093", "094", "095", "096", "097", "098",
            "099", "100", "101", "102", "103", "104", "105", "106", "107",
            "108", "109", "110", "111", "112", "113", "114", "115", "116",
            "117", "118", "119", "120", "121", "122", "123", "124", "125",
            "126", "127" };

    private final transient Logger log = Logger.getLogger(getClass());

    public AlesisA6PgmSingleDriver() {
        super("Prog Single", "Kenneth L. Martinez");
        sysexID = "F000000E1D00****";
        sysexRequestDump =
                new SysexHandler("F0 00 00 0E 1D 01 *bankNum* *patchNum* F7");

        patchSize = 2350;
        patchNameStart = 2; // does NOT include sysex header
        patchNameSize = 16;
        deviceIDoffset = -1;
        bankNumbers = bankList;
        patchNumbers = patchList;
    }

    public void calculateChecksum(Patch p) {
        // A6 doesn't use checksum
    }

    // protected static void calculateChecksum(Patch p, int start, int end, int
    // ofs)
    // {
    // // A6 doesn't use checksum
    // }

    // The program sysex dump is 2350 bytes, beginning with an 8-byte
    // header followed by 2341 7-bit sysex bytes which correspond to 2048 bytes
    // of program data. Here's how the first 6 program data bytes map to the
    // first 7 sysex bytes:
    //
    // program data sysex
    // MSB LSB MSB LSB
    // 0: A7 A6 A5 A4 A3 A2 A1 A0 0: 0 A6 A5 A4 A3 A2 A1 A0
    // 1: B7 B6 B5 B4 B3 B2 B1 B0 1: 0 B5 B4 B3 B2 B1 B0 A7
    // 2: C7 C6 C5 C4 C3 C2 C1 C0 2: 0 C4 C3 C2 C1 C0 B7 B6
    // 3: D7 D6 D5 D4 D3 D2 D1 D0 3: 0 D3 D2 D1 D0 C7 C6 C5
    // 4: E7 E6 E5 E4 E3 E2 E1 E0 4: 0 E2 E1 E0 D7 D6 D5 D4
    // 5: F7 F6 F5 F4 F3 F2 F1 F0 5: 0 F1 F0 E7 E6 E5 E4 E3
    // 6: G7 G6 G5 G4 G3 G2 G1 G0 6: 0 G0 F7 F6 F5 F4 F3 F2
    // 7: 0 G7 G6 G5 G4 G3 G2 G1
    //
    // getA6PgmByte returns the requested program data byte "i" (0 - 2047)
    // by putting together the corresponding bits from two sysex bytes,
    // and setA6PgmByte does the reverse to store a program data byte.
    public static byte getA6PgmByte(byte sysex[], int i) {
        int modulus = i % 7;
        int mask1 = (0xFF << modulus) & 0x7F;
        int mask2 = 0xFF >> (7 - modulus);
        int offset = i * 8 / 7;
        return (byte) (((sysex[8 + offset]) & mask1) >> modulus | ((sysex[9 + offset]) & mask2) << (7 - modulus));
    }

    public static void setA6PgmByte(byte b, byte sysex[], int i) {
        int modulus = i % 7;
        int dstMask1 = (0xFF << modulus) & 0x7F;
        int dstMask2 = 0xFF >> (7 - modulus);
        int srcMask1 = 0xFF >> (modulus + 1);
        int srcMask2 = (~srcMask1) & 0xFF;
        int offset = i * 8 / 7;
        sysex[8 + offset] =
                (byte) ((sysex[8 + offset] & ~dstMask1) | ((b & srcMask1) << modulus));
        sysex[9 + offset] =
                (byte) ((sysex[9 + offset] & ~dstMask2) | ((b & srcMask2) >> (7 - modulus)));
    }

    public String getPatchName(Patch ip) {
        Patch p = (Patch) ip;
        try {
            char c[] = new char[patchNameSize];
            for (int i = 0; i < patchNameSize; i++)
                c[i] = (char) (getA6PgmByte(p.sysex, i + patchNameStart));
            return new String(c);
        } catch (Exception ex) {
            return "-";
        }
    }

    public void setPatchName(Patch p, String name) {
        if (name.length() < patchNameSize + 4)
            name = name + "                ";
        byte nameByte[] = name.getBytes();
        for (int i = 0; i < patchNameSize; i++) {
            setA6PgmByte(nameByte[i], ((Patch) p).sysex, i + patchNameStart);
        }
    }

    public void sendPatch(Patch p) {
        sendPatch((Patch) p, 0, 127); // using user program # 127 as edit buffer
    }

    public void sendPatch(Patch p, int bankNum, int patchNum) {
        Patch p2 = getPatchFactory().createNewPatch(p.sysex);
        p2.sysex[6] = (byte) bankNum;
        p2.sysex[7] = (byte) patchNum;
        sendPatchWorker(p2);
    }

    // Sends a patch to a set location in the user bank
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (bankNum == 1 || bankNum == 2)
            PopupHandlerProvider.get().showMessage(PatchEdit.getInstance(),
                    "Cannot send to a preset bank", "Store Patch",
                    JOptionPane.WARNING_MESSAGE);
        else {
            setBankNum(bankNum);
            setPatchNum(patchNum);
            sendPatch((Patch) p, bankNum, patchNum);
        }
    }

    // Kludge: A6 doesn't seem to receive edit buffer dump, so user program 127
    // is being used for that purpose.
    public void playPatch(Patch p) {
        byte sysex[] = new byte[2352];
        System.arraycopy(((Patch) p).sysex, 0, sysex, 0, 2350);
        sysex[6] = 0; // user bank
        sysex[7] = 127; // program # 127
        sysex[2350] = (byte) (0xC0 + getChannel() - 1); // program change
        sysex[2351] = (byte) 127; // program # 127
        Patch p2 = getPatchFactory().createNewPatch(sysex);
        try {
            super.playPatch(p2);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    public Patch createNewPatch() {
        byte sysex[] =
                {
                        (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x0E,
                        (byte) 0x1D, (byte) 0x00, (byte) 0x00, (byte) 0x7F,
                        (byte) 0x26, (byte) 0x15, (byte) 0x38, (byte) 0x2A,
                        (byte) 0x76, (byte) 0x0E, (byte) 0x54, (byte) 0x30,
                        (byte) 0x74, (byte) 0x46, (byte) 0x21, (byte) 0x03,
                        (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10,
                        (byte) 0x20, (byte) 0x40, (byte) 0x00, (byte) 0x01,
                        (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x4A, (byte) 0x78, (byte) 0x65,
                        (byte) 0x24, (byte) 0x1D, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                        (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x01,
                        (byte) 0x0A, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x3E,
                        (byte) 0x10, (byte) 0x79, (byte) 0x01, (byte) 0x31,
                        (byte) 0x26, (byte) 0x56, (byte) 0x19, (byte) 0x34,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x78, (byte) 0x40, (byte) 0x77,
                        (byte) 0x03, (byte) 0x05, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3F, (byte) 0x70, (byte) 0x22, (byte) 0x01,
                        (byte) 0x0E, (byte) 0x20, (byte) 0x04, (byte) 0x00,
                        (byte) 0x0C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x20, (byte) 0x21, (byte) 0x00,
                        (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x01,
                        (byte) 0x14, (byte) 0x04, (byte) 0x20, (byte) 0x7D,
                        (byte) 0x20, (byte) 0x7F, (byte) 0x7D, (byte) 0x0F,
                        (byte) 0x11, (byte) 0x20, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x00,
                        (byte) 0x07, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x04, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x01, (byte) 0x10, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x2F, (byte) 0x1F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x7E, (byte) 0x37, (byte) 0x00,
                        (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x38,
                        (byte) 0x43, (byte) 0x01, (byte) 0x40, (byte) 0x03,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x60, (byte) 0x01, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x3F, (byte) 0x0A,
                        (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x1D, (byte) 0x00, (byte) 0x08,
                        (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x0F,
                        (byte) 0x00, (byte) 0x20, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x77, (byte) 0x3F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x7E, (byte) 0x07, (byte) 0x00,
                        (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x7B,
                        (byte) 0x1F, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x7F, (byte) 0x06,
                        (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x13, (byte) 0x00, (byte) 0x08,
                        (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x50, (byte) 0x0B, (byte) 0x1C, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x7F,
                        (byte) 0x00, (byte) 0x20, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x78,
                        (byte) 0x77, (byte) 0x5F, (byte) 0x03, (byte) 0x00,
                        (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x46, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x09, (byte) 0x00, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x68,
                        (byte) 0x00, (byte) 0x1A, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x7E, (byte) 0x27, (byte) 0x08,
                        (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x63, (byte) 0x01, (byte) 0x7F, (byte) 0x7B,
                        (byte) 0x1F, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x79, (byte) 0x01, (byte) 0x03, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x3F, (byte) 0x01,
                        (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x7B,
                        (byte) 0x0F, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x21, (byte) 0x00, (byte) 0x08,
                        (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x3F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x07,
                        (byte) 0x01, (byte) 0x20, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x77, (byte) 0x3F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x07,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x10, (byte) 0x00, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
                        (byte) 0x01, (byte) 0x18, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x7E, (byte) 0x27, (byte) 0x00,
                        (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x7B,
                        (byte) 0x2F, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x7F, (byte) 0x00,
                        (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x03, (byte) 0x00, (byte) 0x08,
                        (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x20, (byte) 0x04, (byte) 0x3E, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x77,
                        (byte) 0x00, (byte) 0x20, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x0E,
                        (byte) 0x68, (byte) 0x01, (byte) 0x00, (byte) 0x78,
                        (byte) 0x77, (byte) 0x5F, (byte) 0x03, (byte) 0x00,
                        (byte) 0x01, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x7F,
                        (byte) 0x10, (byte) 0x00, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x48,
                        (byte) 0x03, (byte) 0x19, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x7E, (byte) 0x43, (byte) 0x00,
                        (byte) 0x10, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0x0B, (byte) 0x34,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x7B,
                        (byte) 0x1F, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x2D, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x7F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x1C, (byte) 0x41,
                        (byte) 0x06, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x03, (byte) 0x00, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x7D, (byte) 0x0F,
                        (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x04,
                        (byte) 0x00, (byte) 0x10, (byte) 0x40, (byte) 0x7F,
                        (byte) 0x74, (byte) 0x01, (byte) 0x2C, (byte) 0x00,
                        (byte) 0x50, (byte) 0x63, (byte) 0x3F, (byte) 0x7A,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x40, (byte) 0x4F,
                        (byte) 0x7E, (byte) 0x01, (byte) 0x08, (byte) 0x00,
                        (byte) 0x08, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x70, (byte) 0x03, (byte) 0x00,
                        (byte) 0x70, (byte) 0x5B, (byte) 0x3E, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x1E, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x03,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7C, (byte) 0x40, (byte) 0x24,
                        (byte) 0x01, (byte) 0x26, (byte) 0x3E, (byte) 0x50,
                        (byte) 0x08, (byte) 0x00, (byte) 0x03, (byte) 0x00,
                        (byte) 0x10, (byte) 0x01, (byte) 0x58, (byte) 0x08,
                        (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x30, (byte) 0x40, (byte) 0x48,
                        (byte) 0x12, (byte) 0x44, (byte) 0x0E, (byte) 0x19,
                        (byte) 0x4B, (byte) 0x06, (byte) 0x71, (byte) 0x22,
                        (byte) 0x55, (byte) 0x2D, (byte) 0x19, (byte) 0x3F,
                        (byte) 0x76, (byte) 0x1E, (byte) 0x1E, (byte) 0x04,
                        (byte) 0x08, (byte) 0x33, (byte) 0x64, (byte) 0x54,
                        (byte) 0x22, (byte) 0x75, (byte) 0x4E, (byte) 0x5D,
                        (byte) 0x4C, (byte) 0x18, (byte) 0x77, (byte) 0x6A,
                        (byte) 0x6D, (byte) 0x4D, (byte) 0x7B, (byte) 0x3F,
                        (byte) 0x7F, (byte) 0x7F, (byte) 0x7D, (byte) 0x07,
                        (byte) 0x00, (byte) 0x40, (byte) 0x40, (byte) 0x00,
                        (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x30, (byte) 0x3D, (byte) 0x2F, (byte) 0x59,
                        (byte) 0x00, (byte) 0x54, (byte) 0x3B, (byte) 0x00,
                        (byte) 0x0A, (byte) 0x20, (byte) 0x41, (byte) 0x68,
                        (byte) 0x74, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x20, (byte) 0x40, (byte) 0x0B,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x2C,
                        (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x03, (byte) 0x20, (byte) 0x00,
                        (byte) 0x42, (byte) 0x02, (byte) 0x04, (byte) 0x10,
                        (byte) 0x10, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x6F, (byte) 0x5D, (byte) 0x5C, (byte) 0x05,
                        (byte) 0x70, (byte) 0x5D, (byte) 0x4B, (byte) 0x5B,
                        (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x04,
                        (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x40,
                        (byte) 0x00, (byte) 0x02, (byte) 0x48, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x58, (byte) 0x06,
                        (byte) 0x0D, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x18, (byte) 0x00, (byte) 0x02, (byte) 0x10,
                        (byte) 0x14, (byte) 0x20, (byte) 0x00, (byte) 0x01,
                        (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
                        (byte) 0x28, (byte) 0x00, (byte) 0x41, (byte) 0x00,
                        (byte) 0x01, (byte) 0x03, (byte) 0x06, (byte) 0x04,
                        (byte) 0x10, (byte) 0x60, (byte) 0x00, (byte) 0x47,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                        (byte) 0x10, (byte) 0x60, (byte) 0x14, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x10, (byte) 0x60, (byte) 0x35, (byte) 0x60,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
                        (byte) 0x01, (byte) 0x18, (byte) 0x00, (byte) 0x71,
                        (byte) 0x01, (byte) 0x02, (byte) 0x08, (byte) 0x10,
                        (byte) 0x20, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x2A, (byte) 0x27, (byte) 0x18,
                        (byte) 0x1A, (byte) 0x61, (byte) 0x61, (byte) 0x4B,
                        (byte) 0x30, (byte) 0x05, (byte) 0x42, (byte) 0x3E,
                        (byte) 0x20, (byte) 0x1D, (byte) 0x2A, (byte) 0x20,
                        (byte) 0x1B, (byte) 0x56, (byte) 0x00, (byte) 0x28,
                        (byte) 0x70, (byte) 0x7F, (byte) 0x3F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x50,
                        (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x5C, (byte) 0x00,
                        (byte) 0x0B, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x54, (byte) 0x7C, (byte) 0x06,
                        (byte) 0x50, (byte) 0x20, (byte) 0x41, (byte) 0x02,
                        (byte) 0x05, (byte) 0x0A, (byte) 0x0C, (byte) 0x00,
                        (byte) 0x40, (byte) 0x01, (byte) 0x03, (byte) 0x01,
                        (byte) 0x42, (byte) 0x02, (byte) 0x04, (byte) 0x08,
                        (byte) 0x00, (byte) 0x40, (byte) 0x01, (byte) 0x00,
                        (byte) 0x3C, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x01,
                        (byte) 0x4F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x34, (byte) 0x20, (byte) 0x50,
                        (byte) 0x00, (byte) 0x20, (byte) 0x01, (byte) 0x04,
                        (byte) 0x08, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x05, (byte) 0x7E, (byte) 0x7F, (byte) 0x07,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x10, (byte) 0x40, (byte) 0x00,
                        (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0x4A, (byte) 0x6F,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x18, (byte) 0x30,
                        (byte) 0x60, (byte) 0x40, (byte) 0x41, (byte) 0x01,
                        (byte) 0x00, (byte) 0x18, (byte) 0x30, (byte) 0x10,
                        (byte) 0x20, (byte) 0x28, (byte) 0x40, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x18, (byte) 0x00,
                        (byte) 0x40, (byte) 0x07, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x28,
                        (byte) 0x70, (byte) 0x0A, (byte) 0x00, (byte) 0x00,
                        (byte) 0x47, (byte) 0x11, (byte) 0x7D, (byte) 0x7F,
                        (byte) 0x0F, (byte) 0x00, (byte) 0x22, (byte) 0x40,
                        (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00,
                        (byte) 0x40, (byte) 0x64, (byte) 0x7F, (byte) 0x7F,
                        (byte) 0x71, (byte) 0x21, (byte) 0x03, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x63,
                        (byte) 0x10, (byte) 0x01, (byte) 0x02, (byte) 0x78,
                        (byte) 0x77, (byte) 0x1F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x79,
                        (byte) 0x0C, (byte) 0x00, (byte) 0x01, (byte) 0x01,
                        (byte) 0x06, (byte) 0x0A, (byte) 0x14, (byte) 0x20,
                        (byte) 0x00, (byte) 0x06, (byte) 0x03, (byte) 0x06,
                        (byte) 0x02, (byte) 0x24, (byte) 0x25, (byte) 0x08,
                        (byte) 0x10, (byte) 0x40, (byte) 0x01, (byte) 0x03,
                        (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x10,
                        (byte) 0x10, (byte) 0x12, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x6F, (byte) 0x3F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x18,
                        (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x01,
                        (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x74, (byte) 0x67, (byte) 0x50,
                        (byte) 0x09, (byte) 0x00, (byte) 0x40, (byte) 0x03,
                        (byte) 0x00, (byte) 0x30, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x64, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0C, (byte) 0x00, (byte) 0x30, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3C, (byte) 0x00, (byte) 0x70, (byte) 0x01,
                        (byte) 0x40, (byte) 0x07, (byte) 0x00, (byte) 0x1E,
                        (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x60,
                        (byte) 0x03, (byte) 0x00, (byte) 0x0F, (byte) 0x00,
                        (byte) 0x3C, (byte) 0x00, (byte) 0x70, (byte) 0x01,
                        (byte) 0x40, (byte) 0x07, (byte) 0x00, (byte) 0x1E,
                        (byte) 0x00, (byte) 0x78, (byte) 0x00, (byte) 0x60,
                        (byte) 0x03, (byte) 0x00, (byte) 0x0F, (byte) 0x00,
                        (byte) 0x3C, (byte) 0x00, (byte) 0x70, (byte) 0x01,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x08,
                        (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00,
                        (byte) 0x10, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x08,
                        (byte) 0x00, (byte) 0x20, (byte) 0x00, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x04, (byte) 0x00,
                        (byte) 0x10, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x34, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x08,
                        (byte) 0x11, (byte) 0x22, (byte) 0x44, (byte) 0x08,
                        (byte) 0x11, (byte) 0x22, (byte) 0x44, (byte) 0x08,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x18,
                        (byte) 0x10, (byte) 0x60, (byte) 0x7F, (byte) 0x17,
                        (byte) 0x00, (byte) 0x04, (byte) 0x08, (byte) 0x00,
                        (byte) 0x20, (byte) 0x20, (byte) 0x24, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x48, (byte) 0x01, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                        (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x00,
                        (byte) 0x30, (byte) 0x44, (byte) 0x40, (byte) 0x00,
                        (byte) 0x01, (byte) 0x00, (byte) 0x10, (byte) 0x00,
                        (byte) 0x30, (byte) 0x00, (byte) 0x40, (byte) 0x17,
                        (byte) 0x00, (byte) 0x04, (byte) 0x08, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A,
                        (byte) 0x47, (byte) 0x01, (byte) 0x01, (byte) 0x00,
                        (byte) 0x30, (byte) 0x20, (byte) 0x01, (byte) 0x00,
                        (byte) 0x68, (byte) 0x1D, (byte) 0x0E, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x30, (byte) 0x08, (byte) 0x2C, (byte) 0x20,
                        (byte) 0x00, (byte) 0x5A, (byte) 0x03, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
                        (byte) 0x0C, (byte) 0x00, (byte) 0x72, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x18, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x7F, (byte) 0x7F,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x51,
                        (byte) 0x10, (byte) 0x04, (byte) 0x3F, (byte) 0x56,
                        (byte) 0x00, (byte) 0x0A, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00,
                        (byte) 0x71, (byte) 0x60, (byte) 0x06, (byte) 0x00,
                        (byte) 0x00, (byte) 0x54, (byte) 0x40, (byte) 0x02,
                        (byte) 0x15, (byte) 0x03, (byte) 0x19, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x01, (byte) 0x70, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x28,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x50,
                        (byte) 0x20, (byte) 0x20, (byte) 0x00, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0xF7 };
        return getPatchFactory().createNewPatch(sysex, this);
        // setPatchName(p, "NewPatch");
    }
}
