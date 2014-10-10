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
package org.jsynthlib.midi.service;

import javax.sound.midi.MidiMessage;

/**
 * @author Pascal Collberg
 */
public interface MidiMonitorService {

    void addLogListener(MidiLogListener l);

    void removeLogListener(MidiLogListener l);

    /**
     * Dump output MidiMessage <code>msg</code> on the MIDI Monitor Window with
     * port number information.
     * @param port
     *            port number
     * @param msg
     *            MidiMessage
     */
    void logIn(int port, MidiMessage msg);

    /**
     * Dump input MidiMessage <code>msg</code> on the MIDI Monitor Window with
     * port number information.
     * @param port
     *            port number
     * @param msg
     *            MidiMessage
     */
    void logOut(int port, MidiMessage msg);

    void logOut(MidiMessage msg);
}
