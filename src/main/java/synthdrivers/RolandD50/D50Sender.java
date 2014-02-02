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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;

import org.apache.log4j.Logger;

import core.DriverUtil;
import core.IPatchDriver;
import core.SysexSender;

public class D50Sender extends SysexSender {

    private static final int DEVICE_ID_OFFSET = 2;
    public static final int VALUE_OFFSET = 8;
    public static final int ADDRESS_OFFSET = 5;
    public static final int ADDRESS_SIZE = 3;
    public static final Factory FACTORY = new Factory();
    // Base message containing one value
    public static final byte[] BASE_MESSAGE = new byte[] {
            (byte) 0xF0, 0x41, 0x00, 0x14, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00,
            (byte) 0xF7 };
    private static final int CS_START = D50Constants.SYSEX_HEADER_SIZE;
    private static final int CS_END = BASE_MESSAGE.length
            - D50Constants.SYSEX_FOOTER_SIZE;
    private static final int CS_OFS = BASE_MESSAGE.length - 2;
    protected byte[] message;
    private D50PartialMuteDataModel model;

    public static class Factory {
        private int deviceId;

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public D50Sender newSender(int offset) {
            return new D50Sender(offset, deviceId);
        }

        public D50Sender newPartialMuteSender(int offset,
                final D50PartialMuteDataModel model) {
            return new D50Sender(offset, deviceId, model) {
                void setMessage(int value) {
                    super.setMessage(model.getPatch().sysex[model.getOffset()]);
                }
            };
        }
    }

    private final transient Logger log = Logger.getLogger(getClass());

    D50Sender(int offset, int deviceId) {
        super();
        message = new byte[BASE_MESSAGE.length];
        System.arraycopy(BASE_MESSAGE, 0, message, 0, BASE_MESSAGE.length);
        message[DEVICE_ID_OFFSET] = (byte) deviceId;
        int value = offset;
        for (int index = 2; index >= 0; index--) {
            message[ADDRESS_OFFSET + index] = (byte) (value & 0x7f);
            value >>= 7;
        }
    }

    D50Sender(int offset, int deviceId, D50PartialMuteDataModel model) {
        this(offset, deviceId);
        this.model = model;
    }

    void setMessage(int value) {
        message[VALUE_OFFSET] = (byte) value;
    }

    public void send(IPatchDriver driver, int value) {
        channel = (byte) driver.getDevice().getDeviceID();
        SysexMessage m = new SysexMessage();
        try {
            setMessage(value);
            DriverUtil.calculateChecksum(message, CS_START, CS_END, CS_OFS);
            m.setMessage(message, message.length);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    byte[] getMessage() {
        return message;
    }
}
