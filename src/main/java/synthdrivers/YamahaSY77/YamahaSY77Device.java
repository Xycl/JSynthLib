/*
 * JSynthlib - Device for Yamaha SY77
 * ========================================
 * @version $Id$
 * @author  Vladimir Avdonin
 *
 * Copyright (C) 2011 vldmrrr@yahoo.com
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
package synthdrivers.YamahaSY77;

import core.Device;

import java.util.prefs.Preferences;

public class YamahaSY77Device extends Device {
    public static final String author = "Vladimir Avdonin";
    private static final String infoText =
            "This is JSynthLib \"Yamaha SY77\" device driver";

    /** Creates new YamahaSY77Device */
    public YamahaSY77Device() {
        super("Yamaha", "SY77", null, infoText, author);
    }

    /** Constructor for for actual work. */
    public YamahaSY77Device(Preferences prefs) {
        this();
        this.prefs = prefs;
        YamahaSY77VoiceDriver vd = new YamahaSY77VoiceDriver();
        addDriver(vd);
        addDriver(new YamahaSY77VoiceBankDriver(vd));
        addDriver(new YamahaSY77KSeqDriver());
        addDriver(new YamahaSY77NSeqDriver());
    }
}
