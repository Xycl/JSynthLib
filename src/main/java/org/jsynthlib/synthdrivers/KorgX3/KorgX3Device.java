package org.jsynthlib.synthdrivers.KorgX3;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * This class is a device driver for Korg X3 -synthesizer to be used in
 * JSynthLib-program. Might work directly with Korg X2 as well. Making drivers
 * for N-series (N264, N364) should be an easy task if one has the original
 * reference guide.
 * @author Juha Tukkinen
 * @version $Id: KorgX3Device.java 646 2004-07-30 16:57:44Z ribrdb $
 */
public class KorgX3Device extends Device {
    private static final String infoText =
            "This is the Korg X3 Driver. The Single Editor allows you to edit "
                    + "most of the attributes in this synthesizer. A few are unimplemented as "
                    + "the pan setting for example. Playing the patch in JSynthLib moves first "
                    + "the patch to Korg's patch buffer.";

    /**
     * Creates a new KorgX3Device
     */
    public KorgX3Device() {
        super("Korg", "X3", "F07E..0602423500..00..00..00F7", infoText,
                "Juha Tukkinen");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("X3");
        addDriver(new KorgX3SingleConverter());
        addDriver(new KorgX3BankConverter());
        addDriver(new KorgX3SingleDriver());
        addDriver(new KorgX3BankDriver());
    }
}
