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
package org.jsynthlib.synthdrivers.RolandD50;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.easymock.internal.MockBuilder;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.DriverFactoryUtils;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pascal Collberg
 */
public class D50BankDriverTest {

    private D50BankDriver tested;
    private IDriver singleDriverMock;
    private D50SingleDriver d50SingleDriver;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        XmlBankDriverDefinition xmlDriverSpec =
                XmlBankDriverDefinition.Factory.newInstance();
        MockBuilder<D50BankDriver> mockBuilder =
                new MockBuilder<D50BankDriver>(D50BankDriver.class);
        mockBuilder.withConstructor(XmlBankDriverDefinition.class);
        mockBuilder.withArgs(xmlDriverSpec);
        mockBuilder.addMockedMethod("getSingleDriver");
        tested = mockBuilder.createMock();

        singleDriverMock = createMock(IDriver.class);

        d50SingleDriver =
                DriverFactoryUtils.newSingleDriver(D50SingleDriver.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetPatch() throws Exception {

        Patch singlePatch = new Patch();
        singlePatch.sysex = new byte[458];
        expect(tested.getSingleDriver()).andReturn(singleDriverMock);
        expect(singleDriverMock.createPatch()).andReturn(singlePatch);

        replay(tested);
        replay(singleDriverMock);
        InputStream resource =
                getClass().getResourceAsStream("RolandD50_bank.syx");
        byte[] byteArray = IOUtils.toByteArray(resource);
        Patch bank = new Patch();
        bank.sysex = byteArray;
        Patch patch = tested.getPatch(bank, 0);
        patch.setDriver(d50SingleDriver);
        String name = patch.getName();
        assertEquals("Fantasia          ", name);

        singlePatch.sysex = new byte[458];
        patch = tested.getPatch(bank, 3);
        patch.setDriver(d50SingleDriver);
        assertEquals("Arco Strings      ", patch.getName());

        singlePatch.sysex = new byte[458];
        patch = tested.getPatch(bank, 60);
        patch.setDriver(d50SingleDriver);
        assertEquals("Bones             ", patch.getName());

        verify(tested);
        verify(singleDriverMock);
    }

}
