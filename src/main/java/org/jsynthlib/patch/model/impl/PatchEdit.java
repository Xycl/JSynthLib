package org.jsynthlib.patch.model.impl;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.Constants;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.core.viewcontroller.WaitDialog;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.preferences.PrefsDialog;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DevicesConfig;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MasterKeyboardService;

import com.google.inject.assistedinject.Assisted;

/**
 * This class provides an object that initialises application wide resources.
 */
public final class PatchEdit {
    private static final Logger LOG = Logger.getLogger(PatchEdit.class);
    private static JSLDesktop desktop;
    private static PrefsDialog prefsDialog;
    private static WaitDialog waitDialog;

    /**
     * Construct an object that initialises application wide resources.
     * @param files
     *            the library files to open
     * @param debugLevel
     *            the debug level
     */
    @Inject
    public PatchEdit(@Assisted final List<String> files,
            @Assisted final int debugLevel, MidiSettings midiSettings,
            AppConfig appConfig, DeviceManager deviceManager,
            MasterKeyboardService masterKeyboardService) {
        LOG.info("JSynthLib: " + Constants.VERSION + ", Java: "
                + Utility.getJavaVersion() + ", OS: " + Utility.getOSName()
                + ", " + Utility.getOSVersion());

        // load the supported devices
        DevicesConfig devConfig = DevicesConfig.getInstance();
        devConfig.printAll();

        // Load config file (JSynthLib.properties).
        // boolean loadPrefsSuccessfull = appConfig.loadPrefs();

        // Check if all MIDI ports are available wirski@op.pl
        for (int i = 0; i < deviceManager.deviceCount(); i++) {
            try {
                midiSettings.getInputMidiDeviceInfo(deviceManager.getDevice(i)
                        .getInPort());
                int outPort =
                        midiSettings.getOutPort(deviceManager.getDevice(i)
                                .getOutPortName());
                midiSettings.getOutputMidiDeviceInfo(outPort);
            } catch (Exception ex) {
                int ans =
                        JOptionPane
                                .showConfirmDialog(
                                        null,
                                        "At least one device's MIDI port is unavailable.\n"
                                                + "You might verify your setup and restart the application.\n"
                                                + "Press CANCEL to quit or OK to continue.",
                                        "Unavailable MIDI ports",
                                        JOptionPane.OK_CANCEL_OPTION);
                if (ans == JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                } else {
                    break;
                }
            }
        }

        // define event actions
        Actions.createActions();

        // Set up the GUI.
        JSLDesktop.Factory
                .setGUIMode(appConfig.getGuiStyle() == AppConfig.GUI_MDI);
        desktop = JSLDesktop.Factory.createDesktop("JSynthLib");

        // Show dialog for the 1st invokation.
        // This is no longer normal. Maybe we shouldn't save prefs if this
        // happens (could be difficult)
        // if (!loadPrefsSuccessfull)
        // ErrorMsg.reportError("Error",
        // "Unable to load user preferences. Defaults loaded instead.");

        // popup menu for Library window, etc.
        Actions.createPopupMenu();

        // set up Preference Dialog Window
        prefsDialog = new PrefsDialog(JSLDesktop.Factory.getRootFrame());

        // Set up a silly little dialog we can pop up for the user to
        // gawk at while we do time consuming work later on.
        waitDialog = new WaitDialog(JSLDesktop.Factory.getRootFrame());

        // Start pumping MIDI information from Input --> Output so the
        // user can play a MIDI Keyboard and make pretty music
        masterKeyboardService.masterInEnable();

        // open default library frame.
        String fname = appConfig.getDefaultLibrary();
        if (!fname.equals("")) {
            LOG.info("default lib: " + fname);
            Actions.openFrame(new File(fname));
        }

        // open library frame specified on the command line argument.
        for (String file : files) {
            LOG.info("file name: " + file);
            Actions.openFrame(new File(file));
        }
    }

    public static void showPrefsDialog() {
        prefsDialog.setVisible(true);
    }

    /**
     * Returns the current active JFrame. Used for the <code>owner</code>
     * parameter for <code>JDialog</code> constructor. Use this for a dialog
     * window which depends on a frame.
     * @see #getRootFrame()
     */
    public static JFrame getInstance() {
        return desktop == null ? null : desktop.getSelectedWindow();
    }

    // //////////////////////////////////////////////////////////////////////
    public static void showWaitDialog() {
        waitDialog.showDialog("Please wait until the operation is completed.");
    }

    public static void showWaitDialog(String s) {
        waitDialog.showDialog(s);
    }

    public static void hideWaitDialog() {
        waitDialog.hideDialog();
    }
}
