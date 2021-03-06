// written by Kenneth L. Martinez
//
// @version $Id: AlesisA6PgmBankDriver.java 832 2005-01-16 18:05:50Z hayashi $

package org.jsynthlib.synthdrivers.AlesisA6;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class AlesisA6PgmBankDriver extends AbstractBankDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    public AlesisA6PgmBankDriver() {
        super("Prog Bank", "Kenneth L. Martinez",
                AlesisA6PgmSingleDriver.patchList.length, 4);
        sysexID = "F000000E1D00**00";
        sysexRequestDump = new SysexHandler("F0 00 00 0E 1D 0A *bankNum* F7");
        patchSize = 300800;
        patchNameStart = 2; // does NOT include sysex header
        patchNameSize = 16;
        deviceIDoffset = -1;
        bankNumbers = AlesisA6PgmSingleDriver.bankList;
        patchNumbers = AlesisA6PgmSingleDriver.patchList;
        singleSize = 2350;
        singleSysexID = "F000000E1D00";
    }

    @Override
    public void calculateChecksum(Patch p) {
        // A6 doesn't use checksum
    }

    // protected static void calculateChecksum(Patch p, int start, int end, int
    // ofs)
    // {
    // // A6 doesn't use checksum
    // }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (bankNum == 1 || bankNum == 2) {
            PopupHandlerProvider.get().showMessage(PatchEdit.getInstance(),
                    "Cannot send to a preset bank", "Store Patch",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            sendPatchWorker(p, bankNum);
        }
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            ErrorMsg.reportError("Error",
                    "This type of patch does not fit in to this type of bank.");
            return;
        }

        System.arraycopy(p.sysex, 0, bank.sysex, patchNum * 2350, 2350);
        bank.sysex[patchNum * 2350 + 6] = 0; // user bank
        bank.sysex[patchNum * 2350 + 7] = (byte) patchNum; // set
                                                           // program
                                                           // #
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex = new byte[2350];
        System.arraycopy(bank.sysex, patchNum * 2350, sysex, 0, 2350);
        return getPatchFactory().createNewPatch(sysex, getDevice());
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        Patch pgm = getPatch(p, patchNum);
        try {
            char c[] = new char[patchNameSize];
            for (int i = 0; i < patchNameSize; i++) {
                c[i] =
                        (char) (AlesisA6PgmSingleDriver.getA6PgmByte(pgm.sysex,
                                i + patchNameStart));
            }
            return new String(c);
        } catch (Exception ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        Patch pgm = getPatch(p, patchNum);
        if (name.length() < patchNameSize + 4) {
            name = name + "                ";
        }
        byte nameByte[] = name.getBytes();
        for (int i = 0; i < patchNameSize; i++) {
            AlesisA6PgmSingleDriver.setA6PgmByte(nameByte[i], pgm.sysex, i
                    + patchNameStart);
        }
        putPatch(p, pgm, patchNum);
    }

    // protected void sendPatch (Patch p)
    // {
    // sendPatchWorker((Patch)p, 0);
    // }

    protected void sendPatchWorker(Patch p, int bankNum) {
        byte tmp[] = new byte[2350]; // send in 128 single-program messages
        try {
            PatchEdit.showWaitDialog();
            for (int i = 0; i < 128; i++) {
                System.arraycopy(p.sysex, i * 2350, tmp, 0, 2350);
                tmp[6] = (byte) bankNum;
                tmp[7] = (byte) i; // program #
                send(tmp);
                Thread.sleep(15);
            }
            PatchEdit.hideWaitDialog();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            ErrorMsg.reportError("Error", "Unable to send Patch");
        }
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(((byte) getChannel()),
                new SysexHandler.NameValue[] {
                        new SysexHandler.NameValue("bankNum", bankNum),
                        new SysexHandler.NameValue("patchNum", patchNum) }));
    }
}
