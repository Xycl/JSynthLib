package core;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.jsynthlib.gui.Actions;
import org.jsynthlib.gui.desktop.JSLDesktop;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public final class MacUtils extends Application {

    private static MacUtils instance;

    private MacUtils() {
    }

    public static boolean isMac() {
        return System.getProperty("os.name").startsWith("Mac OS X");
    }

    public static void init(final Action exitAction, final Action prefsAction,
            final Action aboutAction) {
        instance = new MacUtils(); // to create a static context
        instance.addApplicationListener(new ApplicationAdapter() {
            public void handleAbout(ApplicationEvent e) {
                final ActionEvent event =
                        new ActionEvent(e.getSource(), 0, "About");
                // opens dialog, so I think we need to do this to
                // avoid deadlock
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        aboutAction.actionPerformed(event);
                    }
                });
                e.setHandled(true);
            }

            public void handleOpenFile(ApplicationEvent e) {
                final File file = new File(e.getFilename());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Actions.openFrame(file);
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
                        prefsAction.actionPerformed(event);
                    }
                });
            }

            public void handleQuit(ApplicationEvent e) {
                exitAction.actionPerformed(new ActionEvent(e.getSource(), 0,
                        "Exit"));
                if (JSLDesktop.Factory.getDesktop().isReadyToExit()) { // wirski@op.pl
                    e.setHandled(true);
                } else {
                    e.setHandled(false);
                }
            }
        });
        instance.setEnabledPreferencesMenu(true);
    }
}
