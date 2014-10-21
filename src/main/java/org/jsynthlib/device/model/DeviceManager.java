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

import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

/**
 * @author Pascal Collberg
 */
public interface DeviceManager {

    /**
     * Create an instance of a device driver.
     * @param descriptor
     *            the class name of the device driver
     * @param prefs
     *            the preferences
     * @return an instance of the device driver, or null if one cannot be
     *         instantiated
     */
    Device addDevice(DeviceDescriptor descriptor, Preferences prefs)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException;

    /**
     * Add Device into <code>deviceList</code>. A new Preferences node will be
     * created for the Device.
     * @param className
     *            name of Device class (ex.
     *            "synthdrivers.KawaiK4.KawaiK4Device").
     * @return a <code>Device</code> value created.
     */
    Device addDevice(DeviceDescriptor descriptor);

    /** Indexed getter for deviceList elements */
    Device getDevice(int i);

    /**
     * Remover for deviceList elements. The caller must call
     * reassignDeviceDriverNums and revalidateLibraries.
     * @return <code>Device</code> object removed.
     */
    Device removeDevice(int i);

    /** Size query for deviceList */
    int deviceCount();

    /** Getter for the index of <code>device</code>. */
    int getDeviceIndex(Device device);

    /**
     * Returns null driver of Generic Device. It is used when proper driver is
     * not found.
     */
    IDriver getNullDriver();
}