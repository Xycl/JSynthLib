package org.jsynthlib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

public class DeviceParser {

    private static final String ROOT_PACKAGE = "org/jsynthlib/synthdrivers";
    private final transient Logger log = Logger.getLogger(getClass());
    private final transient Pattern classPattern = Pattern
            .compile("(org\\/jsynthlib\\/synthdrivers.+)\\.class");
    private final transient Pattern xmlPattern = Pattern
            .compile("(org\\/jsynthlib\\/synthdrivers.+)\\.xml");

    @SuppressWarnings("unchecked")
    public List<Device> getAllDevices() throws IOException, URISyntaxException,
    ClassNotFoundException, InstantiationException,
    IllegalAccessException, XmlException, NoSuchMethodException,
    InvocationTargetException {
        List<Device> devices = new ArrayList<Device>();
        ClassLoader classLoader = getClass().getClassLoader();
        Set<URL> resourceURLs = Resources.getResourceURLs();
        log.info("Found " + resourceURLs.size() + " URLs");
        for (URL url : resourceURLs) {
            String externalForm = url.toExternalForm();
            if (externalForm.contains(ROOT_PACKAGE)) {
                // log.info("  Checking " + externalForm);
                Matcher classMatcher = classPattern.matcher(externalForm);
                if (classMatcher.find()) {
                    String group = classMatcher.group(1);
                    Class<? extends Device> deviceclass =
                            (Class<? extends Device>) classLoader
                            .loadClass(group.replace('/', '.'));
                    try {
                        deviceclass.asSubclass(Device.class);
                        if (Modifier.isAbstract(deviceclass.getModifiers())) {
                            continue;
                        }
                        try {
                            deviceclass.asSubclass(XMLDevice.class);
                            log.debug("Found XMLDevice "
                                    + deviceclass.getName());
                        } catch (ClassCastException e) {
                            Device device = deviceclass.newInstance();
                            devices.add(device);
                            log.info("    Found " + deviceclass.getName());
                        }

                    } catch (ClassCastException e) {
                        // log.debug("Found other class "
                        // + deviceclass.getName());
                    }
                    continue;
                }

                Matcher xmlMatcher = xmlPattern.matcher(externalForm);
                if (xmlMatcher.find()) {
                    String deviceClassName =
                            xmlMatcher.group(1).replace('/', '.');

                    Class<? extends XMLDevice> deviceclass = null;
                    try {
                        deviceclass =
                                (Class<? extends XMLDevice>) classLoader
                                .loadClass(deviceClassName);
                    } catch (ClassNotFoundException e) {
                        deviceclass = XMLDevice.class;
                    }

                    String resourcePath =
                            deviceClassName.replace('.', '/') + ".xml";
                    InputStream stream =
                            getClass().getClassLoader().getResourceAsStream(
                                    resourcePath);
                    XmlDeviceDefinitionDocument document =
                            XmlDeviceDefinitionDocument.Factory.parse(stream);
                    XmlDeviceDefinition xmlDeviceSpec =
                            document.getXmlDeviceDefinition();
                    Constructor<? extends XMLDevice> constructor =
                            deviceclass.getConstructor(new Class<?>[] {
                                    XmlDeviceDefinition.class });
                    XMLDevice device = constructor.newInstance(xmlDeviceSpec);
                    devices.add(device);
                    log.info("    Found " + deviceclass.getName() + " name "
                            + xmlDeviceSpec.getModelName());
                }
            }
        }
        return devices;
    }
}
