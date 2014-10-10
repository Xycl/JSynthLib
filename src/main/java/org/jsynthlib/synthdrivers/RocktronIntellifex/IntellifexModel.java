package org.jsynthlib.synthdrivers.RocktronIntellifex;

import org.jsynthlib.device.model.ParamModel;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * ParamModel class for Rocktron Intellifex effect parameters.
 * @author Klaus Sailer
 * @version $Id$
 */
public class IntellifexModel extends ParamModel {

    public IntellifexModel(Patch p, int offset) {
        // store address as word index into 200 bytes data
        super(p, 6 + 2 * offset);
    }

    public void set(int value) {
        // save as double byte
        patch.sysex[offset] = (byte) (value & 0x7f);
        patch.sysex[offset + 1] = (byte) (value >> 7);
    }

    public int get() {
        // get from double byte
        return patch.sysex[offset] | patch.sysex[offset + 1] << 7;
    }
}
