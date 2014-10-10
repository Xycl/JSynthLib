package org.jsynthlib.device.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.jsynthlib.core.Constants;
import org.jsynthlib.core.ErrorMsg;

/**
 * This class provides methods for parsing and storing the available device
 * configurations. A device is a supported MIDI enabled device with a a
 * configuration stored in a <code>synthdrivers.properties</code> file.
 */
public final class DevicesConfig {

    private static final Logger LOG = Logger.getLogger(DevicesConfig.class);

    /**
     * The singleton instance.
     */
    private static DevicesConfig instance;

    /**
     * Get the singleton instance.
     * @return the singleton instance
     */
    public static synchronized DevicesConfig getInstance() {
        if (instance == null) {
            instance = new DevicesConfig();
        }
        return instance;
    }

    public static String getShortNameForClassName(final String s) {
        return s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
    }

    /**
     * The available device descriptors.
     */
    private final Set<DeviceDescriptor> descriptors =
            new TreeSet<DeviceDescriptor>();
    /**
     * The available device identifiers.
     */
    private final Set<String> deviceIds = new TreeSet<String>();

    /**
     * Get the available device descriptors.
     * @return the available device descriptors
     */
    public Collection<DeviceDescriptor> getDeviceDescriptors() {
        return Collections.unmodifiableSet(descriptors);
    }

    /**
     * Get the available device identifiers.
     * @return the available device identifiers
     */
    public Collection<String> getDeviceIds() {
        return Collections.unmodifiableSet(deviceIds);
    }

    /**
     * Get the class for a device based on device id.
     * @param deviceId
     *            the device id (for example F07E..0602400000040000000000f7)
     * @return the class for the device (for example,
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    public DeviceDescriptor getDescriptorForIDString(final String deviceId) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getDeviceId().equals(deviceId)) {
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Get the class for a device based on short name.
     * @param shortName
     *            the device short name
     * @return the class for the device (for example,
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    public DeviceDescriptor getDescriptorForShortName(final String shortName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getShortName().equals(shortName)) {
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Get the class for a device based on device name.
     * @param deviceName
     *            the device name (for example Kawai K4/K4R Driver)
     * @return the class for the device (for example
     *         synthdrivers.KawaiK4.KawaiK4Device)
     */
    public DeviceDescriptor getDescriptorForDeviceName(final String deviceName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getDeviceName().equals(deviceName)) {
                return descriptor;
            }
        }
        return null;
    }

    /**
     * Print all available device descriptors to the console.
     */
    public void printAll() {
        for (DeviceDescriptor descriptor : descriptors) {
            LOG.debug(descriptor);
        }
    }

    /**
     * Construct an instance of the device configuration.
     */
    private DevicesConfig() {
        readDevicesFromPropertiesFile();
    }

    /**
     * Read the available devices from a properties file.
     */
    private void readDevicesFromPropertiesFile() {
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
            LOG.warn(exception.getMessage(), exception);
            return;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                LOG.warn(e.getMessage(), e);
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
                    addDevice(deviceName, shortName, deviceClass, idString,
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
    private void addDevice(String deviceName, String shortName,
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
}
