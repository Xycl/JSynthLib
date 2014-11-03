/*
 * JSynthlib - "Performance" Single Driver for Yamaha DX7 Mark-I
 * =============================================================
 * @version $Id: YamahaDX7PerformanceSingleDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7;

import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyXmlVoicePatchDriver;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class YamahaDX7PerformanceSingleDriver extends DX7FamilyXmlVoicePatchDriver {

    public YamahaDX7PerformanceSingleDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec);
    }

    @Override
    public Patch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (isTipsMsg()) {
            // show Information
            YamahaDX7Strings.dxShowInformation(toString(),
                    YamahaDX7Strings.STORE_SINGLE_PERFORMANCE_STRING);
        }

        sendPatchWorker(p);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if (isTipsMsg()) {
            // show Information
            YamahaDX7Strings.dxShowInformation(toString(),
                    YamahaDX7Strings.PERFORMANCE_STRING);
        }
    }

//    @Override
//    public JSLFrame editPatch(Patch p) {
//        if (getSPBPflag()) {
//            // make Sys Info available
//            YamahaDX7SysexHelper.mkSysInfoAvail(this,
//                    (byte) (getChannel() + 0x10));
//        } else {
//            if (isTipsMsg()) {
//                // show Information
//                YamahaDX7Strings.dxShowInformation(toString(),
//                        YamahaDX7Strings.PERFORMANCE_EDITOR_STRING);
//            }
//        }
//
//        return super.editPatch(p);
//    }

}
