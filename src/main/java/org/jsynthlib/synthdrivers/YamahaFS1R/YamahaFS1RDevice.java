package org.jsynthlib.synthdrivers.YamahaFS1R;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * Yamaha FS1R device.
 * @author Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id: YamahaFS1RDevice.java 541 2004-05-21 01:48:24Z hayashi $
 */
public class YamahaFS1RDevice extends Device {
    private static final String infoText =
            "JSynthLib supports librarian and edit functions on voices/performances patches.\n"
                    + "Ensure to edit patches from a bank, not from a library.";

    /**
     * Creates new YamahaFS1RDevice
     */
    public YamahaFS1RDevice() {
        super("Yamaha", "FS1R", null, infoText, "Denis Queffeulou");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // inquiryID = "F07E**06020F050000000000020AF7";
        addDriver(new YamahaFS1RBankConverter());
        addDriver(new YamahaFS1RBankDriver());
        addDriver(new YamahaFS1RVoiceDriver());
        addDriver(new YamahaFS1RPerformanceDriver());
        addDriver(new YamahaFS1RSystemDriver());
        addDriver(new YamahaFS1RFseqDriver());
    }
}
