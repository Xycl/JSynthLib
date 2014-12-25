/*
 * Copyright 2002 Roger Westerlund
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

package org.jsynthlib.synthdrivers.RolandD10;

import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_PATCH_TEMP_AREA;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_RYTHM_SETUP_TEMP_AREA;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_TIMBRE_TEMP_AREA;
import static org.jsynthlib.synthdrivers.RolandD10.D10Constants.BASE_TONE_TEMP_AREA;

import java.util.HashMap;

import org.jsynthlib.device.model.handler.SysexSender;
import org.jsynthlib.synthdrivers.RolandD10.message.D10DataSetMessage;

/**
 * @author Roger Westerlund <roger.westerlund@home.se>
 */
public class EditSender extends SysexSender {

    private static HashMap senderMap = new HashMap(200);

    private D10DataSetMessage message;

    private int address;

    public EditSender() {

    }

    protected EditSender(Entity base, int offset) {
        address = Entity.createFromIntValue(offset).add(base).getDataValue();
        message = new D10DataSetMessage(1, address);
    }

    public static void setDeviceId(int deviceId) {
    }

    public static EditSender getToneSender(int offset) {
        return getSender(BASE_TONE_TEMP_AREA, offset);
    }

    public static EditSender getTimbreSender(int offset) {
        return getSender(BASE_TIMBRE_TEMP_AREA, offset);
    }

    public static EditSender getPatchSender(int offset) {
        return getSender(BASE_PATCH_TEMP_AREA, offset);
    }

    public static EditSender getRythmSetupSender(int offset) {
        return getSender(BASE_RYTHM_SETUP_TEMP_AREA, offset);
    }

    public static EditSender getSender(Entity base, int offset) {
        Integer key = new Integer(base.getIntValue() + offset);
        Object sender = senderMap.get(key);
        if (null == sender) {
            sender = new EditSender(base, offset);
            senderMap.put(key, sender);
        }
        return (EditSender) sender;
    }

    @Override
    public byte[] generate(int value) {
        message.setDeviceId(getChannel());
        message.setData((byte) value);
        return message.getBytes();
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
        message = new D10DataSetMessage(1, address);
    }
}
