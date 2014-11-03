package org.jsynthlib.synthdrivers.KawaiK4;

import org.jsynthlib.device.model.handler.SysexSender;

class K4Sender extends SysexSender {
    private final int source;

    private final byte[] b = {
            (byte) 0xF0, 0x40, 0, 0x10, 0x00, 0x04, 0, 0, 0, (byte) 0xF7 };

    public K4Sender(int parameter, int source) {
        this.source = source;
        b[6] = (byte) parameter;
    }

    public K4Sender(int parameter) {
        this.source = 0;
        b[6] = (byte) parameter;
    }

    @Override
    public byte[] generate(int value) {
        b[2] = (byte) (getChannel() - 1);
        b[7] = (byte) ((value / 128) + (source * 2));
        b[8] = (byte) (value & 127);
        return b;
    }
}
