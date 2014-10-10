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
package org.jsynthlib.device.model.impl;

import javax.inject.Inject;

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DriverIdentifier;
import org.jsynthlib.device.model.IDriver;

/**
 * @author Pascal Collberg
 */
public class DriverIdentifierImpl implements DriverIdentifier {

    private DeviceManager deviceManager;

    @Inject
    public DriverIdentifierImpl(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    /**
     * choose proper driver for sysex byte array.
     * @param sysex
     *            System Exclusive data byte array.
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    public IDriver chooseDriver(byte[] sysex) {
        String patchString = getPatchHeader(sysex);

        for (int idev = 0; idev < deviceManager.deviceCount(); idev++) {
            // Outer Loop, iterating over all installed devices
            Device dev = deviceManager.getDevice(idev);
            for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
                IDriver drv = dev.getDriver(idrv);
                // Inner Loop, iterating over all Drivers of a device
                if (drv.supportsPatch(patchString, sysex)) {
                    return drv;
                }
            }
        }
        // Changed from "return null" - Emenaker 2006-02-03
        return deviceManager.getNullDriver();
    }

    /**
     * choose proper driver in a given device for sysex byte array.
     * @param sysex
     *            System Exclusive data byte array.
     * @param dev
     *            Device
     * @return Driver object chosen
     * @see IDriver#supportsPatch
     */
    public IDriver chooseDriver(byte[] sysex, Device dev) {
        String patchString = getPatchHeader(sysex);
        for (int idrv = 0; idrv < dev.driverCount(); idrv++) {
            IDriver drv = dev.getDriver(idrv);
            // Inner Loop, iterating over all Drivers of a device
            if (drv.supportsPatch(patchString, sysex)) {
                return drv;
            }
        }
        return null;
    }

    /**
     * Return a hexadecimal string for {@link IDriver#supportsPatch
     * IDriver.suppportsPatch} at most 16 byte sysex data.
     * @see IDriver#supportsPatch
     */
    public String getPatchHeader(byte[] sysex) {
        StringBuffer patchstring = new StringBuffer("F0");

        // Some Sysex Messages are shorter than 16 Bytes!
        // for (int i = 1; (sysex.length < 16) ? i < sysex.length : i < 16; i++)
        // {
        for (int i = 1; i < Math.min(16, sysex.length); i++) {
            if ((sysex[i] & 0xff) < 0x10) {
                patchstring.append("0");
            }
            patchstring.append(Integer.toHexString((sysex[i] & 0xff)));
        }
        return patchstring.toString();
    }
}
