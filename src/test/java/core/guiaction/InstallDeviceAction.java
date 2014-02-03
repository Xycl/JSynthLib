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

import java.awt.Dialog;

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTreeFixture;
import org.jsynthlib.gui.DeviceAddDialog;

public class InstallDeviceAction extends AbstractGuiAction {

    private String driverName;
    private String manufacturer;

    public InstallDeviceAction(FrameFixture testFrame, String manufacturer,
            String driverName) {
        super(testFrame);
        this.manufacturer = manufacturer;
        this.driverName = driverName;
    }

    void closeDialog(final String dialogName) {
        DialogFixture dialog =
                testFrame.dialog(new GenericTypeMatcher<Dialog>(Dialog.class) {

                    @Override
                    protected boolean isMatching(Dialog component) {
                        return dialogName.equals(component.getTitle())
                                && component.isShowing();
                    }
                });
        closeDialog(dialog);
    }

    @Override
    public void perform() {
        DialogFixture prefsDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = prefsDialog.tabbedPane();
        tabbedPane.selectTab("Synth Driver");

        prefsDialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return "Add Device...".equals(component.getText());
            }
        }).click();

        DialogFixture driverDialog =
                testFrame.dialog(new GenericTypeMatcher<Dialog>(Dialog.class) {

                    @Override
                    protected boolean isMatching(Dialog component) {
                        if (component instanceof DeviceAddDialog
                                && component.isVisible()) {
                            return true;
                        }
                        return false;
                    }
                });
        JTreeFixture tree = driverDialog.tree();

        String separator = "/";
        if (driverName.contains(separator)) {
            separator = ";";
            tree.separator(separator);
        }

        tree.selectPath("Manufacturers" + separator + manufacturer + separator
                + driverName);

        closeDialog(driverDialog);

        closeDialog("Device Information");

        closeDialog(prefsDialog);
    }

}
