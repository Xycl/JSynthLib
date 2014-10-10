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

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.SysexSender;

/**
 * @author Pascal Collberg
 */
public abstract class AbstractJV80SysexSender extends SysexSender {

    private final transient Logger log = Logger.getLogger(getClass());
    private int datalen;
    
    protected AbstractJV80SysexSender(String sysex, int datalen) {
        super(sysex);
        this.datalen = datalen;
    }
    
    void calculateChecksum(IDriver driver, byte sysex[]) {
        calculateChecksum(driver, sysex, 0);
    }

    // offset points to the start of the message, not CHECKSUM_START!
    void calculateChecksum(IDriver driver, byte sysex[], int offset) {
        driver.calculateChecksum(sysex, offset + JV80Constants.CHECKSUM_START, offset
                + JV80Constants.CHECKSUM_START + datalen + 3, offset
                + JV80Constants.CHECKSUM_START + datalen + 4);
    }


    // SysexWidget.ISender method
    public void send(IDriver driver, int value) {
        channel = (byte) driver.getDevice().getDeviceID();
        byte[] sysex = generate(value);
        calculateChecksum(driver, sysex);
        SysexMessage m = new SysexMessage();
        try {
            m.setMessage(sysex, sysex.length);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }
}
