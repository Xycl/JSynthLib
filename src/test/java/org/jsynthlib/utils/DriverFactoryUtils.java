/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.xmlbeans.XmlException;
import org.easymock.internal.MockBuilder;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.device.model.impl.DriverBeanUtil;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

/**
 * @author Pascal Collberg
 */
public final class DriverFactoryUtils {

    private DriverFactoryUtils() {
    }

    public static <T extends XMLSingleDriver> T newSingleDriver(
            Class<T> driverClass, Method... mockedMethods)
                    throws InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException,
                    NoSuchMethodException, SecurityException, XmlException, IOException {
        InputStream singleDriverAsStream =
                driverClass.getResourceAsStream(driverClass.getSimpleName()
                        + ".xml");
        XmlSingleDriverDefinitionDocument document =
                XmlSingleDriverDefinitionDocument.Factory
                .parse(singleDriverAsStream);
        XmlSingleDriverDefinition singleDriverDefinition =
                document.getXmlSingleDriverDefinition();

        MockBuilder<T> builder = new MockBuilder<T>(driverClass);
        for (Method method : mockedMethods) {
            builder.addMockedMethod(method);
        }
        builder.withConstructor(XmlSingleDriverDefinition.class);
        builder.withArgs(singleDriverDefinition);
        T instance = builder.createMock();
        DriverBeanUtil.copyXmlProperties(instance, singleDriverDefinition);
        return instance;
    }

    public static <T extends XMLBankDriver> T newBankDriver(
            Class<T> driverClass, Method... mockedMethods)
                    throws InstantiationException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException,
                    NoSuchMethodException, SecurityException, XmlException, IOException {
        InputStream singleDriverAsStream =
                driverClass.getResourceAsStream(driverClass.getSimpleName()
                        + ".xml");
        XmlBankDriverDefinitionDocument document =
                XmlBankDriverDefinitionDocument.Factory
                .parse(singleDriverAsStream);
        XmlBankDriverDefinition bankDriverDefinition =
                document.getXmlBankDriverDefinition();

        MockBuilder<T> builder = new MockBuilder<T>(driverClass);
        for (Method method : mockedMethods) {
            builder.addMockedMethod(method);
        }
        builder.withConstructor(XmlBankDriverDefinition.class);
        builder.withArgs(bankDriverDefinition);
        T instance = builder.createMock();

        DriverBeanUtil.copyXmlProperties(instance, bankDriverDefinition);
        return instance;
    }
}
