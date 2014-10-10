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

import java.awt.Dialog;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.exception.ComponentLookupException;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;
import org.jsynthlib.core.IPopupHandler;
import org.jsynthlib.core.PopupContainer;

public class CloseAllEditorPopupsAction extends AbstractGuiAction {

    private final IPopupHandler handler;
    private final ArrayList<PopupContainer> list;
    public CloseAllEditorPopupsAction(FrameFixture testFrame, final IPopupHandler handler) {
        super(testFrame);
        this.handler = handler;
        list = new ArrayList<PopupContainer>();
    }

    @Override
    public void perform() {

        try {
            DialogFixture dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains(
                                    "Nova1 Patch Sender");
                        }
                    });
            dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

                @Override
                protected boolean isMatching(JButton component) {
                    return "Hide this".equals(component.getText());
                }
            }).click();

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());

            dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains(
                                    "Hide Nova1 Patch Sender");
                        }
                    });
            closeDialog(dialog);

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());
        } catch (Exception e) {
        }

        try {
            DialogFixture dialog =
                    testFrame.dialog(new GenericTypeMatcher<Dialog>(
                            Dialog.class) {

                        @Override
                        protected boolean isMatching(Dialog component) {
                            return component.getTitle().contains("Ensoniq ")
                                    && component.isShowing();
                        }
                    });
            closeDialog(dialog);

            list.add(getPopupContents(dialog));
            handler.onPopup(dialog.target.getTitle());
        } catch (Exception e) {
        }
    }

    PopupContainer getPopupContents(DialogFixture dialog) {
        PopupContainer popupContainer = new PopupContainer();
        try {
            popupContainer.setTitle(dialog.target.getTitle());
            JLabelFixture label = dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

                private boolean first = true;

                @Override
                protected boolean isMatching(JLabel arg0) {
                    boolean retval = first;
                    if (first) {
                        first = false;
                    }
                    return retval;
                }
            });

            popupContainer.setContents(label.target.getText());
        } catch (ComponentLookupException e) {
            log.warn("Could not find label in dialog.");
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new PopupContainer();
        }
        return popupContainer;
    }

    public ArrayList<PopupContainer> getList() {
        return list;
    }

}
