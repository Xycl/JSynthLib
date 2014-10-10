/*
 * RolandXV5080Device.java
 */

package org.jsynthlib.synthdrivers.RolandXV5080;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * @author Phil Shepherd
 * @version $Id: RolandXV5080Device.java 541 2004-05-21 01:48:24Z hayashi $
 */
public class RolandXV5080Device extends Device {
    public RolandXV5080Device() {
        super("Roland", "XV5080", null, null, "Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new RolandXV5080PatchDriver());
        addDriver(new RolandXV5080PatchBankDriver());
        addDriver(new RolandXV5080PerfDriver());
        addDriver(new RolandXV5080PerfBankDriver());
    }
}
