/*
 * JSynthlib - Device for Yamaha DX7 Mark-I
 * ========================================
 * @version $Id: YamahaDX7Device.java 580 2004-06-27 16:34:08Z hayashi $
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7;

import java.util.prefs.Preferences;

import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

public class YamahaDX7Device extends XMLDevice { // DX7FamilyDevice

    private static final String dxInfoText = YamahaDX7Strings.INFO_TEXT;

    /** Creates new YamahaDX7Device */
//    public YamahaDX7Device() {
//        super("Yamaha", "DX7", null, dxInfoText, "Torsten Tittmann", 0x02,
//                0x02, 0x03);
//    }

    public YamahaDX7Device(XmlDeviceDefinition xmlDeviceSpec) {
        super(xmlDeviceSpec);
    }

    /** Constructor for for actual work. */
    @Override
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSynthName("DX7 MK-I");

        // setSPBPflag(0x02); // switch off 'Enable Remote Control?', but
        // enabled
        // setSwOffMemProtFlag(0x02); // switch off 'Disable Memory
        // Protection?', but enabled
        // setTipsMsgFlag(0x03); // switch on 'Display Hints and Tips?', but
        // enabled

        // DX7 voice patch - basic patch for all modells of the DX7 family
        addDriver(new YamahaDX7Converter()); // in case a DX7 has the SER7
                                             // firmware
//        addDriver(new YamahaDX7VoiceSingleDriver());
//        addDriver(new YamahaDX7VoiceBankDriver());

        // DX7 Function patch - the single patch is available for DX7-I
        // and the bank driver is added for patch handling
//        addDriver(new YamahaDX7PerformanceSingleDriver());
//        addDriver(new YamahaDX7PerformanceBankDriver());
    }
}
