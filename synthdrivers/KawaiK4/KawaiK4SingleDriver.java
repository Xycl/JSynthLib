package synthdrivers.KawaiK4;
import core.Driver;
import core.DriverUtil;
import core.JSLFrame;
import core.Patch;
import core.SysexHandler;

/**
 * Single Voice Patch Driver for Kawai K4.
 *
 * @version $Id$
 */
public class KawaiK4SingleDriver extends Driver {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Patch size */
    private static final int SSIZE = 131;

    private static final SysexHandler SYS_REQ = new SysexHandler("F0 40 @@ 00 00 04 *bankNum* *patchNum* F7");

    public KawaiK4SingleDriver() {
	super("Single", "Brian Klock");
	sysexID = "F040**2*0004";

	patchSize	= HSIZE + SSIZE + 1;
	patchNameStart	= HSIZE;
	patchNameSize	= 10;
	deviceIDoffset	= 2;
	checksumStart	= HSIZE;
	checksumEnd	= HSIZE + SSIZE - 2;
	checksumOffset	= HSIZE + SSIZE - 1;
	bankNumbers	= new String[] {
	    "0-Internal", "1-External"
	};
	/*
	patchNumbers = new String[] {
	    "A-1", "A-2", "A-3", "A-4", "A-5", "A-6", "A-7", "A-8",
	    "A-9", "A-10", "A-11", "A-12", "A-13", "A-14", "A-15", "A-16",
	    "B-1", "B-2", "B-3", "B-4", "B-5", "B-6", "B-7", "B-8",
	    "B-9", "B-10", "B-11", "B-12", "B-13", "B-14", "B-15", "B-16",
	    "C-1", "C-2", "C-3", "C-4", "C-5", "C-6", "C-7", "C-8",
	    "C-9", "C-10", "C-11", "C-12", "C-13", "C-14", "C-15", "C-16",
	    "D-1", "D-2", "D-3", "D-4", "D-5", "D-6", "D-7", "D-8",
	    "D-9", "D-10", "D-11", "D-12", "D-13", "D-14", "D-15", "D-16"
	};
	*/
	patchNumbers = new String[16 * 4];
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "A-##"), 0, patchNumbers,  0, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "B-##"), 0, patchNumbers, 16, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "C-##"), 0, patchNumbers, 32, 16);
	System.arraycopy(DriverUtil.generateNumbers(1, 16, "D-##"), 0, patchNumbers, 48, 16);
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
	setBankNum(bankNum);
	setPatchNum(patchNum);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	p.sysex[3] = (byte) 0x20;
	p.sysex[6] = (byte) (bankNum << 1);
	p.sysex[7] = (byte) (patchNum);
	sendPatchWorker(p);
	try {
	    Thread.sleep(100);
	} catch (Exception e) {
	}
	setPatchNum(patchNum);
    }

    public void sendPatch(Patch p) {
	p.sysex[3] = (byte) 0x23;
	p.sysex[7] = (byte) 0x00;
	sendPatchWorker(p);
    }

    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
    	int sum = 0;
	for (int i = start; i <= end; i++) {
	    sum += p.sysex[i];
	}
	sum += 0xA5;
	p.sysex[ofs] = (byte) (sum % 128);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]^127);
	// p.sysex[ofs]=(byte)(p.sysex[ofs]+1);
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[HSIZE + SSIZE + 1];

	// Set the sysex format data
	sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x40; sysex[2] = (byte) 0x00;
	sysex[3] = (byte) 0x23; sysex[4] = (byte) 0x00; sysex[5] = (byte) 0x04;
	sysex[6] = (byte) 0x0; sysex[HSIZE + SSIZE] = (byte) 0xF7;
	
	// Set some initial data for the patch, so we can hear actually anything, when playing (Andreas Rueckert <a_rueckert@gmx.net>).
	sysex[HSIZE+10] = (byte)80;  // Set the volume to 80.
	sysex[HSIZE+14] = (byte)1;   // Unmute source 1
	sysex[HSIZE+15] = (byte)2;   // Vibrato = triange and pitchbend range = 2
	
	// For each source to initialize
	for( int s = 0; s < 3; ++s) {

	    // Set some data for the source, so we can hear this source.
	    sysex[HSIZE+42+s] = (byte)(24+64);  // Set coarse to 0 and keyscale on.
	    sysex[HSIZE+58+s] = (byte)80;  // Level (volume) = 80.
	    sysex[HSIZE+62+s] = (byte)10;  // Attack = 10.
	    sysex[HSIZE+66+s] = (byte)90;  // Decay = 90;
	    sysex[HSIZE+70+s] = (byte)90;  // Sustain = 90;
	    sysex[HSIZE+74+s] = (byte)20;  // Release = 20;
	    sysex[HSIZE+78+s] = (byte)50;  // Level Mod velocity = 0 (Range is -50 to 50, with values 0 - 100).
	    sysex[HSIZE+82+s] = (byte)50;  // Level Mod pressure = 0.
	    sysex[HSIZE+86+s] = (byte)50;  // Level Mod keyscale = 0.
	    sysex[HSIZE+90+s] = (byte)50;  // Time Mod on velocity = 0;
	    sysex[HSIZE+94+s] = (byte)50;  // Time Mod off velocity = 0;
	    sysex[HSIZE+98+s] = (byte)50;  // Time Mod keyscale.
	}

	sysex[HSIZE+102] = (byte)90;  // Cutoff DCF 1 = 90;
	sysex[HSIZE+103] = (byte)90;  // Cutoff DCF 2 = 90;
	sysex[HSIZE+106] = (byte)50;  // DCF 1 cutoff Mod velocity = 0.
	sysex[HSIZE+107] = (byte)50;  // DCF 2 cutoff Mod velocity = 0.
	sysex[HSIZE+108] = (byte)50;  // DCF 1 cutoff Mod pressure = 0;
	sysex[HSIZE+109] = (byte)50;  // DCF 2 cutoff Mod pressure = 0;
	sysex[HSIZE+110] = (byte)50;  // DCF 1 cutoff Mod keyscale = 0;
	sysex[HSIZE+111] = (byte)50;  // DCF 2 cutoff Mod keyscale = 0;
	sysex[HSIZE+112] = (byte)50;  // DCF 1 envelope depth = 0.
	sysex[HSIZE+113] = (byte)50;  // DCF 2 envelope depth = 0.
	sysex[HSIZE+114] = (byte)50;  // DCF 1 envelope velocity depth = 0.
	sysex[HSIZE+115] = (byte)50;  // DCF 2 envelope velocity depth = 0.
	sysex[HSIZE+116] = (byte)5;   // DCF 1 attack.
	sysex[HSIZE+117] = (byte)5;   // DCF 2 attack.
	sysex[HSIZE+118] = (byte)90;   // DCF 1 decay.
	sysex[HSIZE+119] = (byte)90;   // DCF 2 decay.
	sysex[HSIZE+120] = (byte)90;   // DCF 1 sustain.
	sysex[HSIZE+121] = (byte)90;   // DCF 2 sustain.
	sysex[HSIZE+122] = (byte)10;   // DCF 1 release.
	sysex[HSIZE+123] = (byte)10;   // DCF 2 release.
	sysex[HSIZE+124] = (byte)50;   // DCF 1 time Mod on velocity.
	sysex[HSIZE+125] = (byte)50;   // DCF 2 time Mod on velocity.
	sysex[HSIZE+126] = (byte)50;   // DCF 1 time Mod off velocity.
	sysex[HSIZE+127] = (byte)50;   // DCF 2 time Mod off velocity.
	sysex[HSIZE+128] = (byte)50;   // DCF 1 time Mod keyscale.
	sysex[HSIZE+129] = (byte)50;   // DCF 2 time Mod keyscale.
	
	Patch p = new Patch(sysex, this);
	setPatchName(p, "New Patch");
	calculateChecksum(p);
	return p;
    }

    public JSLFrame editPatch(Patch p) {
	return new KawaiK4SingleEditor(p);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(SYS_REQ.toSysexMessage(getChannel(),
				    new SysexHandler.NameValue("bankNum", bankNum << 1),
				    new SysexHandler.NameValue("patchNum", patchNum)));
    }
}
