/*
 * JSynthlib - Device for Yamaha DX5
 * =================================
 * @version $Id: YamahaDX5Device.java 580 2004-06-27 16:34:08Z hayashi $
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
package org.jsynthlib.synthdrivers.YamahaDX5;

import java.util.prefs.Preferences;

import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyDevice;

public class YamahaDX5Device extends DX7FamilyDevice {
    private static final String dxInfoText = YamahaDX5Strings.INFO_TEXT;

    /** Creates new YamahaDX5Device */
    public YamahaDX5Device() {
        super("Yamaha", "DX5", null, dxInfoText, "Torsten Tittmann", 0x00,
                0x00, 0x03);
    }

    /** Constructor for for actual work. */
    public void setup(Preferences prefs) {
        super.setup(prefs);

        // setSPBPflag(0x00); // switched off 'Enable Remote Control?' and
        // disabled
        // setSwOffMemProtFlag(0x00); // switched off 'Disable Memory
        // Protection?' and disabled
        // setTipsMsgFlag(0x03); // switched on 'Display Hints and Tips?' and
        // enabled

        // voice patch - basic patch for all modells of the DX7 family
        addDriver(new YamahaDX5VoiceSingleDriver());
        addDriver(new YamahaDX5VoiceBankDriver());

        // DX5 Performance patch
        addDriver(new YamahaDX5PerformanceSingleDriver()); // experimental !!!!
        addDriver(new YamahaDX5PerformanceBankDriver()); // experimental !!!!
    }
}
