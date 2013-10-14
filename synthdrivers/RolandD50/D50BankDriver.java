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

import javax.swing.JOptionPane;

import core.BankDriver;
import core.Patch;
import core.PatchEdit;



public class D50BankDriver extends BankDriver {

    private static final int PATCH_COUNT = 8 * 8;



    public D50BankDriver() {
        super("Voice Bank", "Pascal Collberg", 8 * 8, 4);

        bankNumbers = new String[] {};
        patchNumbers = new String[PATCH_COUNT];
        for(int bank = 0; bank < 8; bank++) {
            for(int number = 0; number < 8; number++) {
                patchNumbers[bank * 8 + number] = "I" + Integer.toString(bank + 1) + "-" + Integer.toString(number + 1);
            }
        }
    }



    @Override
    protected void putPatch(Patch bank, Patch single, int patchNum) {
        // TODO Auto-generated method stub

    }



    @Override
    protected Patch getPatch(Patch bank, int patchNum) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    protected String getPatchName(Patch bank, int patchNum) {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    protected void setPatchName(Patch bank, int patchNum, String name) {
        // TODO Auto-generated method stub

    }



    public void requestPatchDump(int bankNum, int patchNum) {
        StringBuilder sb = new StringBuilder();
        sb.append("Perform a Bulk dump for the ");
        sb.append(toString());
        sb.append(" by pressing the \"B.Dump\" button whie holding down \"Data Transfer\".\n\nPress OK when D-50 says \"Complete.\"");
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), sb.toString(), "Get Patch", JOptionPane.WARNING_MESSAGE);
    }

}
