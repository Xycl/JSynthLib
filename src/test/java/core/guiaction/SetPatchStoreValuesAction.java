/*
 * Copyright 2014 Pascal Collberg
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
package core.guiaction;

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

import core.GuiHandler;

public class SetPatchStoreValuesAction extends AbstractGuiAction {

    private String patchNum;
    private String bank;
    private DialogFixture dialogFixture;

    public SetPatchStoreValuesAction(FrameFixture testFrame,
            DialogFixture dialogFixture, final String bank,
            final String patchNum) {
        super(testFrame);
        this.dialogFixture = dialogFixture;
        if (!GuiHandler.NO_BANK.equals(bank)) {
            this.bank = bank;
        }
        this.patchNum = patchNum;
    }

    @Override
    public void perform() {
        if (bank != null) {
            JComboBoxFixture bankComboBox = dialogFixture.comboBox("bankCb");
            if (bank.equals(GuiHandler.RESET_BANK)) {
                bankComboBox.selectItem(0);
            } else {
                setComboBoxValue(bankComboBox, new ComboBoxMatcher() {

                    @Override
                    public boolean matches(Object item) {
                        return bank.equals(item.toString());
                    }
                });
            }
        }

        if (patchNum != null) {
            JComboBoxFixture patchNumComboBox =
                    dialogFixture.comboBox("patchNumCb");
            if (patchNum.equals(GuiHandler.RESET_PATCH)) {
                patchNumComboBox.selectItem(0);
            } else {
                setComboBoxValue(patchNumComboBox, new ComboBoxMatcher() {

                    @Override
                    public boolean matches(Object item) {
                        return patchNum.equals(item.toString());
                    }
                });
            }
        }
    }

}
