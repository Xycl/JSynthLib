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
package synthdrivers.EmuProteus2;

import java.util.prefs.Preferences;

import synthdrivers.EmuProteusMPS.EmuProteusMPSBankDriver;
import synthdrivers.EmuProteusMPS.EmuProteusMPSSingleDriver;
import core.Device;



public class EmuProteus2Device extends Device {

    private static final String INFO_TEXT = "E-mu Proteus/2. \nSingle and bank drivers work together with editor.\n"
                                            + "Saving patches onto the Proteus from jSynthlib is not implemented yet. "
                                            + "This has to be done manually.";



    public EmuProteus2Device() {
        super("Emu", "Proteus 2", null, INFO_TEXT, "Pascal Collberg");
    }



    /** Constructor for for actual work. */
    public EmuProteus2Device(Preferences prefs) {
        this();
        this.prefs = prefs;

        addDriver(new EmuProteus2BulkConverter());
        addDriver(new EmuProteus2BankDriver());
        addDriver(new EmuProteus2SingleDriver());
    }

}
