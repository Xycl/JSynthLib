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

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JLabelFixture;

import core.IPopupHandler;
import core.PopupContainer;

public class CloseAllStorePopupsAction extends AbstractGuiAction {

    private IPopupHandler handler;
    private ArrayList<PopupContainer> list;

    public CloseAllStorePopupsAction(FrameFixture testFrame,
            final IPopupHandler handler) {
        super(testFrame);
        this.handler = handler;
        list = new ArrayList<PopupContainer>();
    }

    @Override
    public void perform() {

        int foundPopups = 0;
        try {
            while (true) {
                Thread.sleep(100);
                DialogFixture dialog =
                        testFrame.dialog(new GenericTypeMatcher<JDialog>(
                                JDialog.class) {

                            @Override
                            protected boolean isMatching(JDialog component) {
                                return !component.getTitle().equals(
                                        "Store Sysex Data")
                                        && component.isShowing();
                            }
                        });

                log.info("Found patch store popup. Closing...");
                list.add(getPopupContents(dialog));
                handler.onPopup(dialog.target.getTitle());

                dialog.close();
                if (dialog.target.isShowing()) {
                    log.warn("Patch Store dialog is still showing!");
                }

                foundPopups++;
            }
        } catch (Exception e) {
        }
        log.info("Found popups: " + foundPopups);
    }

    PopupContainer getPopupContents(DialogFixture dialog) {
        try {
            JLabelFixture label =
                    dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

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
            PopupContainer popupContainer = new PopupContainer();
            popupContainer.setTitle(dialog.target.getTitle());
            popupContainer.setContents(label.target.getText());
            return popupContainer;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return new PopupContainer();
        }
    }

    public ArrayList<PopupContainer> getList() {
        return list;
    }

}
