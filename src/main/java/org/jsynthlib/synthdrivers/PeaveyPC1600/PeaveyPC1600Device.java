/*
 * PC1600Device.java
 */

package org.jsynthlib.synthdrivers.PeaveyPC1600;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * @author Phil Shepherd
 * @version $Id: PeaveyPC1600Device.java 1167 2011-10-09 02:22:49Z billzwicky $
 */
public class PeaveyPC1600Device extends Device {
    public PeaveyPC1600Device() {
        super("Peavey", "PC1600/PC1600x", null, null, "Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new PeaveyPC1600SingleDriver());
        addDriver(new PeaveyPC1600BankDriver());
    }
}
