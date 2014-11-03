package org.jsynthlib.device.model.impl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.device.model.DriverFactory;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public class DriverFactoryImpl implements DriverFactory {

    private static final Logger LOG = Logger
            .getLogger(DriverFactoryImpl.class);

    @Override
    public IBankDriver newBankDriver(XmlDriverReference driverReference) {
        DriverCreator<XmlBankDriverDefinition, IBankDriver> driverCreator =
                new DriverCreator<XmlBankDriverDefinition, IBankDriver>(
                        XmlBankDriverDefinition.class, XMLBankDriver.class) {

                    @Override
                    XmlBankDriverDefinition parseDocument(InputStream xmlStream)
                            throws XmlException, IOException {
                        XmlBankDriverDefinitionDocument document =
                                XmlBankDriverDefinitionDocument.Factory
                                        .parse(xmlStream);
                        return document.getXmlBankDriverDefinition();
                    }
                };
        return driverCreator.createDriver(driverReference);
    }

    @Override
    public IPatchDriver newSingleDriver(XmlDriverReference driverReference) {
        DriverCreator<XmlSingleDriverDefinition, IPatchDriver> driverCreator =
                new DriverCreator<XmlSingleDriverDefinition, IPatchDriver>(
                        XmlSingleDriverDefinition.class, XMLSingleDriver.class) {

                    @Override
                    XmlSingleDriverDefinition parseDocument(
                            InputStream xmlStream) throws XmlException,
                            IOException {
                        XmlSingleDriverDefinitionDocument document =
                                XmlSingleDriverDefinitionDocument.Factory
                                        .parse(xmlStream);
                        return document.getXmlSingleDriverDefinition();
                    }
                };
        return driverCreator.createDriver(driverReference);
    }

    abstract static class DriverCreator<T extends XmlDriverDefinition, U extends IDriver> {
        private Class<? extends U> defaultClass;
        private Class<? extends T> definitionClass;

        DriverCreator(Class<? extends T> definitionClass,
                Class<? extends U> defaultClass) {
            this.defaultClass = defaultClass;
            this.definitionClass = definitionClass;
        }

        @SuppressWarnings("unchecked")
        U createDriver(XmlDriverReference driverReference) {
            String driverClass = driverReference.getDriverClass();
            InputStream xmlStream =
                    getClass().getClassLoader().getResourceAsStream(
                            driverClass.replace('.', '/') + ".xml");

            try {
                if (xmlStream == null) {
                    // Oldstyle device
                    Class<U> c = (Class<U>) Class.forName(driverClass);
                    return JSynthLibInjector.getInstance(c);
                } else {
                    // XML driver
                    XmlDriverDefinition driverDefinition = null;

                    Class<? extends U> c = null;
                    try {
                        c = (Class<? extends U>) Class.forName(driverClass);
                    } catch (ClassNotFoundException e) {
                        // Use default class
                        c = defaultClass;
                    }

                    driverDefinition = parseDocument(xmlStream);
                    Class<?>[] args = {
                        definitionClass };
                    Constructor<? extends U> con = c.getConstructor(args);

                    U driver = con.newInstance(new Object[] {
                        driverDefinition });
                    DriverBeanUtil.copyXmlProperties(driver, driverDefinition);
                    return driver;
                }
            } catch (ClassNotFoundException | NoSuchMethodException
                    | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | XmlException | IOException e) {
                LOG.warn(e.getMessage(), e);
                return null;
            }
        }

        abstract T parseDocument(InputStream xmlStream) throws XmlException,
                IOException;
    }
}
