package org.jsynthlib.synthdrivers.YamahaDX7.common;

import org.jsynthlib.device.model.handler.SysexSender;

/*
 * SysexSender - Voice Parameter (g=0; h=0 & g=0; h=1)
 */
public class VoiceSender extends SysexSender {
    private int parameter;
    private int paraHigh;
    private final byte[] buf;

    public VoiceSender() {
        buf = new byte[7];
        buf[0] = (byte) 0xF0;
        buf[1] = (byte) 0x43;
        buf[6] = (byte) 0xF7;

        paraHigh = 0;
        this.buf[3] = (byte) this.paraHigh; // group/subgroup number
    }

    public int getParameter() {
        if (paraHigh == 0) {
            return parameter;
        } else {
            return parameter + 128;
        }
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;

        if (this.parameter >= 128) {
            this.paraHigh = 0x01;
            this.buf[3] = (byte) this.paraHigh; // group/subgroup number
            this.parameter -= 128;
        }

        this.buf[4] = (byte) this.parameter;
    }

    @Override
    public byte[] generate(int value) {
        buf[2] = (byte) (0x10 + getChannel() - 1);
        buf[5] = (byte) value;

        return buf;
    }
}