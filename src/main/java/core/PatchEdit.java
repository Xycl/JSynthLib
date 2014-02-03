package core;

import java.io.File;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.gui.Actions;
import org.jsynthlib.gui.WaitDialog;
import org.jsynthlib.gui.desktop.JSLDesktop;
import org.jsynthlib.gui.preferences.PrefsDialog;

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
    public PatchEdit(final List<String> files, final int debugLevel) {
        LOG.info("JSynthLib: " + Constants.VERSION + ", Java: "
                + Utility.getJavaVersion() + ", OS: " + Utility.getOSName()
                + ", " + Utility.getOSVersion());

        // load the supported devices

        DevicesConfig devConfig = DevicesConfig.getInstance();
        devConfig.printAll();

        // Load config file (JSynthLib.properties).
        boolean loadPrefsSuccessfull = AppConfig.loadPrefs();

        // Check if all MIDI ports are available wirski@op.pl
        for (int i = 0; i < AppConfig.deviceCount(); i++) {
            try {
                MidiUtil.getInputMidiDeviceInfo(AppConfig.getDevice(i)
                        .getInPort());
                MidiUtil.getOutputMidiDeviceInfo(AppConfig.getDevice(i)
                        .getPort());
            } catch (Exception ex) {
                int ans =
                        JOptionPane
                                .showConfirmDialog(
                                        null,
                                        "At list one device's MIDI port is unavailable.\n"
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
        JSLDesktop.Factory.setGUIMode(AppConfig.getGuiStyle() == AppConfig.GUI_MDI);
        desktop = JSLDesktop.Factory.createDesktop("JSynthLib");

        // Show dialog for the 1st invokation.
        // This is no longer normal. Maybe we shouldn't save prefs if this
        // happens (could be difficult)
        if (!loadPrefsSuccessfull)
            ErrorMsg.reportError("Error",
                    "Unable to load user preferences. Defaults loaded instead.");

        // popup menu for Library window, etc.
        Actions.createPopupMenu();

        // set up Preference Dialog Window
        prefsDialog = new PrefsDialog(JSLDesktop.Factory.getRootFrame());

        // Set up a silly little dialog we can pop up for the user to
        // gawk at while we do time consuming work later on.
        waitDialog = new WaitDialog(JSLDesktop.Factory.getRootFrame());

        // Start pumping MIDI information from Input --> Output so the
        // user can play a MIDI Keyboard and make pretty music
        masterInEnable(AppConfig.getMasterInEnable());

        // open default library frame.
        String fname = AppConfig.getDefaultLibrary();
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
    // This allows icons to be loaded even if they are inside a Jar file
    private static ImageIcon loadIcon(String name) { // not used now
        Object icon;
        String jarName = null;
        icon = new ImageIcon(name);
        if (((ImageIcon) icon).getIconWidth() == -1) {
            jarName = new String("/");
            jarName = jarName.concat(name);
            try {
                // icon = new ImageIcon(this.getClass().getResource(jarName));
                icon =
                        new ImageIcon(PatchEdit.class.getClass().getResource(
                                jarName));
            } catch (java.lang.NullPointerException e) {
                LOG.info("ImageIcon:LoadIcon Could not find: " + name);
            }
        }
        return (ImageIcon) icon;
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

    // //////////////////////////////////////////////////////////////////////
    // MIDI Master Input
    // masterInTrans (trns) -> MasterReceiver (rcvr1) -> initPortOut(rcvr)
    private static class MasterReceiver implements Receiver {
        private Receiver rcvr;

        MasterReceiver(Receiver rcvr) {
            this.rcvr = rcvr;
        }

        // Receiver interface
        public void close() {
            // don't close a shared Receiver
            // if (rcvr != null) rcvr.close();
        }

        public void send(MidiMessage message, long timeStamp) {
            int status = message.getStatus();
            if ((0x80 <= status) && (status < 0xF0)) { // MIDI channel Voice
                                                       // Message
                // I believe Sysex message must be ignored.
                // || status == SysexMessage.SYSTEM_EXCLUSIVE)
                LOG.info("MasterReceiver: " + message);
                this.rcvr.send(message, timeStamp);
                MidiUtil.log("RECV: ", message);
            }
        }
    }

    private static Transmitter trns;
    private static Receiver rcvr1;

    static void masterInEnable(boolean enable) {
        if (enable) {
            // disable previous master in port if enabled.
            masterInEnable(false);
            // get transmitter
            trns = MidiUtil.getTransmitter(AppConfig.getMasterController());
            // create output receiver
            try {
                Receiver rcvr =
                        MidiUtil.getReceiver(AppConfig.getInitPortOut());
                rcvr1 = new MasterReceiver(rcvr);
                trns.setReceiver(rcvr1);
                // LOG.info("masterInEnable.rcvr: " + rcvr);
                // LOG.info("masterInEnable.rcvr1: " + rcvr1);
                // LOG.info("masterInEnable.trns: " + trns);
            } catch (MidiUnavailableException e) {
                LOG.warn(e.getMessage(), e);
            }
        } else {
            if (trns != null)
                trns.close();
            if (rcvr1 != null)
                rcvr1.close();
        }
    }
}
