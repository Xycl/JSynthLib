package synthdrivers.EmuProteus2;

import core.ParamModel;
import core.Patch;



public class EmuParamModel extends ParamModel {

    public static final int ADDRESS_SIZE = 2;



    public EmuParamModel(Patch p, int o) {
        super(p, o * 2 + EmuProteus2SingleDriver.SYSEX_HEADER.length + ADDRESS_SIZE);
    }



    public void set(int i) {
        if(i < 0) {
            patch.sysex[ofs + 1] = (byte)0x7F;
        }

        patch.sysex[ofs] = (byte)(i % 128);
    }



    public int get() {
        if((patch.sysex[ofs + 1] & 0xFF) == 0x7F) {
            return (int)patch.sysex[ofs] - 128;
        } else if ((patch.sysex[ofs + 1] & 0xFF) != 0) {
            return patch.sysex[ofs + 1] * 128 + patch.sysex[ofs];
        }
        else {
            return patch.sysex[ofs] & 0xFF;
        }

    }
}
