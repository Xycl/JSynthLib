package org.jsynthlib.synthdrivers.TCElectronicM350;

import org.jsynthlib.device.model.handler.ParamModel;
import org.jsynthlib.patch.model.impl.Patch;

class M350Model extends ParamModel {
    private int param;

    public M350Model(Patch p, int par) {
        // params 0->9 are stored contiguously
        // param 0d is stored earlier. no other
        // of the controllable params are stored
        // in the patch messages. no other params
        // are valid (although its technically possible
        // to toggle bypass buttons etc via sysex, this is
        // not supported in the editor)
        super(p, (par == 13 ? 29 : par + 31));

        param = par;
    }

    public void set(int i) {
        if (param == 13) {
            patch.sysex[offset] = (byte) (i % 128);
            patch.sysex[offset + 1] = (byte) (i / 128);
        } else
            patch.sysex[offset] = (byte) i;

    }

    public int get() {
        if (param == 13)
            return (patch.sysex[offset] + 128 * patch.sysex[offset + 1]);
        else
            return patch.sysex[offset];
    }

}