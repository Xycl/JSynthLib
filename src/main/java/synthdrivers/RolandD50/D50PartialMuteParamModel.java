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

import core.ParamModel;
import core.Patch;

public class D50PartialMuteParamModel extends ParamModel {

    private D50PartialMuteDataModel dataModel;
    private boolean first;

    public D50PartialMuteParamModel(D50PartialMuteDataModel dataModel,
            boolean first) {
        super(dataModel.getPatch(), dataModel.getOffset());
        this.dataModel = dataModel;
        this.first = first;
    }

    public void set(int value) {
        if (first) {
            dataModel.setPart1(value == 1);
        } else {
            dataModel.setPart2(value == 1);
        }
    }
}
