/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.synthdrivers.AlesisDM5;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Alesis DM5 Trigger Setup Driver.
 * @author Jeff Weber
 */
public class AlesisDM5TrSetDriver extends AbstractPatchDriver {

    /**
     * Alesis DM5 Trigger Setup Driver.
     */
    private static final SysexHandler SYS_REQ = new SysexHandler(
            Constants.TRIG_SETP_DUMP_REQ_ID); // System Info Dump Request

    /** Sysex program dump byte array representing a new trigger setup patch */
    private static final byte NEW_SYSEX[] = {
            (byte) 0xF0, (byte) 0x00, (byte) 0x00, (byte) 0x0E, (byte) 0x13,
            (byte) 0x00, (byte) 0x05, (byte) 0x63, (byte) 0x63, (byte) 0x63,
            (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x63,
            (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07,
            (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07, (byte) 0x63,
            (byte) 0x63, (byte) 0x63, (byte) 0x07, (byte) 0x63, (byte) 0x63,
            (byte) 0x63, (byte) 0x07, (byte) 0x63, (byte) 0x63, (byte) 0x63,
            (byte) 0x07, (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07,
            (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07, (byte) 0x63,
            (byte) 0x63, (byte) 0x63, (byte) 0x07, (byte) 0x63, (byte) 0x63,
            (byte) 0x63, (byte) 0x07, (byte) 0x63, (byte) 0x63, (byte) 0x63,
            (byte) 0x07, (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07,
            (byte) 0x63, (byte) 0x63, (byte) 0x63, (byte) 0x07, (byte) 0x63,
            (byte) 0x63, (byte) 0x63, (byte) 0x24, (byte) 0xF7 };

    /**
     * Constructs a AlesisDM5TrSetDriver.
     */
    public AlesisDM5TrSetDriver() {
        super(Constants.TRIG_SETP_PATCH_TYP_STR, Constants.AUTHOR);
        sysexID = Constants.TRIG_SETP_SYSEX_MATCH_ID;

        patchSize = Constants.HDR_SIZE + Constants.TRIG_SETP_SIZE + 1;
        deviceIDoffset = Constants.DEVICE_ID_OFFSET;
        bankNumbers = Constants.TRIG_SETP_BANK_LIST;
        patchNumbers = Constants.TRIG_SETP_PATCH_LIST;
        checksumStart = Constants.HDR_SIZE;
        checksumEnd = patchSize - 3;
        checksumOffset = checksumEnd + 1;
    }

    /**
     * Constructs a AlesisDM5TrSetDriver.
     */
    public AlesisDM5TrSetDriver(String patchType, String authors) {
        super(patchType, authors);
    }

    /**
     * Send Program Change MIDI message. The Alesis Trigger Setup driver does
     * not utilize program change messages. This method is overriden with a null
     * method.
     */
    @Override
    protected void setPatchNum(int patchNum) {
    }

    /**
     * Send Control Change (Bank Select) MIDI message. The Alesis Trigger Setup
     * driver does not utilize bank select. This method is overriden with a null
     * method.
     */
    @Override
    protected void setBankNum(int bankNum) {
    }

    /**
     * Calculates the checksum for the DM5. Equal to the mod 128 of the sum of
     * all the bytes from offset header+1 to offset total patchlength-3.
     */
    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int offset) {
        int sum = 0;

        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[offset] = (byte) (sum % 128);
    }

    /**
     * Requests a dump of the system info message. This patch does not utilize
     * bank select or program changes.
     */
    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(), new SysexHandler.NameValue(
                "channel", getChannel())));
    }

    /**
     * Creates a new trigger setup patch with default values.
     */
    @Override
    protected Patch createNewPatch() {
        Patch p = getPatchFactory().createNewPatch(NEW_SYSEX, this);
        calculateChecksum(p);
        return p;
    }

    /**
     * Opens an edit window on the specified patch.
     */
    @Override
    public JSLFrame editPatch(Patch p) {
        return new AlesisDM5TrSetEditor(p);
    }
}