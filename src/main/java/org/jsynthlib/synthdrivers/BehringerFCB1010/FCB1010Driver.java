/*
 * Copyright 2005 Jeff Weber
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

package org.jsynthlib.synthdrivers.BehringerFCB1010;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Behringer FCB1010 Driver.
 * @author Jeff Weber
 */
class FCB1010Driver extends AbstractPatchDriver {

    /**
     * FCB1010 Dump Request
     */
    private static final SysexHandler SYS_REQ = new SysexHandler(
            Constants.FCB1010_DUMP_REQ_ID); // FCB1010 Dump Request

    /**
     * Constructs a FCB1010Driver.
     */
    public FCB1010Driver() {
        super(Constants.FCB1010_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.FCB1010_SYSEX_MATCH_ID;

        patchSize = Constants.HDR_SIZE + Constants.FCB1010_NATIVE_SIZE + 1;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.FCB1010_BANK_LIST;
        patchNumbers = Constants.FCB1010_PATCH_LIST;
    }

    /**
     * Send Program Change MIDI message. The FCB1010 driver does not utilize
     * program change messages. This method is overriden with a null method.
     */
    @Override
    protected void setPatchNum(int patchNum) {
    }

    /**
     * Send Control Change (Bank Select) MIDI message. The FCB1010 driver does
     * not utilize bank select. This method is overriden with a null method.
     */
    @Override
    protected void setBankNum(int bankNum) {
    }

    /**
     * FCB1010Driver patch does not utilize checksum. Method overridded with
     * null method.
     */
    @Override
    public void calculateChecksum(Patch p) {
    }

    /**
     * FCB1010Driver patch does not utilize checksum. Method overridded with
     * null method.
     */
    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int offset) {
    }

    /**
     * Requests a dump of the FCB1010 patch. This patch does not utilize bank
     * select or program changes.
     */
    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "channel", getChannel())));
    }

    /**
     * Creates a getPatchFactory().createNewPatch with default values.
     */
    @Override
    protected Patch createNewPatch() {
        Patch p = getPatchFactory().createNewPatch(Constants.NEW_SYSEX, this);
        return p;
    }

    /**
     * Opens an edit window on the specified patch.
     */
    @Override
    public JSLFrame editPatch(Patch p) {
        return new FCB1010Editor(p);
    }
}
