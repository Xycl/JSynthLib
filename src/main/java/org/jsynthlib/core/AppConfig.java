package org.jsynthlib.core;

import javax.swing.JFrame;

/**
 * AppConfig.java - class to hold collect application configuration variables in
 * one place for easy saving and loading, and separation of data from display
 * code. Persistent values are keeped by using
 * <code>java.util.prefs.Preferences</code>.
 * @author Zellyn Hunter (zellyn@zellyn.com)
 * @author Rib Rob
 * @author Hiroo Hayashi
 * @version $Id: AppConfig.java 1182 2011-12-04 22:07:24Z chriswareham $
 */
public interface AppConfig {

    int GUI_MDI = 0;
    int GUI_SDI = 1;

    /**
     * This routine just saves the current settings in the config file. Its
     * called when the user quits the app.
     */
    void savePrefs();

    /** Getter for libPath for library/scene file. */
    String getLibPath();

    /** Setter for libPath for library/scene file. */
    void setLibPath(String libPath);

    /** Getter for sysexPath for import/export Sysex Message. */
    String getSysexPath();

    /** Setter for sysexPath for import/export Sysex Message. */
    void setSysexPath(String sysexPath);

    /** Getter for default library which is open at start-up. */
    String getDefaultLibrary();

    /** Setter for default library which is open at start-up. */
    void setDefaultLibrary(String file);

    /** Getter for sequencerEnable */
    boolean getSequencerEnable();

    /** Setter for sequencerEnable */
    void setSequencerEnable(boolean sequencerEnable);

    /** Getter for midi file (Sequence) to play */
    String getSequencePath();

    /** Setter for midi file (Sequence) to play */
    void setSequencePath(String sequencePath);

    /** Getter for note */
    int getNote();

    /** Setter for note */
    void setNote(int note);

    /** Getter for velocity */
    int getVelocity();

    /** Setter for velocity */
    void setVelocity(int velocity);

    /** Getter for delay */
    int getDelay();

    /** Setter for delay */
    void setDelay(int delay);

    /** Getter for RepositoryURL */
    String getRepositoryURL();

    /** Setter for RepositoryURL */
    void setRepositoryURL(String url);

    /** Getter for RepositoryUser */
    String getRepositoryUser();

    /** Setter for RepositoryUser */
    void setRepositoryUser(String user);

    /** Getter for RepositoryPass */
    String getRepositoryPass();

    /** Setter for RepositoryPass */
    void setRepositoryPass(String password);

    /** Getter for lookAndFeel */
    String getLookAndFeel();

    /** Setter for lookAndFeel */
    void setLookAndFeel(String lookAndFeel);

    /** Getter for guiStyle */
    int getGuiStyle();

    /** Setter for guiStyle */
    void setGuiStyle(int guiStyle);

    /** Getter for tool bar */
    boolean getToolBar();

    /** Setter for tool bar */
    void setToolBar(boolean b);

    /**
     * Getter for faderEnable. Returns false if MIDI input is unavailable.
     */
    boolean getFaderEnable();

    /** Setter for faderEnable */
    void setFaderEnable(boolean faderEnable);

    /** Getter for faderPort */
    int getFaderPort();

    /** Setter for faderPort */
    void setFaderPort(int faderPort);

    // int[] faderChannel (0 <= channel < 16, 16:off)
    /** Indexed getter for fader Channel number */
    int getFaderChannel(int i);

    /** Indexed setter for fader Channel number */
    void setFaderChannel(int i, int faderChannel);

    // int[] faderControl (0 <= controller < 120, 120:off)
    /** Indexed getter for fader Control number */
    int getFaderControl(int i);

    /** Indexed setter for fader Control number. */
    void setFaderControl(int i, int faderControl);

    /**
     * Set frame to remembered size. Updates position, size, and 'maximized'
     * state.
     * @return true if frame's size was changed
     */
    boolean getMainWindowBounds(JFrame frame);

    void setMainWindowBounds(JFrame frame);

    String getJSLVersion();
}
