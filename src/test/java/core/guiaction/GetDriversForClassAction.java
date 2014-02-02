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

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;

public class GetDriversForClassAction extends AbstractGuiAction {

    private String deviceName;
    private ArrayList<Class<?>> list;

    public GetDriversForClassAction(FrameFixture testFrame, String deviceName) {
        super(testFrame);
        this.deviceName = deviceName;
        list = new ArrayList<Class<?>>();
    }

    @Override
    public void perform() {
        DialogFixture dialog = openNewPatchDialogAndSelectDevice(deviceName);

        JComboBoxFixture comboBox =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "driverCb".equals(component.getName());
                    }
                });

        JComboBox component = comboBox.component();
        for (int i = 0; i < component.getItemCount(); i++) {
            list.add(component.getItemAt(i).getClass());
        }

        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return component.getText().contains("Cancel");
            }
        }).click();
    }

    public ArrayList<Class<?>> getList() {
        return list;
    }

}
