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

package org.jsynthlib.midi.service.impl;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.midi.service.MidiMonitorService;

/**
 * MIDI input queue.
 * @author Pascal Collberg
 */
public class SysexInputQueue implements Receiver {

    private static final int EMPTY_LIST_TIMEOUT = 10;

    /**
     * For 32KB sysex message which is biggest we know.
     */
    private static final int DEFAULT_TIMEOUT = 10000;
    private static final int MIN_TIMEOUT = 1000;

    private List<MidiMessage> list;
    private MidiMonitorService midiMonitor;

    private int port;

    public SysexInputQueue(int port) {
        list = Collections.synchronizedList(new LinkedList<MidiMessage>());
        this.port = port;
    }

    // Receiver interface

    public void send(MidiMessage msg, long timeStamp) {
        int status = msg.getStatus();
        if ((status == SysexMessage.SYSTEM_EXCLUSIVE)
                || (status == SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE)) {
            list.add(msg);
        }
    }

    public void close() {
    }

    void clearQueue() {
        list.clear();
    }

    boolean isEmpty() {
        return list.size() == 0;
    }

    MidiMessage getMessage() throws InvalidMidiDataException, TimeoutException {
        // pop the oldest message
        MidiMessage msg = (MidiMessage) list.remove(0);
        // for java 1.4.2 bug
        msg = fixShortMessage(msg);
        midiMonitor.logIn(port, msg);
        return msg;
    }

    MidiMessage getMessage(long timeout) throws TimeoutException,
            InvalidMidiDataException {
        long start = System.currentTimeMillis();
        byte[] buffer = {};
        int totalLen = 0;
        boolean firstMsg = true;
        if (timeout == 0) {
            timeout = DEFAULT_TIMEOUT;
        } else if (timeout < MIN_TIMEOUT) {
            timeout = MIN_TIMEOUT;
        }
        do {
            // wait for data
            while (isEmpty()) {
                try {
                    Thread.sleep(EMPTY_LIST_TIMEOUT);
                } catch (InterruptedException e) {
                    // ignore
                }
                if (System.currentTimeMillis() - start > timeout) {
                    throw new TimeoutException();
                }
            }
            MidiMessage msg = getMessage();
            if (msg == null) {
                throw new InvalidMidiDataException(); // !!!add info
            }
            int len = msg.getLength();
            if (firstMsg) {
                if (msg.getStatus() != SysexMessage.SYSTEM_EXCLUSIVE) {
                    // this is illegal and just ignore
                    continue;
                }
                buffer = msg.getMessage();
                totalLen = len;
                if (buffer[totalLen - 1] == (byte) ShortMessage.END_OF_EXCLUSIVE) {
                    return msg;
                }
                firstMsg = false;
            } else {
                int status = msg.getStatus();
                // take the Real Time messages (0xf8-0xff) out of
                // the messages a MidiWrapper returns.
                if ((status & 0xf8) == 0xf8) {
                    continue;
                }
                // throw an Exception, if an exclusive message is
                // terminated by "any other Status byte (except
                // Real Time messages)".
                // THIS IS NOT CORRECT BEHAVIOR. !!!FIXIT!!!
                if (status != SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE) {
                    throw new InvalidMidiDataException(); // add info !!!
                }
                // Combine the newly-read stuff into an new array
                // with the existing stuff
                byte[] buf = msg.getMessage();
                byte[] combineBuffer = new byte[totalLen + len];
                System.arraycopy(buffer, 0, combineBuffer, 0, totalLen);
                if (len == 1) { // I think this is javax.sound.midi bug.
                    combineBuffer[totalLen] =
                            (byte) ShortMessage.END_OF_EXCLUSIVE;
                    totalLen++;
                } else {
                    System.arraycopy(buf, 1, combineBuffer, totalLen, len - 1);
                    totalLen += len - 1;
                }
                buffer = combineBuffer;
            }
        } while (firstMsg
                || buffer[totalLen - 1] != (byte) ShortMessage.END_OF_EXCLUSIVE);
        SysexMessage sysexmsg = new SysexMessage();
        sysexmsg.setMessage(buffer, totalLen);
        return sysexmsg;
    }

    MidiMessage fixShortMessage(MidiMessage msg)
            throws InvalidMidiDataException {
        // We cannot use
        // "msg instanceof com.sun.media.sound.FastShortMessage"
        // since we don't have the class.
        if (msg.getClass().toString()
                .equals("class com.sun.media.sound.FastShortMessage")) {
            return conv(msg);
        } else {
            return msg;
        }
    }

    /**
     * Convert a <code>com.sun.media.sound.FastShortMessage</code> object to a
     * <code>ShortMessage</code> object.
     */
    ShortMessage conv(MidiMessage mm) throws InvalidMidiDataException {
        ShortMessage m = (ShortMessage) mm;
        ShortMessage msg = new ShortMessage();
        int c = m.getStatus();
        if (c < 0xf0) {
            c = m.getStatus() & 0xf0;
        }
        switch (c) {
        case 0x80:
        case 0x90:
        case 0xa0:
        case 0xb0:
        case 0xe0:
        case 0xf2:
            msg.setMessage(c, m.getData1(), m.getData2());
            break;
        case 0xc0:
        case 0xd0:
        case 0xf1:
        case 0xf3:
            msg.setMessage(c, m.getData1(), 0);
            break;
        case 0xf4:
        case 0xf5:
        case 0xf6:
        case 0xf7:
        case 0xf8:
        case 0xf9:
        case 0xfa:
        case 0xfb:
        case 0xfc:
        case 0xfd:
        case 0xfe:
        case 0xff:
            msg.setMessage(c);
            break;
        default:
            throw new InvalidMidiDataException();
        }
        return msg;
    }

    public MidiMonitorService getMidiMonitor() {
        return midiMonitor;
    }

    @Inject
    public void setMidiMonitor(MidiMonitorService midiMonitor) {
        this.midiMonitor = midiMonitor;
    }

}
