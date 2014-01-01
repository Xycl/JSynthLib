package core;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.fest.swing.fixture.ContainerFixture;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JComboBoxFixture;
import org.fest.swing.fixture.JTableFixture;

import core.guiaction.AbstractGuiAction.IPopupListener;
import core.guiaction.CleanLibraryAction;
import core.guiaction.CloseAllEditorPopupsAction;
import core.guiaction.CloseAllStorePopupsAction;
import core.guiaction.CloseDialogAction;
import core.guiaction.CloseFrameAction;
import core.guiaction.CloseLibraryAction;
import core.guiaction.CloseStorePatchDialogAction;
import core.guiaction.GetBanksForDriverAction;
import core.guiaction.GetDriversForClassAction;
import core.guiaction.InstallDeviceAction;
import core.guiaction.IsDeviceInstalledAction;
import core.guiaction.NewPatchAction;
import core.guiaction.OpenDialogAction;
import core.guiaction.OpenHelpDialogAction;
import core.guiaction.OpenLibraryAction;
import core.guiaction.OpenPatchEditorAction;
import core.guiaction.OpenPreferencesDialogAction;
import core.guiaction.OpenStoreEditorAction;
import core.guiaction.SelectLibraryFrameAction;
import core.guiaction.SendPatchAction;
import core.guiaction.SetMidiDeviceAction;
import core.guiaction.SetPatchStoreValuesAction;
import core.guiaction.UninstallDeviceAction;

@SuppressWarnings("rawtypes")
public class GuiHandler {

    private final Logger log = Logger.getLogger(getClass());
    private FrameFixture testFrame;

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

    public JTableFixture openLibrary() throws InterruptedException {
        OpenLibraryAction action = new OpenLibraryAction(testFrame);
        action.perform();
        return action.getTableFixture();
    }

    public Map<String, List<String>> getBanksForDriver(DialogFixture dialog) {
        GetBanksForDriverAction action =
                new GetBanksForDriverAction(testFrame, dialog);
        action.perform();
        return action.getMap();
    }

    public List<Class> getDriversForDevice(String deviceName) {
        GetDriversForClassAction action =
                new GetDriversForClassAction(testFrame, deviceName);
        action.perform();
        return action.getList();
    }

    public void newPatch(String deviceName, Class<?> driverClass,
            final IPopupListener listener) {
        new NewPatchAction(testFrame, deviceName, driverClass, listener)
                .perform();
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
                for (int i = 0; i < bankComboBox.target.getItemCount(); i++) {
                    String bank = bankComboBox.target.getItemAt(i).toString();
                    List<String> patchNumList = map.get(bank);
                    if (patchNumList == null) {
                        patchNumList = new ArrayList<String>();
                        map.put(bank, patchNumList);
                    }
                    JComboBoxFixture patchNumComboBox =
                            dialogFixture.comboBox("patchNumCb");
                    if (patchNumComboBox != null) {
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

        // Due to novation Xio...
        new CloseAllStorePopupsAction(testFrame, new IPopupHandler() {
            
            @Override
            public void onPopup(String popupName) {
            }
        }).perform();
        return map;
    }

    public ContainerFixture openPatchEditor(JTableFixture table,
            final IPopupListener listener) {
        OpenPatchEditorAction action =
                new OpenPatchEditorAction(testFrame, table, listener);
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

    public void installDevice(String manufacturer, String driverName) {
        if (isDeviceInstalled(driverName)) {
            log.info("Driver already installed");
            return;
        }
        new InstallDeviceAction(testFrame, manufacturer, driverName).perform();
    }

    public void selectLibraryFrame() {
        new SelectLibraryFrameAction(testFrame).perform();
    }

    public void closeLibrary() {
        new CloseLibraryAction(testFrame).perform();
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

    public void closeDialog(String title) {
        new CloseDialogAction(testFrame, title).perform();
    }

    public void closeFrame(ContainerFixture fixture) {
        new CloseFrameAction(testFrame, fixture).perform();
    }

    public DialogFixture openPreferencesDialog() {
        OpenPreferencesDialogAction action =
                new OpenPreferencesDialogAction(testFrame);
        action.perform();
        return action.getDialog();
    }

    public JPanel findContentPanelRecursive(Container component) {
        Component[] components = component.getComponents();
        for (Component child : components) {
            if (child instanceof JPanel) {
                JPanel panel = (JPanel) child;
                if (panel.getName().equals("null.contentPane")) {
                    return panel;
                }
            } else if (child instanceof Container) {
                return findContentPanelRecursive((Container) child);
            }
        }
        return null;
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
}
