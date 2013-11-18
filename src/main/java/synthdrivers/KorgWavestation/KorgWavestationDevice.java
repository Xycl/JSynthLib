/*
 * KorgWavestationDevice.java
 *
 * Created on 10.Feb.2002
 */

package synthdrivers.KorgWavestation;

import core.Device;
import java.util.prefs.Preferences;

/**
 * @author Gerrit Gehnen
 * @version $Id: KorgWavestationDevice.java 646 2004-07-30 16:57:44Z ribrdb $
 */
public class KorgWavestationDevice extends Device {
    private static final String infoText =
            "This is an experimental driver. It is not tested on a real device yet!";

    /** Creates new KorgWavestationDevice */
    public KorgWavestationDevice() {
        super("Korg", "Wavestation", "F07E..06024228000100........F7",
                infoText, "Gerrit Gehnen");
    }

    /** Constructor for for actual work. */
    public KorgWavestationDevice(Preferences prefs) {
        this();
        this.prefs = prefs;

        // setSynthName("Wavestation");

        addDriver(new KorgWavestationSinglePatchDriver());
        addDriver(new KorgWavestationSinglePerformanceDriver());
        addDriver(new KorgWavestationBankPatchDriver());
        addDriver(new KorgWavestationBankPerformanceDriver());
        addDriver(new KorgWavestationSystemSetupDriver());
        addDriver(new KorgWavestationWaveSequenceDriver());
        addDriver(new KorgWavestationMultiModeSetupDriver());
        addDriver(new KorgWavestationMicroTuneScaleDriver());
        addDriver(new KorgWavestationPerformanceMapDriver());
    }
}
