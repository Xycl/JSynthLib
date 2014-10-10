/*
 * Copyright 2009 Christopher Arndt
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
/*
 * Device class for the Yamaha SY85
 *
 * YamahaSY85Device.java
 *
 * Created on August 2, 2009
 */

package org.jsynthlib.synthdrivers.YamahaSY85;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * @author Christopher Arndt
 * @version $Id$
 */
public class YamahaSY85Device extends Device {

    private static final String INFO_TEXT =
            "Librarian & editor for the Yamaha SY86 Workstation.\n"
                    + "\n"
                    + "This is currently in development. So far only library support for "
                    + "single Voices is working. No support for Drum Voices, Performances, "
                    + "Multis (songs) or editing yet.";

    /**
     * Constructor for DeviceListWriter.
     */
    public YamahaSY85Device() {
        super("Yamaha", "SY85", null, INFO_TEXT, "Christopher Arndt");
        // the SY85 does not reply to a Device Identity Request message
    }

    /**
     * Constructor for the actual work.
     * @param prefs
     *            The Preferences for this device
     */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new YamahaSY85SingleDriver());
        // addDriver(new YamahaSY85DrumVoiceDriver());
        // addDriver(new YamahaSY85CombiDriver());
        // addDriver(new YamahaSY85MultiDriver());
        // addDriver(new YamahaSY85AllConverter());
        // addDriver(new YamahaSY85BankDriver());
    }
}
