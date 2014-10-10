/*
 * Copyright 2004 Joachim Backhaus
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

package org.jsynthlib.synthdrivers.YamahaTG100;

/**
 * Model for the Yamaha TG-100 synthdriver
 *
 * @author  Joachim Backhaus
 * @version $Id: TG100Model.java 843 2005-01-24 07:24:21Z jbackhaus $
 */

import org.jsynthlib.device.model.ParamModel;
import org.jsynthlib.patch.model.impl.Patch;

public class TG100Model extends ParamModel {
    private boolean has2ByteValue = false;

    public TG100Model(Patch patch, int offset) {
        this(patch, offset, false);
    }

    public TG100Model(Patch patch, int offset, boolean has2ByteValue) {
        super(patch, offset);

        this.has2ByteValue = has2ByteValue;
    }

    /** Set a parameter value <code>int</code>. */
    public void set(int value) {
        if (this.has2ByteValue) {
            // Data
            patch.sysex[offset] =
                    (byte) ((value & TG100Constants.BITMASK_11110000) >> 4);
            patch.sysex[offset + 1] = (byte) (value & TG100Constants.BITMASK_1111);
        } else {
            patch.sysex[offset] = (byte) value;
        }
    }

    /** Get a parameter value. */
    public int get() {
        if (this.has2ByteValue) {
            int iTemp;

            iTemp = patch.sysex[offset] << 4;
            iTemp += patch.sysex[offset + 1];

            return iTemp;
        } else {
            return patch.sysex[offset];
        }
    }
}
