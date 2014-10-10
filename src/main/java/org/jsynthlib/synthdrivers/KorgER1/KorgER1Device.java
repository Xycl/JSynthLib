/*
 * 
 * KorgER1Device.java
 *
 * Created on 10. Oktober 2001, 22:18
 */

package org.jsynthlib.synthdrivers.KorgER1;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * @author Gerrit Gehnen
 * @version $Id: KorgER1Device.java 646 2004-07-30 16:57:44Z ribrdb $
 */
public class KorgER1Device extends Device {
    /** Creates new KorgER1Device */
    public KorgER1Device() {
        super("Korg", "Electribe ER1", "F07E..06024251..............F7", null,
                "Yves Lefebvre");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("ER1");
        addDriver(new KorgER1SingleDriver());
    }
}
