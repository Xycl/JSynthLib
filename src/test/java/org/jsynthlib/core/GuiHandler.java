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
package org.jsynthlib.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.apache.log4j.Logger;
import org.fest.swing.core.GenericTypeMatcher;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTableFixture;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.guiaction.AbstractGuiAction.IPopupListener;
import org.jsynthlib.core.guiaction.CleanLibraryAction;
import org.jsynthlib.core.guiaction.CloseAllEditorPopupsAction;
import org.jsynthlib.core.guiaction.CloseAllStorePopupsAction;
import org.jsynthlib.core.guiaction.CloseDialogAction;
import org.jsynthlib.core.guiaction.CloseFrameAction;
import org.jsynthlib.core.guiaction.CloseLibraryAction;
import org.jsynthlib.core.guiaction.CloseStorePatchDialogAction;
import org.jsynthlib.core.guiaction.CutCopyPastePatchAction;
import org.jsynthlib.core.guiaction.DeleteDupsAction;
import org.jsynthlib.core.guiaction.DragNDropAction;
import org.jsynthlib.core.guiaction.GetBanksForDriverAction;
import org.jsynthlib.core.guiaction.GetDriversForClassAction;
import org.jsynthlib.core.guiaction.InstallDeviceAction;
import org.jsynthlib.core.guiaction.IsDeviceInstalledAction;
import org.jsynthlib.core.guiaction.NewPatchAction;
import org.jsynthlib.core.guiaction.OpenDialogAction;
import org.jsynthlib.core.guiaction.OpenHelpDialogAction;
import org.jsynthlib.core.guiaction.OpenLibraryAction;
import org.jsynthlib.core.guiaction.OpenPatchEditorAction;
import org.jsynthlib.core.guiaction.OpenPreferencesDialogAction;
import org.jsynthlib.core.guiaction.OpenStoreEditorAction;
import org.jsynthlib.core.guiaction.PlayNoteAction;
import org.jsynthlib.core.guiaction.SaveLibraryAction;
import org.jsynthlib.core.guiaction.SearchLibraryAction;
import org.jsynthlib.core.guiaction.SelectLibraryFrameAction;
import org.jsynthlib.core.guiaction.SendPatchAction;
import org.jsynthlib.core.guiaction.SetMidiDeviceAction;
import org.jsynthlib.core.guiaction.SetPatchStoreValuesAction;
import org.jsynthlib.core.guiaction.SetPlayNoteValues;
import org.jsynthlib.core.guiaction.SetTableCellValueAction;
import org.jsynthlib.core.guiaction.SortLibraryAction;
import org.jsynthlib.core.guiaction.UninstallDeviceAction;
import org.jsynthlib.device.model.IDriver;

public class GuiHandler {

    public static final String NO_BANK = "no-bank";
    public static final String RESET_BANK = "reset-bank";
    public static final String RESET_PATCH = "reset-patch";

    public enum SortFields {
        PATCH_NAME, SYNTH_NAME, PATCH_TYPE, FIELD1, FIELD2
    }

    public enum SearchFields {
        PATCH_NAME, FIELD1, FIELD2, COMMENT, ALL_FIELDS
    }

    public interface ISearchHandler {
        void cancel();

        void findFirst(String searchString);

        void findNext();

        void setField(SearchFields field);
    }

    public static final int SYNTH = 0;
    public static final int TYPE = 1;
    public static final int PATCH_NAME = 2;
    public static final int FIELD1 = 3;
    public static final int FIELD2 = 4;
    public static final int COMMENT = 5;
    private final transient Logger log = Logger.getLogger(getClass());
    private final FrameFixture testFrame;

    public GuiHandler(FrameFixture testFrame) {
        this.testFrame = testFrame;
    }

    public void setTestMidiDevices() {
        setMidiOutDevice("Midi Output");
        setMidiInDevice("Midi Input");
    }

    public void setMidiOutDevice(String name) {
        setMidiDevice("cbOut", name);
    }

    public void setMidiInDevice(String name) {
        setMidiDevice("cbIn", name);
    }

    public void setMidiDevice(String comboBoxName, String name) {
        new SetMidiDeviceAction(testFrame, comboBoxName, name).perform();
    }

    public void playNote(JTableFixture table) {
        new PlayNoteAction(testFrame, table).perform();
    }

    public void sortLibrary(FrameWrapper library, SortFields field) {
        new SortLibraryAction(testFrame, library, field).perform();
    }

    public void cutCopyPastePatch(FrameWrapper library, int row, int col,
            FrameWrapper library2, boolean copy) {
        new CutCopyPastePatchAction(testFrame, library, row, col, library2,
                copy).perform();
    }

    public ISearchHandler openSearchDialog(FrameWrapper library) {
        SearchLibraryAction action =
                new SearchLibraryAction(testFrame, library);
        action.perform();
        return action.getSearchHandler();
    }

    public void setPlayNoteValues(int noteValue, int velocityValue,
            int durationValue) {
        new SetPlayNoteValues(testFrame, noteValue, velocityValue,
                durationValue).perform();
    }

    public void addPatchMetaData(FrameWrapper library, int row, int field,
            String data) {
        new SetTableCellValueAction(testFrame, library.table(), row, field,
                data).perform();
    }

    public void setPatchName(FrameWrapper bank, int row, int col, String name) {
        new SetTableCellValueAction(testFrame, bank.table(), row, col, name)
                .perform();
    }

    public String deleteDups() {
        DeleteDupsAction action = new DeleteDupsAction(testFrame);
        action.perform();
        return action.getPopupMsg();
    }

    public FrameWrapper openLibrary() throws InterruptedException {
        OpenLibraryAction action = new OpenLibraryAction(testFrame, null);
        action.perform();
        return action.getFixture();
    }

    public Map<String, List<String>> getBanksForDriver(DialogFixture dialog) {
        GetBanksForDriverAction action =
                new GetBanksForDriverAction(testFrame, dialog);
        action.perform();
        return action.getMap();
    }

    public List<Class<? extends IDriver>> getDriversForDevice(String deviceName) {
        GetDriversForClassAction action =
                new GetDriversForClassAction(testFrame, deviceName);
        action.perform();
        return action.getList();
    }

    public void newPatch(FrameWrapper libraryFrame, String deviceName,
            Class<?> driverClass, final IPopupListener listener) {
        NewPatchAction action =
                new NewPatchAction(testFrame, libraryFrame, deviceName,
                        driverClass, listener);
        action.perform();
    }

    public Map<String, List<String>> getPatchStoreOptions(JTableFixture table) {
        HashMap<String, List<String>> map = new HashMap<String, List<String>>();
        DialogFixture dialogFixture = null;
        try {
            OpenStoreEditorAction action =
                    new OpenStoreEditorAction(testFrame, table);
            action.perform();
            dialogFixture = action.getFrame();
            if (dialogFixture != null) {
                JComboBoxFixture bankComboBox =
                        dialogFixture.comboBox("bankCb");
                if (bankComboBox != null && bankComboBox.target.isEnabled()) {
                    for (int i = 0; i < bankComboBox.target.getItemCount(); i++) {
                        String bank =
                                bankComboBox.target.getItemAt(i).toString();
                        List<String> patchNumList = map.get(bank);
                        if (patchNumList == null) {
                            patchNumList = new ArrayList<String>();
                            map.put(bank, patchNumList);
                        }
                        JComboBoxFixture patchNumComboBox =
                                dialogFixture.comboBox("patchNumCb");
                        if (patchNumComboBox != null
                                && patchNumComboBox.target.isEnabled()) {
                            for (int j = 0; j < patchNumComboBox.target
                                    .getItemCount(); j++) {
                                String patchNum =
                                        patchNumComboBox.target.getItemAt(j)
                                                .toString();
                                patchNumList.add(patchNum);
                            }
                        }
                    }
                } else {
                    List<String> patchNumList = map.get(NO_BANK);
                    if (patchNumList == null) {
                        patchNumList = new ArrayList<String>();
                        map.put(NO_BANK, patchNumList);
                    }
                    JComboBoxFixture patchNumComboBox =
                            dialogFixture.comboBox("patchNumCb");
                    if (patchNumComboBox != null
                            && patchNumComboBox.target.isEnabled()) {
                        for (int j = 0; j < patchNumComboBox.target
                                .getItemCount(); j++) {
                            String patchNum =
                                    patchNumComboBox.target.getItemAt(j)
                                            .toString();
                            patchNumList.add(patchNum);
                        }
                    }
                }
            }
        } finally {
            if (dialogFixture != null) {
                new CloseStorePatchDialogAction(testFrame, dialogFixture, false)
                        .perform();
            }
        }

        // TODO: Workaround for Novation Xio...
        new CloseAllStorePopupsAction(testFrame, new IPopupHandler() {

            @Override
            public void onPopup(String popupName) {
            }
        }).perform();
        return map;
    }

    public FrameWrapper openPatchEditor(JTableFixture table, int row, int col,
            final IPopupListener listener, boolean maximize) {
        OpenPatchEditorAction action =
                new OpenPatchEditorAction(testFrame, table, row, col, listener,
                        maximize);
        action.perform();
        return action.getFrame();
    }

    public void cleanLibrary(JTableFixture table) {
        new CleanLibraryAction(testFrame, table).perform();
    }

    public boolean uninstallDevice(String deviceName) {
        UninstallDeviceAction action =
                new UninstallDeviceAction(testFrame, deviceName);
        action.perform();
        return action.isUninstalledDriver();
    }

    public boolean isDeviceInstalled(String deviceName) {
        IsDeviceInstalledAction action =
                new IsDeviceInstalledAction(testFrame, deviceName);
        action.perform();
        return action.isFoundDevice();
    }

    public String installDevice(String manufacturer, String deviceName) {
        String fullDriverName = deviceName;
        if (!deviceName.endsWith(" Driver")) {
            fullDriverName = deviceName + " Driver";
        }
        if (isDeviceInstalled(fullDriverName)) {
            log.info("Driver already installed");
            return null;
        }
        InstallDeviceAction action = new InstallDeviceAction(testFrame, manufacturer, fullDriverName);
        action.perform();
        return action.getInfoText();
    }

    public void selectLibraryFrame(FrameWrapper library) {
        new SelectLibraryFrameAction(testFrame, library).perform();
    }

    public void closeLibrary(FrameWrapper library) {
        new CloseLibraryAction(testFrame, library).perform();
    }

    public DialogFixture openDialog(final String menuName) {
        OpenDialogAction action = new OpenDialogAction(testFrame, menuName);
        action.perform();
        return action.getDialog();
    }

    public DialogFixture openHelpDialog() {
        OpenHelpDialogAction action = new OpenHelpDialogAction(testFrame);
        action.perform();
        return action.getDialog();
    }

    public void closeDialog(DialogFixture dialog) {
        if (dialog != null && dialog.target.isShowing()) {
            new CloseDialogAction(testFrame, dialog).perform();
        }

    }

    public void closeFrame(FrameWrapper fixture, boolean save) {
        new CloseFrameAction(testFrame, fixture, save).perform();
    }

    public void saveLibrary(File patchTestFolder, String filename) {
        new SaveLibraryAction(testFrame, patchTestFolder, filename).perform();
    }

    public FrameWrapper openLibrary(File patchlib) {
        OpenLibraryAction action = new OpenLibraryAction(testFrame, patchlib);
        action.perform();
        return action.getFixture();
    }

    public List<JComboBoxFixture> getComboBoxFixtures(
            ContainerFixture<?> container) {
        ArrayList<JComboBoxFixture> list = new ArrayList<JComboBoxFixture>();
        int index = 0;
        while (true) {
            try {
                final int currentIndex = index;
                index++;
                JComboBoxFixture comboBox =
                        container.comboBox(new GenericTypeMatcher<JComboBox>(
                                JComboBox.class) {
                            private int tmp = 0;

                            @Override
                            protected boolean isMatching(JComboBox component) {
                                boolean retval = tmp == currentIndex;
                                tmp++;
                                return retval;
                            }
                        });
                list.add(comboBox);
            } catch (Exception e) {
                break;
            }
        }
        return list;
    }

    public List<JCheckBoxFixture> getCheckBoxFixtures(
            ContainerFixture<?> container) {
        ArrayList<JCheckBoxFixture> list = new ArrayList<JCheckBoxFixture>();
        int index = 0;
        while (true) {
            try {
                final int currentIndex = index;
                index++;
                JCheckBoxFixture checkBox =
                        container.checkBox(new GenericTypeMatcher<JCheckBox>(
                                JCheckBox.class) {
                            private int tmp = 0;

                            @Override
                            protected boolean isMatching(JCheckBox component) {
                                boolean retval = tmp == currentIndex;
                                tmp++;
                                return retval;
                            }
                        });
                list.add(checkBox);
            } catch (Exception e) {
                break;
            }
        }
        return list;
    }

    public DialogFixture openPreferencesDialog() {
        OpenPreferencesDialogAction action =
                new OpenPreferencesDialogAction(testFrame);
        action.perform();
        return action.getDialog();
    }

    public List<PopupContainer> storePatch(final JTableFixture table,
            final String bank, final String patchNum) {
        log.info("Storing patch " + bank + " -> " + patchNum);
        DialogFixture dialogFixture = null;
        try {
            OpenStoreEditorAction action =
                    new OpenStoreEditorAction(testFrame, table);
            action.perform();
            dialogFixture = action.getFrame();

            if (dialogFixture == null) {
                log.warn("Could not find store patch dialog!");
            } else {
                new SetPatchStoreValuesAction(testFrame, dialogFixture, bank,
                        patchNum).perform();
            }
        } finally {
            if (dialogFixture != null) {
                new CloseStorePatchDialogAction(testFrame, dialogFixture, true)
                        .perform();
            }
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        CloseAllStorePopupsAction closeAllPopupsAction =
                new CloseAllStorePopupsAction(testFrame, new IPopupHandler() {

                    @Override
                    public void onPopup(String popupName) {
                    }
                });
        closeAllPopupsAction.perform();
        return closeAllPopupsAction.getList();
    }

    public List<PopupContainer> sendPatch(final JTableFixture table,
            final int col, final int row) {
        new SendPatchAction(testFrame, table, col, row).perform();

        // TODO: Workaround for Nova1.
        CloseAllEditorPopupsAction action =
                new CloseAllEditorPopupsAction(testFrame, new IPopupHandler() {

                    @Override
                    public void onPopup(String popupName) {
                        if ("Hide Nova1 Patch Sender".equals(popupName)) {
                            sendPatch(table, col, row);
                        }
                    }
                });
        action.perform();
        return action.getList();
    }

    public void dragNdropPatch(FrameWrapper bank1, int row, int col,
            int destRow, int destCol) {
        new DragNDropAction(testFrame, bank1, row, col, destRow, destCol)
                .perform();
    }

    public void restorePatchStoreDialog(JTableFixture table) {
        DialogFixture dialogFixture = null;
        try {
            OpenStoreEditorAction action =
                    new OpenStoreEditorAction(testFrame, table);
            action.perform();
            dialogFixture = action.getFrame();
            if (dialogFixture != null) {
                new SetPatchStoreValuesAction(testFrame, dialogFixture,
                        RESET_BANK, RESET_PATCH).perform();
            }
        } finally {
            if (dialogFixture != null) {
                new CloseStorePatchDialogAction(testFrame, dialogFixture, true)
                        .perform();
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

            CloseAllStorePopupsAction closeAllPopupsAction =
                    new CloseAllStorePopupsAction(testFrame,
                            new IPopupHandler() {

                                @Override
                                public void onPopup(String popupName) {
                                }
                            });
            closeAllPopupsAction.perform();
        }
    }
}
