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
package synthdrivers.RolandD50;

import java.util.prefs.Preferences;

import synthdrivers.KawaiK4.KawaiK4SingleDriver;

import core.Device;



public class D50Device extends Device {

    static final String         INFO_TEXT = "Roland D50. \nSingle and bank drivers work together with editor.\n"
                                            + "Saving patches onto the D50 does not work though as this must "
                                            + "be done by manually pressing buttons on the D50.";

    // Roland D-50 does not support the universal inquiry message.
    private static final String inquiryId = null;



    public D50Device() {
        super("Roland", "D-50", inquiryId, INFO_TEXT, "Pascal Collberg");
    }



    public D50Device(Preferences preferences) {
        this();
        this.prefs = preferences;

        addDriver(new D50BankDriver());
        addDriver(new D50BulkConverter());
        addDriver(new D50SingleDriver());
    }
}
