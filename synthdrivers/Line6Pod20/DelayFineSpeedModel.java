//
//  DelayFineSpeedModel.java
//  JSynthLib
//
//  Created by Jeff Weber on 9/6/04.
//  Copyright 2004 __MyCompanyName__. All rights reserved.
//

package synthdrivers.Line6Pod20;

import core.*;

/** Delay Speed is represented by three bytes in offsets 27-29. There are two controls in the editor, one for coarse 
adjustment and the other for fine adjustment. The range for coarse values in the sysex record is 0-97536, where 
the max value is represented in bytes 27-29 as 0x017D00. The range for fine values in the sysex record is 0-762, 
where the max value is represented in bytes 27-29 as 0x0002FA. The value stored in offset 27-29 of the sysex 
record is simply the sum of the course and fine values. So the value with the coarse and fine values both set to 
maximum is 98298 or 0x017FFA. For the coarse control the control range of 0-127 split over the range 0-97536 gives 
us steps of 768 or 0x300. So to separate the two value, we can take the mod 300 of the combined value to obtain 
the coarse value. Subtracting this from the original value gives us the fine value.*/
class DelayFineSpeedModel extends ParamModel {
    private int maxCC;
    private int maxSysex;
    
    public DelayFineSpeedModel(Patch p, int o) {
        this(p, o, 1, 1);
    }
    
    public DelayFineSpeedModel(Patch p, int o, int maxCC, int maxSysex) {
        super(p, o);
        this.maxCC = maxCC;
        this.maxSysex = maxSysex;
    }
    
    public void set(int i) {
        int coarseValue = getCoarseValue();
        int newValue = coarseValue + (i * maxSysex / maxCC);

        byte byte1 = (byte)(newValue >>> 16);
        PatchBytes.setSysexByte(patch, 9, ofs-2, byte1);

        byte byte2 = (byte)((newValue >>> 8) & 0xFF);
        PatchBytes.setSysexByte(patch, 9, ofs-1, byte2);

        byte byte3 = (byte)(newValue & 0xFF);
        PatchBytes.setSysexByte(patch, 9, ofs, byte3);
    }
    
    public int get() {
        int returnValue =  getFineValue() * maxCC / maxSysex;
        return returnValue;
    }
    private int getBytesValue() {
        int msB  = (int)(0xff & PatchBytes.getSysexByte(patch.sysex, 9, ofs-2));
        int midB = (int)(0xff & PatchBytes.getSysexByte(patch.sysex, 9, ofs-1));
        int lsB  = (int)(0xff & PatchBytes.getSysexByte(patch.sysex, 9, ofs));
        
        int bytesValue = msB << 16;
        bytesValue = bytesValue | midB << 8;
        bytesValue = bytesValue | lsB;
        return bytesValue;
    }
    
    private int getCoarseValue() {
        int coarseValue = getBytesValue() - getFineValue();
        return coarseValue;
    }
    
    private int getFineValue() {
        int fineValue = getBytesValue() % (3 * 256);
        return fineValue;
    }
}