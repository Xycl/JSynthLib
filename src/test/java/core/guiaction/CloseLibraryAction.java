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
import javax.swing.JDialog;
import javax.swing.UIManager;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.FrameFixture;

import core.TitleFinder.FrameWrapper;

public class CloseLibraryAction extends AbstractGuiAction {

    private FrameWrapper fixture;

    public CloseLibraryAction(FrameFixture testFrame,
            FrameWrapper library) {
        super(testFrame);
        this.fixture = library;
    }

    @Override
    public void perform() {
        final String noOption =
                (String) UIManager.get("OptionPane.noButtonText");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    testFrame
                            .dialog(new GenericTypeMatcher<JDialog>(
                                    JDialog.class) {

                                @Override
                                protected boolean isMatching(JDialog component) {
                                    return component.getTitle().contains(
                                            "Unsaved Data")
                                            && component.isVisible();
                                }
                            })
                            .button(new GenericTypeMatcher<JButton>(
                                    JButton.class) {

                                @Override
                                protected boolean isMatching(JButton component) {
                                    return noOption.equals(component.getText());
                                }
                            }).click();
                } catch (Exception e) {
                    log.info("No popup when closing library");
                }
            }
        }).start();

        log.info("Closing library frame");
        try {
            closeFrame(fixture, false);
        } catch (InterruptedException e) {
        }
    }

}
