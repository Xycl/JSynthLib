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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

public class GetBanksForDriverAction extends AbstractGuiAction {

    private DialogFixture dialog;
    private HashMap<String, List<String>> map;

    public GetBanksForDriverAction(FrameFixture testFrame,
            DialogFixture dialog) {
        super(testFrame);
        this.dialog = dialog;
        map = new HashMap<String, List<String>>();
    }

    @Override
    public void perform() {
        JComboBoxFixture bankCb =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "bankCb".equals(component.getName());
                    }
                });

        JComboBox component = bankCb.component();
        for (int i = 0; i < component.getItemCount(); i++) {
            ArrayList<String> list = new ArrayList<String>();
            map.put(component.getItemAt(i).toString(), list);
            bankCb.selectItem(i);
            JComboBoxFixture patchNumCb =
                    dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                            JComboBox.class) {

                        @Override
                        protected boolean isMatching(JComboBox component) {
                            return "patchNumCb".equals(component.getName());
                        }
                    });
            JComboBox patchNumComponent = patchNumCb.component();
            for (int j = 0; j < patchNumComponent.getItemCount(); j++) {
                list.add(patchNumComponent.getItemAt(i).toString());
            }
        }
    }

    public HashMap<String, List<String>> getMap() {
        return map;
    }

}
