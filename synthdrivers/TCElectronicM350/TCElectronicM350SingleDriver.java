package synthdrivers.TCElectronicM350;
import core.Driver;
import core.DriverUtil;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * Single Voice Patch Driver for TC Electronic M350.
 *
 * @version $Id: KawaiK4SingleDriver.java 939 2005-03-03 04:05:40Z hayashi $
 */
public class TCElectronicM350SingleDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 9;
    /** Single Patch size */
    private static final int SSIZE = 33;

   //possibly the 00 after *patchnum* is bank num in future devices
    private static final SysexHandler SYS_REQ = new SysexHandler("F0 00 20 1F 00 58 45 *patchNum* 00 F7"); 

    public TCElectronicM350SingleDriver() {
	super("Single", "Frankie Fisher");
	
	sysexID = "F000201F**5820";

	patchSize	= HSIZE + SSIZE + 1;
	patchNameStart	= HSIZE;
	patchNameSize	= 20;
	deviceIDoffset	= 4;
	checksumStart	= HSIZE;
	checksumEnd	= HSIZE + SSIZE - 2;
	checksumOffset	= HSIZE + SSIZE - 1;
	bankNumbers	= new String[] {
	    "0"
	};

	patchNumbers = new String[100];
	patchNumbers[0] = "Edit Buffer";
	System.arraycopy(DriverUtil.generateNumbers(1, 99, "##"), 0, patchNumbers,  1, 99);
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
	p.sysex[6] = (byte) 0x20;
	p.sysex[7] = (byte) (patchNum);
	sendPatchWorker(p);
    }

    public void sendPatch(Patch p) {
	p.sysex[7] = (byte) 0x00;
	sendPatchWorker(p);
    }

	protected void calculateChecksum(Patch patch, int start, int end, int offset) {
        int sum = 0;
        for (int i = start; i <= end; i++)
            sum += patch.sysex[i];
        patch.sysex[offset] = (byte) (sum & 0x7f);
	}

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x00; sysex[2] = (byte) 0x20;
	sysex[3] = (byte) 0x1F; sysex[4] = (byte) 0x7F; sysex[5] = (byte) 0x58;
	sysex[6] = (byte) 0x20;
	//patch name for the next 20 bytes
	sysex[27] = (byte) 0x00; sysex[8] = (byte) 0x00; //tap in ms (LSB first)
	sysex[29] = (byte) 0x00; //input gain
	sysex[30] = (byte) 0x00; //mix ratio
	sysex[31] = (byte) 0x00; //effect bal
	sysex[32] = (byte) 0x00; //delay effect type
	sysex[33] = (byte) 0x00; //delay/timing
	sysex[34] = (byte) 0x00; //feedback/depth
	sysex[35] = (byte) 0x00; //reverb type
	sysex[36] = (byte) 0x00; //pre delay
	sysex[37] = (byte) 0x00; //decay time
	sysex[38] = (byte) 0x00; //colour filter
	//checksum
	
	
	sysex[HSIZE + SSIZE] = (byte) 0xF7;
	Patch p = new Patch(sysex, this);
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new TCElectronicM350SingleEditor(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new SysexHandler.NameValue("patchNum", patchNum)));
    }
    
    
}
