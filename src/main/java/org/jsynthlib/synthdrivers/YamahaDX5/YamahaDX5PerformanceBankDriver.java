/*
 * JSynthlib - "Performance" Bank Driver for Yamaha DX5
 * ====================================================
 * @version $Id: YamahaDX5PerformanceBankDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
package org.jsynthlib.synthdrivers.YamahaDX5;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyDevice;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyPerformanceBankDriver;

public class YamahaDX5PerformanceBankDriver extends
        DX7FamilyPerformanceBankDriver {
    public YamahaDX5PerformanceBankDriver() {
        super(YamahaDX5PerformanceConstants.INIT_PERFORMANCE,
                YamahaDX5PerformanceConstants.BANK_PERFORMANCE_PATCH_NUMBERS,
                YamahaDX5PerformanceConstants.BANK_PERFORMANCE_BANK_NUMBERS);
    }

    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return super.editPatch(p);
    }

    public void sendPatch(Patch p, int bankNum, int patchNum) {
        if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
            // show Information
            YamahaDX5Strings.dxShowInformation(toString(),
                    YamahaDX5Strings.SELECT_PATCH_STRING);
        }

        sendPatchWorker(p);
    }

    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        sendPatch(p, bankNum, patchNum);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if ((((DX7FamilyDevice) (getDevice())).getTipsMsgFlag() & 0x01) == 1) {
            // show Information
            YamahaDX5Strings.dxShowInformation(toString(),
                    YamahaDX5Strings.SELECT_PATCH_STRING);
        }

        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }

}
