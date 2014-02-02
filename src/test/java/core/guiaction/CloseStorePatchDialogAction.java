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

import javax.swing.JButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class CloseStorePatchDialogAction extends AbstractGuiAction {

    private boolean send;
    private DialogFixture fixture;

    public CloseStorePatchDialogAction(FrameFixture testFrame,
            DialogFixture fixture, boolean send) {
        super(testFrame);
        this.fixture = fixture;
        this.send = send;
    }

    @Override
    public void perform() {
        fixture.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                if (send) {
                    return "Store...".equals(component.getText());
                } else {
                    return "Cancel".equals(component.getText());
                }
            }
        }).click();
    }
}
