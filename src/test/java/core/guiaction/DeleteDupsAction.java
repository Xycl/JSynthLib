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

import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class DeleteDupsAction extends AbstractGuiAction {

    private String popupMsg;

    public DeleteDupsAction(FrameFixture testFrame) {
        super(testFrame);
    }

    @Override
    public void perform() {
        OpenDialogAction action =
                new OpenDialogAction(testFrame, "Delete Dups...");
        action.perform();
        DialogFixture dialog = action.getDialog();
        closeDialog(dialog);
        IPopupListener listener = new IPopupListener() {

            @Override
            public void onPopupDetected(DialogFixture dialog) {
                try {
                    popupMsg =
                            dialog.label(
                                    new GenericTypeMatcher<JLabel>(JLabel.class) {

                                        @Override
                                        protected boolean isMatching(
                                                JLabel component) {
                                            return component.getText() != null
                                                    && component
                                                            .getText()
                                                            .contains(
                                                                    "Patches and Scenes were deleted")
                                                    && component.isVisible();
                                        }
                                    }).text();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        };
        waitForPopups(listener);
    }

    public String getPopupMsg() {
        return popupMsg;
    }

}
