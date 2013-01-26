package synthdrivers.RocktronIntellifex;

import core.ParamModel;
import core.Patch;

/**
 * ParamModel class for Rocktron Intellifex effect parameters.
 * @author  Klaus Sailer
 * @version $Id$
 */
public class IntellifexModel extends ParamModel {
 
    public IntellifexModel(Patch p, int offset) {
        //store address as word index into 200 bytes data
        super(p, 6 + 2 * offset);
    }
    
    public void set(int value) {
        //save as double byte
        patch.sysex[ofs] = (byte)(value & 0x7f);
        patch.sysex[ofs+1] = (byte)(value>>7);
    }

    public int get() {
        //get from double byte
        return patch.sysex[ofs] | patch.sysex[ofs+1]<<7; 
    }
}
