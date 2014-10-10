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

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.jsynthlib.core.TitleFinder.FrameWrapper;

public abstract class AbstractGuiAction {

    protected final transient Logger log = Logger.getLogger(getClass());
    protected final FrameFixture testFrame;

    public AbstractGuiAction(FrameFixture testFrame) {
        this.testFrame = testFrame;
    }

    public abstract void perform();

    public DialogFixture openPreferencesDialog() {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals("Preferences...");
            }
        }).click();

        return testFrame.dialog();
    }

    public void closeDialog(final DialogFixture dialog) {
        final String yesOption =
                (String) UIManager.get("OptionPane.yesButtonText");
        dialog.button(new GenericTypeMatcher<JButton>(JButton.class) {

            @Override
            protected boolean isMatching(JButton component) {
                return "OK".equals(component.getText().trim())
                        || yesOption.equals(component.getText())
                        || "Close".equals(component.getText().trim());
            }
        }).click();
    }

    public void closeFrame(FrameWrapper fixture, final boolean save)
            throws InterruptedException {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                try {
                    testFrame
                            .dialog(new GenericTypeMatcher<JDialog>(
                                    JDialog.class) {

                                @Override
                                protected boolean isMatching(JDialog component) {
                                    return component.getTitle().contains(
                                            "Save Changes?")
                                            && component.isVisible();
                                }
                            })
                            .button(new GenericTypeMatcher<JButton>(
                                    JButton.class) {

                                @Override
                                protected boolean isMatching(JButton component) {
                                    if (save) {
                                        return component.getText().contains(
                                                "Keep");
                                    } else {
                                        return component.getText().contains(
                                                "Revert");
                                    }
                                }
                            }).click();
                } catch (Exception e) {
                    // log.warn(e.getMessage(), e);
                }
            }
        }).start();
        log.info("Closing Frame " + fixture.getTitle());
        fixture.close();
    }

    public interface IPopupListener {
        void onPopupDetected(DialogFixture dialog);
    }

    protected void waitForPopups(final IPopupListener listener) {
        final Semaphore semaphore = new Semaphore(0);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {
                        Thread.sleep(500);
                        DialogFixture patchEditDialog = testFrame.dialog();
                        log.info("Found dialog when opening editor");
                        listener.onPopupDetected(patchEditDialog);
                        log.info("Closing open editor dialog");
                        closeDialog(patchEditDialog);
                    }
                } catch (Exception e) {
                } finally {
                    semaphore.release();
                }
            }
        }).start();

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
        }
    }

    protected DialogFixture openNewPatchDialogAndSelectDevice(String deviceName) {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals("New Patch...");
            }
        }).click();

        DialogFixture dialog = testFrame.dialog();

        JComboBoxFixture comboBox =
                dialog.comboBox(new GenericTypeMatcher<JComboBox>(
                        JComboBox.class) {

                    @Override
                    protected boolean isMatching(JComboBox component) {
                        return "deviceCb".equals(component.getName());
                    }
                });
        JComboBox component = comboBox.component();
        if (component.isEnabled()) {
            for (int i = 0; i < component.getItemCount(); i++) {
                String item = component.getItemAt(i).toString();
                if (item.contains(deviceName)) {
                    comboBox.selectItem(i);
                }
            }
        }

        return dialog;
    }

    protected interface ComboBoxMatcher {
        boolean matches(Object item);
    }

    protected void setComboBoxValue(JComboBoxFixture comboBox,
            ComboBoxMatcher itemMatcher) {
        final JComboBox component = comboBox.component();

        if (component.isEnabled()) {
            for (int i = 0; i < component.getItemCount(); i++) {
                Object item = component.getItemAt(i);
                if (itemMatcher.matches(item)) {
                    comboBox.selectItem(i);
                    if (component.getSelectedIndex() != i) {
                        final int j = i;
                        GuiActionRunner.execute(new GuiQuery<Object>() {
                            public Object executeInEDT() {
                                component.setSelectedIndex(j);
                                return null;
                            }
                        });
                    }
                    break;
                }
            }
        }
    }

    protected void clickMenuItem(final String menuitem) {
        testFrame.menuItem(new GenericTypeMatcher<JMenuItem>(JMenuItem.class) {

            @Override
            protected boolean isMatching(JMenuItem component) {
                return component.getActionCommand().equals(menuitem);
            }
        }).click();
    }

    protected FrameWrapper getOpenedFrame(List<FrameWrapper> before,
            List<FrameWrapper> after) {
        after.removeAll(before);
        Iterator<FrameWrapper> iterator = after.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }
}
