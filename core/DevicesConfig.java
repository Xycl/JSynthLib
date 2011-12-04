package core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;

import org.jsynthlib.jsynthlib.xml.XMLDeviceFactory;

/**
 * This class provides methods for parsing and storing the available device
 * configurations. A device is a supported MIDI enabled device with a
 * a configuration stored in a <code>synthdrivers.properties</code> file.
 */
public class DevicesConfig {
    /**
     * The separator for device configuration files in the application configuration.
     */
    private static final char XML_FILE_SEPARATOR = ':';
    /**
     * The default configuration value for an unknown property.
     */
    private static final String DEFAULT_CONFIGURATION_VALUE = "";

    /**
     * The singleton instance.
     */
    private static DevicesConfig instance;

    /**
     * Get the singleton instance.
     *
     * @return the singleton instance
     */
    public static synchronized DevicesConfig getInstance() {
        if (instance == null) {
            instance = new DevicesConfig();
        }
        return instance;
    }

    public static String getShortNameForClassName(final String s) {
        if (s.charAt(0) == XML_FILE_SEPARATOR) {
            int start = s.lastIndexOf(XML_FILE_SEPARATOR);
            int end = s.lastIndexOf(".xml");
            return s.substring(start + 1, end) + "(XML)";
        }
        return s.substring(s.lastIndexOf('.') + 1, s.lastIndexOf("Device"));
    }

    /**
     * The available device descriptors.
     */
    private Set<DeviceDescriptor> descriptors = new TreeSet<DeviceDescriptor>();
    /**
     * The available device identifiers.
     */
    private Set<String> deviceIds = new TreeSet<String>();

    /**
     * Get the available device descriptors.
     *
     * @return the available device descriptors
     */
    public Collection<DeviceDescriptor> getDeviceDescriptors() {
        return Collections.unmodifiableSet(descriptors);
    }

    /**
     * Get the available device identifiers.
     *
     * @return the available device identifiers
     */
    public Collection<String> getDeviceIds() {
        return Collections.unmodifiableSet(deviceIds);
    }

    /**
     * Get the class for a device based on device id.
     *
     * @param deviceId the device id (for example F07E..0602400000040000000000f7)
     * @return the class for the device (for example, synthdrivers.KawaiK4.KawaiK4Device)
     */
    public String getClassNameForIDString(final String deviceId) {
        DeviceDescriptor descriptor = getDeviceDescriptorForIDString(deviceId);
        return descriptor != null ? descriptor.getDeviceClass() : DEFAULT_CONFIGURATION_VALUE;
    }

    /**
     * Get the class for a device based on short name.
     *
     * @param shortName the device short name
     * @return the class for the device (for example, synthdrivers.KawaiK4.KawaiK4Device)
     */
    public String getClassNameForShortName(String shortName) {
        DeviceDescriptor descriptor = getDeviceDescriptorForShortName(shortName);
        return descriptor != null ? descriptor.getDeviceClass() : DEFAULT_CONFIGURATION_VALUE;
    }

    /**
     * Get the class for a device based on device name.
     *
     * @param deviceName the device name (for example Kawai K4/K4R Driver)
     * @return the class for the device (for example synthdrivers.KawaiK4.KawaiK4Device)
     */
    public String getClassNameForDeviceName(final String deviceName) {
        DeviceDescriptor descriptor = getDeviceDescriptorForDeviceName(deviceName);
        return descriptor != null ? descriptor.getDeviceClass() : DEFAULT_CONFIGURATION_VALUE;
    }

    /**
     * Get the manufacturer for a device based on device name.
     *
     * @param deviceName the device name (for example Kawai K4/K4R Driver)
     * @return the class for the device (for example Kawai)
     */
    public String getManufacturerForDeviceName(final String deviceName) {
        DeviceDescriptor descriptor = getDeviceDescriptorForDeviceName(deviceName);
        return descriptor != null ? descriptor.getManufacturer() : DEFAULT_CONFIGURATION_VALUE;
    }

    /**
     * Create an instance of a device driver.
     *
     * @param className the class name of the device driver
     * @param prefs the preferences
     * @return an instance of the device driver, or null if one cannot be instantiated
     */
    public Device createDevice(final String className, final Preferences prefs) {
        if (className.isEmpty()) {
            return null;
        }

        if (className.charAt(0) == XML_FILE_SEPARATOR) {
            return XMLDeviceFactory.createDevice(className.replace(XML_FILE_SEPARATOR, File.separatorChar).substring(1), prefs);
        }

        try {
            Class c = Class.forName(className);
            Class[] args = { Class.forName("java.util.prefs.Preferences") };
            Constructor con = c.getConstructor(args);
            Device device = (Device) con.newInstance(new Object[] { prefs });
            return device;
        } catch (Exception exception) {
            ErrorMsg.reportError("Device Create Failure",
                "Failed to create device of class '" + className + "'",
                exception);
            return null;
        }
    }

	/**
     * Print all available device descriptors to the console.
     */
    public void printAll() {
        for (DeviceDescriptor descriptor : descriptors) {
            System.out.println(descriptor + "\n");
        }
    }

    /**
     * Construct an instance of the device configuration.
     */
    private DevicesConfig() {
        readDevicesFromPropertiesFile();
        readDevicesFromXMLFile();
	}

    /**
     * Read the available devices from a properties file.
     */
    private void readDevicesFromPropertiesFile() {
        InputStream in = getClass().getResourceAsStream("/" + Constants.DEV_CONFIG_FILE_NAME);

        if (in == null) {
            ErrorMsg.reportError("Configuration Error",
                "Device configuration file " + Constants.DEV_CONFIG_FILE_NAME + " not found.");
            return;
        }

        Properties props = new Properties();

        try {
            props.load(in);
        } catch (Exception exception) {
            ErrorMsg.reportError("Configuration Error",
                "Failed to read configuration file " + Constants.DEV_CONFIG_FILE_NAME + ".",
                exception);
            return;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                // do nothing
            }
        }

        for (String name : props.stringPropertyNames()) {
            if (name.startsWith(Constants.DEV_CONFIG_DEVICE_NAME_PREFIX)) {
                // Process a device
                String shortName    = name.substring(Constants.DEV_CONFIG_DEVICE_NAME_PREFIX.length());
                String deviceName   = props.getProperty(name);
                String deviceClass  = props.getProperty(Constants.DEV_CONFIG_DEVICE_CLASS_PREFIX + shortName);
                String idString     = props.getProperty(Constants.DEV_CONFIG_ID_STRING_PREFIX + shortName);
                String manufacturer = props.getProperty(Constants.DEV_CONFIG_MANUFACTURER_PREFIX + shortName);
                // Since Devices don't have types yet, just use the first letter of the manufacturer
                // so that we can test it.
                String type = manufacturer.substring(0, 1);

                if (deviceClass != null && idString != null) {
                    addDevice(deviceName, shortName, deviceClass, idString, manufacturer, type);
                } else {
                    ErrorMsg.reportError("Configuration Error",
                        "Invalid device configuration for " + shortName + ".");
                }
            }
        }
    }

    /**
     * Read the available devices from XML files.
     */
    private void readDevicesFromXMLFile() {
        String[][] xmldevices = XMLDeviceFactory.getDeviceNames();
        if (xmldevices != null) {
            for (int i = 0; i < xmldevices.length; ++i) {
                String deviceName = xmldevices[i][0];
                String deviceClass = XML_FILE_SEPARATOR + xmldevices[i][2];
                deviceClass = deviceClass.replace(File.separatorChar, XML_FILE_SEPARATOR);
                String shortName = getShortNameForClassName(deviceClass);
                addDevice(deviceName, shortName, deviceClass,
                    xmldevices[i][1], // IDString
                    xmldevices[i][3], // Manufacturer
                    xmldevices[i][3].substring(0,1) // Type
                );
            }
        }
    }

    /**
     * Add a device.
     *
     * @param deviceName the name of the device
     * @param shortName the short name of the device
     * @param deviceClass the class of the device
     * @param deviceId the id of the device
     */
    private void addDevice(String deviceName, String shortName, String deviceClass, String deviceId, String manufacturer, String type) {
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

    private DeviceDescriptor getDeviceDescriptorForDeviceName(final String deviceName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getDeviceName().equals(deviceName)) {
                return descriptor;
            }
        }
        return null;
    }

    private DeviceDescriptor getDeviceDescriptorForIDString(final String deviceId) {
        for (DeviceDescriptor descriptor : descriptors) {
            if(descriptor.getDeviceId().equals(deviceId)) {
                return descriptor;
            }
        }
        return null;
    }

    private DeviceDescriptor getDeviceDescriptorForShortName(final String shortName) {
        for (DeviceDescriptor descriptor : descriptors) {
            if (descriptor.getShortName().equals(shortName)) {
                return descriptor;
            }
        }
        return null;
    }
}
