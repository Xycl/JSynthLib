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
package org.jsynthlib.midi.domain;

import javax.sound.midi.MidiMessage;

/**
 * @author Pascal Collberg
 */
public class MidiLogEntry {
    private final int port;
    private final boolean receive;
    private final MidiMessage message;

    public MidiLogEntry(final int port, final boolean receive,
            final MidiMessage message) {
        super();
        this.port = port;
        this.receive = receive;
        this.message = message;
    }

    public int getPort() {
        return port;
    }

    public boolean isReceive() {
        return receive;
    }

    public MidiMessage getMessage() {
        return message;
    }

}
