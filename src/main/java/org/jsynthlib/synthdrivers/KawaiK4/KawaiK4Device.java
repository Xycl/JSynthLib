/*
 * KawaiK4Device.java
 *
 */

package org.jsynthlib.synthdrivers.KawaiK4;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * Device class for KAWAI K4/K4r.
 * @author Gerrit Gehnen
 * @version $Id: KawaiK4Device.java 646 2004-07-30 16:57:44Z ribrdb $
 */
public class KawaiK4Device extends Device {
    private static final String INFO_TEXT =
            "There are no known issues or problems with this synthesizer. Librarian and Editing functions are "
                    + "available for all K4 datatypes, Singles, Single Banks, Multis, MultiBanks, Effects, EffectBanks, and "
                    + "Drumkits. Note that the K4r does not have the effects section that is present on the K4.";

    /** Constructor for DeviceListWriter. */
    public KawaiK4Device() {
        super("Kawai", "K4/K4R", "F07E..0602400000040000000000f7", INFO_TEXT,
                "Brian Klock & Gerrit Gehnen");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new KawaiK4BulkConverter());
        addDriver(new KawaiK4SingleDriver());
        addDriver(new KawaiK4BankDriver());
        addDriver(new KawaiK4MultiDriver());
        addDriver(new KawaiK4MultiBankDriver());
        addDriver(new KawaiK4EffectDriver());
        addDriver(new KawaiK4EffectBankDriver());
        addDriver(new KawaiK4DrumsetDriver());
    }
}
