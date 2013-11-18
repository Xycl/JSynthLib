package synthdrivers.EmuProteus2;

import synthdrivers.RolandD50.D50Constants;
import synthdrivers.RolandD50.D50PartialMuteDataModel;
import synthdrivers.RolandD50.D50Sender;
import synthdrivers.RolandD50.D50Sender.Factory;
import core.SysexSender;

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
            return new EmuProteus2Sender(offset, deviceId);
        }

        public EmuProteus2Sender newInstrumentSender(int offset) {
            return new EmuInstrumentParamModel.EmuProteus2InstrumentSender(
                    offset, deviceId);
        }
    }

    protected EmuProteus2Sender(int offset, int deviceId) {
        super();
        message = new byte[BASE_MESSAGE.length];
        System.arraycopy(BASE_MESSAGE, 0, message, 0, BASE_MESSAGE.length);
        message[DEVICE_ID_OFFSET] = (byte) deviceId;
        message[ADDRESS_OFFSET] = (byte) (offset & 127);
        message[ADDRESS_OFFSET + 1] = (byte) (offset / 128);
    }

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
