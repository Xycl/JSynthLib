package core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.prefs.Preferences;

/**
 * This class encapsulates the details of a MIDI enabled device.
 */
public class DeviceDescriptor implements Comparable<DeviceDescriptor> {
    private String manufacturer;
    private String type;
    private String shortName;
    private String deviceName;
    private String deviceClass;
    private String deviceId;

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String m) {
        manufacturer = m;
    }

    public String getType() {
        return type;
    }

    public void setType(final String t) {
        type = t;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(final String sn) {
        shortName = sn;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(final String dn) {
        deviceName = dn;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(final String dc) {
        deviceClass = dc;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(final String di) {
        deviceId = di;
    }

    @Override
    public String toString() {
        return manufacturer + " " + deviceName + " " + deviceId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DeviceDescriptor)) {
            return false;
        }
        DeviceDescriptor dd = (DeviceDescriptor) obj;
        return (manufacturer == null ? dd.manufacturer == null : manufacturer
                .equals(dd.manufacturer))
                && (deviceName == null ? dd.deviceName == null : deviceName
                        .equals(dd.deviceName))
                && (deviceId == null ? dd.deviceId == null : deviceId
                        .equals(dd.deviceId));
    }

    @Override
    public int hashCode() {
        int hashCode = 13;
        hashCode =
                31 * hashCode
                        + (manufacturer != null ? manufacturer.hashCode() : 0);
        hashCode =
                31 * hashCode
                        + (deviceName != null ? deviceName.hashCode() : 0);
        hashCode = 31 * hashCode + (deviceId != null ? deviceId.hashCode() : 0);
        return hashCode;
    }

    @Override
    public int compareTo(DeviceDescriptor dd) {
        int n =
                manufacturer == null ? dd.manufacturer == null ? 0 : 1
                        : manufacturer.compareTo(dd.manufacturer);

        if (n != 0) {
            return n;
        }

        n =
                deviceName == null ? dd.deviceName == null ? 0 : 1 : deviceName
                        .compareTo(dd.deviceName);

        if (n != 0) {
            return n;
        }

        return deviceId == null ? dd.deviceId == null ? 0 : 1 : deviceId
                .compareTo(dd.deviceId);
    }

    public Device newDevice(final Preferences prefs)
            throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException,
            InvocationTargetException {
        Class<?> c = Class.forName(deviceClass);
        Class<?>[] args = {
            Preferences.class };
        Constructor<?> con = c.getConstructor(args);
        Device device = (Device) con.newInstance(new Object[] {
            prefs });
        return device;
    }
}
