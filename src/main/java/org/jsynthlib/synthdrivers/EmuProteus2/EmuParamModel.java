package org.jsynthlib.synthdrivers.EmuProteus2;

import org.jsynthlib.device.model.handler.ParamModel;

public class EmuParamModel extends ParamModel {

    public static final int ADDRESS_SIZE = 2;

    @Override
    public void set(int i) {
        if (i < 0) {
            patch.sysex[offset + 1] = (byte) 0x7F;
        }

        patch.sysex[offset] = (byte) (i % 128);
    }

    @Override
    public int get() {
        if ((patch.sysex[offset + 1] & 0xFF) == 0x7F) {
            return patch.sysex[offset] - 128;
        } else if ((patch.sysex[offset + 1] & 0xFF) != 0) {
            return patch.sysex[offset + 1] * 128 + patch.sysex[offset];
        } else {
            return patch.sysex[offset] & 0xFF;
        }
    }
}
