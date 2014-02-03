/*
 * JSynthlib - Single Driver for Yamaha SY77
 * ========================================
 * @version $Id$
 * @author  Vladimir Avdonin
 *
 * Copyright (C) 2011 vldmrrr@yahoo.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package synthdrivers.YamahaSY77;

import org.jsynthlib.gui.desktop.JSLFrame;

import core.Driver;
import core.Patch;
import core.SysexHandler;

public class YamahaSY77KSeqDriver extends Driver {
    static final byte[] initNSeqSysex = {
            (byte) 0xf0, 0x43, 0x00, 0x0a, 0x08, 0x0a, 0x4c, 0x4d, 0x20, 0x20,
            0x4e, 0x53, 0x45, 0x51, 0x20, 0x20, 0x46, 0x30, 0x30, 0x30, 0x37,
            0x44, 0x42, 0x33, 0x33, 0x43, 0x32, 0x45, 0x46, 0x38, 0x30, 0x33,
            0x30, 0x30, 0x46, 0x35, 0x46, 0x32, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38,
            0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x46,
            0x38, 0x46, 0x38, 0x46, 0x38, 0x46, 0x38, 0x33, (byte) 0xf7 };

    public YamahaSY77KSeqDriver() {
        super("KSEQ", YamahaSY77Device.author);
        sysexID = "F043**0A****4C4D20204B5345512020";
        patchNameStart = 0;
        patchNameSize = 0;
        deviceIDoffset = 2;
        numSysexMsgs = 1;
        sysexRequestDump =
                new SysexHandler("F0 43 @@ 0A 4C 4D 20 20 4B 53 45 51 20 20 f7");
        bankNumbers = new String[] {
            "Sequence Memory" };
        patchNumbers = new String[] {
            "Sequence Memory" };
    }

    public void storePatch(Patch p, int bankNum, int patchNum) {
        sendPatch(p);
    }

    public void sendPatch(Patch p) {
        sendPatchWorker(p);
    }

    public Patch createNewPatch() {
        Patch p = new Patch(initNSeqSysex, this);
        return p;
    }

    public void requestPatchDump(int bankNum, int patchNum) {
        send(sysexRequestDump.toSysexMessage(getChannel() + 0x20));
    }

    public void calculateChecksum(Patch p) {
        // We do not edit these things
    }

}
