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
package org.jsynthlib.synthdrivers.RolandD50;

import javax.swing.JOptionPane;

import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

public class D50BankDriver extends XMLBankDriver {

    private int d50AddressOffset;
    private int d50ReverbDataOffset;
    private byte[] d50ReverbData;

    public D50BankDriver(XmlBankDriverDefinition driverSpec) {
        super(driverSpec);
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append("Perform a Bulk dump for the ");
        sb.append(toString());
        sb.append(" by pressing the \"B.Dump\" button whie holding down \"Data Transfer\".\n\nPress OK when D-50 says \"Complete.\"");
        PopupHandlerProvider.get().showMessage(PatchEdit.getInstance(),
                sb.toString(), "Get Patch", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    protected BankPatch createNewPatch() {
        BankPatch newPatch = super.createNewPatch();
        System.arraycopy(d50ReverbData, 0, newPatch.sysex, d50ReverbDataOffset,
                d50ReverbData.length);
        calculateChecksum(newPatch);
        return newPatch;
    }

    @Override
    public void storePatch(Patch bank, int bankNum, int patchNum) {
        byte[] buf = {
                0x02, 0x00, 0x00 };
        System.arraycopy(buf, 0, bank.sysex, d50AddressOffset, buf.length);
        super.storePatch(bank, bankNum, patchNum);
    }

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        // TODO Auto-generated method stub
        super.putPatch(bank, single, patchNum);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        Patch patch = getSingleDriver().createPatch();
        int bankHeaderSize = 8;
        int bankFooterSize = 2;
        int patchDataSize = 452;
        int i = 0;
        int j = 0;
        while (j < patchDataSize) {
            if (bank.sysex[i] == (byte) 0xF0) {
                i += bankHeaderSize;
            } else if (bank.sysex[i + bankFooterSize - 1] == (byte) 0xF7) {
                i += bankFooterSize;
            }
            patch.sysex[j] = bank.sysex[i];
            j++;
            i++;
        }
        return patch;
    }

    public int getD50ReverbDataOffset() {
        return d50ReverbDataOffset;
    }

    public void setD50ReverbDataOffset(int d50ReverbDataOffset) {
        this.d50ReverbDataOffset = d50ReverbDataOffset;
    }

    public byte[] getD50ReverbData() {
        return d50ReverbData;
    }

    public void setD50ReverbData(byte[] d50ReverbData) {
        this.d50ReverbData = d50ReverbData;
    }

    public int getD50AddressOffset() {
        return d50AddressOffset;
    }

    public void setD50AddressOffset(int d50AddressOffset) {
        this.d50AddressOffset = d50AddressOffset;
    }

}
