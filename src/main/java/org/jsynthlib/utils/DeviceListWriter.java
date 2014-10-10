package org.jsynthlib.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jsynthlib.core.Constants;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;

/**
 * This class provides a device list writer.
 * @author Gerrit Gehnen
 */
public class DeviceListWriter {

    private static final Logger LOG = Logger.getLogger(DeviceListWriter.class);

    /**
     * Run the device list writer.
     * @param args
     *            the command line arguments
     */
    public static void main(final String[] args) {
        try {
            if (args.length != 1) {
                LOG.info("Syntax: DeviceListWriter <output directory>");
                System.exit(-1);
            }
            File outdir = new File(args[0]);
            DeviceListWriter writer = new DeviceListWriter();
            DeviceParser deviceParser = new DeviceParser();
            List<Device> devices = deviceParser.getAllDevices();
            List<DeviceDescriptor> descriptorList = writer.getDescriptorList(devices);
            writer.writeProps(
                    new File(outdir, Constants.DEV_CONFIG_FILE_NAME), descriptorList);
        } catch (Exception exception) {
            LOG.info("Failed to write device list");
            LOG.warn(exception.getMessage(), exception);
            System.exit(1);
        }

        System.exit(0);
    }

    /**
     * Build and append {@link DeviceDescriptor}s from a set of Device classes.
     * @param devices
     *            The device list
     */
    List<DeviceDescriptor> getDescriptorList(List<Device> devices) {
        List<DeviceDescriptor> devs = new ArrayList<DeviceDescriptor>();
        for (Device device : devices) {
            String shortname = device.getClass().getSimpleName();
            shortname = shortname.substring(0, shortname.lastIndexOf("Device"));

            DeviceDescriptor d = new DeviceDescriptor();
            d.setDeviceClass(device.getClass().getName());
            d.setDeviceName(device.getManufacturerName() + " " + device.getModelName()
                    + " Driver");
            d.setDeviceId(device.getInquiryID());
            d.setManufacturer(device.getManufacturerName());
            d.setShortName(shortname);
            devs.add(d);
        }
        Collections.sort(devs);
        return devs;
    }

    protected void writeProps(final File outFile,
            final List<DeviceDescriptor> devs) throws FileNotFoundException {
        Properties props = new Properties();

        for (DeviceDescriptor d : devs) {
            setProperty(props, d);
        }

        // save into synthdrivers.properties
        FileOutputStream out = new FileOutputStream(outFile);
        try {
            LOG.info("Saving synthdrivers to " + outFile.getAbsolutePath());
            props.store(out, "Generated devicesfile");
            out.close();
            LOG.info("done!");
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    private static void setProperty(final Properties props,
            final DeviceDescriptor dev) {
        String shortname = dev.getShortName();

        props.setProperty(
                Constants.DEV_CONFIG_DEVICE_CLASS_PREFIX
                        + shortname, dev.getDeviceClass());
        props.setProperty(
                Constants.DEV_CONFIG_MANUFACTURER_PREFIX
                        + shortname, dev.getManufacturer());
        props.setProperty(
                Constants.DEV_CONFIG_ID_STRING_PREFIX
                        + shortname, dev.getDeviceId());
        // saving only model name is better.
        props.setProperty(
                Constants.DEV_CONFIG_DEVICE_NAME_PREFIX
                        + shortname, dev.getDeviceName());
    }
}
