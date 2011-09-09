/*
 * Copyright 2011 Frankie Fisher
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
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

package synthdrivers.TCElectronicM350;
import core.Device;
import java.util.prefs.Preferences;

public class TCElectronicM350Device extends Device {
    private static final String INFO_TEXT="Initial support for TC Electronic M350 Effect / Reverb Processor\n"
					 +"Requires firmware 1.3 which can be acquired from "
					 +"'http://www.tcelectronic.com/m350software.asp'.\n"
					 +"Excessive widget movement may cause the unit to stop receiving input.\n"
					 +"\nFrankie Fisher.";
    /** Constructor for DeviceListWriter. */
    public TCElectronicM350Device() {
//prior to applying the 1.3 software upgrade from their website, the device inquiry string was: "F07E..060200201F5700000000000101F7" but after the upgrade has a model(?) number of 58
        super("TC Electronic", "M350", "F07E..060200201F5[78]0000000000010.F7", INFO_TEXT, "Frankie Fisher");
    }

    /** Constructor for for actual work. */
    public TCElectronicM350Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        // add drivers
        addDriver(new TCElectronicM350SingleDriver());
    }

}
