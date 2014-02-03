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

import javax.swing.JPanel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JTabbedPaneFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.gui.preferences.SynthConfigPanel;

public class IsDeviceInstalledAction extends AbstractGuiAction {

    private String deviceName;
    private boolean foundDevice;

    public IsDeviceInstalledAction(FrameFixture testFrame, String deviceName) {
        super(testFrame);
        this.deviceName = deviceName;
    }

    @Override
    public void perform() {
        log.info("Check driver " + deviceName);
        DialogFixture prefsDialog = openPreferencesDialog();
        JTabbedPaneFixture tabbedPane = prefsDialog.tabbedPane();
        tabbedPane.selectTab("Synth Driver");

        JTableFixture table =
                prefsDialog.panel(new GenericTypeMatcher<JPanel>(JPanel.class) {

                    @Override
                    protected boolean isMatching(JPanel component) {
                        if (component instanceof SynthConfigPanel) {
                            return true;
                        }
                        return false;
                    }
                }).table();

        foundDevice = false;
        String[][] contents = table.contents();
        for (int i = 0; i < contents.length; i++) {
            if (deviceName.contains(contents[i][1])) {
                foundDevice = true;
                break;
            }
        }

        closeDialog(prefsDialog);
    }

    public boolean isFoundDevice() {
        return foundDevice;
    }

}
