// written by Kenneth L. Martinez
//
// @version $Id: AlesisA6MixSingleDriver.java 677 2004-08-21 14:39:35Z hayashi $

package org.jsynthlib.synthdrivers.AlesisA6;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class AlesisA6MixSingleDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public AlesisA6MixSingleDriver() {
        super("Mix Single", "Kenneth L. Martinez");
        sysexID = "F000000E1D04****";
        sysexRequestDump =
                new SysexHandler("F0 00 00 0E 1D 05 *bankNum* *patchNum* F7");
        patchSize = 1180;
        patchNameStart = 2; // does NOT include sysex header
        patchNameSize = 16;
        deviceIDoffset = -1;
        bankNumbers = AlesisA6PgmSingleDriver.bankList;
        patchNumbers = AlesisA6PgmSingleDriver.patchList;
    }

    public void calculateChecksum(Patch p) {
        // A6 doesn't use checksum
    }

    // protected static void calculateChecksum(Patch p, int start, int end, int
    // ofs)
    // {
    // // A6 doesn't use checksum
    // }

    public String getPatchName(Patch ip) {
        Patch p = (Patch) ip;
        try {
            char c[] = new char[patchNameSize];
            for (int i = 0; i < patchNameSize; i++)
                c[i] =
                        (char) (AlesisA6PgmSingleDriver.getA6PgmByte(p.sysex, i
                                + patchNameStart));
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
            AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i],
                    ((Patch) p).sysex, i + patchNameStart);
        }
    }

    public void sendPatch(Patch p) {
        sendPatch((Patch) p, 0, 127); // using user mix # 127 as edit buffer
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
            JOptionPane.showMessageDialog(PatchEdit.getInstance(),
                    "Cannot send to a preset bank", "Store Patch",
                    JOptionPane.WARNING_MESSAGE);
        else {
            setBankNum(bankNum);
            setPatchNum(patchNum);
            sendPatch((Patch) p, bankNum, patchNum);
        }
    }

    // Kludge: A6 doesn't seem to receive edit buffer dump, so user mix 127
    // is being used for that purpose.
    public void playPatch(Patch p) {
        byte sysex[] = new byte[1182];
        System.arraycopy(((Patch) p).sysex, 0, sysex, 0, 1180);
        sysex[6] = 0; // user bank
        sysex[7] = 127; // mix # 127
        sysex[1180] = (byte) (0xC0 + getChannel() - 1); // program change
        sysex[1181] = (byte) 127; // mix # 127
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
                        (byte) 0x1D, (byte) 0x04, (byte) 0x00, (byte) 0x7F,
                        (byte) 0x36, (byte) 0x15, (byte) 0x30, (byte) 0x0A,
                        (byte) 0x16, (byte) 0x2F, (byte) 0x19, (byte) 0x39,
                        (byte) 0x20, (byte) 0x20, (byte) 0x05, (byte) 0x23,
                        (byte) 0x06, (byte) 0x24, (byte) 0x0C, (byte) 0x10,
                        (byte) 0x20, (byte) 0x40, (byte) 0x00, (byte) 0x01,
                        (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x04, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x67, (byte) 0x3F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x02, (byte) 0x15,
                        (byte) 0x05, (byte) 0x2A, (byte) 0x00, (byte) 0x01,
                        (byte) 0x1E, (byte) 0x00, (byte) 0x7C, (byte) 0x0B,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x18, (byte) 0x10,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x41,
                        (byte) 0x0B, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x68,
                        (byte) 0x73, (byte) 0x1F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x41, (byte) 0x61, (byte) 0x7E, (byte) 0x7F,
                        (byte) 0x1F, (byte) 0x40, (byte) 0x00, (byte) 0x3C,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x05, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x00,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x08, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x39,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x20, (byte) 0x40, (byte) 0x00, (byte) 0x00,
                        (byte) 0x06, (byte) 0x06, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x1C, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x18,
                        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x03,
                        (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3F, (byte) 0x0E, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x20,
                        (byte) 0x00, (byte) 0x40, (byte) 0x41, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x1F,
                        (byte) 0x07, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x2F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x0A, (byte) 0x14, (byte) 0x00,
                        (byte) 0x60, (byte) 0x60, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x40,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x4F, (byte) 0x43,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x17, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x06, (byte) 0x0C, (byte) 0x00, (byte) 0x30,
                        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x04, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x67, (byte) 0x21, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x0B,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x03,
                        (byte) 0x07, (byte) 0x00, (byte) 0x18, (byte) 0x18,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                        (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x73, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x05, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x04,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x0C, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x39,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x7F, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x10, (byte) 0x21, (byte) 0x02, (byte) 0x00,
                        (byte) 0x06, (byte) 0x06, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x40, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x1C, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x3F,
                        (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x50,
                        (byte) 0x20, (byte) 0x01, (byte) 0x00, (byte) 0x03,
                        (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x20,
                        (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x3F, (byte) 0x0E, (byte) 0x02, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x40,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x5F, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x2C, (byte) 0x58,
                        (byte) 0x00, (byte) 0x40, (byte) 0x41, (byte) 0x01,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x10, (byte) 0x00,
                        (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x1F,
                        (byte) 0x07, (byte) 0x01, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x2F, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x18, (byte) 0x30, (byte) 0x00,
                        (byte) 0x60, (byte) 0x60, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x40,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x4F, (byte) 0x43,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x17, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x0D, (byte) 0x1A, (byte) 0x00, (byte) 0x30,
                        (byte) 0x30, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x04, (byte) 0x00, (byte) 0x20, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x67, (byte) 0x21, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x7C, (byte) 0x0B,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
                        (byte) 0x0E, (byte) 0x00, (byte) 0x18, (byte) 0x18,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
                        (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x78,
                        (byte) 0x73, (byte) 0x10, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x7E, (byte) 0x05, (byte) 0x00,
                        (byte) 0x00, (byte) 0x60, (byte) 0x43, (byte) 0x07,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x0C, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x40, (byte) 0x3E, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x40, (byte) 0x04,
                        (byte) 0x50, (byte) 0x34, (byte) 0x03, (byte) 0x20,
                        (byte) 0x06, (byte) 0x0A, (byte) 0x14, (byte) 0x00,
                        (byte) 0x1E, (byte) 0x20, (byte) 0x49, (byte) 0x02,
                        (byte) 0x00, (byte) 0x60, (byte) 0x3F, (byte) 0x28,
                        (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x70, (byte) 0x1F, (byte) 0x19, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
                        (byte) 0x00, (byte) 0x00, (byte) 0x19, (byte) 0x00,
                        (byte) 0x00, (byte) 0x0C, (byte) 0x10, (byte) 0x00,
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
                        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xF7 };
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, "NewPatch");
        return p;
    }
}