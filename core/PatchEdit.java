/**
 * This is the main Application object.  It's called PatchEdit, which
 * is probably ambiguous, but probably to late to change now.
 */

/* @version $Id$ */

package core;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.sound.midi.*;

import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

//TODO import /*TODO org.jsynthlib.*/midi.*;
public class PatchEdit extends JFrame implements MidiDriverChangeListener {
    public static PatchEdit instance;
    public static MidiWrapper MidiOut;
    public static MidiWrapper MidiIn;
    public static AppConfig appConfig;
    // public static NoteChooserDialog noteChooserDialog; -- replaced by NoteChooserConfigPanel - emenaker 2003.03.17
    public static WaitDialog waitDialog; // define showWaitDialog() and hideWaitDialog()

    static JDesktopPane desktop;
    static Patch Clipboard;
    static JPopupMenu menuPatchPopup; // define showMenuPatchPopup()
    static javax.swing.Timer echoTimer;

    // accessed by BankEditorFrame
    static ExtractAction extractAction;
    static SendAction sendAction;
    static SendToAction sendToAction;
    static StoreAction storeAction;
    static ReassignAction reassignAction;
    static EditAction editAction;
    static PlayAction playAction;
    static GetAction receiveAction;
    static SaveAction saveAction;
    static SaveAsAction saveAsAction;
    static SortAction sortAction;
    static SearchAction searchAction;
    static DeleteDuplicatesAction dupAction;
    static CopyAction copyAction;
    static CutAction cutAction;
    static PasteAction pasteAction;
    static DeleteAction deleteAction;
    static ImportAction importAction;
    static ExportAction exportAction;
    static ImportAllAction importAllAction;
    static NewPatchAction newPatchAction;
    static CrossBreedAction crossBreedAction;
    static DocsAction docsAction;
    static MonitorAction monitorAction;
    static NewSceneAction newSceneAction;
    static TransferSceneAction transferSceneAction;

    private static NextFaderAction nextFaderAction;
    private static NewAction newAction;
    private static OpenAction openAction;
    private static ExitAction exitAction;
    private static PrefsAction prefsAction;
    private static SynthAction synthAction;
    private static AboutAction aboutAction;

    private static MidiMonitor midiMonitor;
    private static JToolBar toolBar;
    private static int currentPort;
    //private static int[] newFaderValue = new int[33];
    private static PrefsDialog prefsDialog;
    private static SearchDialog searchDialog;
    private static DocumentationWindow documentationWindow;

    /** Initialize Application: */
    public PatchEdit() {
	/*
	 * Initialize JFrame
	 */
        super("JSynthLib");
	// phil@muqus.com (so can pop-up windows with PatchEdit as the
        instance = this;

	/*
	 * Load config file (JSynthLib.properties).
	 */
	appConfig = new AppConfig();
        boolean loadPrefsSuccessfull = appConfig.loadPrefs();

	/*
	 * Setup preference dialog window.
	 */
        prefsDialog = new PrefsDialog(this);
	// Add the configuration panels to the prefsDialog
        prefsDialog.addPanel(new GeneralConfigPanel(appConfig));

	prefsDialog.addPanel(new DirectoryConfigPanel(appConfig));

	MidiConfigPanel midiConfigPanel = null;
	midiConfigPanel = new MidiConfigPanel(appConfig);
	midiConfigPanel.addDriverChangeListener(this);
	prefsDialog.addPanel(midiConfigPanel);
	MidiIn = MidiOut = midiConfigPanel.getMidiWrapper();

	// FaderBoxConfigPanel() have to be called after MidiIn is initialized.
	FaderBoxConfigPanel faderbox = new FaderBoxConfigPanel(appConfig);
	midiConfigPanel.addDriverChangeListener(faderbox); // Notify the faderbox, too... - emenaker 2003.03.19
	prefsDialog.addPanel(faderbox);

	prefsDialog.addPanel(new NoteChooserConfigPanel(appConfig));

	// Create preference dialog window and initialize each config
	// panel.
	prefsDialog.init();

        /*
	 * Now lets set up how the pretty application should look.
	 */
        int inset = 100;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
		  screenSize.width  - inset * 2,
		  screenSize.height - inset * 2);

        //Quit this app when the big window closes.
        addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    appConfig.savePrefs();
		    // We shouldn't need to unload the midi driver if
		    // the whole JVM is going away.
		    // unloadMidiDriver();
		    System.exit(0);
		}
	    });

	createActions();

        //Set up the GUI.
        Container c = getContentPane();
        setJMenuBar(createMenuBar());
	JToolBar tb = createToolBar();
        c.add(tb, BorderLayout.NORTH);
        tb.setVisible(true);	//necessary as of kestrel
	if (MacUtils.isMac())
	    initForMac(exitAction, prefsAction, aboutAction);

        desktop = new JDesktopPane();
        desktop.setOpaque(false);
        desktop.putClientProperty("JDesktopPane.dragMode", "outline");
        c.add(desktop, BorderLayout.CENTER);

        setVisible(true);

	// popup menu for Library window, etc.
	menuPatchPopup = createPopupMenu();

	/*
	 * Show dialog for the 1st invokation.
	 */
        if (!loadPrefsSuccessfull)
            ErrorMsg.reportError("Error",
				 "Unable to load user preferences. Defaults loaded\n"
				 + "If you've just installed or just upgraded this software, this is normal.");

        //Set up a silly little dialog we can pop up for the user to
        //gawk at while we do time consuming work later on.
        waitDialog = new WaitDialog(this);

        // Start pumping MIDI information from Input --> Output so the
        // user can play a MIDI Keyboard and make pretty music
        beginEcho();
    }

    private void createActions() {
        HashMap mnemonics = new HashMap();

        newAction		= new NewAction(mnemonics);
        openAction		= new OpenAction(mnemonics);
        saveAction		= new SaveAction(mnemonics);
        saveAsAction		= new SaveAsAction(mnemonics);
        newSceneAction		= new NewSceneAction(mnemonics);
        transferSceneAction	= new TransferSceneAction(mnemonics);
        sortAction		= new SortAction(mnemonics);
        searchAction		= new SearchAction(mnemonics);
        dupAction		= new DeleteDuplicatesAction(mnemonics);
        exitAction		= new ExitAction(mnemonics);

        copyAction		= new CopyAction(mnemonics);
        cutAction		= new CutAction(mnemonics);
        pasteAction		= new PasteAction(mnemonics);
        deleteAction		= new DeleteAction(mnemonics);
        importAction		= new ImportAction(mnemonics);
        exportAction		= new ExportAction(mnemonics);
        importAllAction		= new ImportAllAction(mnemonics);
        sendAction		= new SendAction(mnemonics);
        sendToAction		= new SendToAction(mnemonics);
        storeAction		= new StoreAction(mnemonics);
        receiveAction		= new GetAction(mnemonics);

        playAction		= new PlayAction(mnemonics);
        editAction		= new EditAction(mnemonics);
        reassignAction		= new ReassignAction(mnemonics);
        crossBreedAction	= new CrossBreedAction(mnemonics);
        newPatchAction		= new NewPatchAction(mnemonics);
        extractAction		= new ExtractAction(mnemonics);

	prefsAction		= new PrefsAction(mnemonics);
	synthAction		= new SynthAction(mnemonics);
	monitorAction		= new MonitorAction(mnemonics);

        aboutAction		= new AboutAction(mnemonics);
        docsAction		= new DocsAction(mnemonics);

        nextFaderAction		= new NextFaderAction(mnemonics);

	// set keyboard short cut
	if (!MacUtils.isMac())
	    setMnemonics(mnemonics);
    }

    /** This sets up the Menubar as well as the main right-click Popup
	menu and the toolbar */
    private JMenuBar createMenuBar() {
	JMenuItem mi;
        HashMap mnemonics = new HashMap();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        JMenuBar menuBar = new JMenuBar();

	// create "Library" Menu
        JMenu menuLib = new JMenu("Library");
	mnemonics.put(menuLib, new Integer(KeyEvent.VK_L));

	mi = menuLib.add(newAction);
	mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
        menuLib.add(newSceneAction);
        mi = menuLib.add(openAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
        mi = menuLib.add(saveAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
        menuLib.add(saveAsAction);
        menuLib.addSeparator();

        menuLib.add(transferSceneAction);
        menuLib.addSeparator();

        menuLib.add(sortAction);
        menuLib.add(searchAction);
        menuLib.add(dupAction);
        if (!MacUtils.isMac()) {
	    menuLib.addSeparator();
	    menuLib.add(exitAction);
	}
	menuBar.add(menuLib);

	// create "Patch" Menu
        JMenu menuPatch = new JMenu("Patch");
	mnemonics.put(menuPatch, new Integer(KeyEvent.VK_P));
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

        menuPatch.add(importAction);
        menuPatch.add(exportAction);
        menuPatch.add(importAllAction);
        menuPatch.addSeparator();

        mi = menuPatch.add(sendAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));
        menuPatch.add(sendToAction);
        menuPatch.add(storeAction);
        menuPatch.add(receiveAction); // phil@muqus.com
        menuPatch.addSeparator();

        mi = menuPatch.add(playAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        mi = menuPatch.add(editAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
        menuPatch.addSeparator();

 	menuPatch.add(reassignAction);
        menuPatch.add(crossBreedAction);
        menuPatch.add(newPatchAction);
        menuPatch.add(extractAction);
        menuBar.add(menuPatch);

	// create "Window" menu
	JMenu menuWindow = new JMenu("Window");
	mnemonics.put(menuWindow, new Integer(KeyEvent.VK_W));
	menuWindow.add(synthAction);
	if (!MacUtils.isMac())
	    menuWindow.add(prefsAction);
	menuWindow.add(monitorAction);
	menuBar.add(menuWindow);

	// create "Help" menu
        JMenu menuHelp = new JMenu("Help");
	mnemonics.put(menuHelp, new Integer(KeyEvent.VK_H));
	mi = menuHelp.add(docsAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0));
	if (!MacUtils.isMac())
	    menuHelp.add(aboutAction);
        menuBar.add(menuHelp);

	// set keyboard short cut
	if (!MacUtils.isMac())
	    setMnemonics(mnemonics);

        return menuBar;
    }

    /** This sets up the mnemonics */
    private void setMnemonics(Map mnemonics) {
	Iterator it = mnemonics.keySet().iterator();
	Object key, value;
	while (it.hasNext()) {
	    key = it.next();
	    value = mnemonics.get(key);
	    if (key instanceof JMenuItem)
		((JMenuItem) key).setMnemonic(((Integer) value).intValue());
	    else if (key instanceof Action)
		((Action) key).putValue(Action.MNEMONIC_KEY, value);
	}
    }

    private JPopupMenu createPopupMenu() {
	// crate popup menu
        JPopupMenu popup = new JPopupMenu();
        popup.add(playAction);
        popup.add(editAction);
        popup.addSeparator();

        popup.add(reassignAction);
        popup.add(storeAction);
        popup.add(sendAction);
        popup.add(sendToAction);
        popup.addSeparator();

        popup.add(cutAction);
        popup.add(copyAction);
        popup.add(pasteAction);
	return popup;
    }

    private JToolBar createToolBar() {
	// create tool bar
        JButton b;
        toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(500, 35));
        toolBar.setFloatable(true);

        b = toolBar.add(newAction);
        b.setToolTipText("New Library");
        b.setIcon(loadIcon("images/New24.gif"));
        b.setText(null);
        b = toolBar.add(openAction);
        b.setToolTipText("Open Library");
        b.setIcon(loadIcon("images/Open24.gif"));
        b.setText(null);
        b = toolBar.add(saveAction);
        b.setToolTipText("Save Library");
        b.setIcon(loadIcon("images/Save24.gif"));
        b.setText(null);
        toolBar.addSeparator();

        b = toolBar.add(copyAction);
        b.setToolTipText("Copy Patch");
        b.setIcon(loadIcon("images/Copy24.gif"));
        b.setText(null);
        b = toolBar.add(cutAction);
        b.setToolTipText("Cut Patch");
        b.setIcon(loadIcon("images/Cut24.gif"));
        b.setText(null);
        b = toolBar.add(pasteAction);
        b.setToolTipText("Paste Patch");
        b.setIcon(loadIcon("images/Paste24.gif"));
        b.setText(null);
        b = toolBar.add(importAction);
        b.setToolTipText("Import Patch");
        b.setIcon(loadIcon("images/Import24.gif"));
        b.setText(null);
        b = toolBar.add(exportAction);
        b.setToolTipText("Export Patch");
        b.setIcon(loadIcon("images/Export24.gif"));
        b.setText(null);
        toolBar.addSeparator();

        b = toolBar.add(playAction);
        b.setToolTipText("Play Patch");
        b.setIcon(loadIcon("images/Volume24.gif"));
        b.setText(null);
        b = toolBar.add(storeAction);
        b.setToolTipText("Store Patch");
        b.setIcon(loadIcon("images/ComposeMail24.gif"));
        b.setText(null);
        b = toolBar.add(editAction);
        b.setToolTipText("Edit Patch");
        b.setIcon(loadIcon("images/Edit24.gif"));
        b.setText(null);
        toolBar.addSeparator();

        b = toolBar.add(nextFaderAction);
        b.setToolTipText("Go to Next Fader Bank");
        b.setIcon(loadIcon("images/Forward24.gif"));
        b.setText(null);

        return toolBar;
    }

    private void initForMac(final ExitAction exitAction,
			    final PrefsAction prefsAction,
			    final AboutAction aboutAction) {
	MacUtils.init(new ApplicationAdapter() {
		public void handleAbout(ApplicationEvent e) {
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "About");
		    // opens dialog, so I think we need to do this to
		    // avoid deadlock
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    aboutAction.actionPerformed(event);
				} catch (Exception e) {
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handleOpenFile(ApplicationEvent e) {
		    final File file = new File(e.getFilename());
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    openFrame(file);
				} catch (Exception e) {
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handlePreferences(ApplicationEvent e) {
		    e.setHandled(true);
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "Preferences");
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    prefsAction.actionPerformed(event);
				} catch (Exception e) {
				}
			    }
			});
		}
		public void handleQuit(ApplicationEvent e) {
		    exitAction.actionPerformed(new ActionEvent(e.getSource(), 0,
							       "Exit"));
		    e.setHandled(true);
		}
	    });
    }

    /** This creates a new [empty] Library Window */
    private void createLibraryFrame() {
        LibraryFrame frame = new LibraryFrame();
        frame.setVisible(true);
        desktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}
    }

    private void createSceneFrame() {
        SceneFrame frame = new SceneFrame();
        frame.setVisible(true);
        desktop.add(frame);
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}  //I don't *actually* know what this is for :-)
    }

    /** Create a new Library Window and load a Library from disk to
	fill it! Fun! */
    private void openFrame(File file) {
        LibraryFrame frame = new LibraryFrame(file);
        try {
	    frame.setVisible(true);
	    frame.open(file);
	    desktop.add(frame);
	} catch (Exception e) {
	    SceneFrame frame2 = new SceneFrame(file);
	    try {
		frame2.setVisible(true);
		frame2.open(file);
		desktop.add(frame2);
	    } catch (Exception e2) {
		ErrorMsg.reportError("Error", "Error Loading Library", e2);
		return;
	    }
	    try {
		frame2.setSelected(true);
	    } catch (java.beans.PropertyVetoException e2) {
	    }
	}
        try {
	    frame.setSelected(true);
	} catch (java.beans.PropertyVetoException e) {
	}
    }

    /** This one saves a Library to Disk */
    private void saveFrame() {
	try {
	    Object oFrame = desktop.getSelectedFrame();
	    if (oFrame instanceof LibraryFrame) {
		LibraryFrame libFrame = (LibraryFrame) oFrame;
		if (libFrame.getTitle().startsWith("Unsaved Library")) {
		    saveFrameAs();
		    return;
		}
		libFrame.save();
	    } else if (oFrame instanceof SceneFrame) {
		SceneFrame sceneFrame = (SceneFrame) oFrame;
		if (sceneFrame.getTitle().startsWith("Unsaved ")) {
		    saveFrameAs();
		    return;
		}
		sceneFrame.save();
	    }
	} catch (Exception e) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", e);
	    return;
	}
    }

    /** Save and specify a file name */
    private void saveFrameAs() {
        CompatibleFileDialog fc2 = new CompatibleFileDialog();
        FileFilter type1 = new ExtensionFilter("PatchEdit Library Files (*.patchlib)", ".patchlib");
        fc2.addChoosableFileFilter(type1);
        fc2.setFileFilter(type1);
        fc2.setCurrentDirectory(new File (appConfig.getLibPath()));
        if (fc2.showSaveDialog(PatchEdit.this) != JFileChooser.APPROVE_OPTION)
            return;
        File file = fc2.getSelectedFile();
	try {
	    if (desktop.getSelectedFrame() == null) {
		ErrorMsg.reportError("Error", "Unable to Save Library. Library to save must be focused");
	    } else {
		if (!file.getName().toUpperCase().endsWith(".PATCHLIB"))
		    file = new File(file.getPath() + ".patchlib");
		if (file.isDirectory()) {
		    ErrorMsg.reportError("Error", "Can not Save over a Directory");
		    return;
		}
		if (file.exists())
		    if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists",
						      JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			return;
		try {
		    ((LibraryFrame) desktop.getSelectedFrame()).save(file);
		} catch (Exception pr) {
		    ((SceneFrame) desktop.getSelectedFrame()).save(file);
		}
	    }
	} catch (Exception ex) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", ex);
	}
    }


    // Generally the app is started by running JSynthLib, so the
    // following few lines are not necessary, but I won't delete them
    // just yet.
    public static void main(String[] args) {
        PatchEdit frame = new PatchEdit();
        frame.setVisible(true);
    }

    /**
     * This used to be loadMidiDriver() but it is now a callback
     * method that gets notified by MidiConfigPanel if the user
     * changes midi drivers. Initialization of the drivers is now
     * handled by the drivers themselves. - emenaker 2003.03.12
     * @param driver The new MidiWrapper
     */
    public void midiDriverChanged(MidiWrapper driver) {
	MidiIn = MidiOut = driver;
    }

    public static Driver getDriver(int deviceNumber, int driverNumber) {
	if (appConfig == null)
	    return null;
        return (Driver) appConfig.getDevice(deviceNumber).driverList.get(driverNumber);
    }

    /**
     * Output string to MIDI Monitor Window.  Use MidiUtil.log()
     * instead of this.
     *
     * @param s string to be output
     */
    static void midiMonitorLog(String s) {
	if (midiMonitor != null && midiMonitor.isVisible())
	    midiMonitor.log(s);
    }

    ////////////////////////////////////////////////////////////////////////
    /*
     * Now we start with the various action classes. Each of these
     * preforms one of the menu commands and are called either from
     * the menubar, popup menu or toolbar.
     */
    public class AboutAction extends AbstractAction {
	public AboutAction(Map mnemonics) {
	    super("About");
	    mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(null,
					  "JSynthLib Version 0.18\nCopyright (C) 2000-04 Brian Klock et al.\n"
					  + "See the file 'LICENSE.TXT' for more info.",
					  "About JSynthLib", JOptionPane.INFORMATION_MESSAGE);
	    return;
	}
    }

    public class ReassignAction extends AbstractAction {
	public ReassignAction(Map mnemonics) {
	    super("Reassign", null); // show a dialog frame???
	    // mnemonics.put(this, new Integer('R'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) desktop.getSelectedFrame()).ReassignSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Reassign must be highlighted in the focused Window.", ex);
	    }
	}
    }

    public class PlayAction extends AbstractAction {
        public PlayAction(Map mnemonics) {
            super("Play", null);
            mnemonics.put(this, new Integer('P'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).PlaySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Play must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class StoreAction extends AbstractAction {
        public StoreAction(Map mnemonics) {
            super("Store...", null);
            mnemonics.put(this, new Integer('R'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).StoreSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Store must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class SendAction extends AbstractAction {
        public SendAction(Map mnemonics) {
            super("Send", null);
	    mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).SendSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Send must be highlighted in the focused Window.", ex);
	    }
        }
    }

    public class SendToAction extends AbstractAction {
	public SendToAction(Map mnemonics) {
	    super("Send to...", null);
	    // mnemonics.put(this, new Integer('S'));
	    setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		((PatchBasket) desktop.getSelectedFrame()).SendToSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to 'Send to...' must be highlighted in the focused Window.", ex);
	    }
	}
    }

    public class DeleteAction extends AbstractAction {
        public DeleteAction(Map mnemonics) {
            super("Delete", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to delete must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    public class CopyAction extends AbstractAction {
        public CopyAction(Map mnemonics) {
            super("Copy", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('C'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to copy must be highlighted\nin the focused Window.", ex);
	    }
        }
    }

    public class CutAction extends AbstractAction {
        public CutAction(Map mnemonics) {
            super("Cut", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('T'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
		((PatchBasket) desktop.getSelectedFrame()).DeleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to cut must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    public class PasteAction extends AbstractAction {
        public PasteAction(Map mnemonics) {
            super("Paste", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((PatchBasket) desktop.getSelectedFrame()).PastePatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Paste into must be the focused Window.", ex);
	    }
        }
    }

    public class EditAction extends AbstractAction {
        public EditAction(Map mnemonics) {
            super("Edit...", null);
	    mnemonics.put(this, new Integer('E'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
	    Worker w = new Worker();
	    w.setDaemon(true);
	    w.start();
 	}
	class Worker extends Thread {
	    public void run() {
		try {
		    JInternalFrame frm =
			((PatchBasket) desktop.getSelectedFrame()).EditSelectedPatch();
		    if (frm != null) {
			frm.setVisible(true);
			desktop.add(frm);
			if (frm instanceof PatchEditorFrame)
			    for (int i = 0; i < ((PatchEditorFrame) frm).sliderList.size(); i++) {
				JSlider slider = (JSlider) ((PatchEditorFrame) frm).sliderList.get(i);
				Dimension dim = slider.getSize();
				if (dim.width > 0) {
				    dim.width++;
				    slider.setSize(dim);
				}
			    }
			try {
			    frm.setSelected(true);
			} catch (java.beans.PropertyVetoException ex) {
			}
		    }
		} catch (Exception ex) {
		    ErrorMsg.reportError("Error", "Library holding Patch to Edit must be the focused Window.", ex);
		}
	    }
	}
    }

    public class ExportAction extends AbstractAction {
        public ExportAction(Map mnemonics) {
            super("Export...", null);
            mnemonics.put(this, new Integer('O'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc3 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
            fc3.addChoosableFileFilter(type1);
            fc3.setFileFilter(type1);
            fc3.setCurrentDirectory(new File (appConfig.getSysexPath()));
            if (fc3.showSaveDialog(PatchEdit.this) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc3.getSelectedFile();
	    try {
		if (desktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error", "Patch to export must be hilighted\n in the currently focuses Library");
		} else {
		    if (!file.getName().toUpperCase().endsWith(".SYX"))
			file = new File(file.getPath() + ".syx");
		    if (file.exists())
			if (JOptionPane.showConfirmDialog(null, "Are you sure?", "File Exists", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			    return;

		    ((PatchBasket) desktop.getSelectedFrame()).ExportPatch(file);
		}
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Save Exported Patch", ex);
	    }
	}
    }

    //------ Start phil@muqus.com
    //=====================================================================
    // Sub Class: GetAction
    //=====================================================================

    public class GetAction extends AbstractAction {
	//-----------------------------------------------------------------
	// Constructor: GetAction
	//-----------------------------------------------------------------
	public GetAction(Map mnemonics) {
	    super("Get...", null);
	    mnemonics.put(this, new Integer('G'));
	    setEnabled(false);
	}

	//-----------------------------------------------------------------
	// GetAction->actionPerformed
	//-----------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
	    echoTimer.stop();
	    SysexGetDialog myDialog = new SysexGetDialog(PatchEdit.instance);
	    myDialog.show();
	    echoTimer.start();
	}

    } // End SubClass: GetAction
    //------ End phil@muqus.com

    // denis: mis en public toutes les classes Action
    public class ImportAction extends AbstractAction {
        public ImportAction(Map mnemonics) {
            super("Import...", null);
            mnemonics.put(this, new Integer('I'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc2 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
	    // core.ImportMidiFile extracts Sysex Messages from MidiFile
            FileFilter type2 = new ExtensionFilter("MIDI Files (*.mid)" , ".mid");
            fc2.addChoosableFileFilter(type1);
            fc2.addChoosableFileFilter(type2);
            fc2.setFileFilter(type1);
            fc2.setCurrentDirectory(new File(appConfig.getSysexPath()));
            if (fc2.showOpenDialog(PatchEdit.this) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc2.getSelectedFile();
	    try {
		if (desktop.getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error", "Library to Import Patch\n into Must be in Focus");
		} else
		    ((PatchBasket) desktop.getSelectedFrame()).ImportPatch(file);
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Load Sysex Data", ex);
	    }
	}
    }

    public class NewAction extends AbstractAction {
        public NewAction(Map mnemonics) {
	    super("New Library", null);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
	    System.out.println(UIManager.get("MenuItemUI"));
	    createLibraryFrame();
	}
    }

    public class NewSceneAction extends AbstractAction {
        public NewSceneAction(Map mnemonics) {
            super("New Scene", null);
        }

        public void actionPerformed(ActionEvent e) {
            createSceneFrame();
        }
    }

    public class TransferSceneAction extends AbstractAction {
        public TransferSceneAction(Map mnemonics) {
            super("Transfer Scene", null); // show a dialog frame???
            // mnemonics.put(this, new Integer('S'));
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((SceneFrame) desktop.getSelectedFrame()).sendScene();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
	    }
        }
    }

    public class OpenAction extends AbstractAction {
        public OpenAction(Map mnemonics) {
            super("Open...", null);
            mnemonics.put(this, new Integer('O'));
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("PatchEdit Library Files (*.patchlib, *.scenelib)",
						   new String[] {".patchlib", ".scenelib"});
            fc.addChoosableFileFilter(type1);
            fc.setFileFilter(type1);
            fc.setCurrentDirectory(new File(appConfig.getLibPath()));
            if (fc.showOpenDialog(PatchEdit.this) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc.getSelectedFile();
	    openFrame(file);
        }
    }

    public class SaveAction extends AbstractAction {
        public SaveAction(Map mnemonics) {
	    super("Save", null);
	    setEnabled(false);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrame();
        }
    }

    public class SaveAsAction extends AbstractAction {
        public SaveAsAction(Map mnemonics) {
	    super("Save As...", null);
	    setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrameAs();
        }
    }

    public class ExitAction extends AbstractAction {
        public ExitAction(Map mnemonics) {
	    super("Exit", null);
	    mnemonics.put(this, new Integer('X'));
        }

        public void actionPerformed(ActionEvent e) {
            appConfig.savePrefs();
            System.exit(0);
        }
    }

    public class ExtractAction extends AbstractAction {
        public ExtractAction(Map mnemonics) {
            super("Extract", null);
            mnemonics.put(this, new Integer('E'));
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((AbstractLibraryFrame) desktop.getSelectedFrame()).ExtractSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
	    }
        }
    }

    public class SortAction extends AbstractAction {
        public SortAction(Map mnemonics) {
            super("Sort...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('R'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		SortDialog sd = new SortDialog(PatchEdit.this);
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    public class SearchAction extends AbstractAction {
        public SearchAction(Map mnemonics) {
            super("Search...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('E'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (searchDialog == null)
		    searchDialog = new SearchDialog(PatchEdit.this);
		searchDialog.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    public class ImportAllAction extends AbstractAction {
        public ImportAllAction(Map mnemonics) {
            super("Import All...", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		CompatibleFileDialog fc = new CompatibleFileDialog();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (appConfig.getLibPath() != null)
		    fc.setSelectedFile(new File(appConfig.getLibPath()));
		if (fc.showDialog(PatchEdit.this, "Choose Import All Directory") != JFileChooser.APPROVE_OPTION)
		    return;
		File file = fc.getSelectedFile();

		ImportAllDialog sd = new ImportAllDialog(PatchEdit.this, file);
		sd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
	    }
        }
    }

    public class DeleteDuplicatesAction extends AbstractAction {
        public DeleteDuplicatesAction(Map mnemonics) {
            super("Delete Dups...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (JOptionPane.showConfirmDialog(null,
						  "This Operation will change the ordering of the Patches. Continue?",
						  "Delete Duplicate Patches",
						  JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
		    return;
		PatchEdit.waitDialog.show();
		Collections.sort(((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList, new SysexSort());
		int numDeleted = 0;
		Patch p, q;
		Iterator it = ((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList.iterator();
		p = (Patch) it.next();
		while (it.hasNext()) {
		    q = (Patch) it.next();
		    if (Arrays.equals(p.sysex, q.sysex)) {
			it.remove();
			numDeleted++;
		    } else
			p = q;
		}
		JOptionPane.showMessageDialog(null, numDeleted + " Patches were Deleted",
					      "Delete Duplicates", JOptionPane.INFORMATION_MESSAGE);
		((LibraryFrame) desktop.getSelectedFrame()).myModel.fireTableDataChanged();
		((LibraryFrame) desktop.getSelectedFrame()).statusBar.setText
		    (((LibraryFrame) desktop.getSelectedFrame()).myModel.PatchList.size() + " Patches");
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Delete Duplicates in must be Focused", ex);
	    }
            PatchEdit.waitDialog.hide();
        }
    }

    //This is a comparator class used by the delete duplicated action
    //to sort based on the sysex data
    //Sorting this way makes the Dups search much easier, since the
    //dups must be next to each other
    static class SysexSort implements Comparator {
        public int compare(Object a1, Object a2) {
	    String s1 = new String(((Patch) (a1)).sysex);
	    String s2 = new String(((Patch) (a2)).sysex);
	    return s1.compareTo(s2);
        }
    }

    public class NewPatchAction extends AbstractAction {
        public NewPatchAction(Map mnemonics) {
            super("New Patch...", null);
            setEnabled(false);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		Patch p = Clipboard;
		NewPatchDialog np = new NewPatchDialog(PatchEdit.this);
		np.show();
		((PatchBasket) desktop.getSelectedFrame()).PastePatch();
		Clipboard = p;
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to create this new patch.", ex);
	    }
        }
    }

    public class PrefsAction extends AbstractAction {
        public PrefsAction(Map mnemonics) {
            super("Preferences...", null);
	    mnemonics.put(this, new Integer('P'));
        }

        public void actionPerformed(ActionEvent e) {
            prefsDialog.show();
        }
    }

    public class SynthAction extends AbstractAction {
        public SynthAction(Map mnemonics) {
            super("Synths...", null);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
	    SynthConfigDialog scd = new SynthConfigDialog(PatchEdit.this);
	    scd.show();
        }
    }

    /*
      noteChooserDialog got replaced by NoteChooserConfigPanel - emenaker 2003.03.17

      class NoteChooserAction extends AbstractAction
      {
      public NoteChooserAction(Map mnemonics)
      {
      super("Choose Note",null);
      mnemonics.put(this, new Integer('C'));
      }

      public void actionPerformed(ActionEvent e)
      {
      noteChooserDialog.show();
      }

      }
    */

    public class CrossBreedAction extends AbstractAction {
        public CrossBreedAction(Map mnemonics) {
            super("Cross Breed...", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('B'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		CrossBreedDialog cbd = new CrossBreedDialog(PatchEdit.this);
		cbd.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to perform Crossbreed. (No Library selected?)", ex);
	    }
        }
    }

    public class NextFaderAction extends AbstractAction {
        public NextFaderAction(Map mnemonics) {
	    super("Go to Next Fader Bank", null);
	    mnemonics.put(this, new Integer('F'));
        }
        public void actionPerformed(ActionEvent e) {
            if (!(desktop.getSelectedFrame() instanceof PatchEditorFrame))
		return;
            PatchEditorFrame pf = (PatchEditorFrame) desktop.getSelectedFrame();
            pf.faderBank = (pf.faderBank + 1) % pf.numFaderBanks; pf.faderHighlight();
	    return;
        }
    }

    public class DocsAction extends AbstractAction {
        public DocsAction(Map mnemonics) {
	    super("Help", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('H'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (documentationWindow == null)
		    documentationWindow = new DocumentationWindow();
		documentationWindow.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    public class MonitorAction extends AbstractAction {
        public MonitorAction(Map mnemonics) {
	    super("MIDI Monitor", null);
	    setEnabled(true);
	    mnemonics.put(this, new Integer('M'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (midiMonitor == null)
		    midiMonitor = new MidiMonitor();
		midiMonitor.show();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Midi Monitor)", ex);
	    }
        }
    }

    ////////////////////////////////////////////////////////////////////////
    // This allows icons to be loaded even if they are inside a Jar file
    private ImageIcon loadIcon(String name) {
        Object icon;
        String jarName = null;
        icon = new ImageIcon(name);
        if (((ImageIcon) icon).getIconWidth() == -1) {
	    jarName = new String("/");
	    jarName = jarName.concat(name);
	    try {
		icon = new ImageIcon(this.getClass().getResource(jarName));
	    } catch (java.lang.NullPointerException e) {
		ErrorMsg.reportStatus("ImageIcon:LoadIcon Could not find: " + name);
	    }
	}
        return (ImageIcon) icon;
    }

    private void beginEcho() {
        echoTimer = new javax.swing.Timer(5, new ActionListener() {
		public void actionPerformed(ActionEvent evt) {
		    try {
			//FIXME there is a bug in the javaMIDI classes
			//so this routine gets the input messages from
			//the faderbox as well as the master
			//controller. I cant figure out a way to fix
			//it so let's just handle them here.
			int mstPort = appConfig.getMasterController();
			if (appConfig.getMasterControllerEnable() || appConfig.getFaderEnable()) {
			    while (PatchEdit.MidiIn.messagesWaiting(mstPort) > 0) {
 				MidiMessage msg = PatchEdit.MidiIn.readMessage(mstPort);
				Patch p = PatchEdit.Clipboard; // save Clipboard
				//???
				if (desktop.getSelectedFrame() instanceof PatchBasket
				    && (!(desktop.getSelectedFrame() instanceof PatchEditorFrame))) {
				    if (desktop.getSelectedFrame() instanceof LibraryFrame
					&& ((LibraryFrame) ((desktop.getSelectedFrame()))).table.getSelectedRowCount() == 0)
					break; // Is this OK for master controller?
				    ((PatchBasket) desktop.getSelectedFrame()).CopySelectedPatch();
				} else
				    Clipboard = ((PatchEditorFrame) desktop.getSelectedFrame()).p;

				if (msg.getStatus() == SysexMessage.SYSTEM_EXCLUSIVE) {
				    PatchEdit.MidiOut.send(Clipboard.getDevice().getPort(),
							   (SysexMessage) msg);
				} else {
				    ShortMessage smsg = (ShortMessage) msg;
				    int cmd = smsg.getCommand();

				    if (appConfig.getFaderEnable()
					&& desktop.getSelectedFrame() instanceof PatchEditorFrame
					&& cmd == ShortMessage.CONTROL_CHANGE) {
					sendFaderMessage(smsg);
				    } else if (cmd == ShortMessage.CHANNEL_PRESSURE) {
					//do nothing for now
				    } else {
					if ((0x80 <= cmd) && (cmd < 0xF0)) { // MIDI channel Voice Message
					    smsg.setMessage(cmd, Clipboard.getDevice().getChannel() - 1,
							    smsg.getData1(), smsg.getData2());
					}
					PatchEdit.MidiOut.send(Clipboard.getDevice().getPort(), smsg);
				    }
				}
				PatchEdit.Clipboard = p; // restore Clipboard
			    }
			}
		    } catch (Exception ex) {
			ErrorMsg.reportStatus(ex);
		    }
		}
	    });
        echoTimer.start();
    }

    private void sendFaderMessage(ShortMessage msg) {
        int channel = msg.getChannel();
	int controller = msg.getData1();
        for (int i = 0; i < 33; i++) {
	    if ((appConfig.getFaderController(i) == controller)
		&& (appConfig.getFaderChannel(i) == channel)) {
		((PatchEditorFrame) desktop.getSelectedFrame()).faderMoved((byte) i, (byte) msg.getData2());
		break;
	    }
	}
    }
}
