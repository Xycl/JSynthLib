package synthdrivers.TCElectronicM350;

import core.*;

class M350Model extends ParamModel {
	private int param;

    public M350Model(Patch p, int par) {
    	//params 0->9 are stored contiguously
    	//param 0d is stored earlier. no other 
    	//of the controllable params are stored
    	//in the patch messages. no other params
    	//are valid (although its technically possible
    	//to toggle bypass buttons etc via sysex, this is
    	//not supported in the editor)
    	super(p, (par == 13 ? 28 : par+30));   
    	
    	param = par;
    }
    
    public void set(int i) {
    	if (param == 13)
    	{
    		patch.sysex[ofs] = (byte) (i % 128);
    		patch.sysex[ofs+1] = (byte) (i / 128);
    	}
    	else
    		patch.sysex[ofs] = (byte)i;
    		
    }

    public int get() {
    	if (param == 13)
    		return (patch.sysex[ofs] + 128*patch.sysex[ofs+1]);
    	else
          return patch.sysex[ofs];
    }

}