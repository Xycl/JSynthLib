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
package org.jsynthlib.device.model;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertArrayEquals;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;

import org.easymock.Capture;
import org.jsynthlib.patch.model.PatchFactory;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.YamahaDX7VoiceBankDriver;
import org.jsynthlib.synthdrivers.YamahaDX7.YamahaDX7VoiceSingleDriver;
import org.jsynthlib.utils.DriverFactoryUtils;
import org.jsynthlib.utils.SysexUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pascal Collberg
 */
public class YamahaDX7VoiceBankDriverTest {

    private static final String PUT_PATCH_PROPERTIES =
            "YamahaDX7VoiceBankDriver_PutPatch.properties";
    private static final String GET_PATCH_PROPERTIES =
            "YamahaDX7VoiceBankDriver_GetPatch.properties";
    private YamahaDX7VoiceBankDriver dx7VoiceBankDriver;
    private YamahaDX7VoiceSingleDriver dx7VoiceSingleDriver;
    private PatchFactory patchFactoryMock;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        Method method =
                YamahaDX7VoiceBankDriver.class.getSuperclass().getSuperclass()
                .getSuperclass().getSuperclass()
                .getDeclaredMethod("getPatchFactory", null);
        dx7VoiceBankDriver =
                DriverFactoryUtils.newBankDriver(
                        YamahaDX7VoiceBankDriver.class, method);
        dx7VoiceSingleDriver =
                DriverFactoryUtils
                .newSingleDriver(YamahaDX7VoiceSingleDriver.class);
        patchFactoryMock = createMock(PatchFactory.class);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testPutPatch() throws Exception {
        BankPatch bankPatch = new BankPatch();
        bankPatch.sysex = new byte[dx7VoiceBankDriver.getPatchSize()];
        Patch patch = new Patch() {

            private static final long serialVersionUID = 1L;

            @Override
            public String getPatchHeader() {
                return dx7VoiceSingleDriver.getSysexID();
            }

        };
        Properties properties = new Properties();
        InputStream stream =
                getClass().getResourceAsStream(PUT_PATCH_PROPERTIES);
        properties.load(stream);

        patch.sysex = new byte[dx7VoiceSingleDriver.getPatchSize()];
        for (int i = 0; i < 128; i++) {
            Arrays.fill(patch.sysex, (byte) i);
            dx7VoiceBankDriver.putPatch(bankPatch, patch, 0);
            String property = properties.getProperty(Integer.toString(i));
            byte[] expected = SysexUtils.stringToSysex(property);
            assertArrayEquals(expected, bankPatch.sysex);
            // properties.setProperty(Integer.toString(i), StringEscapeUtils
            // .escapeJava(SysexUtils.sysexToString(bankPatch.sysex)));
        }

        // properties.store(new FileOutputStream(PUT_PATCH_PROPERTIES), null);
    }

    void replayAll() {
        replay(dx7VoiceBankDriver);
        replay(patchFactoryMock);
    }

    void verifyAll() {
        verify(dx7VoiceBankDriver);
        verify(patchFactoryMock);
    }

    @Test
    public void testGetPatch() throws Exception {
        Capture<byte[]> capture = new Capture<byte[]>();
        expect(dx7VoiceBankDriver.getPatchFactory())
        .andReturn(patchFactoryMock).times(128);
        expect(
                patchFactoryMock.createNewPatch(capture(capture),
                        anyObject(Device.class))).andReturn(new Patch() {

                            private static final long serialVersionUID = 1L;

                            @Override
                            public void calculateChecksum() {
                            }
                        }).times(128);

        replayAll();

        BankPatch bankPatch = new BankPatch();
        bankPatch.sysex = new byte[dx7VoiceBankDriver.getPatchSize()];
        Properties properties = new Properties();
        InputStream stream =
                getClass().getResourceAsStream(GET_PATCH_PROPERTIES);
        properties.load(stream);

        // ArrayList<byte[]> resultList = new ArrayList<byte[]>();

        for (int i = 0; i < 128; i++) {
            Arrays.fill(bankPatch.sysex, (byte) i);
            dx7VoiceBankDriver.getPatch(bankPatch, 0);
            String property = properties.getProperty(Integer.toString(i));
            byte[] expected = SysexUtils.stringToSysex(property);
            assertArrayEquals(expected, capture.getValue());
            // resultList.add(capture.getValue());
        }

        verifyAll();

        // assertEquals(128, resultList.size());
        //
        // for (int i = 0; i < resultList.size(); i++) {
        // properties.setProperty(Integer.toString(i), StringEscapeUtils
        // .escapeJava(SysexUtils.sysexToString(resultList.get(i))));
        // }
        // properties.store(new FileOutputStream(GET_PATCH_PROPERTIES), null);

    }
}
