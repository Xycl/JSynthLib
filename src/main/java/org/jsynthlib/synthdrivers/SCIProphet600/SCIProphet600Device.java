/**
 * written by Kenneth L. Martinez
 * @version $Id: SCIProphet600Device.java 541 2004-05-21 01:48:24Z hayashi $
 */
package org.jsynthlib.synthdrivers.SCIProphet600;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

public class SCIProphet600Device extends Device {
    static final String DRIVER_INFO =
            "The Prophet-600 lacks a MIDI addressable patch buffer. Therefore, when "
                    + "you send or play a patch from within JSynthLib, user program 99 will be "
                    + "overwritten. JSynthLib treats this location as an edit buffer.";

    /** Creates new SCIProphet600 */
    public SCIProphet600Device() {
        super("Sequential", "P600", null, DRIVER_INFO, "Kenneth L. Martinez");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("Prophet-600");
        addDriver(new P600ProgBankDriver());
        addDriver(new P600ProgSingleDriver());
        /*
         * PopupHandlerProvider.get().showMessage(PatchEdit.getInstance(), DRIVER_INFO,
         * "Prophet-600 Driver Release Notes", JOptionPane.WARNING_MESSAGE );
         */
    }
}
