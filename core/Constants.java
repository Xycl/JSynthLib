package core;

/**
 * This class provides application wide constants.
 *
 * @author Zellyn Hunter (zellyn@zellyn.com)
 */
public class Constants {
    /** The application version string. */
    public static final String VERSION = "0.21-alpha";

    /** The file containing the application configuration. */
    public static final String APP_CONFIG_FILE_NAME = "JSynthLib.properties";
    /** The header of the application configuration file. */
    public static final String APP_CONFIG_HEADER = "JSynthLib Saved Properties";

    /** The file containing the device configurations. */
    public static final String DEV_CONFIG_FILE_NAME = "synthdrivers.properties";
    /** The device name property prefix. */
    public static final String DEV_CONFIG_DEVICE_NAME_PREFIX = "deviceName.";
    /** The device class property prefix. */
    public static final String DEV_CONFIG_DEVICE_CLASS_PREFIX = "deviceClass.";
    /** The device ID string property prefix. */
    public static final String DEV_CONFIG_ID_STRING_PREFIX = "inquiryID.";
    /** The device manufacturer property string prefix. */
    public static final String DEV_CONFIG_MANUFACTURER_PREFIX = "manufacturer.";

    /** Number of faders. */
    public static final int NUM_FADERS = 33;

    private Constants() {
        // non-instantiable
    }
}
