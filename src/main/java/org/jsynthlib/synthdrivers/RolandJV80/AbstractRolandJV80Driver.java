/*
 * Copyright 2014 Pascal Collberg
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
package org.jsynthlib.synthdrivers.RolandJV80;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.Device;

/**
 * @author Pascal Collberg
 */
public abstract class AbstractRolandJV80Driver extends AbstractPatchDriver
        implements NumberGenerator {

    /**
     * @param patchType
     * @param authors
     */
    public AbstractRolandJV80Driver(String patchType, String authors) {
        super(patchType, authors);
    }

    @Override
    public String[] generateNumbers(int min, int max, String format) {
        return super.generateNumbers(min, max, format);
    }

    protected void postSendWait() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignore) {
        }
    }
    
    // banknum == -1: patchNum -1: patch mode temp patch, 0..7 = perf mode temp
    // patch
    // tone -1 == patch common, tone 0..3 is tones for patch
    protected void setPatchNum(byte[] sysex, int offset, int bankNum,
            int patchNum, int toneNum) {
        sysex[offset + JV80Constants.ADDR1_IDX] = (byte) (0x01 + bankNum);
        if (patchNum == -1) {
            sysex[offset + JV80Constants.ADDR2_IDX] = 0x08;
        } else {
            sysex[offset + JV80Constants.ADDR2_IDX] = (byte) (0x40 + patchNum);
        }
        if (toneNum == -1) {
            sysex[offset + JV80Constants.ADDR3_IDX] = 0x20;
        } else {
            sysex[offset + JV80Constants.ADDR3_IDX] = (byte) (0x28 + toneNum);
        }
    }

    protected void setRequestLength(byte[] sysex, int size) {
        sysex[JV80Constants.SIZEL_IDX - 1] = (byte) (size >> 7);
        sysex[JV80Constants.SIZEL_IDX] = (byte) (size & 0x7F);
    }

    // offset points to the start of the message, not CHECKSUM_START!
    void calculateChecksum(byte sysex[], int offset, int datalen) {
        calculateChecksum(sysex, offset + JV80Constants.CHECKSUM_START, offset
                + JV80Constants.CHECKSUM_START + datalen + 3, offset
                + JV80Constants.CHECKSUM_START + datalen + 4);
    }

    protected void sendRequestSysex(Device dev, byte[] sysex) {
        sysex[JV80Constants.DEVICEIDOFFSET] = (byte) (dev.getDeviceID() - 1);
        calculateChecksum(sysex, 0, JV80Constants.SYSREQDATALEN);

        try {
            SysexMessage sm = new SysexMessage();
            sm.setMessage(sysex, sysex.length);
            dev.send(sm);
            postSendWait();
        } catch (InvalidMidiDataException imde) {
            ErrorMsg.reportError("program eror",
                    "Tried to send invalid midi data", imde);
        }
    }
}
