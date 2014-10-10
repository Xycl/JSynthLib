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
import javax.swing.JDialog;
import javax.swing.JRadioButton;

import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.jsynthlib.core.GuiHandler.ISearchHandler;
import org.jsynthlib.core.GuiHandler.SearchFields;
import org.jsynthlib.core.TitleFinder.FrameWrapper;

public class SearchLibraryAction extends AbstractGuiAction {

    private FrameWrapper library;
    private SearchHandler searchHandler;

    public SearchLibraryAction(FrameFixture testFrame,
            FrameWrapper library2) {
        super(testFrame);
        this.library = library2;
    }

    @Override
    public void perform() {
        // Set focus
        library.table().click();
        clickMenuItem("Search...");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        DialogFixture dialog =
                testFrame
                        .dialog(new GenericTypeMatcher<JDialog>(JDialog.class) {

                            @Override
                            protected boolean isMatching(JDialog component) {
                                return "Search Library".equals(component
                                        .getTitle()) && component.isShowing();
                            }
                        });
        searchHandler = new SearchHandler(dialog);
    }

    public ISearchHandler getSearchHandler() {
        return searchHandler;
    }

    private class SearchHandler implements ISearchHandler {

        private DialogFixture dialog;

        public SearchHandler(DialogFixture dialog) {
            this.dialog = dialog;
        }

        @Override
        public void cancel() {
            dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

                @Override
                protected boolean isMatching(JButton component) {
                    return "Cancel".equals(component.getText().trim());
                }
            }).click();
        }

        @Override
        public void findFirst(String searchString) {
            dialog.textBox().deleteText();
            dialog.textBox().enterText(searchString);
            dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

                @Override
                protected boolean isMatching(JButton component) {
                    return "Find First".equals(component.getText().trim());
                }
            }).click();
        }

        @Override
        public void findNext() {
            dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

                @Override
                protected boolean isMatching(JButton component) {
                    return "Find Next".equals(component.getText().trim());
                }
            }).click();
        }

        @Override
        public void setField(final SearchFields field) {
            dialog.radioButton(
                    new GenericTypeMatcher<JRadioButton>(JRadioButton.class) {

                        @Override
                        protected boolean isMatching(JRadioButton component) {
                            switch (field) {
                            case PATCH_NAME:
                                return "Patch Name".equals(component.getText());
                            case FIELD1:
                                return "Field 1".equals(component.getText());
                            case FIELD2:
                                return "Field 2".equals(component.getText());
                            case COMMENT:
                                return "Comment".equals(component.getText());
                            case ALL_FIELDS:
                                return "All Fields".equals(component.getText());
                            default:
                                return false;
                            }
                        }
                    }).check();
        }

    }

}
