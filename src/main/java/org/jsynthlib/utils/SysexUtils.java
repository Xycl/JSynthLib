package org.jsynthlib.utils;

public final class SysexUtils {

    private SysexUtils() {
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String sysexToString(byte[] sysex) {
        char[] hexChars = new char[sysex.length * 2];
        for (int j = 0; j < sysex.length; j++) {
            int v = sysex[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static byte[] stringToSysex(String sysex) {
        int len = sysex.length();
        String cleanSysex = sysex.replaceAll("\\*", "0");
        byte[] buf = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            buf[i / 2] =
                    (byte) ((Character.digit(cleanSysex.charAt(i), 16) << 4) + Character
                            .digit(cleanSysex.charAt(i + 1), 16));
        }
        return buf;
    }
}
