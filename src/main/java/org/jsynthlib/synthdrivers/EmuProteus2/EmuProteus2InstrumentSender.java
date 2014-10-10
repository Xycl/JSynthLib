package org.jsynthlib.synthdrivers.EmuProteus2;

public class EmuProteus2InstrumentSender extends EmuProteus2Sender {

    @Override
    public byte[] generate(int value) {
        return EmuInstrumentParamModel.setInstrumentBytes(value, VALUE_OFFSET, message);
    }

}
