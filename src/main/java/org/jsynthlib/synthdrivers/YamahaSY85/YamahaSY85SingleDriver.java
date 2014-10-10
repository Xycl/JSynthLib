/*
 * Copyright 2009 Christopher Arndt
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package org.jsynthlib.synthdrivers.YamahaSY85;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Driver for Yamaha SY85 Singles's (Yamaha calls them "Voices")
 * @author Christopher Arndt
 * @version $Id$
 */
public class YamahaSY85SingleDriver extends AbstractPatchDriver {

    /** patch file name for createNewPatch() */
    private static final String patchFileName = "InitVce.syx";

    private final transient Logger log = Logger.getLogger(getClass());

    public YamahaSY85SingleDriver() {
        super("Voice", "Christopher Arndt");

        this.sysexID = SY85Constants.SYSEX_ID;

        this.patchNameStart = SY85Constants.VOICE_NAME_START;
        this.patchNameSize = SY85Constants.VOICE_NAME_SIZE;

        this.deviceIDoffset = 2;

        this.checksumStart = SY85Constants.VOICE_CHECKSUM_START;
        this.checksumEnd = SY85Constants.VOICE_CHECKSUM_END;
        this.checksumOffset = SY85Constants.VOICE_CHECKSUM_OFFSET;

        this.bankNumbers =
                new String[] {
                        "Internal Voices I-1", "Internal Voices I-2",
                        "Internal Voices I-3", "Internal Voices I-4" };

        patchNumbers = new String[SY85Constants.VOICE_BANK_SIZE];
        System.arraycopy(generateNumbers(1, 8, "A-##"), 0,
                patchNumbers, 0, 8);
        System.arraycopy(generateNumbers(1, 8, "B-##"), 0,
                patchNumbers, 8, 8);
        System.arraycopy(generateNumbers(1, 8, "C-##"), 0,
                patchNumbers, 16, 8);
        System.arraycopy(generateNumbers(1, 8, "D-##"), 0,
                patchNumbers, 24, 8);
        System.arraycopy(generateNumbers(1, 8, "E-##"), 0,
                patchNumbers, 32, 8);
        System.arraycopy(generateNumbers(1, 8, "F-##"), 0,
                patchNumbers, 40, 8);
        System.arraycopy(generateNumbers(1, 8, "G-##"), 0,
                patchNumbers, 48, 8);
        System.arraycopy(generateNumbers(1, 7, "H-##"), 0,
                patchNumbers, 54, 7);

        this.patchSize = SY85Constants.VOICE_SIZE;
    }

    /**
     * Store the voice in the voice edit buffer of the SY85.
     * @param p
     *            The voice data
     */
    public void sendPatch(Patch p) {
        p.sysex[SY85Constants.SYSEX_BANK_NUMBER_OFFSET] = (byte) 127;
        p.sysex[SY85Constants.SYSEX_VOICE_NUMBER_OFFSET] = (byte) 0;
        calculateChecksum(p);
        sendPatchWorker(p);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
        }
    }

    /**
     * Store the voice in the given slot in one of SY85's internal voice banks.
     * @param p
     *            The voice data
     * @param bankNum
     *            The internal voice bank number
     * @param patchNum
     *            The voice program (slot) number
     */
    public void storePatch(Patch p, int bankNum, int patchNum) {
        p.sysex[SY85Constants.SYSEX_BANK_NUMBER_OFFSET] = (byte) (bankNum * 3);
        p.sysex[SY85Constants.SYSEX_VOICE_NUMBER_OFFSET] = (byte) patchNum;
        calculateChecksum(p);
        sendPatchWorker(p);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
        setBankNum(bankNum);
        setPatchNum(patchNum);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
        // XXX: send EXIT button push
    }

    /**
     * Request the dump of a single voice from SY85's internal voice banks.
     * @param bankNum
     *            The internal voice bank number
     * @param patchNum
     *            The number of the Voice which is requested
     */
    public void requestPatchDump(int bankNum, int patchNum) {
        // setBankNum(bankNum);
        // setPatchNum(patchNum);
        try {
            Thread.sleep(50);
        } catch (Exception e) {
        }
        // We have to add 0x20 to the device ID. Don't ask me why...
        send(SY85Constants.VOICE_DUMP_REQ.toSysexMessage(getDeviceID() + 0x20,
                new SysexHandler.NameValue("patchNum", patchNum),
                new SysexHandler.NameValue("bankNum", bankNum * 3)));
    }

    /**
     * Create new voice using a template patch file <code>patchFileName</code>.
     * The the template patch file must be located in the same directory as this
     * driver.
     * @return a <code>Patch</code> value
     */
    public Patch createNewPatch() {
        return (Patch) getPatchFactory().createNewPatch(this, patchFileName, patchSize);
    }

    /**
     * Create a new voice at a given memory number.
     * @param bankNum
     *            The internal voice bank number
     * @param patchNum
     *            The voice program (slot) number XXX: to be done...
     */
    /*
     * public static final Patch createNewPatch(int bankNum, int patchNum) {
     * byte [] sysex = new byte[SY85Constants.PATCH_SIZE]; // Create getPatchFactory().createNewPatch
     * on internal voice 1 sysex[0] = (byte) 0xF0; sysex[1] = (byte) 0x43;
     * sysex[2] = (byte) 0x00; // Device number sysex[3] = (byte) 0x7A; // [...]
     * // 238 is the checksum sysex[239] = (byte) 0xF7; Patch p = new
     * Patch(sysex); return p; }
     */

    /**
     * Send Control Change (Bank Select) MIDI message.
     * @param bankNum
     *            The internal voice bank number
     * @see #storePatch(Patch, int, int)
     */
    protected void setBankNum(int bankNum) {
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1, 0x00, // Bank
                                                                                // Select
                                                                                // (MSB)
                    0x00); // Bank Number (MSB)
            send(msg);
            msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1, 0x20, // Bank
                                                                                // Select
                                                                                // (LSB)
                    bankNum * 3); // Bank Number (LSB)
            send(msg);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Open the single voice patch editor. XXX: to be done...
     */
    /*
     * public JSLFrame editPatch(Patch p) { return new
     * YamahaSY85SingleEditor(p); }
     */
}
