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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.jsynthlib.core.Constants;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSynthLib;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.DeviceFactory;
import org.jsynthlib.device.model.DeviceList;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.IDriver;

/**
 * @author Pascal Collberg
 */
@Singleton
public class DeviceManagerImpl implements DeviceManager {

    private final transient Logger log = Logger.getLogger(getClass());

    private final DeviceList deviceList;
    private Preferences devicePreferences;

    /**
     * The available device descriptors.
     */
    private final Set<DeviceDescriptor> descriptors;

    /**
     * The available device identifiers.
     */
    private final Set<String> deviceIds;

    private final DeviceFactory deviceFactory;

    @Inject
    public DeviceManagerImpl(DeviceList deviceList, DeviceFactory deviceFactory) {
        this.deviceList = deviceList;
        this.deviceFactory = deviceFactory;
        descriptors = new TreeSet<DeviceDescriptor>();
        deviceIds = new TreeSet<String>();
        readDevicesFromPropertiesFile();

        try {
            Preferences preferences =
                    Preferences.userNodeForPackage(JSynthLib.class);
            devicePreferences = preferences.node("devices");

            try {
                preferences.sync();
            } catch (BackingStoreException e) {
                log.warn(e.getMessage(), e);
            }
            String[] devs;
            // Some classes assume that the 1st driver is a Generic Driver.
            DeviceDescriptor defaultDescriptor = new DeviceDescriptor();
            defaultDescriptor
            .setDeviceClass("org.jsynthlib.synthdrivers.Generic.GenericDevice");
            if (deviceList.isEmpty()) {
                if (devicePreferences.nodeExists("Generic#0")) {
                    addDevice(defaultDescriptor,
                            devicePreferences.node("Generic#0"));
                } else {
                    // create for the 1st time.
                    addDevice(defaultDescriptor);
                }
            }
            devs = devicePreferences.childrenNames();

            for (int i = 0; i < devs.length; i++) {
                if ("Generic#0".equals(devs[i])) {
                    continue;
                }

                // get class name from preferences node name
                log.debug("loadDevices: \"" + devs[i] + "\"");
                String s = devs[i].substring(0, devs[i].indexOf('#'));
                DeviceDescriptor descriptor = getDescriptorForShortName(s);

                log.info("loadDevices: -> " + s);
                addDevice(descriptor, devicePreferences.node(devs[i]));

                // default look and feel is
            }
            log.debug("deviceList: " + deviceList);
        } catch (BackingStoreException e) {
            log.warn(e.getMessage(), e);
        } catch (DeviceException e) {
            log.warn(e.getMessage(), e);
        }
    }

    Device addDevice(DeviceDescriptor descriptor, Preferences prefs)
            throws DeviceException {
        return deviceFactory.newDevice(descriptor, prefs);
    }

    @Override
    public Device addDevice(DeviceDescriptor descriptor) throws DeviceException {
        return addDevice(descriptor, getDeviceNode(descriptor.getDeviceClass()));
    }

    /** returns the 1st unused device node name for Preferences. */
    Preferences getDeviceNode(String s) {
        s = s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
        log.debug("getDeviceNode: -> " + s);
        try {
            int i = 0;
            while (devicePreferences.nodeExists(s + "#" + i)) {
                i++;
            }
            return devicePreferences.node(s + "#" + i);
        } catch (BackingStoreException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#getDevice(int)
     */
    @Override
    public Device getDevice(int i) {
        return deviceList.get(i);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#removeDevice(int)
     */
    @Override
    public Device removeDevice(int i) {
        Device ret = deviceList.remove(i);
        try {
            ret.getPreferences().removeNode();
        } catch (BackingStoreException e) {
            log.warn(e.getMessage(), e);
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#deviceCount()
     */
    @Override
    public int deviceCount() {
        return deviceList.size();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.DeviceManager#getDeviceIndex(org.jsynthlib
     * .device.model.Device)
     */
    @Override
    public int getDeviceIndex(Device device) {
        return deviceList.indexOf(device);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#getNullDriver()
     */
    @Override
    public IDriver getNullDriver() {
        return getDevice(0).getDriver(0);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#getDeviceDescriptors()
     */
    @Override
    public Collection<DeviceDescriptor> getDeviceDescriptors() {
        return Collections.unmodifiableSet(descriptors);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#getDeviceIds()
     */
    @Override
    public Collection<String> getDeviceIds() {
        return Collections.unmodifiableSet(deviceIds);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.DeviceManager#getDescriptorForIDString(java
     * .lang.String)
     */
    @Override
    public DeviceDescriptor getDescriptorForIDString(final String deviceId) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getDeviceId().equals(deviceId)) {
                return descriptor;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.DeviceManager#getDescriptorForShortName(java
     * .lang.String)
     */
    @Override
    public DeviceDescriptor getDescriptorForShortName(final String shortName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getShortName().equals(shortName)) {
                return descriptor;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.DeviceManager#getDescriptorForDeviceName(java
     * .lang.String)
     */
    @Override
    public DeviceDescriptor getDescriptorForDeviceName(final String deviceName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getDeviceName().equals(deviceName)) {
                return descriptor;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceManager#printAll()
     */
    @Override
    public void printAll() {
        for (DeviceDescriptor descriptor : descriptors) {
            log.debug(descriptor);
        }
    }

    /**
     * Read the available devices from a properties file.
     */
    void readDevicesFromPropertiesFile() {
        InputStream in =
                getClass().getResourceAsStream(
                        "/" + Constants.DEV_CONFIG_FILE_NAME);

        if (in == null) {
            ErrorMsg.reportError("Configuration Error",
                    "Device configuration file "
                            + Constants.DEV_CONFIG_FILE_NAME + " not found.");
            return;
        }

        Properties props = new Properties();

        try {
            props.load(in);
        } catch (Exception exception) {
            ErrorMsg.reportError("Configuration Error",
                    "Failed to read configuration file "
                            + Constants.DEV_CONFIG_FILE_NAME + ".");
            log.warn(exception.getMessage(), exception);
            return;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
        }

        for (String name : props.stringPropertyNames()) {
            if (name.startsWith(Constants.DEV_CONFIG_DEVICE_NAME_PREFIX)) {
                // Process a device
                String shortName =
                        name.substring(Constants.DEV_CONFIG_DEVICE_NAME_PREFIX
                                .length());
                String deviceName = props.getProperty(name);
                String deviceClass =
                        props.getProperty(Constants.DEV_CONFIG_DEVICE_CLASS_PREFIX
                                + shortName);
                String idString =
                        props.getProperty(Constants.DEV_CONFIG_ID_STRING_PREFIX
                                + shortName);
                String manufacturer =
                        props.getProperty(Constants.DEV_CONFIG_MANUFACTURER_PREFIX
                                + shortName);
                // Since Devices don't have types yet, just use the first letter
                // of the manufacturer
                // so that we can test it.
                String type = manufacturer.substring(0, 1);

                if (deviceClass != null && idString != null) {
                    installDevice(deviceName, shortName, deviceClass, idString,
                            manufacturer, type);
                } else {
                    ErrorMsg.reportError("Configuration Error",
                            "Invalid device configuration for " + shortName
                            + ".");
                }
            }
        }
    }

    /**
     * Add a device.
     * @param deviceName
     *            the name of the device
     * @param shortName
     *            the short name of the device
     * @param deviceClass
     *            the class of the device
     * @param deviceId
     *            the id of the device
     */
    @Override
    public void installDevice(String deviceName, String shortName,
            String deviceClass, String deviceId, String manufacturer,
            String type) {
        DeviceDescriptor descriptor = new DeviceDescriptor();
        descriptor.setDeviceName(deviceName);
        descriptor.setShortName(shortName);
        descriptor.setDeviceClass(deviceClass);
        descriptor.setDeviceId(deviceId);
        descriptor.setManufacturer(manufacturer);
        descriptor.setType(type);

        descriptors.add(descriptor);

        deviceIds.add(deviceId);
    }

    @Override
    public void uninstallDevice(DeviceDescriptor deviceDescriptor) {
        descriptors.remove(deviceDescriptor);
        deviceIds.remove(deviceDescriptor.getDeviceId());
    }
}
