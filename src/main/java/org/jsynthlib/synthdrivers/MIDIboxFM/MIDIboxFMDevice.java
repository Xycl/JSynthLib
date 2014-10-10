/*
 * JSynthlib-Device for MIDIbox FM
 * =====================================================================
 * @author  Thorsten Klose
 * @version $Id: MIDIboxFMDevice.java 827 2005-01-15 16:27:59Z hayashi $
 *
 * Copyright (C) 2005  Thorsten.Klose@gmx.de
 *                     http://www.uCApps.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.jsynthlib.synthdrivers.MIDIboxFM;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

public class MIDIboxFMDevice extends Device {
    private static final String infoText =
            "This driver has been created for MIDIbox FM, a non-commercial DIY "
                    + "synthesizer based on the OPL3 soundchip from Yamaha."
                    + "\n"
                    + "More informations about the features can be found under http://www.uCApps.de/midibox_fm.html";

    /** Creates new MIDIboxFMDevice */
    public MIDIboxFMDevice() {
        super("MIDIbox", "FM", "F000007E49000FF7", infoText, "Thorsten Klose");
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("MIDIbox FM");

        addDriver(new MIDIboxFMPatchDriver());
        addDriver(new MIDIboxFMPatchBankDriver());
        addDriver(new MIDIboxFMDrumDriver());
        addDriver(new MIDIboxFMDrumBankDriver());
        addDriver(new MIDIboxFMEnsDriver());
        addDriver(new MIDIboxFMEnsBankDriver());
    }

}
