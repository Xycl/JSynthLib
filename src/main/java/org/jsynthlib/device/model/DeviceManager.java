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

import java.util.Collection;

/**
 * @author Pascal Collberg
 */
public interface DeviceManager {

    /**
     * Add Device into <code>deviceList</code>. A new Preferences node will be
     * created for the Device.
     * @param className
     *            name of Device class (ex.
     *            "synthdrivers.KawaiK4.KawaiK4Device").
     * @return a <code>Device</code> value created.
     * @throws DeviceException
     *             if the device creation fails
     */
    Device addDevice(DeviceDescriptor descriptor) throws DeviceException;

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

    /**
     * Get the available device descriptors.
     * @return the available device descriptors
     */
    Collection<DeviceDescriptor> getDeviceDescriptors();

    /**
     * Get the available device identifiers.
     * @return the available device identifiers
     */
    Collection<String> getDeviceIds();

    /**
     * Get the class for a device based on device id.
     * @param deviceId
     *            the device id (for example F07E..0602400000040000000000f7)
     * @return the class for the device (for example,
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    DeviceDescriptor getDescriptorForIDString(String deviceId);

    /**
     * Get the class for a device based on short name.
     * @param shortName
     *            the device short name
     * @return the class for the device (for example,
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    DeviceDescriptor getDescriptorForShortName(String shortName);

    /**
     * Get the class for a device based on device name.
     * @param deviceName
     *            the device name (for example Kawai K4/K4R Driver)
     * @return the class for the device (for example
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    DeviceDescriptor getDescriptorForDeviceName(String deviceName);

    /**
     * Makes the device available to JSynthLib
     * @param deviceName
     *            Name of the device
     * @param shortName
     *            Short name of the device
     * @param deviceClass
     *            The class of the device
     * @param deviceId
     *            The id of the device
     * @param manufacturer
     * @param type
     */
    void installDevice(String deviceName, String shortName, String deviceClass,
            String deviceId, String manufacturer, String type);

    /**
     * Removes the device from the installable device list. TODO: Should also
     * remove any running instances.
     * @param deviceDescriptor
     */
    void uninstallDevice(DeviceDescriptor deviceDescriptor);

    /**
     * Print all available device descriptors to the console.
     */
    void printAll();

}
