/*
 * JSynthlib - generic bank patch, implementing collection of Patch objects
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

import core.ErrorMsg;
import core.DriverUtil;
import core.AppConfig;
import core.IBankPatch;
import core.BankDriver;
import core.IPatch;
import core.Patch;
import core.Device;
import core.IPatchDriver;
import core.JSLFrame;
import core.ISinglePatch;
import core.PatchTransferHandler;
import core.Driver;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.UnsupportedEncodingException;
import javax.swing.JOptionPane;
import javax.sound.midi.SysexMessage;

public class BankPatch extends Patch {
    private Patch[] mPatches;
    private String mName;

    public BankPatch(Driver iBankDriver, String iID) {
        super(new byte[16], iBankDriver);
        mPatches = new Patch[getNumPatches()];
        mName = "Unnamed Bank";
        try {
            sysex = iID.getBytes("ISO-8859-1");
        } catch (Exception e) {
        }
    }

    // IPatch interface methods
    public String getPatchHeader() {
        return DriverUtil.getPatchHeader(mPatches[0].getByteArray());
    }

    public byte[] updateSysex() {
        int i, j, l = 0;
        byte[] msg = new byte[calcSize()];
        for (i = 0; i < getNumPatches(); i++)
            if (mPatches[i] != null) {
                byte[] p = mPatches[i].getByteArray();
                for (j = 0; j < p.length; j++)
                    msg[l + j] = p[j];
                l += p.length;
            }
        return msg;
    }

    public int calcSize() {
        int i, l = 0;
        for (i = 0; i < getNumPatches(); i++)
            if (mPatches[i] != null)
                l += mPatches[i].getByteArray().length;
        return l;
    }

    public void useSysexFromPatch(IPatch ip) {
        throw new IllegalArgumentException();
    }

    public void putSingle(Patch singlePatch, int patchNum) {
        mPatches[patchNum] = singlePatch;
        updateSysex();
    }

    public Patch getSingle(int patchNum) {
        Patch single = mPatches[patchNum];
        if (single.getDriver() == null)
            single.setDriver();
        return single;
    }

    public String getBankName() {
        return mName;
    }

    public void setBankName(String s) {
        mName = s;
    }
}
