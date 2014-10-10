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
package org.jsynthlib.core.guiaction;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;

public class SetMidiDeviceAction extends AbstractGuiAction {

    private String name;
    private String comboBoxName;

    public SetMidiDeviceAction(FrameFixture testFrame, String comboBoxName, String name) {
        super(testFrame);
        this.comboBoxName = comboBoxName;
        this.name = name;
    }

    @Override
    public void perform() {
        DialogFixture preferencesDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = preferencesDialog.tabbedPane();
        tabbedPane.selectTab("MIDI");
        preferencesDialog.checkBox(
                new GenericTypeMatcher<JCheckBox>(JCheckBox.class) {

                    @Override
                    protected boolean isMatching(JCheckBox component) {
                        return "Enable MIDI Interface".equals(component
                                .getActionCommand());
                    }
                }).check();
        JComboBoxFixture outBox = preferencesDialog.comboBox(comboBoxName);

        JComboBox outComponent = outBox.component();
        int outCount = outComponent.getItemCount();
        for (int i = 0; i < outCount; i++) {
            String value = outComponent.getItemAt(i).toString();
            if (name.equals(value)) {
                outBox.selectItem(i);
            }
        }

        closeDialog(preferencesDialog);
    }

}
