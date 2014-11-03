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

import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.patch.model.impl.Patch;

public class D50PartialMuteDataModel extends ParamModel {
    private boolean part1;
    private boolean part2;

    public D50PartialMuteDataModel(Patch patch, int offset) {
        super(patch, offset);
    }

    public void setPart1(boolean part1) {
        this.part1 = part1;
        setSysex();
    }

    void setSysex() {
        int value = 0;
        if (part1) {
            value += 1;
        }
        if (part2) {
            value += 16;
        }
        patch.sysex[offset] = (byte) value;
    }

    public void setPart2(boolean part2) {
        this.part2 = part2;
        setSysex();
    }
}
