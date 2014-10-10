package org.jsynthlib.synthdrivers.RolandD50;

import org.jsynthlib.patch.model.impl.Patch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class D50StringHandlerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSetPatchName() {
        Patch patch = new Patch();
        patch.sysex = new byte[20];
//        String s = "DigitalNativeDance";
        String s = "Velo-Brass";
        D50StringHandler.setName(patch, s, 0, s.length());
        System.out.println(bytesToHex(patch.sysex));
    }

    public static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();


    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
