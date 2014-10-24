package org.jsynthlib.synthdrivers.RolandTD6;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PadInfoTest {

    private final Logger log = Logger.getLogger(getClass());

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testClone() {
        PadInfo p0 = new PadInfo("P0", 0, true, true, true);
        PadInfo p1 = (PadInfo) p0.clone();
        PadInfo p2 = new PadInfo("P2", 1, true, true, true);
        p2 = (PadInfo) p0.clone();
        assertEquals(p0.name, p1.name);
        assertEquals(p0.offset, p1.offset);
        assertEquals(p0.name, p2.name);
        assertEquals(p0.offset, p2.offset);
    }
}
