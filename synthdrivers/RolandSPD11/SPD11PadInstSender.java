/*
 * Copyright 2009 Peter Geirnaert
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

package synthdrivers.RolandSPD11;
import core.*;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;
/**
 *
 * @author peter
 */
public class SPD11PadInstSender extends SysexSender {
    int patch;
    int pad;
    // int offset;

    /**
     * Uses SysexSender(String) constructor
     * @param pad the pad this sender instance works for. Used for <code>calculateChecksum</code>
     * @param patch same as param pad
     *
     */
    public SPD11PadInstSender(int patch, int pad) {
     //offset: 0 1 2 3 4 5 6 7 8 9 10
        super("F041@@601200000000**00F7"); //calls SysexSender(String)
        this.patch = patch;
        this.pad = pad;
        //this.offset = offset;


    }
    public byte[] generate(int value) {
        byte[] syse = super.generate(value/16);
        syse[6] = (byte) patch;
        syse[7] = (byte) pad;
        //syse[8] = (byte) offset; can stay at zero
        syse[10] = (byte) SPD11_Constants.calculateChecksum(syse);
        return syse;
    }
    /**
     *
     * @param value
     * @return the message to send with generated value and checksum
     */
    public byte[] generate2(int value) {
        byte[] syse = super.generate(value % 16);
        syse[6] = (byte) patch;
        syse[7] = (byte) pad;
        syse[8] = (byte) 0x01;
        syse[10] = (byte) SPD11_Constants.calculateChecksum(syse);
        return syse;
    }
    public void send(IPatchDriver driver, int value){
        channel = (byte) driver.getDevice().getDeviceID();
        // generate the message to set the first offset and use value/16
        byte[] sysex = generate(value);
        // send the first message m
        SysexMessage m = new SysexMessage();
        try {
            m.setMessage(sysex, sysex.length);
            driver.send(m);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
        // generate the message to set the second offset and use value % 16
        sysex = generate2(value);
        // wait more than 20 milliseconds internal.
        try {
            Thread.sleep(22);  
        } catch (Exception e) {
            ErrorMsg.reportStatus(e);
        }
        // send the second message n
        SysexMessage n = new SysexMessage();
        try {
            n.setMessage(sysex, sysex.length);
            driver.send(n);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportStatus(e);
        }
    }
}
