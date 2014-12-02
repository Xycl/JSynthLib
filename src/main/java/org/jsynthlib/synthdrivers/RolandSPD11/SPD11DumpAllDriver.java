package org.jsynthlib.synthdrivers.RolandSPD11;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.AbstractBankDriver;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author peter
 */
public class SPD11DumpAllDriver extends AbstractBankDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    private final SPD11SystemDriver systemDriver;
    private final SPD11ChainDriver chainDriver;
    private final SPD11PadDriver padDriver;
    private final SPD11SettingsDriver settingsDriver;
    private final SPD11Patch2Driver patchDriver;

    /**
     * @param systemDriver
     * @param chainDriver
     * @param padDriver
     * @param settingsDriver
     * @param patchDriver
     */
    public SPD11DumpAllDriver(SPD11SystemDriver systemDriver,
            SPD11ChainDriver chainDriver, SPD11PadDriver padDriver,
            SPD11SettingsDriver settingsDriver, SPD11Patch2Driver patchDriver) {
        super("DumpAll", "Peter Geirnaert", 66, 6);
        this.systemDriver = systemDriver;
        this.chainDriver = chainDriver;
        this.padDriver = padDriver;
        this.settingsDriver = settingsDriver;
        this.patchDriver = patchDriver;
        sysexID = "F041**601200";
        deviceIDoffset = 2;
        singleSysexID = "F041**60";
        singleSize = 28 * 32 + 19 | 75 | 39;
        bankNumbers = new String[] {
            "SPD11 Complete Back Up" };
        patchNumbers =
                new String[] {
                        "01", "02", "03", "04", "05", "06", "07", "08", "09",
                        "10", "11", "12", "13", "14", "15", "16", "17", "18",
                        "19", "20", "21", "22", "23", "24", "25", "26", "27",
                        "28", "29", "30", "31", "32", "33", "34", "35", "36",
                        "37", "38", "39", "40", "41", "42", "43", "44", "45",
                        "46", "47", "48", "49", "50", "51", "52", "53", "54",
                        "55", "56", "57", "58", "59", "60", "61", "62", "63",
                        "64", "System setup", "Chain setup" };
        // this.numSysexMsgs = 66; // not used in standard JSynthLib
    }

    // private final int numPatches = 64+2;
    // private final int numColumns = 8;
    /**
     * SPD11BankDriver CheckSum calculation replaces the offset byte<br>
     * with the result of the formula 128 -(sum % 128)
     * @param p
     *            the patch we work with
     * @param start
     *            is where the values start adding to the sum
     * @param end
     *            is where the values stops adding to the sum
     * @param offset
     *            is where the checksum value is replaced
     */
    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int offset) {
        int sum = 0x00;
        // add all values of address and data
        for (int i = start; i < offset; i++) {
            sum += sysex[i];
        }
        // calculate checkSum with a simple formula
        int chkSm = 128 - (sum % 128);
        sysex[offset] = (byte) chkSm;
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        switch (patchNum) {
        case 65:
            byte[] chain = new byte[75];
            // copy chain setup data
            System.arraycopy(bank.sysex, 64 * (19 + (28 * 32)) + 39 + 1, chain,
                    19 + (28 * 32) + 39 + 75, 75);
            return getPatchFactory().createNewPatch(chain, chainDriver);
        case 64:
            byte[] system = new byte[39];
            // copy system setup data
            System.arraycopy(bank.sysex, 64 * (19 + (28 * 32)) + 1, system,
                    64 * (19 + (28 * 32)) + 39, 39);
            return getPatchFactory().createNewPatch(system, systemDriver);
        default:
            byte[] patch = new byte[28];
            // copy patch data
            System.arraycopy(bank.sysex, patchNum * (19 + (28 * 32))
                    + (patchNum == 0 ? 0 : 1), patch, (patchNum + 1)
                    * (19 + (28 * 32)), 19 + (28 * 32));
            return getPatchFactory().createNewPatch(patch, patchDriver);

        }
    }

    @Override
    public String getPatchName(Patch bank, int patchNum) {
        // SPD11 doesn't use names
        return "SPD11DumpAll";
    }

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        // TODO Auto-generated method stub
        if (!canHoldPatch(bank)) {
            PopupHandlerProvider.get().showMessage(null,
                    "This type of patch does not fit into this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // find out the single's type looking at the sixth byte and put it where
        // it belongs.
        switch (single.sysex[5]) {
        // it's a chain setup
        case (byte) 2:
            // copy chain setup data
            System.arraycopy(single, 64 * (19 + 28 * 32) + 39 + 1, bank,
                    64 * (19 + 28 * 32) + 39 + 1, 75);
            // it's a system setup
        case (byte) 1:
            // copy system setup data
            System.arraycopy(single, 64 * (19 + 28 * 32) + 1, bank,
                    64 * (19 + 28 * 32) + 1, 39);
            // it's a patch
        case (byte) 0:
            // recalculate the checksums of the patch with the new address
            // replace patchNum value of the first part: "settings"
            single.sysex[6] = (byte) patchNum;
            // calculate the new "settings" checksum with the new bankNum .
            calculateChecksum(single, 5, 16, 17);
            // replace patchNum values and calculate checkSums of "pad1" to
            // "pad32"
            for (int i = 0; i < 32; i++) {
                single.sysex[25 + i * 28] = (byte) patchNum;
                // recalculate the new "pad" checksums with the new bankNum .
                calculateChecksum(single, 24 + i * 28, 25 + i * 28, 36 + i * 28);
            }
            // copy patch data
            System.arraycopy(single, 0, bank, patchNum * (19 + 28 * 32)
                    + (patchNum == 0 ? 0 : 1), 19 + 28 * 32);
        }

    }

    @Override
    public void setPatchName(Patch bank, int patchNum, String name) {
        throw new UnsupportedOperationException(
                "Setting a name is not supported.");
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        // request all 64 patches
        for (int i = 0; i < 64; i++) {
            patchDriver.requestPatchDump(0, i);
            try {
                Thread.sleep(22); // wait at least 20 milliseconds .
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
        // request chain
        chainDriver.requestPatchDump(0, 0);
        try {
            Thread.sleep(22); // wait at least 20 milliseconds .
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        systemDriver.requestPatchDump(0, 0);

    }
}
