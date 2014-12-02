package org.jsynthlib.synthdrivers.YamahaDX100;

import org.jsynthlib.device.model.handler.SysexSender;

class AcedSender extends SysexSender {
    private final int parameter;
    private final byte[] b = new byte[7];

    public AcedSender(int param) {
        parameter = param;
        b[0] = (byte) 0xF0;
        b[1] = (byte) 0x43;
        b[3] = (byte) 0x13;
        b[4] = (byte) parameter;
        b[6] = (byte) 0xF7;
    }

    @Override
    public byte[] generate(int value) {
        b[5] = (byte) value;
        b[2] = (byte) (16 + getChannel() - 1);
        return b;
    }

    public int getParameter() {
        return parameter;
    }
}
