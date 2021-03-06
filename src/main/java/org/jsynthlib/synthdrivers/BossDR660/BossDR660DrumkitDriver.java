/*
 * @version $Id: BossDR660DrumkitDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
package org.jsynthlib.synthdrivers.BossDR660;

import java.io.InputStream;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;

public class BossDR660DrumkitDriver extends AbstractPatchDriver {
    int[] xvrt;

    public BossDR660DrumkitDriver() {
        super("Drumkit", "Brian Klock");
        sysexID = "F041**5212";
        patchNameStart = 1378;
        patchNameSize = 7;
        deviceIDoffset = 2;
        bankNumbers = new String[] {
            "0-Internal" };
        patchNumbers =
                new String[] {
                        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19",
                        "20", "21", "22", "23", "24", "25", "26", "27", "28",
                        "29", "30", "31", "32", "33", "34", "35", "36", "37",
                        "38" };
        xvrt =
                new int[] {
                        0, 8, 16, 24, 25, 32, 40, 64, 65, 66, 67, 68, 69, 70,
                        71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
                        85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95 };
    }

    @Override
    public void setPatchNum(int patchNum) {
        try {

            send(0xC0 + (getChannel() - 1), xvrt[patchNum]);
            Thread.sleep(150);
        } catch (Exception e) {
        }
        ;
    }

    @Override
    public void calculateChecksum(Patch ip) {
        Patch p = ip;
        for (int i = 0; i < 55; i++) {
            calculateChecksum(p, 23 * i + 5, 23 * i + 20, 23 * i + 21);
            p.sysex[i * 23 + 2] = ((byte) (getChannel() - 1));
        }
        calculateChecksum(p, 1265 + 5, 1265 + 63, 1265 + 64);
        p.sysex[1265 + 2] = ((byte) (getChannel() - 1));
        calculateChecksum(p, 1331 + 5, 1331 + 21, 1331 + 22);
        p.sysex[1331 + 2] = ((byte) (getChannel() - 1));
        calculateChecksum(p, 1355 + 5, 1355 + 11, 1355 + 12);
        p.sysex[1355 + 2] = ((byte) (getChannel() - 1));
        calculateChecksum(p, 1369 + 5, 1369 + 15, 1369 + 16);
        p.sysex[1369 + 2] = ((byte) (getChannel() - 1));

    }

    // XXX this method can be commented out, because same as superclass.
    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[ofs] = (byte) (sum % 128);
        sysex[ofs] = (byte) (sysex[ofs] ^ 127);
        sysex[ofs] = (byte) (sysex[ofs] + 1);
        sysex[ofs] = (byte) (sysex[ofs] % 128);

    }

    @Override
    public void sendPatch(Patch p) {
        setPatchNum(0);
        sendPatchWorker(p);
        try {
            Thread.sleep(25);
        } catch (Exception e) {
        }
        ;
        // setPatchNum(0);
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        // setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatchWorker(p);
        try {
            Thread.sleep(25);
        } catch (Exception e) {
        }
        ;
        setPatchNum(patchNum);

    }

    @Override
    public void setBankNum(int bankNum) {
    }

    @Override
    public void playPatch(Patch p) {
        try {

            sendPatch(p);
            Thread.sleep(100);
            send((0x90 + (getChannel() - 1)), 36, 127);
            Thread.sleep(100);
            send((0x80 + (getChannel() - 1)), 36, 0);
            send((0x90 + (getChannel() - 1)), 42, 127);
            Thread.sleep(100);
            send((0x80 + (getChannel() - 1)), 42, 0);
            send((0x90 + (getChannel() - 1)), 38, 127);
            Thread.sleep(100);
            send((0x80 + (getChannel() - 1)), 38, 0);
            send((0x90 + (getChannel() - 1)), 46, 127);
            Thread.sleep(100);
            send((0x80 + (getChannel() - 1)), 46, 0);
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Unable to Play Drums", e);
        }
    }

    @Override
    public Patch createNewPatch() {
        try {
            InputStream fileIn =
                    getClass().getResourceAsStream("BossDR660Drumkit.new");
            byte[] buffer = new byte[1387];
            fileIn.read(buffer);
            fileIn.close();
            return getPatchFactory().createNewPatch(buffer, this);
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Unable to find Defaults", e);
            return null;
        }
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new BossDR660DrumkitEditor(p);
    }

}
