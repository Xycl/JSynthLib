/**
 * AlesisQSProgramDriver.java
 *
 * Program program driver for Alesis QS series synths
 * March 2002
 * Authors: Zellyn Hunter (zellyn@bigfoot.com, zjh)
 *          Chris Halls <chris.halls@nikocity.de>
 * @version $Id: AlesisQSProgramDriver.java 1182 2011-12-04 22:07:24Z chriswareham $
 * GPL v2
 */

package org.jsynthlib.synthdrivers.AlesisQS;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.patch.model.impl.Patch;

public class AlesisQSProgramDriver extends AbstractPatchDriver {

    private final transient Logger log = Logger.getLogger(getClass());

    /**
     * The offset at which program data starts. In other words, when the Alesis
     * specification says data starts at offset n, it will be found at sysex
     * buffer[dataoffset + n].
     */
    final int dataoffset = 7;

    public AlesisQSProgramDriver() {
        super("Program", "Zellyn Hunter/Chris Halls");
        sysexID = "F000000E0E**";
        sysexRequestDump =
                new SysexHandler("F0 00 00 0E 0E *opcode* *patchNum* F7");
        // patchSize=350/7*8;
        patchSize = QSConstants.PATCH_SIZE_PROGRAM;
        // zjh - I think this should be 0 so sendPatchWorker doesn't use it
        deviceIDoffset = 0;
        ;
        checksumStart = 0;
        checksumEnd = 0;
        checksumOffset = 0;
        // bankNumbers =new String[] {"Internal 1", "Internal 2", "Internal 3",
        // "GenMIDI", "User"};
        bankNumbers = QSConstants.WRITEABLE_BANK_NAMES;
        patchNumbers = QSConstants.PATCH_NUMBERS_PROGRAM_WITH_EDIT_BUFFERS;
    }

    /**
     * Print a byte in binary, for debugging packing/unpacking code
     **/
    public String toBinaryStr(byte b) {
        String output = new String();
        for (int i = 7; i >= 0; i--) {
            output += ((b >> i) & 1);
        }
        return output;
    }

    /**
     * Get patch name from sysex buffer
     * @param ip
     *            the patch to get the name from
     * @return the name of the patch
     */
    @Override
    public String getPatchName(Patch ip) {
        // ErrorMsg.reportStatus("Alesis getPatchName ", p.sysex);
        return SysexRoutines.getChars(ip.sysex, QSConstants.HEADER,
                QSConstants.PROG_NAME_START, QSConstants.PROG_NAME_LENGTH);
    }

    /**
     * Set patch name in sysex buffer
     * @param p
     *            the patch to set the name in
     * @param name
     *            the string to set the name to
     */
    @Override
    public void setPatchName(Patch p, String name) {
        // ErrorMsg.reportStatus("Alesis setPatchName ", p.sysex);
        SysexRoutines.setChars(name, p.sysex, QSConstants.HEADER,
                QSConstants.PROG_NAME_START, QSConstants.PROG_NAME_LENGTH);
    }

    /**
     * Override the checksum and do nothing - the Alesis does not use checksums
     * @param p
     *            the ignored
     * @param start
     *            ignored
     * @param end
     *            ignored
     * @param ofs
     *            ignored
     */
    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        // This synth does not use a checksum
    }

    /**
     * Create a new program patch
     * @return the getPatchFactory().createNewPatch
     */
    @Override
    public Patch createNewPatch() {
        // Copy over the Alesis QS header
        byte[] sysex = new byte[patchSize];
        for (int i = 0; i < QSConstants.GENERIC_HEADER.length; i++) {
            sysex[i] = QSConstants.GENERIC_HEADER[i];
        }
        // Set it to be a program, at position 0
        sysex[QSConstants.POSITION_OPCODE] =
                QSConstants.OPCODE_MIDI_USER_PROG_DUMP;
        sysex[QSConstants.POSITION_LOCATION] = 0;

        // Create the patch, and set the name
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, QSConstants.DEFAULT_NAME_PROG);
        return p;
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        return new AlesisQSProgramEditor(p);
    }

    /**
     * Copied from Driver.java by zjh. Requests a patch dump. If the patch
     * number is 0..127, then use opcode 01 - MIDI User Program Dump Request. If
     * the patch number is 128..144, use opcode 03 - MIDI Edit Program Dump
     * Request. 128 corresponds to the Program mode edit buffer, and 129..144 to
     * Mix mode edit buffers 0..15
     * @param bankNum
     *            not used
     * @param patchNum
     *            the patch number
     */
    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        // setBankNum(bankNum);
        // setPatchNum(patchNum);

        // default to simple case - get specified patch from the User bank
        int location = patchNum;
        int opcode = QSConstants.OPCODE_MIDI_USER_PROG_DUMP_REQ;

        // if the patch number is > max location, get from Edit buffers
        if (location > QSConstants.MAX_LOCATION_PROG) {
            location -= (QSConstants.MAX_LOCATION_PROG + 1);
            opcode = QSConstants.OPCODE_MIDI_EDIT_PROG_DUMP_REQ;
        }

        send(sysexRequestDump.toSysexMessage(getChannel(),
                new SysexHandler.NameValue("opcode", opcode),
                new SysexHandler.NameValue("patchNum", location)));
    }

    /**
     * Sends a patch to the synth's edit buffer.
     * @param p
     *            the patch to send to the edit buffer
     */
    @Override
    public void sendPatch(Patch p) {
        storePatch(p, 0, QSConstants.MAX_LOCATION_PROG + 1);
    }

    /**
     * Sends a patch to a set location on a synth. See comment for
     * requestPatchDump for explanation of patch numbers > 127. We save the old
     * values, then set the opcode and target location, then send it, then
     * restore the old values
     * @param p
     *            the patch to send
     * @param bankNum
     *            ignored - you can only send to the User bank on Alesis QS
     *            synths
     * @param patchNum
     *            the patch number to send it to
     */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        // default to simple case - set specified patch in the User bank
        int location = patchNum;
        byte opcode = QSConstants.OPCODE_MIDI_USER_PROG_DUMP;
        byte oldOpcode = p.sysex[QSConstants.POSITION_OPCODE];
        byte oldLocation = p.sysex[QSConstants.POSITION_LOCATION];

        // if the patch number is > max location, get from Edit buffers
        if (location > QSConstants.MAX_LOCATION_PROG) {
            location -= (QSConstants.MAX_LOCATION_PROG + 1);
            opcode = QSConstants.OPCODE_MIDI_EDIT_PROG_DUMP;
        }
        // set the opcode and target location
        p.sysex[QSConstants.POSITION_OPCODE] = opcode;
        p.sysex[QSConstants.POSITION_LOCATION] = (byte) location;

        log.info(MidiMessageFormatter.hexDump(p.sysex, 0,
                p.sysex.length, 20));
        // setBankNum (bankNum);
        // setPatchNum (patchNum);

        // actually send the patch
        sendPatchWorker(p);

        // restore the old values
        p.sysex[QSConstants.POSITION_OPCODE] = oldOpcode;
        p.sysex[QSConstants.POSITION_LOCATION] = oldLocation;
    }
}
