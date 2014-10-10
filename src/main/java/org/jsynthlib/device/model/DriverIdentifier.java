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
package org.jsynthlib.device.model;

/**
 * @author Pascal Collberg
 */
public interface DriverIdentifier {

    /**
     * choose proper driver for sysex byte array.
     * @param sysex
     *            System Exclusive data byte array.
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    IDriver chooseDriver(byte[] sysex);

    /**
     * choose proper driver in a given device for sysex byte array.
     * @param sysex
     *            System Exclusive data byte array.
     * @param dev
     *            Device
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    IDriver chooseDriver(byte[] sysex, Device dev);

    /**
     * Return a hexadecimal string for {@link IDriver#supportsPatch
     * IDriver.suppportsPatch} at most 16 byte sysex data.
     * @see IDriver#supportsPatch
     */
    String getPatchHeader(byte[] sysex);
}
