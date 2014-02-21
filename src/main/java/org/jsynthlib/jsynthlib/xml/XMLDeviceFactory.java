package org.jsynthlib.jsynthlib.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.jsynthlib.jsynthlib.Dummy;
import org.jsynthlib.utils.ResourceURLFilter;
import org.jsynthlib.utils.Resources;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import core.Device;
import core.DeviceDescriptor;
import core.ErrorMsg;

/**
 * @author ribrdb
 */
public abstract class XMLDeviceFactory {
    private static final Logger LOG = Logger.getLogger(XMLDeviceFactory.class);

    public static List<DeviceDescriptor> getDeviceDescriptors() {
        List<DeviceDescriptor> devices = new ArrayList<DeviceDescriptor>();

//        if (descriptor.charAt(0) == XML_FILE_SEPARATOR) {
//            return XMLDeviceFactory.createDevice(
//                    descriptor.replace(XML_FILE_SEPARATOR, File.separatorChar)
//                            .substring(1), prefs);
//        }
        
//        String[][] xmldevices = XMLDeviceFactory.getDeviceDescriptors();
//        if (xmldevices != null) {
//            for (int i = 0; i < xmldevices.length; ++i) {
//                String deviceName = xmldevices[i][0];
//                String deviceClass = XML_FILE_SEPARATOR + xmldevices[i][2];
//                deviceClass =
//                        deviceClass.replace(File.separatorChar,
//                                XML_FILE_SEPARATOR);
//                String shortName = getShortNameForClassName(deviceClass);
//                addDevice(deviceName, shortName, deviceClass, xmldevices[i][1], // IDString
//                        xmldevices[i][3], // Manufacturer
//                        xmldevices[i][3].substring(0, 1) // Type
//                );
//            }
//        }
        
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            DeviceSearchHandler handler = new DeviceSearchHandler();
            
            Set<URL> xmlfiles =
                    Resources.getResourceURLs(Dummy.class,
                            new ResourceURLFilter() {

                                @Override
                                public boolean accept(URL resourceUrl) {
                                    String path = resourceUrl.getPath();
                                    return path
                                            .contains("org/jsynthlib/jsynthlib/synthdrivers")
                                            && path.endsWith(".xml");
                                }
                            });
            for (URL url : xmlfiles) {
                InputStream resourceAsStream = url.openStream();
                handler.reset();
                try {
                    parser.parse(resourceAsStream, handler);
                    XMLDeviceDescriptor descriptor = new XMLDeviceDescriptor(url, handler);
                    devices.add(descriptor);
                } catch (NotDeviceException e) {
                    LOG.warn(e.getMessage(), e);
                } catch (FinishedParsingException e) {
                    XMLDeviceDescriptor descriptor = new XMLDeviceDescriptor(url, handler);
                    devices.add(descriptor);
                } catch (SAXException e) {
                    LOG.warn(e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        return devices;
    }

    private static class XMLDeviceDescriptor extends DeviceDescriptor {
        private URL url;
        
        public XMLDeviceDescriptor(URL url, DeviceSearchHandler handler) {
            setDeviceName(handler.getName());
            setDeviceId(handler.getId());
            setManufacturer(handler.getManufacturer());
            
            setDeviceClass("org.jsynthlib." + handler.getName() + "Device");
            setShortName(handler.getName());
            this.url = url;
        }

        @Override
        public Device newDevice(Preferences prefs)
                throws ClassNotFoundException, NoSuchMethodException,
                InstantiationException, IllegalAccessException,
                InvocationTargetException {
            SAXParser parser;
            try {
                parser = SAXParserFactory.newInstance().newSAXParser();
            } catch (ParserConfigurationException e) {
                ErrorMsg.reportError("Can't load driver",
                        "Unable to create XML parser", e);
                LOG.warn(e.getMessage(), e);
                return null;
            } catch (SAXException e) {
                ErrorMsg.reportError("Can't load driver",
                        "Unable to create XML parser", e);
                LOG.warn(e.getMessage(), e);
                return null;
            }
            DeviceLoadHandler handler = new DeviceLoadHandler();
            File devicefile = new File(url.getPath());
            handler.setBasePath(devicefile.getParentFile());
            try {
                parser.parse(devicefile, handler);
                XMLDevice d = handler.getDevice();
                d.setPreferences(prefs);
                return d;
            } catch (SAXParseException ex) {
                Exception x = ex;
                if (ex.getException() != null)
                    x = ex.getException();
                ErrorMsg.reportError("Can't load device", "Error parsing line "
                        + ex.getLineNumber() + " of device\n" + ex.getSystemId()
                        + "\n\n" + ex.getMessage(), x);
                LOG.warn(ex.getMessage(), ex);
            } catch (NotDeviceException ex) {
                ErrorMsg.reportError("Can't load device", "Can't load " + url.getPath()
                        + ". It is not a device.");
                LOG.warn(ex.getMessage(), ex);
            } catch (SAXException ex) {
                Exception x = ex;
                if (ex.getException() != null)
                    x = ex.getException();
                ErrorMsg.reportError("Can't load device", "Error parsing "
                        + url.getPath() + ".\n" + ex.getMessage(), x);
                LOG.warn(ex.getMessage(), ex);
            } catch (IOException e) {
                ErrorMsg.reportError("Can't load device", "Error reading "
                        + url.getPath(), e);
                LOG.warn(e.getMessage(), e);
            }

            return null;
        }
        
        
    }
}