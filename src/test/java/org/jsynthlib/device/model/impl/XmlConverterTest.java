package org.jsynthlib.device.model.impl;

import static org.junit.Assert.*;

import org.jsynthlib.utils.SysexUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlConverterTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testStringToSysex() {
        String sysex = "F0430*092000";
        byte[] result = SysexUtils.stringToSysex(sysex);
        assertNotNull(result);
        assertEquals(6, result.length);
        assertEquals(-16, result[0]);
        assertEquals(0, result[2]);
        assertEquals(32, result[4]);
        assertEquals(0, result[5]);
    }

}
