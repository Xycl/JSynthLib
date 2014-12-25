/*
 * Copyright 2001 Roger Westerlund
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
package org.jsynthlib.synthdrivers.RolandD10v2;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

/**
 * @author Roger Westerlund <roger.westerlund@home.se>
 */
public class RolandD10v2Device extends XMLDevice {

    /**
     * @param xmlDeviceSpec
     */
    public RolandD10v2Device(XmlDeviceDefinition xmlDeviceSpec) {
        super(xmlDeviceSpec);
    }

    @Override
    public void setup(Preferences prefs) {
        super.setup(prefs);
    }
}
