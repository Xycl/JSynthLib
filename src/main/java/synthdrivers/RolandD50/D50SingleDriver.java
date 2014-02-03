/*
 * Copyright 2013 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
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
package synthdrivers.RolandD50;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Semaphore;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.jsynthlib.gui.desktop.JSLFrame;

import core.Driver;
import core.DriverUtil;
import core.ErrorMsg;
import core.MidiUtil;
import core.Patch;
import core.PatchEdit;
import core.SysexHandler;

/**
 * Single Voice Patch Driver for Roland D-50
 */
public class D50SingleDriver extends Driver {

    final static int PATCH_NUMBER_OFFSET = 7;

    public D50SingleDriver() {
        super("Single", "Pascal Collberg");
        patchSize = D50Constants.PATCH_SIZE;
        sysexID = "F041**1412";
        patchNameStart = D50Constants.PATCH_NAME_START;
        patchNameSize = D50Constants.PATCH_NAME_SIZE;
        deviceIDoffset = 2;
        checksumStart = D50Constants.SYSEX_HEADER_SIZE;
        checksumEnd = D50Constants.PATCH_SIZE - D50Constants.SYSEX_FOOTER_SIZE;
        checksumOffset = D50Constants.PATCH_SIZE - 2;
        bankNumbers = new String[] {
                "0-Internal", "1-External" };
        patchNumbers = new String[8 * 8];
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append("You cannot store patches using this driver. Please use the RolandD50BankDriver instead.");
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), sb.toString(),
                "Get Patch", JOptionPane.WARNING_MESSAGE);
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append("You cannot dump patches using this driver. Please use the RolandD50BankDriver instead.");
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), sb.toString(),
                "Get Patch", JOptionPane.WARNING_MESSAGE);
    }

    public void sendPatch(Patch p) {
        // Set address to temp area
        p.sysex[5] = 0x0;
        p.sysex[6] = 0x0;
        p.sysex[7] = 0x0;

        sendPatchWorker(p);
    }

    protected JSLFrame editPatch(Patch p) {
        return new D50SingleEditor(p);
    }

    protected void calculateChecksum(Patch p, int start, int end, int ofs) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += p.sysex[i];
        }
        sum += 0xA5;
        p.sysex[ofs] = (byte) (sum % 128);
    }

    public Patch createNewPatch() {
        byte[] sysex = new byte[D50Constants.PATCH_SIZE];
        System.arraycopy(D50Constants.SYSEX_HEADER, 0, sysex, 0,
                D50Constants.SYSEX_HEADER_SIZE);
        sysex[sysex.length - 1] = (byte) 0xF7;
        Patch p = new Patch(sysex, this);
        setPatchName(p, "New Patch");
        calculateChecksum(p);
        return p;
    }

    protected void setPatchName(Patch p, String name) {
        D50StringHandler.setName(p, name, D50Constants.PATCH_NAME_START,
                D50Constants.PATCH_NAME_SIZE);
        calculateChecksum(p);
        sendPatch(p);
    }

    protected String getPatchName(Patch p) {
        return D50StringHandler.getName(p, D50Constants.PATCH_NAME_START,
                D50Constants.PATCH_NAME_SIZE);
    }
}
