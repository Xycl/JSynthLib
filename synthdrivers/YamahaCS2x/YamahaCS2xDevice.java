/*
 * Copyright 2009 Peter Geirnaert
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

package synthdrivers.YamahaCS2x;

import core.*;
import java.util.prefs.Preferences;

/**
 * Yamaha CS2x Device description.
 * @author Peter Geirnaert
 */
public class YamahaCS2xDevice extends Device {
    private static final String infoText = "Driver for Yamaha CS2x, see http://cs2x.wikispaces.com";

    /** Constructor for DeviceListWriter. */
    public YamahaCS2xDevice() {
        super("Yamaha", "CS2x", "F07E7F06024300414F0300000001F7",infoText, "Peter Geirnaert");
    }
    
   /** Constructor for actual work to be done.<br>
   * Creates a new <code>YamahaCS2xDevice</code> instance.<p>
   * Put here what to do : load the preferences, set deviceID and channel and add the drivers
   **/
    public YamahaCS2xDevice(Preferences prefs) {
    this();
    this.prefs = prefs;
    setDeviceID(-1);//**
    YamahaCS2xCommonDriver CS2xCommonDriver = new YamahaCS2xCommonDriver();
    YamahaCS2xLayersDriver CS2xLayersDriver = new YamahaCS2xLayersDriver();
    addDriver(CS2xCommonDriver);
    addDriver(new YamahaCS2xLayerDriver());
    addDriver(CS2xLayersDriver);
    YamahaCS2xPerformanceDriver CS2xPerformanceDriver = new YamahaCS2xPerformanceDriver(CS2xCommonDriver, CS2xLayersDriver);
    addDriver(CS2xPerformanceDriver);
    addDriver(new YamahaCS2xSystemDriver()); // uncomment when you want to start testing the driver!
    //addDriver(new YamahaCS2xBankDriver(CS2xCommonDriver, CS2xLayersDriver));
    addDriver(new YamahaCS2xBank2Driver()); //*/
    }    

}

