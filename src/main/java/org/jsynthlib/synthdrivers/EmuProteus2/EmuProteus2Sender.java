package org.jsynthlib.synthdrivers.EmuProteus2;

import org.jsynthlib.device.model.SysexSender;
import org.jsynthlib.synthdrivers.RolandD50.D50Constants;

public class EmuProteus2Sender extends SysexSender {

    private static final int DEVICE_ID_OFFSET = 3;
    public static final int VALUE_OFFSET = 7;
    public static final int ADDRESS_OFFSET = 5;
    public static final int ADDRESS_SIZE = 2;
    public static final Factory FACTORY = new Factory();
    // Base message containing one value
    public static final byte[] BASE_MESSAGE = new byte[] {
            (byte) 0xF0, 0x18, 0x04, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00,
            (byte) 0xF7 };
    private static final int CS_START = D50Constants.SYSEX_HEADER_SIZE;
    private static final int CS_END = BASE_MESSAGE.length
            - D50Constants.SYSEX_FOOTER_SIZE;
    private static final int CS_OFS = BASE_MESSAGE.length - 2;
    protected byte[] message;

    public static class Factory {
        private int deviceId;

        public void setDeviceId(int deviceId) {
            this.deviceId = deviceId;
        }

        public EmuProteus2Sender newSender(int offset) {
            EmuProteus2Sender sender = new EmuProteus2Sender();
            sender.setDeviceId(deviceId);
            sender.setOffset(offset);
            return sender;
        }

        public EmuProteus2Sender newInstrumentSender(int offset) {
            EmuProteus2InstrumentSender sender =
                    new EmuProteus2InstrumentSender();
            sender.setDeviceId(deviceId);
            sender.setOffset(offset);
            return sender;
        }
    }

    public EmuProteus2Sender() {
        super();
        message = new byte[BASE_MESSAGE.length];
        System.arraycopy(BASE_MESSAGE, 0, message, 0, BASE_MESSAGE.length);
    }

    public void setDeviceId(int deviceId) {
        message[DEVICE_ID_OFFSET] = (byte) deviceId;
    }

    private int offset;

    public void setOffset(int offset) {
        message[ADDRESS_OFFSET] = (byte) (offset & 127);
        message[ADDRESS_OFFSET + 1] = (byte) (offset / 128);
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public byte[] generate(int value) {
        int temp = value;
        if (temp < 0) {
            // we need to handle the negative case
            temp = -temp;
            temp = ~temp;
            temp = temp & 16383;
            temp++;
        }

        message[VALUE_OFFSET] = (byte) (temp & 127);
        message[VALUE_OFFSET + 1] = (byte) (temp / 128);
        return message;
    }
}
