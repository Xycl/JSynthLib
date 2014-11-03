package org.jsynthlib.device.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.DeviceFactory;
import org.jsynthlib.device.model.DeviceList;
import org.jsynthlib.device.model.DriverFactory;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class DeviceFactoryImpl implements DeviceFactory {

    private final transient Logger log = Logger.getLogger(getClass());

    private final DeviceList deviceList;

    private final HandlerBindingMap bindingMap;

    @Inject
    public DeviceFactoryImpl(DeviceList deviceList, HandlerBindingMap bindingMap) {
        this.deviceList = deviceList;
        this.bindingMap = bindingMap;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Device newDevice(DeviceDescriptor descriptor, Preferences preferences)
            throws DeviceException {
        Device device = null;
        try {
            String deviceClass = descriptor.getDeviceClass();
            InputStream xmlStream =
                    getClass().getClassLoader().getResourceAsStream(
                            deviceClass.replace('.', '/') + ".xml");
            if (xmlStream == null) {
                // Oldstyle device
                Class<Device> c = (Class<Device>) Class.forName(deviceClass);
                device = JSynthLibInjector.getInstance(c);
            } else {
                // XML device
                XmlOptions xmlOptions = new XmlOptions();
                xmlOptions.setLoadStripWhitespace();
                XmlDeviceDefinitionDocument deviceSpecDocument =
                        XmlDeviceDefinitionDocument.Factory.parse(xmlStream,
                                xmlOptions);
                XmlDeviceDefinition deviceDefinition =
                        deviceSpecDocument.getXmlDeviceDefinition();
                String devicePath = bindingMap.addDevice(deviceDefinition);
                Map<String, HandlerDefinitionBase> deviceBindingsMap =
                        bindingMap.getDeviceBindings(devicePath);
                DeviceModule deviceModule = new DeviceModule(deviceBindingsMap);
                Injector driverInjector =
                        JSynthLibInjector.getInjector().createChildInjector(
                                deviceModule);
                DriverFactory driverFactory =
                        driverInjector.getInstance(DriverFactory.class);

                Class<? extends XMLDevice> c = null;
                try {
                    c = (Class<XMLDevice>) Class.forName(deviceClass);
                } catch (ClassNotFoundException e) {
                    c = XMLDevice.class;
                }
                Class<?>[] args = {
                    XmlDeviceDefinition.class };
                Constructor<? extends XMLDevice> con = c.getConstructor(args);
                device = con.newInstance(new Object[] {
                    deviceDefinition });
                JSynthLibInjector.getInjector().injectMembers(device);
                XmlDriverReferences drivers = deviceDefinition.getDrivers();

                XmlDriverReference[] driverArray =
                        drivers.getXmlDriverReferenceArray();
                for (XmlDriverReference xmlDriver : driverArray) {
                    int driverType = xmlDriver.getDriverType().intValue();
                    IDriver driver = null;
                    switch (driverType) {
                    case DriverType.INT_BANK:
                        driver = driverFactory.newBankDriver(xmlDriver);
                        break;
                    case DriverType.INT_CONVERTER:
                        break;
                    case DriverType.INT_PATCH:
                    default:
                        driver = driverFactory.newSingleDriver(xmlDriver);
                        break;
                    }
                    if (driver == null) {
                        log.warn("Driver was null! Skipping...");
                    } else {
                        driverInjector.injectMembers(driver);
                        device.addDriver(driver);
                    }
                }
            }

            if (!deviceList.contains(device)) {
                device.setup(preferences);
                deviceList.add(device); // always returns true
                int driverCount = device.driverCount();
                for (int i = 0; i < driverCount; i++) {
                    IDriver driver = device.getDriver(i);
                    DriverBeanUtil.copyPreferences(driver);
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException
                | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | XmlException | IOException e) {
            throw new DeviceException(e);
        }

        if (device == null) {
            throw new DeviceException("Failed to create device of class '"
                    + descriptor + "'");
        }
        return device;
    }

}
