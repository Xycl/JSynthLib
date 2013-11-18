/*
 * PC1600Device.java
 */

package synthdrivers.PeaveyPC1600;

import core.*;
import java.util.prefs.Preferences;

/**
 * @author Phil Shepherd
 * @version $Id: PeaveyPC1600Device.java 1167 2011-10-09 02:22:49Z billzwicky $
 */
public class PeaveyPC1600Device extends Device {
    public PeaveyPC1600Device() {
        super("Peavey", "PC1600/PC1600x", null, null, "Phil Shepherd");
    }

    /** Constructor for for actual work. */
    public PeaveyPC1600Device(Preferences prefs) {
        this();
        this.prefs = prefs;

        addDriver(new PeaveyPC1600SingleDriver());
        addDriver(new PeaveyPC1600BankDriver());
    }
}
