/*
 * Copyright 2013 Pascal Collberg
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
package org.jsynthlib.synthdrivers.RolandD50;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument.XmlDeviceSpec;

public class D50Device extends XMLDevice {

    /**
     * @param xmlDeviceSpec
     */
    public D50Device(XmlDeviceSpec xmlDeviceSpec) {
        super(xmlDeviceSpec);
        // super("Roland", "D-50", null, "Pelle plutt", "Pascal Collberg");

    }

    @Override
    public void setup(Preferences prefs) {
        super.setup(prefs);

//        addDriver(new D50BankDriver());
        addDriver(new D50BulkConverter());
//        addDriver(new D50SingleDriver());
    }
}
