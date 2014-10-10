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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSynthLib;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceList;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DevicesConfig;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.device.model.XMLPatchDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.xmldevice.XmlBankDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlBankDriverSpecDocument.XmlBankDriverSpec;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument;
import org.jsynthlib.xmldevice.XmlDeviceSpecDocument.XmlDeviceSpec;
import org.jsynthlib.xmldevice.XmlDriverDefs;
import org.jsynthlib.xmldevice.XmlDriverDefs.XmlDriverDef;
import org.jsynthlib.xmldevice.XmlDriverDefs.XmlDriverDef.DriverType.Enum;
import org.jsynthlib.xmldevice.XmlDriverSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;

/**
 * @author Pascal Collberg
 */
@Singleton
public class DeviceManagerImpl implements DeviceManager {

    private final transient Logger log = Logger.getLogger(getClass());

    private final DeviceList deviceList;
    private Preferences devicePreferences;

    @Inject
    public DeviceManagerImpl(DeviceList deviceList) {
        this.deviceList = deviceList;
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
                DevicesConfig devConfig = DevicesConfig.getInstance();
                DeviceDescriptor descriptor =
                        devConfig.getDescriptorForShortName(s);

                log.info("loadDevices: -> " + s);
                addDevice(descriptor, devicePreferences.node(devs[i]));

                // default look and feel is
            }
            log.debug("deviceList: " + deviceList);
        } catch (BackingStoreException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }

    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.DeviceFactory#newDevice()
     */
    @Override
    public Device addDevice(DeviceDescriptor descriptor, Preferences preferences) {
        Device device = null;
        try {
            String deviceClass = descriptor.getDeviceClass();
            device = createDevice(deviceClass);
            if (!deviceList.contains(device)) {
                device.setup(preferences);
                deviceList.add(device); // always returns true
                int driverCount = device.driverCount();
                for (int i = 0; i < driverCount; i++) {
                    IDriver driver = device.getDriver(i);
                    DriverBeanUtil.copyPreferences(driver);
                }
            }
        } catch (SecurityException e) {
            log.warn(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
        } catch (InstantiationException e) {
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            log.warn(e.getMessage(), e);
        } catch (XmlException e) {
            log.warn(e.getMessage(), e);
        } catch (IOException e) {
            log.warn(e.getMessage(), e);
        }

        if (device == null) {
            ErrorMsg.reportError("Device Create Failure",
                    "Failed to create device of class '" + descriptor + "'");
        }
        return device;
    }

    @Override
    public Device addDevice(DeviceDescriptor descriptor) {
        return addDevice(descriptor, getDeviceNode(descriptor.getDeviceClass()));
    }

    /** returns the 1st unused device node name for Preferences. */
    private Preferences getDeviceNode(String s) {
        log.info("getDeviceNode: " + s);
        s = DevicesConfig.getShortNameForClassName(s);
        log.info("getDeviceNode: -> " + s);
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

    @SuppressWarnings("unchecked")
    Device createDevice(String resource) throws XmlException, IOException,
            ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        InputStream xmlStream =
                getClass().getClassLoader().getResourceAsStream(
                        resource.replace('.', '/') + ".xml");
        if (xmlStream == null) {
            // Oldstyle device
            Class<Device> c = (Class<Device>) Class.forName(resource);
            return JSynthLibInjector.getInstance(c);
        } else {
            // XML device
            XmlDeviceSpecDocument deviceSpecDocument =
                    XmlDeviceSpecDocument.Factory.parse(xmlStream);
            XmlDeviceSpec deviceSpec = deviceSpecDocument.getXmlDeviceSpec();

            Class<? extends XMLDevice> c = null;
            try {
                c = (Class<XMLDevice>) Class.forName(resource);
            } catch (ClassNotFoundException e) {
                c = XMLDevice.class;
            }
            Class<?>[] args = {
                XmlDeviceSpec.class };
            Constructor<? extends XMLDevice> con = c.getConstructor(args);
            XMLDevice device = con.newInstance(new Object[] {
                deviceSpec });
            JSynthLibInjector.getInjector().injectMembers(device);
            XmlDriverDefs drivers = deviceSpec.getDrivers();

            XmlDriverDef[] driverArray = drivers.getXmlDriverDefArray();
            for (XmlDriverDef xmlDriver : driverArray) {
                IDriver driver = createDriver(xmlDriver);
                device.addDriver(driver);
            }

            return device;
        }
    }

    @SuppressWarnings("unchecked")
    IDriver createDriver(XmlDriverDef xmlDriver) throws XmlException,
            IOException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException,
            ClassNotFoundException {
        String driverClass = xmlDriver.getDriverClass();
        InputStream xmlStream =
                getClass().getClassLoader().getResourceAsStream(
                        driverClass.replace('.', '/') + ".xml");

        if (xmlStream == null) {
            // Oldstyle device
            Class<IDriver> c = (Class<IDriver>) Class.forName(driverClass);
            return JSynthLibInjector.getInstance(c);
        } else {
            // XML driver
            Enum driverType = xmlDriver.getDriverType();
            XmlDriverSpec driverSpec = null;

            Class<? extends IDriver> c = null;
            try {
                c = (Class<? extends IDriver>) Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                c = XMLPatchDriver.class;
            }

            Constructor<? extends IDriver> con = null;
            switch (driverType.intValue()) {
            case XmlDriverDef.DriverType.INT_BANK:
                XmlBankDriverSpecDocument document =
                        XmlBankDriverSpecDocument.Factory.parse(xmlStream);
                driverSpec = document.getXmlBankDriverSpec();
                Class<?>[] args = {
                    XmlBankDriverSpec.class };
                con = c.getConstructor(args);
                break;
            case XmlDriverDef.DriverType.INT_CONVERTER:
                // TODO
                break;
            case XmlDriverDef.DriverType.INT_PATCH:
                XmlPatchDriverSpecDocument patchDriverDocument =
                        XmlPatchDriverSpecDocument.Factory.parse(xmlStream);
                driverSpec = patchDriverDocument.getXmlPatchDriverSpec();
                Class<?>[] args2 = {
                    XmlPatchDriverSpec.class };
                con = c.getConstructor(args2);
                break;
            default:
                throw new IllegalArgumentException("Bad patch type: "
                        + driverType.intValue());
            }

            IDriver driver = con.newInstance(new Object[] {
                driverSpec });
            DriverBeanUtil.copyXmlProperties(driver, driverSpec);
            return driver;
        }
    }
}
