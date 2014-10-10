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

import javax.swing.JButton;
import javax.swing.JComboBox;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.jsynthlib.core.TitleFinder.FrameWrapper;

public class NewPatchAction extends AbstractGuiAction {

    private IPopupListener listener;
    private Class<?> driverClass;
    private String deviceName;
    private FrameWrapper libraryFrame;

    public NewPatchAction(FrameFixture testFrame, FrameWrapper libraryFrame, String deviceName,
            Class<?> driverClass, final IPopupListener listener) {
        super(testFrame);
        this.libraryFrame = libraryFrame;
        this.deviceName = deviceName;
        this.driverClass = driverClass;
        this.listener = listener;
    }

    @Override
    public void perform() {
        libraryFrame.moveToFront();
        libraryFrame.table().click();

        DialogFixture dialog = openNewPatchDialogAndSelectDevice(deviceName);

        JComboBoxFixture comboBox =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "driverCb".equals(component.getName());
                    }
                });

        final JComboBox component = comboBox.component();
        log.info("Driver to select: " + driverClass);
        if (component.isEnabled()) {
            setComboBoxValue(comboBox, new ComboBoxMatcher() {
                @Override
                public boolean matches(Object item) {
                    Class<?> klass = item.getClass();
                    return klass.equals(driverClass);
                }
            });
        }

        log.info("Selected item: " + component.getSelectedItem());

        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return component.getText().contains("Create");
            }
        }).click();

        waitForPopups(listener);

    }
}
