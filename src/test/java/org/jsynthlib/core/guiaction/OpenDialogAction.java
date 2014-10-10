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

import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;

public class OpenDialogAction extends AbstractGuiAction {

    private String menuName;
    private DialogFixture dialog;

    public OpenDialogAction(FrameFixture testFrame, String menuName) {
        super(testFrame);
        this.menuName = menuName;
    }

    @Override
    public void perform() {
        clickMenuItem(menuName);
        dialog = testFrame.dialog();

    }

    public DialogFixture getDialog() {
        return dialog;
    }

}
