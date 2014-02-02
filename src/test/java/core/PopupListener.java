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
package core;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.JLabelFixture;

import core.guiaction.AbstractGuiAction.IPopupListener;

public class PopupListener implements IPopupListener {

    private List<PopupContainer> popups;

    public PopupListener() {
        super();
        this.popups = new ArrayList<PopupContainer>();
    }

    @Override
    public void onPopupDetected(DialogFixture dialog) {
        PopupContainer container = new PopupContainer();
        container.setTitle(dialog.target.getTitle());
        JLabelFixture label;
        try {
            label = dialog.label(new GenericTypeMatcher<JLabel>(JLabel.class) {

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
            container.setContents(label.target.getText());
        } catch (Exception e) {
            e.printStackTrace();
        }
        popups.add(container);
    }

    public List<PopupContainer> getPopups() {
        return popups;
    }
}
