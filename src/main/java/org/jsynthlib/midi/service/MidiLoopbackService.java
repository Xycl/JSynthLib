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

/**
 * @author Pascal Collberg
 */
public interface MidiLoopbackService {

    /**
     * This runs a few tests on a midi in/out pair. The idea is that you connect
     * the two ports with a midi cable and it basically tries to send one of
     * every kind of message and it checks to see if those messages get back to
     * the "in" port intact. - emenake 2003.03.20
     * @param inport
     *            The number of the port for receiving
     * @param outport
     *            The number of the port for sending
     */
    boolean runLoopbackTest(int inport, int outport);
}
