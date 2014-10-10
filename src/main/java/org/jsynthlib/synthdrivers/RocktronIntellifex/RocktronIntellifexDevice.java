package org.jsynthlib.synthdrivers.RocktronIntellifex;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * Device class for Rocktron Intellifex (Black Face).
 * @author Klaus Sailer
 * @version $Id$
 */
public class RocktronIntellifexDevice extends Device {
    private static final String INFO_TEXT =
            "An effects device that support 80 presets (editable) and two additional data blocks (not yet editable). Use 'PRESET DUMP'/'PRESET LOAD' to get/store single presets. Use 'DUMP USER DATA'/'LOAD USER DATA' to get/store full bank.";

    /** Constructor for DeviceListWriter. */
    public RocktronIntellifexDevice() {
        super("Rocktron", "Intellifex", "", INFO_TEXT, "Klaus Sailer");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        RocktronIntellifexPresetDriver pDriver =
                new RocktronIntellifexPresetDriver();
        RocktronIntellifexS81Driver s81Driver =
                new RocktronIntellifexS81Driver();
        RocktronIntellifexS82Driver s82Driver =
                new RocktronIntellifexS82Driver();

        addDriver(pDriver);
        addDriver(s81Driver);
        addDriver(s82Driver);
        addDriver(new RocktronIntellifexBankDriver(pDriver, s81Driver,
                s82Driver));

    }

}
