// written by Kenneth L. Martinez
// @version $Id: NLPerfSingleDriver.java 698 2004-09-11 04:39:44Z hayashi $

package synthdrivers.NordLead;

import org.apache.log4j.Logger;

import core.Driver;
import core.Patch;
import core.SysexHandler;

public class NLPerfSingleDriver extends Driver {
    static final String BANK_LIST[] = new String[] {
        "PCMCIA" };
    static final String PATCH_LIST[] = new String[] {
            "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "B0",
            "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "C0", "C1",
            "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "D0", "D1", "D2",
            "D3", "D4", "D5", "D6", "D7", "D8", "D9", "E0", "E1", "E2", "E3",
            "E4", "E5", "E6", "E7", "E8", "E9", "F0", "F1", "F2", "F3", "F4",
            "F5", "F6", "F7", "F8", "F9", "G0", "G1", "G2", "G3", "G4", "G5",
            "G6", "G7", "G8", "G9", "H0", "H1", "H2", "H3", "H4", "H5", "H6",
            "H7", "H8", "H9", "J0", "J1", "J2", "J3", "J4", "J5", "J6", "J7",
            "J8", "J9", "L0", "L1", "L2", "L3", "L4", "L5", "L6", "L7", "L8",
            "L9" };
    static final int BANK_NUM_OFFSET = 4;
    static final int PATCH_NUM_OFFSET = 5;
    static final byte NEW_PATCH[] = {
            (byte) 0xF0, (byte) 0x33, (byte) 0x00, (byte) 0x04, (byte) 0x1F,
            (byte) 0x00, (byte) 0x07, (byte) 0x06, (byte) 0x0A, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x03,
            (byte) 0x02, (byte) 0x09, (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x0B, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x0E,
            (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x06,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x05, (byte) 0x00,
            (byte) 0x00, (byte) 0x02, (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x0B, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x01, (byte) 0x01, (byte) 0x04, (byte) 0x08, (byte) 0x04,
            (byte) 0x0E, (byte) 0x03, (byte) 0x0B, (byte) 0x02, (byte) 0x0E,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x0E,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01,
            (byte) 0x0F, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x03,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x03,
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x0A, (byte) 0x04, (byte) 0x0B, (byte) 0x03, (byte) 0x06,
            (byte) 0x03, (byte) 0x03, (byte) 0x01, (byte) 0x0E, (byte) 0x04,
            (byte) 0x07, (byte) 0x03, (byte) 0x08, (byte) 0x04, (byte) 0x00,
            (byte) 0x00, (byte) 0x0E, (byte) 0x04, (byte) 0x0C, (byte) 0x02,
            (byte) 0x08, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x05, (byte) 0x02, (byte) 0x06, (byte) 0x06, (byte) 0x01,
            (byte) 0x03, (byte) 0x02, (byte) 0x03, (byte) 0x03, (byte) 0x00,
            (byte) 0x00, (byte) 0x0D, (byte) 0x00, (byte) 0x03, (byte) 0x06,
            (byte) 0x03, (byte) 0x02, (byte) 0x00, (byte) 0x06, (byte) 0x0C,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x0A, (byte) 0x05, (byte) 0x00, (byte) 0x04, (byte) 0x0D,
            (byte) 0x03, (byte) 0x03, (byte) 0x05, (byte) 0x07, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x03, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x08, (byte) 0x01,
            (byte) 0x0E, (byte) 0x03, (byte) 0x0E, (byte) 0x02, (byte) 0x03,
            (byte) 0x06, (byte) 0x08, (byte) 0x03, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x02, (byte) 0x0C,
            (byte) 0x01, (byte) 0x02, (byte) 0x02, (byte) 0x0E, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x0A, (byte) 0x05, (byte) 0x00,
            (byte) 0x04, (byte) 0x0D, (byte) 0x03, (byte) 0x03, (byte) 0x05,
            (byte) 0x07, (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x04,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01,
            (byte) 0x08, (byte) 0x01, (byte) 0x0E, (byte) 0x03, (byte) 0x0E,
            (byte) 0x02, (byte) 0x03, (byte) 0x06, (byte) 0x08, (byte) 0x03,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B,
            (byte) 0x02, (byte) 0x0C, (byte) 0x01, (byte) 0x02, (byte) 0x02,
            (byte) 0x0E, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x04, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x00,
            (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x07,
            (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01,
            (byte) 0x07, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x08, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x07, (byte) 0x01,
            (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x08,
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00,
            (byte) 0x08, (byte) 0x00, (byte) 0x07, (byte) 0x01, (byte) 0x07,
            (byte) 0x01, (byte) 0x07, (byte) 0x01, (byte) 0x07, (byte) 0x01,
            (byte) 0x03, (byte) 0x00, (byte) 0x03, (byte) 0x00, (byte) 0x01,
            (byte) 0x00, (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x0F, (byte) 0x0F,
            (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x01, (byte) 0x01,
            (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x04,
            (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0xF7 };

    private final transient Logger log = Logger.getLogger(getClass());

    public NLPerfSingleDriver() {
        super("Perf Single", "Kenneth L. Martinez");
        sysexID = "F033**04**";
        sysexRequestDump =
                new SysexHandler("F0 33 @@ 04 *bankNum* *patchNum* F7");

        patchSize = 711;
        patchNameStart = -1;
        patchNameSize = 0;
        deviceIDoffset = 2;
        bankNumbers = BANK_LIST;
        patchNumbers = PATCH_LIST;
    }

    public void calculateChecksum(Patch p) {
        // doesn't use checksum
    }

    // protected static void calculateChecksum(Patch p, int start, int end, int
    // ofs) {
    // // doesn't use checksum
    // }

    public String getPatchName(Patch ip) {
        return "perf" + (((Patch) ip).sysex[PATCH_NUM_OFFSET] + 1);
    }

    public void setPatchName(Patch p, String name) {
    }

    public void sendPatch(Patch p) {
        sendPatch((Patch) p, 30, 0); // using edit buffer
    }

    public void sendPatch(Patch p, int bankNum, int patchNum) {
        Patch p2 = new Patch(p.sysex);
        p2.sysex[BANK_NUM_OFFSET] = (byte) bankNum;
        p2.sysex[PATCH_NUM_OFFSET] = (byte) patchNum;
        mySendPatch(p2);
    }

    // Sends a patch to a set location in the user bank
    public void storePatch(Patch p, int bankNum, int patchNum) {
        sendPatch((Patch) p, 31, patchNum);
        setPatchNum(patchNum); // send program change to get new sound in edit
                               // buffer
    }

    protected void playPatch(Patch p) {
        byte sysex[] = new byte[patchSize];
        System.arraycopy(((Patch) p).sysex, 0, sysex, 0, patchSize);
        sysex[BANK_NUM_OFFSET] = 30; // edit buffer
        sysex[PATCH_NUM_OFFSET] = 0;
        Patch p2 = new Patch(sysex);
        super.playPatch(p2);
    }

    public Patch createNewPatch() {
        return new Patch(NEW_PATCH, this);
    }

    protected void mySendPatch(Patch p) {
        p.sysex[deviceIDoffset] =
                (byte) (((NordLeadDevice) getDevice()).getGlobalChannel() - 1);
        try {
            send(p.sysex);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(
                ((NordLeadDevice) getDevice()).getGlobalChannel(),
                new SysexHandler.NameValue("bankNum", 41),
                new SysexHandler.NameValue("patchNum", patchNum)));
    }
}
