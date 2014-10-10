package org.jsynthlib.core.viewcontroller;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;
import org.jsynthlib.actions.AboutAction;
import org.jsynthlib.actions.CloseAction;
import org.jsynthlib.actions.CopyAction;
import org.jsynthlib.actions.CrossBreedAction;
import org.jsynthlib.actions.CutAction;
import org.jsynthlib.actions.DeleteAction;
import org.jsynthlib.actions.DeleteDuplicatesAction;
import org.jsynthlib.actions.DocsAction;
import org.jsynthlib.actions.EditAction;
import org.jsynthlib.actions.ExitAction;
import org.jsynthlib.actions.ExportAction;
import org.jsynthlib.actions.ExtractAction;
import org.jsynthlib.actions.GetAction;
import org.jsynthlib.actions.HomePageAction;
import org.jsynthlib.actions.ImportAction;
import org.jsynthlib.actions.ImportAllAction;
import org.jsynthlib.actions.LicenseAction;
import org.jsynthlib.actions.MonitorAction;
import org.jsynthlib.actions.NewAction;
import org.jsynthlib.actions.NewPatchAction;
import org.jsynthlib.actions.NewSceneAction;
import org.jsynthlib.actions.NextFaderAction;
import org.jsynthlib.actions.OpenAction;
import org.jsynthlib.actions.PasteAction;
import org.jsynthlib.actions.PlayAction;
import org.jsynthlib.actions.PrefsAction;
import org.jsynthlib.actions.PrevFaderAction;
import org.jsynthlib.actions.PrintAction;
import org.jsynthlib.actions.ReassignAction;
import org.jsynthlib.actions.SaveAction;
import org.jsynthlib.actions.SaveAsAction;
import org.jsynthlib.actions.SearchAction;
import org.jsynthlib.actions.SendAction;
import org.jsynthlib.actions.SendToAction;
import org.jsynthlib.actions.SortAction;
import org.jsynthlib.actions.StoreAction;
import org.jsynthlib.actions.TransferSceneAction;
import org.jsynthlib.actions.UpdateSceneAction;
import org.jsynthlib.actions.UpdateSelectedAction;
import org.jsynthlib.actions.UploadAction;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.MacUtils;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLWindowMenu;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.viewcontroller.MidiMonitor;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.AbstractLibraryFrame;
import org.jsynthlib.patch.viewcontroller.CompatibleFileDialog;
import org.jsynthlib.patch.viewcontroller.LibraryFrame;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

/**
 * Define Action classes
 * @version $Id: Actions.java 1182 2011-12-04 22:07:24Z chriswareham $
 */
public final class Actions {

    private static final Logger LOG = Logger.getLogger(Actions.class);
    // I hope 64bit is enough for a while.
    public static final long EN_ABOUT = 0x0000000000000001L;
    public static final long EN_COPY = 0x0000000000000002L;
    public static final long EN_CROSSBREED = 0x0000000000000004L;
    public static final long EN_CUT = 0x0000000000000008L;
    public static final long EN_DELETE = 0x0000000000000010L;
    public static final long EN_DELETE_DUPLICATES = 0x0000000000000020L;
    public static final long EN_DOCS = 0x0000000000000040L;
    public static final long EN_EDIT = 0x0000000000000080L;
    public static final long EN_EXIT = 0x0000000000000100L;
    public static final long EN_EXPORT = 0x0000000000000200L;
    public static final long EN_EXTRACT = 0x0000000000000400L;
    public static final long EN_GET = 0x0000000000000800L;
    public static final long EN_HOME_PAGE = 0x0000000000001000L;
    public static final long EN_IMPORT = 0x0000000000002000L;
    public static final long EN_IMPORT_ALL = 0x0000000000004000L;
    public static final long EN_LICENSE = 0x0000000000008000L;
    public static final long EN_MONITOR = 0x0000000000010000L;
    public static final long EN_NEW = 0x0000000000020000L;
    public static final long EN_NEW_PATCH = 0x0000000000040000L;
    public static final long EN_NEW_SCENE = 0x0000000000080000L;
    public static final long EN_NEXT_FADER = 0x0000000000100000L;
    public static final long EN_OPEN = 0x0000000000200000L;
    public static final long EN_PASTE = 0x0000000000400000L;
    public static final long EN_PLAY = 0x0000000000800000L;
    public static final long EN_PREFS = 0x0000000001000000L;
    public static final long EN_REASSIGN = 0x0000000002000000L;
    public static final long EN_SAVE = 0x0000000004000000L;
    public static final long EN_SAVE_AS = 0x0000000008000000L;
    public static final long EN_SEARCH = 0x0000000010000000L;
    public static final long EN_SEND = 0x0000000020000000L;
    public static final long EN_SEND_TO = 0x0000000040000000L;
    public static final long EN_SORT = 0x0000000080000000L;
    public static final long EN_STORE = 0x0000000100000000L;
    public static final long EN_TRANSFER_SCENE = 0x0000000200000000L;
    public static final long EN_UPLOAD = 0x0000000400000000L;
    public static final long EN_PREV_FADER = 0x0000000800000000L;
    public static final long EN_PRINT = 0x0000001000000000L;
    public static final long EN_UPDATE_SCENE = 0x0000002000000000L;
    public static final long EN_UPDATE_SELECTED = 0x0000004000000000L;

    /** All actions excluding ones which are always enabled. */
    public static final long EN_ALL = (// EN_ABOUT
            EN_COPY
                    | EN_CROSSBREED
                    | EN_CUT
                    | EN_DELETE
                    | EN_DELETE_DUPLICATES
                    // | EN_DOCS
                    | EN_EDIT
                    // | EN_EXIT
                    | EN_EXPORT
                    | EN_EXTRACT
                    | EN_GET
                    | EN_HOME_PAGE
                    | EN_IMPORT
                    | EN_IMPORT_ALL
                    // | EN_LICENSE
                    // | EN_MONITOR
                    // | EN_NEW
                    | EN_NEW_PATCH
                    // | EN_NEW_SCENE
                    // | EN_NEXT_FADER
                    // | EN_PREV_FADER
                    // | EN_OPEN
                    // | EN_PASTE : 'paste' needs special handling
                    | EN_PLAY
                    // | EN_PREFS
                    | EN_REASSIGN | EN_SAVE | EN_SAVE_AS | EN_SEARCH | EN_SEND
                    | EN_SEND_TO | EN_SORT | EN_STORE | EN_TRANSFER_SCENE
                    | EN_UPDATE_SCENE | EN_UPDATE_SELECTED | EN_UPLOAD);

    private static Action aboutAction;
    private static Action copyAction;
    private static Action crossBreedAction;
    private static Action cutAction;
    private static Action deleteAction;
    private static Action deleteDuplicatesAction;
    private static Action docsAction;
    private static Action editAction;
    /*
     * refered by PatchEdit
     */
    public static Action exitAction;
    private static Action exportAction;
    private static Action extractAction;
    private static Action getAction;
    private static Action homePageAction;
    private static Action importAction;
    private static Action importAllAction;
    private static Action licenseAction;
    private static Action monitorAction;
    private static Action newAction;
    private static Action newPatchAction;
    private static Action newSceneAction;
    private static Action nextFaderAction;
    private static Action prevFaderAction;
    private static Action openAction;
    private static Action pasteAction;
    private static Action playAction;
    private static Action prefsAction;
    private static Action printAction;
    private static Action reassignAction;
    private static Action saveAction;
    private static Action saveAsAction;
    private static Action searchAction;
    private static Action sendAction;
    private static Action sendToAction;
    private static Action sortAction;
    private static Action storeAction;
    private static Action transferSceneAction;
    private static Action updateSceneAction;
    private static Action updateSelectedAction;
    private static Action uploadAction;
    private static CloseAction closeAction;

    private static JPopupMenu menuPatchPopup;
    public static MidiMonitor midiMonitor;

    /** just for efficiency. */
    private static boolean isMac = MacUtils.isMac();

    // don't have to call constructor for Utility class.
    private Actions() {
    }

    public static void createActions() {
        Map<Action, Integer> mnemonics = new HashMap<Action, Integer>();
        AppConfig appConfig = JSynthLibInjector.getInstance(AppConfig.class);

        newAction = new NewAction(mnemonics);
        openAction = new OpenAction(mnemonics, appConfig);
        saveAction = new SaveAction(mnemonics);
        saveAsAction = new SaveAsAction(mnemonics);
        newSceneAction = new NewSceneAction(mnemonics);
        transferSceneAction = new TransferSceneAction(mnemonics);
        updateSceneAction = new UpdateSceneAction(mnemonics);
        sortAction = new SortAction(mnemonics);
        searchAction = new SearchAction(mnemonics);
        deleteDuplicatesAction = new DeleteDuplicatesAction(mnemonics);
        exitAction = new ExitAction(mnemonics);

        copyAction = new CopyAction(mnemonics);
        cutAction = new CutAction(mnemonics);
        pasteAction = new PasteAction(mnemonics);
        deleteAction = new DeleteAction(mnemonics);
        importAction = new ImportAction(mnemonics, appConfig);
        exportAction = new ExportAction(mnemonics, appConfig);
        importAllAction = new ImportAllAction(mnemonics, appConfig);
        sendAction = new SendAction(mnemonics);
        sendToAction = new SendToAction(mnemonics);
        // R. Wirski
        updateSelectedAction = new UpdateSelectedAction(mnemonics);
        printAction = new PrintAction(mnemonics);
        storeAction = new StoreAction(mnemonics);
        getAction = new GetAction(mnemonics);

        playAction = new PlayAction(mnemonics);
        editAction = new EditAction(mnemonics);
        reassignAction = new ReassignAction(mnemonics);
        crossBreedAction = new CrossBreedAction(mnemonics);
        newPatchAction = new NewPatchAction(mnemonics);
        extractAction = new ExtractAction(mnemonics);

        prefsAction = new PrefsAction(mnemonics);
        monitorAction = new MonitorAction(mnemonics);

        aboutAction = new AboutAction(mnemonics);
        docsAction = new DocsAction(mnemonics);
        licenseAction = new LicenseAction(mnemonics);
        homePageAction = new HomePageAction(mnemonics);

        nextFaderAction = new NextFaderAction(mnemonics);
        prevFaderAction = new PrevFaderAction(mnemonics);
        uploadAction = new UploadAction(mnemonics);
        closeAction = new CloseAction(mnemonics);

        if (isMac) {
            MacUtils.init(exitAction, prefsAction, aboutAction);
        } else {
            setMnemonics(mnemonics); // set keyboard short cut
        }
    }

    /** This sets up the Menubar. Called from JSLDesktop. */
    public static JMenuBar createMenuBar() {
        HashMap<Action, Integer> mnemonics = new HashMap<Action, Integer>();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(mnemonics, mask));
        menuBar.add(createLibraryMenu(mnemonics, mask));
        menuBar.add(createPatchMenu(mnemonics, mask));
        menuBar.add(createWindowMenu(mnemonics, mask));
        menuBar.add(createHelpMenu(mnemonics, mask));

        // set keyboard short cut
        if (!isMac) {
            setMnemonics(mnemonics);
        }

        return menuBar;
    }

    private static JMenu createFileMenu(HashMap<Action, Integer> mnemonics,
            int mask) {
        JMenuItem mi;
        JMenu menuFile = new JMenu("File");

        mnemonics.put(menuFile.getAction(), new Integer(KeyEvent.VK_F));
        mi = menuFile.add(openAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
        mi = menuFile.add(saveAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
        menuFile.add(saveAsAction);

        menuFile.addSeparator();
        menuFile.add(importAction);
        menuFile.add(exportAction);
        menuFile.add(importAllAction);

        if (!isMac) {
            menuFile.addSeparator();
            menuFile.add(exitAction);
        }
        return menuFile;
    }

    private static JMenu createLibraryMenu(HashMap<Action, Integer> mnemonics,
            int mask) {
        JMenuItem mi;
        JMenu menuLib = new JMenu("Library");
        mnemonics.put(menuLib.getAction(), new Integer(KeyEvent.VK_L));

        menuLib.add(transferSceneAction);
        menuLib.add(updateSceneAction);
        menuLib.addSeparator();

        menuLib.add(sortAction);
        menuLib.add(searchAction);
        menuLib.add(crossBreedAction);
        menuLib.add(deleteDuplicatesAction);
        menuLib.addSeparator();

        mi = menuLib.add(newAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask));
        mi = menuLib.add(newSceneAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
        return menuLib;
    }

    private static JMenu createPatchMenu(HashMap<Action, Integer> mnemonics,
            int mask) {
        JMenuItem mi;
        JMenu menuPatch = new JMenu("Patch");
        mnemonics.put(menuPatch.getAction(), new Integer(KeyEvent.VK_P));
        mi = menuPatch.add(copyAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
        mi = menuPatch.add(cutAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
        mi = menuPatch.add(pasteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
        mi = menuPatch.add(deleteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuPatch.addSeparator();

        mi = menuPatch.add(sendAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));
        menuPatch.add(updateSelectedAction); // wirski@op.pl
        menuPatch.add(sendToAction);
        menuPatch.add(storeAction);
        mi = menuPatch.add(getAction); // phil@muqus.com
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, mask)); // wirski@op.pl
        menuPatch.addSeparator();

        mi = menuPatch.add(editAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
        mi = menuPatch.add(printAction);
        mi = menuPatch.add(playAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        menuPatch.addSeparator();

        menuPatch.add(reassignAction);
        menuPatch.add(extractAction);
        menuPatch.addSeparator();

        mi = menuPatch.add(newPatchAction);
        // ctrl+shift+p
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                KeyEvent.SHIFT_MASK + mask));
        menuPatch.add(uploadAction);
        menuPatch.addMenuListener(new MenuListener() { // need this???
                    @Override
                    public void menuCanceled(MenuEvent e) {
                    }

                    @Override
                    public void menuDeselected(MenuEvent e) {
                    }

                    @Override
                    public void menuSelected(MenuEvent e) {
                        pasteAction.setEnabled(true);
                    }
                });
        return menuPatch;
    }

    /** List of JSLWindowMenus including one for invisible frame. */
    private static List<JMenu> windowMenus = new ArrayList<JMenu>();

    private static JMenu createWindowMenu(HashMap<Action, Integer> mnemonics,
            int mask) {
        JSLWindowMenu wm = new JSLWindowMenu("Window");
        windowMenus.add(wm);

        if (!isMac) {
            wm.add(prefsAction);
            wm.setMnemonic(KeyEvent.VK_W);
        }
        wm.add(monitorAction);
        // wm.add(JSLDesktop.toolBarAction);
        wm.addSeparator();
        JMenuItem mi = wm.add(closeAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
        wm.addSeparator();

        // add menu entries of existing frames
        JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
        if (desktop != null) { // when desktop is created, 'desktop' is still
                               // null.
            Iterator<JSLFrame> it = desktop.getJSLFrameIterator();
            while (it.hasNext()) {
                wm.add(it.next());
            }
        }
        return wm;
    }

    private static JMenu createHelpMenu(HashMap<Action, Integer> mnemonics,
            int mask) {
        JMenuItem mi;
        JMenu menuHelp = new JMenu("Help");
        mnemonics.put(menuHelp.getAction(), new Integer(KeyEvent.VK_H));
        mi = menuHelp.add(docsAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0));
        // J2SE 1.4.2 is not ready for HTML of www.jsynthlib.org site
        // mi = menuHelp.add(homePageAction);
        mi = menuHelp.add(licenseAction);
        if (!isMac) {
            menuHelp.add(aboutAction);
        }
        return menuHelp;
    }

    /** This sets up the mnemonics */
    private static void setMnemonics(Map<Action, Integer> mnemonics) {
        Iterator<Action> it = mnemonics.keySet().iterator();
        Object key, value;
        while (it.hasNext()) {
            key = it.next();
            value = mnemonics.get(key);
            if (key instanceof JMenuItem) {
                ((JMenuItem) key).setMnemonic(((Integer) value).intValue());
            } else if (key instanceof Action) {
                ((Action) key).putValue(Action.MNEMONIC_KEY, value);
            }
        }
    }

    public static void createPopupMenu() {
        // create popup menu
        menuPatchPopup = new JPopupMenu();
        menuPatchPopup.add(playAction);
        menuPatchPopup.add(editAction);
        menuPatchPopup.add(updateSelectedAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(sendAction);
        menuPatchPopup.add(sendToAction);
        menuPatchPopup.add(storeAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(reassignAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(copyAction);
        menuPatchPopup.add(cutAction);
        menuPatchPopup.add(pasteAction);
        menuPatchPopup.add(deleteAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(uploadAction);
    }

    /** show popup menu for patch. */
    public static void showMenuPatchPopup(JTable tbl, int x, int y) {
        if (menuPatchPopup == null) {
            createPopupMenu();
        }

        menuPatchPopup.show(tbl, x, y);
    }

    public static JToolBar createToolBar() {
        // create tool bar
        JToolBar toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(500, 35));
        toolBar.setFloatable(true);

        toolBar.add(createToolBarButton(newAction, "New", "New Library"));
        toolBar.add(createToolBarButton(openAction, "Open", "Open Library"));
        toolBar.add(createToolBarButton(saveAction, "Save", "Save Library"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(copyAction, "Copy", "Copy Patch"));
        toolBar.add(createToolBarButton(cutAction, "Cut", "Cut Patch"));
        toolBar.add(createToolBarButton(pasteAction, "Paste", "Paste Patch"));
        toolBar.add(createToolBarButton(importAction, "Import", "Import Patch"));
        toolBar.add(createToolBarButton(exportAction, "Export", "Export Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(playAction, "Play", "Play Patch"));
        toolBar.add(createToolBarButton(storeAction, "Store", "Store Patch"));
        toolBar.add(createToolBarButton(editAction, "Edit", "Edit Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(prevFaderAction, "Prev",
                "Go to Previous Fader Bank"));
        toolBar.add(createToolBarButton(nextFaderAction, "Next",
                "Go to Next Fader Bank"));

        return toolBar;
    }

    private static JButton createToolBarButton(Action a, String label,
            String tooltip) {
        String label2 = label.toLowerCase();
        URL u1 = PatchEdit.class.getResource("/images/" + label2 + ".png");
        URL u2 =
                PatchEdit.class.getResource("/images/disabled-" + label2
                        + ".png");
        // Create and initialize the button.
        JButton button = new JButton(a);
        button.setToolTipText(tooltip);

        if (u1 != null) { // image found
            button.setText(null);
            button.setIcon(new ImageIcon(u1, label));
            if (u2 != null) {
                button.setDisabledIcon(new ImageIcon(u2, label));
            }
        } else { // no image found
            button.setText(label);
            LOG.info("Resource not found: images/" + label + ".png");
        }
        return button;
    }

    /**
     * Enable/disable Actions.
     * @param b
     *            <code>true</code> to enable Actions, <code>false</code> to
     *            disable them.
     * @param v
     *            Specify Actions to be enabled/disabled. Use constants
     *            <code>EN_*</code>.
     */
    public static void setEnabled(boolean b, long v) {
        if ((v & EN_ABOUT) != 0) {
            aboutAction.setEnabled(b);
        }
        if ((v & EN_COPY) != 0) {
            copyAction.setEnabled(b);
        }
        if ((v & EN_CROSSBREED) != 0) {
            crossBreedAction.setEnabled(b);
        }
        if ((v & EN_CUT) != 0) {
            cutAction.setEnabled(b);
        }
        if ((v & EN_DELETE) != 0) {
            deleteAction.setEnabled(b);
        }
        if ((v & EN_DELETE_DUPLICATES) != 0) {
            deleteDuplicatesAction.setEnabled(b);
        }
        if ((v & EN_DOCS) != 0) {
            docsAction.setEnabled(b);
        }
        if ((v & EN_EDIT) != 0) {
            editAction.setEnabled(b);
        }
        if ((v & EN_EXIT) != 0) {
            exitAction.setEnabled(b);
        }
        if ((v & EN_EXPORT) != 0) {
            exportAction.setEnabled(b);
        }
        if ((v & EN_EXTRACT) != 0) {
            extractAction.setEnabled(b);
        }
        if ((v & EN_GET) != 0) {
            getAction.setEnabled(b);
        }
        if ((v & EN_HOME_PAGE) != 0) {
            homePageAction.setEnabled(b);
        }
        if ((v & EN_IMPORT) != 0) {
            importAction.setEnabled(b);
        }
        if ((v & EN_IMPORT_ALL) != 0) {
            importAllAction.setEnabled(b);
        }
        if ((v & EN_LICENSE) != 0) {
            licenseAction.setEnabled(b);
        }
        if ((v & EN_MONITOR) != 0) {
            monitorAction.setEnabled(b);
        }
        if ((v & EN_NEW) != 0) {
            newAction.setEnabled(b);
        }
        if ((v & EN_NEW_PATCH) != 0) {
            newPatchAction.setEnabled(b);
        }
        if ((v & EN_NEW_SCENE) != 0) {
            newSceneAction.setEnabled(b);
        }
        if ((v & EN_NEXT_FADER) != 0) {
            nextFaderAction.setEnabled(b);
        }
        if ((v & EN_PREV_FADER) != 0) {
            prevFaderAction.setEnabled(b);
        }
        if ((v & EN_OPEN) != 0) {
            openAction.setEnabled(b);
        }
        if ((v & EN_PASTE) != 0) {
            pasteAction.setEnabled(b);
        }
        if ((v & EN_PLAY) != 0) {
            playAction.setEnabled(b);
        }
        if ((v & EN_PREFS) != 0) {
            prefsAction.setEnabled(b);
        }
        if ((v & EN_REASSIGN) != 0) {
            reassignAction.setEnabled(b);
        }
        if ((v & EN_SAVE) != 0) {
            saveAction.setEnabled(b);
        }
        if ((v & EN_SAVE_AS) != 0) {
            saveAsAction.setEnabled(b);
        }
        if ((v & EN_SEARCH) != 0) {
            searchAction.setEnabled(b);
        }
        if ((v & EN_SEND) != 0) {
            sendAction.setEnabled(b);
        }
        if ((v & EN_SEND_TO) != 0) {
            sendToAction.setEnabled(b);
        }
        if ((v & EN_SORT) != 0) {
            sortAction.setEnabled(b);
        }
        if ((v & EN_STORE) != 0) {
            storeAction.setEnabled(b);
        }
        if ((v & EN_TRANSFER_SCENE) != 0) {
            transferSceneAction.setEnabled(b);
        }
        if ((v & EN_UPDATE_SCENE) != 0) {
            updateSceneAction.setEnabled(b); // wirski@op.pl
        }
        if ((v & EN_UPDATE_SELECTED) != 0) {
            // wirski@op.pl
            updateSelectedAction.setEnabled(b);
        }
        if ((v & EN_UPLOAD) != 0) {
            uploadAction.setEnabled(b);
        }
        if ((v & EN_PRINT) != 0) {
            printAction.setEnabled(b);
        }
    }

    /**
     * Create a new Library Window and load a Library from disk to fill it! Fun!
     */
    public static void openFrame(File file) {
        // PatchEdit.showWaitDialog("Loading " + file + " ...");
        // The previous line won't do anything unless we use a new thread to do
        // the loading.
        // Right now, it's using the GUI event thread (since it was triggered
        // from a GUI button
        // so the GUI never gets a chance to make the WaitDialog until we've
        // finished loading.
        // - Emenaker, 2006-02-02
        AbstractLibraryFrame frame;
        if (file.exists()) {
            // try LibraryFrame then SceneFrame
            frame = new LibraryFrame(file);
            try {
                frame.open(file);
            } catch (Exception e) {
                frame = new SceneFrame(file);
                try {
                    frame.open(file);
                } catch (Exception e1) {
                    // PatchEdit.hideWaitDialog();
                    // See comment at beginning of this method
                    ErrorMsg.reportError("Error", "Error Loading Library:\n "
                            + file.getAbsolutePath());
                    LOG.warn(e1.getMessage(), e1);
                    return;
                }
            }
        } else {
            // PatchEdit.hideWaitDialog();
            // See comment at beginning of this method
            ErrorMsg.reportError("Error",
                    "File Does Not Exist:\n" + file.getAbsolutePath());
            return;
        }
        // PatchEdit.hideWaitDialog();
        // See comment at beginning of this method

        addLibraryFrame(frame);
    }

    /** add and show a Library/Scene Window */
    public static void addLibraryFrame(AbstractLibraryFrame frame) {
        JSLDesktop.Factory.getDesktop().add(frame);
        frame.moveToDefaultLocation();
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            // I don't *actually* know what this is for :-)
            LOG.warn(e.getMessage(), e);
        }
    }

    /** This one saves a Library to Disk */
    public static void saveFrame() {
        try {
            AbstractLibraryFrame oFrame =
                    (AbstractLibraryFrame) JSLDesktop.Factory.getDesktop()
                            .getSelectedFrame();
            if (oFrame.getTitle().startsWith("Unsaved ")) {
                File fn = showSaveDialog(oFrame);
                if (fn != null) {
                    oFrame.save(fn);
                }
            } else {
                oFrame.save();
            }
        } catch (IOException e) {
            ErrorMsg.reportError("Error", "Unable to Save Library");
            LOG.warn(e.getMessage(), e);
        }
    }

    public static File showSaveDialog(AbstractLibraryFrame oFrame) {
        CompatibleFileDialog fc = new CompatibleFileDialog();
        FileFilter filter = oFrame.getFileFilter();

        AppConfig appConfig = JSynthLibInjector.getInstance(AppConfig.class);
        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File(appConfig.getLibPath()));
        if (fc.showSaveDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        File file = fc.getSelectedFile();

        if (!file.getName().toLowerCase().endsWith(oFrame.getFileExtension())) {
            file = new File(file.getPath() + oFrame.getFileExtension());
        }
        if (file.isDirectory()) {
            ErrorMsg.reportError("Error", "Can not Save over a Directory");
            return null;
        }
        if (file.exists()) {
            if (JOptionPane.showConfirmDialog(null, "Are you sure?",
                    "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return null;
            }
        }
        return file;
    }
}
