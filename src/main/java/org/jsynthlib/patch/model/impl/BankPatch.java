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
package org.jsynthlib.patch.model.impl;

import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IDriver;

public class BankPatch extends Patch {

    private static final long serialVersionUID = 1L;

    private Patch[] mPatches;
    private String mName;

    public BankPatch() {
        super();
        sysex = new byte[16];
        mName = "Unnamed Bank";
    }

    public void setIID(String iID) {
        try {
            sysex = iID.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(getClass()).warn(e.getMessage(), e);
        }
    }

    // Patch methods
    @Override
    public String getPatchHeader() {
        return getDriverIdentifier().getPatchHeader(mPatches[0].getByteArray());
    }

    public byte[] updateSysex() {
        int i, j, l = 0;
        byte[] msg = new byte[calcSize()];
        for (i = 0; i < getNumPatches(); i++) {
            if (mPatches[i] != null) {
                byte[] p = mPatches[i].getByteArray();
                for (j = 0; j < p.length; j++) {
                    msg[l + j] = p[j];
                }
                l += p.length;
            }
        }
        return msg;
    }

    public int calcSize() {
        int i, l = 0;
        for (i = 0; i < getNumPatches(); i++) {
            if (mPatches[i] != null) {
                l += mPatches[i].getByteArray().length;
            }
        }
        return l;
    }

    @Override
    public void useSysexFromPatch(Patch ip) {
        throw new IllegalArgumentException();
    }

    public void putSingle(Patch singlePatch, int patchNum) {
        mPatches[patchNum] = singlePatch;
        updateSysex();
    }

    public Patch getSingle(int patchNum) {
        Patch single = mPatches[patchNum];
        if (single.getDriver() == null) {
            single.setDriver();
        }
        return single;
    }

    public String getBankName() {
        return mName;
    }

    public void setBankName(String s) {
        mName = s;
    }

    @Override
    public IBankDriver getDriver() {
        return (IBankDriver) super.getDriver();
    }

    @Override
    public void setDriver(IDriver driver) {
        super.setDriver(driver);
        mPatches = new Patch[getNumPatches()];
    }

    public final int getNumPatches() {
        return getDriver().getNumPatches();
    }

    public final int getNumColumns() {
        return getDriver().getNumColumns();
    }

    public final void put(Patch singlePatch, int patchNum) {
        if (getDriver().canHoldPatch(singlePatch)) {
            getDriver().putPatch(this, singlePatch,
                    patchNum);
        } else {
            JOptionPane.showMessageDialog(null,
                    "This type of patch does not fit in to this type of bank.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    public final void delete(int patchNum) {
        getDriver().deletePatch(this, patchNum);
    }

    public final Patch get(int patchNum) {
        return getDriver().getPatch(this, patchNum);
    }

    public final String getName(int patchNum) {
        return getDriver().getPatchName(this, patchNum);
    }

    public final void setName(int patchNum, String name) {
        getDriver().setPatchName(this, patchNum, name);
    }

}
