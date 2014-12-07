package org.jsynthlib.synthdrivers.YamahaDX100;

import org.jsynthlib.device.model.handler.SysexSender;

public class VcedSender extends SysexSender {
    private int parameter;
    private final byte[] b = new byte[7];

    public VcedSender() {
        b[0] = (byte) 0xF0;
        b[1] = (byte) 0x43;
        b[3] = (byte) 0x12;
        b[6] = (byte) 0xF7;
    }
    public VcedSender(int param) {
        this();
        setParameter(param);
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

    public void setParameter(int parameter) {
        this.parameter = parameter;
        b[4] = (byte) parameter;
    }

}
