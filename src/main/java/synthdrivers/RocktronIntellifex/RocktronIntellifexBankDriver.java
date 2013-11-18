package synthdrivers.RocktronIntellifex;

import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;

import core.*;

/**
 * Bank driver for Rocktron Intellifex user data.
 * @version $Id$
 */
public class RocktronIntellifexBankDriver extends BankDriver {

    /** Header size */
    private static final int HSIZE = 6;

    /** Preset data size */
    private static final int PRESET_DATA = 200;
    /** Preset size */
    private static final int PRESET_SIZE = HSIZE + PRESET_DATA + 2;
    /** the number of Presets in a bank patch. */
    private static final int NUM_PRESETS = 80;

    /** the patch number of the S81 patch. */
    private static final int S81_PATCHNUM = 80;
    /** String 81 data size */
    private static final int S81_DATA = 256;
    /** String 81 Patch size */
    private static final int S81_SIZE = HSIZE + S81_DATA + 2;

    /** the patch number of the S81 patch. */
    private static final int S82_PATCHNUM = 81;
    /** String 82 data size */
    private static final int S82_DATA = 12;
    /** String 82 Patch size */
    private static final int S82_SIZE = HSIZE + S82_DATA + 2;

    private final RocktronIntellifexPresetDriver presetDriver;
    private final RocktronIntellifexS81Driver s81Driver;
    private final RocktronIntellifexS82Driver s82Driver;

    public RocktronIntellifexBankDriver(RocktronIntellifexPresetDriver pDriver,
            RocktronIntellifexS81Driver s81Driver,
            RocktronIntellifexS82Driver s82Driver) {
        super("Bank", "Klaus Sailer", NUM_PRESETS + 3, 2);

        this.presetDriver = pDriver;
        this.s81Driver = s81Driver;
        this.s82Driver = s82Driver;

        patchNameSize = presetDriver.getPatchNameSize();
        patchNameStart = 116;
        bankNumbers = new String[] {
            "User Data" };
        patchNumbers = new String[NUM_PRESETS + 2];
        for (int i = 0; i < NUM_PRESETS + 2; i++)
            patchNumbers[i] = (i + 1 < 10 ? "0" : "") + String.valueOf(i + 1);

        patchSize = PRESET_SIZE * NUM_PRESETS + S81_SIZE + S82_SIZE;

        sysexID = "F0000029022A";
        singleSysexID = "F0000029022*";
        singleSize = 0; // we always have preset and extra patch types
    }

    public int getPatchStart(int patchNum) {
        if (patchNum > NUM_PRESETS)
            return PRESET_SIZE * NUM_PRESETS + S81_SIZE;
        else
            return PRESET_SIZE * patchNum;
    }

    protected String getPatchName(Patch p, int patchNum) {
        if (patchNum == NUM_PRESETS)
            return "(MappingData)";
        if (patchNum == NUM_PRESETS + 1)
            return "(MiscData)";
        int patchStart = getPatchStart(patchNum);
        try {
            String name = new String();
            for (int i = 0; i < patchNameSize; i++) {
                String s =
                        new String(p.sysex,
                                patchStart + patchNameStart + 2 * i, 1,
                                "US-ASCII");
                name = name.concat(s);
            }
            return name;
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    public void setPatchName(Patch p, int patchNum, String name) {
        if (patchNum >= NUM_PRESETS)
            return;

        while (name.length() < patchNameSize)
            name = name + " ";
        int patchStart = getPatchStart(patchNum);

        byte[] namebytes = new byte[patchNameSize];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchStart + patchNameStart + 2 * i] = namebytes[i];
                p.sysex[patchStart + patchNameStart + 2 * i + 1] = 0x00;
            }
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    public void calculateChecksum(Patch p) {
        for (int i = 0; i < NUM_PRESETS; i++)
            presetDriver.calculateChecksum(p, PRESET_SIZE * i + 6, PRESET_SIZE
                    * i + 6 + PRESET_DATA, PRESET_SIZE * i + 6 + PRESET_DATA);

        int pos = PRESET_SIZE * NUM_PRESETS;
        s81Driver.calculateChecksum(p, pos + 6, pos + 6 + S81_DATA, pos + 6
                + S81_DATA);

        pos += S81_SIZE;
        s82Driver.calculateChecksum(p, pos + 6, pos + 6 + S82_DATA, pos + 6
                + S82_DATA);
    }

    public void putPatch(Patch bank, Patch p, int patchNum) {
        if (!canHoldPatch(p)) {
            JOptionPane.showMessageDialog(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (patchNum) {
        case S81_PATCHNUM:
            System.arraycopy(p.sysex, 0, bank.sysex, getPatchStart(patchNum),
                    S81_SIZE);
            break;
        case S82_PATCHNUM:
            System.arraycopy(p.sysex, 0, bank.sysex, getPatchStart(patchNum),
                    S82_SIZE);
            break;
        default:
            System.arraycopy(p.sysex, 0, bank.sysex, getPatchStart(patchNum),
                    PRESET_SIZE);
        }
        bank.sysex[getPatchStart(patchNum) + 5] =
                (byte) (patchNum == 0 ? 0x2a : 0x2b);
        calculateChecksum(bank);
    }

    public Patch getPatch(Patch bank, int patchNum) {
        byte[] sysex;
        Driver driver;

        switch (patchNum) {
        case S81_PATCHNUM:
            sysex = new byte[S81_SIZE];
            System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, 0,
                    S81_SIZE);
            driver = s81Driver;
            break;
        case S82_PATCHNUM:
            sysex = new byte[S82_SIZE];
            System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, 0,
                    S82_SIZE);
            driver = s82Driver;
            break;
        default:
            sysex = new byte[PRESET_SIZE];
            System.arraycopy(bank.sysex, getPatchStart(patchNum), sysex, 0,
                    PRESET_SIZE);
            sysex[5] = 0x28; // set preset type instead of user dump
            driver = presetDriver;
        }

        try {
            Patch p = new Patch(sysex, driver.getDevice());
            p.calculateChecksum();
            return p;
        } catch (Exception e) {
            ErrorMsg.reportError("Error",
                    "Error in RocktronIntellifex Bank Driver", e);
            return null;
        }
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[patchSize];
        Patch bank = new Patch(sysex, this);

        Patch patch = presetDriver.createNewPatch();
        for (int i = 0; i < NUM_PRESETS; i++)
            putPatch(bank, patch, i);

        patch = s81Driver.createNewPatch();
        putPatch(bank, patch, S81_PATCHNUM);

        patch = s82Driver.createNewPatch();
        putPatch(bank, patch, S82_PATCHNUM);

        return bank;
    }

    @Override
    protected void storePatch(Patch bank, int bankNum, int patchNum) {
        Patch patch;
        for (int i = 0; i < NUM_PRESETS; i++) {
            patch = getPatch(bank, i);
            patch.sysex[5] = (byte) (i == 0 ? 0x2A : 0x2B);
            patch.send();
            try {
                sleepAtLeast(1000);
            } catch (InterruptedException e) {
            }
        }
        patch = getPatch(bank, S81_PATCHNUM);
        patch.sysex[5] = 0x2B;
        patch.send();
        try {
            sleepAtLeast(1000);
        } catch (InterruptedException e) {
        }

        patch = getPatch(bank, S82_PATCHNUM);
        patch.sysex[5] = 0x2B;
        patch.send();
    }

    public void sleepAtLeast(long millis) throws InterruptedException {
        long t0 = System.currentTimeMillis();
        long millisLeft = millis;
        while (millisLeft > 0) {
            Thread.sleep(millisLeft);
            long t1 = System.currentTimeMillis();
            millisLeft = millis - (t1 - t0);
        }
    }
}