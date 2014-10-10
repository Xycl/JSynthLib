package org.jsynthlib.synthdrivers.RolandD50;

import static org.junit.Assert.assertEquals;

import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class D50SingleDriverTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCalculateChecksum() {
        XmlPatchDriverSpec xmlDriverSpec = XmlPatchDriverSpec.Factory.newInstance();
        D50SingleDriver tested = new D50SingleDriver(xmlDriverSpec);

        byte[] sysex =
                {
                        (byte) 0xF0, 0x41, 0x00, 0x14, 0x12, 0x40, 0x00, 0x04,
                        0x64, 0x00, (byte) 0xF7, };
        tested.calculateChecksum(sysex, 5, sysex.length - 3, sysex.length - 2);

        assertEquals(0x58, sysex[sysex.length - 2]);

        byte[] sysex2 =
                {
                        (byte) 0xF0, 0x41, 0x00, 0x14, 0x12, 0x00, 0x00, 0x00,
                        0x24, 0x32, 0x00, (byte) 0xF7, };
        tested.calculateChecksum(sysex2, 5, sysex2.length - 3,
                sysex2.length - 2);
        assertEquals(0x2A, sysex2[sysex2.length - 2]);
    }
}
