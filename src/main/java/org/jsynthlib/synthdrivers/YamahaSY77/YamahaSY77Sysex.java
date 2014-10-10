package org.jsynthlib.synthdrivers.YamahaSY77;


public abstract class YamahaSY77Sysex {
    public static final int DEVICE_ID_OFFSET = 2;
    public static final int SIZE_OFFSET = 4;
    public static final int SIZE_LENGTH = 2;
    public static final int CHECKSUM_OFFSET = -2;
    public static final int SYSEX_OVERHEAD = 6;

    public static void checksum(byte[] sysex) {
        for (int len = 0, offset = 0; offset <= sysex.length - SYSEX_OVERHEAD; offset +=
                len + SYSEX_OVERHEAD + 2) {
            len = getSize(sysex, offset);
            checksum(sysex, offset + SYSEX_OVERHEAD, len);
        }
    }

    public static void checksum(byte[] sysex, int offset) {
        int len = getSize(sysex, offset);
        checksum(sysex, offset + SIZE_OFFSET, len);
    }

    public static void checksum(byte[] sysex, int start, int len) {
        int end = start + len;

        if (start < 0 || start > end || end >= sysex.length)
            return;

        int sum = 0;

        for (int i = start; i < end; i++)
            sum += sysex[i];

        sysex[end] = (byte) ((((sum & 127) ^ 127) + 1) & 127);
    }

    public static int getSize(byte[] sysex, int offset) {
        int size = sysex[offset + SIZE_OFFSET] << 7;
        size |= (sysex[offset + SIZE_OFFSET + 1] & 127);
        return size;
    }
}
