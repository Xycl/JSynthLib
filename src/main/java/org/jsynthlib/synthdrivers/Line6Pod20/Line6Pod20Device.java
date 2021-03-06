/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.synthdrivers.Line6Pod20;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.Device;

/**
 * Device file for Line6 Pod
 * @author Jeff Weber
 */
public class Line6Pod20Device extends Device {
    /** Constructor for DeviceListWriter. */
    public Line6Pod20Device() {
        super(Constants.MANUFACTURER_NAME, Constants.DEVICE_NAME,
                Constants.INQUIRY_ID, Constants.INFO_TEXT, Constants.AUTHOR);
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new Line6Pod20Converter());
        addDriver(new Line6Pod20SingleDriver());
        addDriver(new Line6Pod20BankDriver());
        addDriver(new Line6Pod20EdBufDriver());
    }
}
