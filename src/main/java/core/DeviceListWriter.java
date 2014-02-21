package core;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

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
            if (args.length != 2) {
                LOG.info("Syntax: DeviceListWriter "
                        + "<source directory> <output directory>");
                System.exit(-1);
            }
            File sourceDir = new File(args[0]);
            File outdir = new File(args[1]);
            DeviceListWriter writer = new DeviceListWriter();
            List<DeviceDescriptor> devs =
                    writer.addClasses(sourceDir, "synthdrivers");
            writer.writeProps(new File(outdir, Constants.DEV_CONFIG_FILE_NAME),
                    devs);
        } catch (IOException exception) {
            LOG.info("Failed to write device list");
            LOG.warn(exception.getMessage(), exception);
            System.exit(1);
        }

        System.exit(0);
    }

    public void run() throws IOException {
        List<DeviceDescriptor> devs = addClasses(new File("."), "synthdrivers");
        writeProps(new File(".", Constants.DEV_CONFIG_FILE_NAME), devs);
    }

    /**
     * Build and append {@link DeviceDescriptor}s from a set of Java classes.
     * @param codeDir
     *            xxx
     * @param pakkage
     *            xxx
     */
    private List<DeviceDescriptor> addClasses(final File codeDir, String pakkage) {
        // Clean package references.
        String pakkDir = pakkage.replace('.', File.separatorChar);
        pakkage = pakkDir.replace('/', '.');
        pakkage = pakkage.replace('\\', '.');

        // select only directories
        File dir = new File(codeDir, pakkDir);
        File[] synthDirs = dir.listFiles(new SynthDirsFilter());

        LOG.info("In dir " + codeDir + ":");

        List<DeviceDescriptor> devs = new ArrayList<DeviceDescriptor>();

        // for all subdirectories = synthesizer models
        for (int i = 0; i < synthDirs.length; i++) {
            // select *Device.java
            File actSynthDir = synthDirs[i];
            String[] synthDevices = actSynthDir.list(new SynthFileFilter());
            try {
                MyClassLoader loader = new MyClassLoader(actSynthDir.getPath());
                // for each Device class
                for (int j = 0; j < synthDevices.length; j++) {
                    // get Device class name by removing the ".java" from the
                    // list of files
                    String devName =
                            synthDevices[j].substring(0,
                                    synthDevices[j].indexOf('.'));
                    try {
                        LOG.info("  Checking " + actSynthDir.getPath());

                        Class<?> deviceclass = loader.loadClass(devName, true);
                        Device dev = (Device) deviceclass.newInstance();
                        devs.add(describe(dev));

                        LOG.info("    Found " + deviceclass.getName());
                    } catch (Exception e) {
                        LOG.warn(
                                "Exception with " + devName + ": "
                                        + e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                LOG.warn(e.getMessage(), e);
            }
        }

        Collections.sort(devs);

        return devs;
    }

    private void writeProps(final File outFile,
            final List<DeviceDescriptor> devs) throws FileNotFoundException {
        Properties props = new Properties();

        for (DeviceDescriptor d : devs) {
            setProperty(props, d);
        }

        // save into synthdrivers.properties
        FileOutputStream out = new FileOutputStream(outFile);
        try {
            props.store(out, "Generated devicesfile");
            out.close();
            LOG.info("done!");
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    /**
     * Build a {@link DeviceDescriptor} from a {@link Device}.
     * @param dev
     * @return
     */
    private static DeviceDescriptor describe(final Device dev) {
        

        DeviceDescriptor d = new DeviceDescriptor();
        d.setDeviceClass(dev.getClass().getName());
        d.setDeviceName(dev.getManufacturerName() + " " + dev.getModelName()
                + " Driver");
        d.setDeviceId(dev.getInquiryID());
        d.setManufacturer(dev.getManufacturerName());

        return d;
    }

    private static void setProperty(final Properties props,
            final DeviceDescriptor dev) {
        String shortname = dev.getShortName();

        props.setProperty(Constants.DEV_CONFIG_DEVICE_CLASS_PREFIX + shortname,
                dev.getDeviceClass());
        props.setProperty(Constants.DEV_CONFIG_MANUFACTURER_PREFIX + shortname,
                dev.getManufacturer());
        props.setProperty(Constants.DEV_CONFIG_ID_STRING_PREFIX + shortname,
                dev.getDeviceId());
        // saving only model name is better.
        props.setProperty(Constants.DEV_CONFIG_DEVICE_NAME_PREFIX + shortname,
                dev.getDeviceName());
    }

    /** FilenameFilter which select <code>*Device.class</code>. */
    private static class SynthFileFilter implements FilenameFilter {
        @Override
        public boolean accept(final File dir, final String name) {
            return ((name.indexOf('$') == -1) && (name.endsWith("Device.java") || name
                    .endsWith("Device.class")));
        }
    }

    /** FileFilter which select a directory. */
    private static class SynthDirsFilter implements FileFilter {
        @Override
        public boolean accept(final File dir) {
            // keep only dirs that are not hidden
            return dir.isDirectory() && !dir.isHidden()
                    && !dir.getName().startsWith(".");
        }
    }

    /**
     * This class loader uses an alternate directory for loading classes. When a
     * class is resolved, its class loader is expected to be able to load any
     * additional classes, but this loader doesn't want to have to figure out
     * where to find java.lang.Object, for instance, so it uses Class.forName to
     * locate classes that the system already knows about.
     * <p>
     * Created on 12. September 1999, 00:30
     */
    private static class MyClassLoader extends ClassLoader {
        private String classDir; // root dir to load classes from
        private Map<String, Class<?>> loadedClasses; // Classes that have been
                                                     // loaded

        public MyClassLoader(final String cd) {
            classDir = cd;
            loadedClasses = new HashMap<String, Class<?>>();
        }

        @Override
        public synchronized Class<?> loadClass(final String className,
                final boolean resolve) throws ClassNotFoundException {
            Class<?> newClass = findLoadedClass(className);

            if (newClass != null) {
                return newClass;
            }

            // If the class was in the loadedClasses table, we don't
            // have to load it again, but we better resolve it, just
            // in case.
            newClass = loadedClasses.get(className);

            if (newClass != null) {
                if (resolve) { // Should we resolve?
                    resolveClass(newClass);
                }
                return newClass;
            }

            try {
                // Read in the class file
                byte[] classData = getClassData(className);
                // Define the new class
                newClass = defineClass(null, classData, 0, classData.length);
            } catch (IOException readError) {
                // Before we throw an exception, see if the system
                // already knows about this class
                try {
                    newClass = findSystemClass(className);
                    return newClass;
                } catch (Exception any) {
                    throw new ClassNotFoundException(className);
                }
            }

            // Store the class in the table of loaded classes
            loadedClasses.put(className, newClass);

            // If we are supposed to resolve this class, do it
            if (resolve) {
                resolveClass(newClass);
            }

            return newClass;
        }

        // This version of loadClass uses classDir as the root directory
        // for where to look for classes, it then opens up a read stream
        // and reads in the class file as-is.
        protected byte[] getClassData(final String className)
                throws IOException {
            // Rather than opening up a FileInputStream directly, we create
            // a File instance first so we can use the length method to
            // determine how big a buffer to allocate for the class

            File classFile = new File(classDir, className + ".class");

            byte[] classData = new byte[(int) classFile.length()];

            // Now open up the input stream
            FileInputStream inFile = new FileInputStream(classFile);

            // Read in the class
            inFile.read(classData);

            inFile.close();

            return classData;
        }
    }
}
