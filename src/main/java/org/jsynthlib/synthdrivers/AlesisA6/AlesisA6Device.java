// written by Kenneth L. Martinez
// 
// @version $Id: AlesisA6Device.java 1167 2011-10-09 02:22:49Z billzwicky $

package org.jsynthlib.synthdrivers.AlesisA6;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

public class AlesisA6Device extends Device {
    static final String DRIVER_INFO =
            "The A6 lacks a MIDI addressable patch buffer. Therefore, when you\n"
                    + "send or play a patch from within JSynthLib, user program 127 will be\n"
                    + "overwritten. JSynthLib treats this location as an edit buffer.";

    /** Creates new AlesisA6Device */
    public AlesisA6Device() {
        super("Alesis", "Andromeda A6", null, DRIVER_INFO,
                "Kenneth L. Martinez");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("Andromeda");

        addDriver(new AlesisA6PgmBankDriver());
        addDriver(new AlesisA6PgmSingleDriver());
        addDriver(new AlesisA6MixBankDriver());
        addDriver(new AlesisA6MixSingleDriver());
        /*
         * PopupHandlerProvider.get().showMessage(PatchEdit.getInstance(), DRIVER_INFO,
         * "A6 Driver Release Notes", JOptionPane.WARNING_MESSAGE );
         */
    }
}
