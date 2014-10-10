/*
 * BossDR660Device.java
 *
 * Created on 10. Oktober 2001, 09:25
 */

package org.jsynthlib.synthdrivers.BossDR660;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * @author Gerrit Gehnen
 * @version $Id: BossDR660Device.java 541 2004-05-21 01:48:24Z hayashi $
 */
public class BossDR660Device extends Device {
    private static final String infoText =
            "JSynthLib functions both as an editor and librarian for DR660 Drumkits. Banks of Drumkits are not "
                    + "supported due to Roland not documenting the format of the bank sysex dump. Also, while most parameters "
                    + "are editable, JSynthLib can not currently edit the effects settings of the DR660.\n\n"
                    + "Keep in mind that the first several locations to store drumkits on the DR660 are ROM"
                    + " locations and are not user writable. Though JSynthLib can store drumkits to these locations, "
                    + "they will revert back to their old values once a patch change message is received.";

    /** Creates new BossDR660Device */
    public BossDR660Device() {
        super("Boss", "DR660", null, infoText, "Brian Klock");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new BossDR660DrumkitDriver());
    }
}
