package org.jsynthlib.device.model;

import java.util.prefs.Preferences;

public interface DeviceFactory {

    /**
     * Create an instance of a device driver.
     * @param descriptor
     *            the class name of the device driver
     * @param prefs
     *            the preferences
     * @return an instance of the device driver, or null if one cannot be
     *         instantiated
     * @throws DeviceException
     *             if the device creation fails
     */
    Device newDevice(DeviceDescriptor descriptor, Preferences preferences)
            throws DeviceException;
}
