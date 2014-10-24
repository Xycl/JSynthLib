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
package org.jsynthlib.core.impl;

import java.io.IOException;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.inject.Inject;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.log4j.Logger;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.JSynthLib;
import org.jsynthlib.core.MacUtils;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.utils.AWTUtils;

/**
 * @author Pascal Collberg
 */
public class AppConfigImpl implements AppConfig {

    private static final String JSL_PROPS = "jsl.properties";

    private final transient Logger log = Logger.getLogger(getClass());
    private final Preferences preferences;
    private final MidiSettings midiSettings;

    private final Properties properties;

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#savePrefs()
     */
    @Override
    public void savePrefs() {
        try {
            // Save the appconfig
            store();
        } catch (Exception e) {
            ErrorMsg.reportError("Error", "Unable to Save Preferences");
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * @throws BackingStoreException
     */
    private void store() throws BackingStoreException {
        // This shouldn't be necessary unless the jvm crashes.
        // Save prefs
        preferences.flush();
    }

    @Inject
    public AppConfigImpl(MidiSettings midiSettings) {
        this.midiSettings = midiSettings;

        preferences = Preferences.userNodeForPackage(JSynthLib.class);

        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            ErrorMsg.reportError("Could not load preferences.", e.getMessage());
            log.warn(e.getMessage(), e);
        }

        properties = new Properties();
        try {
            properties.load(getClass().getResourceAsStream("/" + JSL_PROPS));
        } catch (IOException e) {
            ErrorMsg.reportError("Could not load core properties.", e.getMessage());
            log.warn(e.getMessage(), e);
        }
        setLookAndFeel(getLookAndFeel());
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getLibPath()
     */
    @Override
    public String getLibPath() {
        return preferences.get("libPath", ".");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setLibPath(java.lang.String)
     */
    @Override
    public void setLibPath(String libPath) {
        preferences.put("libPath", libPath);
    }

    /** Getter for XML Path */
    String getXMLpaths() {
        return preferences.get("XMLpaths", "");
    }

    /** Setter for XML Path */
    void setXMLpaths(String libPath) {
        preferences.put("XMLpaths", libPath);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getSysexPath()
     */
    @Override
    public String getSysexPath() {
        return preferences.get("sysexPath", ".");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setSysexPath(java.lang.String)
     */
    @Override
    public void setSysexPath(String sysexPath) {
        preferences.put("sysexPath", sysexPath);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getDefaultLibrary()
     */
    @Override
    public String getDefaultLibrary() {
        return preferences.get("defaultLib", "");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setDefaultLibrary(java.lang.String)
     */
    @Override
    public void setDefaultLibrary(String file) {
        preferences.put("defaultLib", file);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getSequencerEnable()
     */
    @Override
    public boolean getSequencerEnable() {
        return preferences.getBoolean("sequencerEnable", false);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setSequencerEnable(boolean)
     */
    @Override
    public void setSequencerEnable(boolean sequencerEnable) {
        preferences.putBoolean("sequencerEnable", sequencerEnable);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getSequencePath()
     */
    @Override
    public String getSequencePath() {
        return preferences.get("sequencePath", "");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setSequencePath(java.lang.String)
     */
    @Override
    public void setSequencePath(String sequencePath) {
        preferences.put("sequencePath", sequencePath);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getNote()
     */
    @Override
    public int getNote() {
        return preferences.getInt("note", 0);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setNote(int)
     */
    @Override
    public void setNote(int note) {
        preferences.putInt("note", note);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getVelocity()
     */
    @Override
    public int getVelocity() {
        return preferences.getInt("velocity", 0);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setVelocity(int)
     */
    @Override
    public void setVelocity(int velocity) {
        preferences.putInt("velocity", velocity);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getDelay()
     */
    @Override
    public int getDelay() {
        return preferences.getInt("delay", 0);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setDelay(int)
     */
    @Override
    public void setDelay(int delay) {
        preferences.putInt("delay", delay);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getRepositoryURL()
     */
    @Override
    public String getRepositoryURL() {
        return preferences.get("repositoryURL", "http://www.jsynthlib.org");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setRepositoryURL(java.lang.String)
     */
    @Override
    public void setRepositoryURL(String url) {
        preferences.put("repositoryURL", url);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getRepositoryUser()
     */
    @Override
    public String getRepositoryUser() {
        return preferences.get("repositoryUser", "");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setRepositoryUser(java.lang.String)
     */
    @Override
    public void setRepositoryUser(String user) {
        preferences.put("repositoryUser", user);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getRepositoryPass()
     */
    @Override
    public String getRepositoryPass() {
        return preferences.get("repositoryPass", "");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setRepositoryPass(java.lang.String)
     */
    @Override
    public void setRepositoryPass(String password) {
        preferences.put("repositoryPass", password);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getLookAndFeel()
     */
    @Override
    public String getLookAndFeel() {
        return preferences.get("lookAndFeel", "Nimbus");
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setLookAndFeel(java.lang.String)
     */
    @Override
    public void setLookAndFeel(String lookAndFeel) {
        // This causes dialogs and non-internal frames to be painted with the
        // look-and-feel. Emenaker 2005-06-08
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        preferences.put("lookAndFeel", lookAndFeel);
        UIManager.LookAndFeelInfo[] installedLF;
        installedLF = UIManager.getInstalledLookAndFeels();
        try {
            for (UIManager.LookAndFeelInfo info : installedLF) {
                if (info.getName().equals(lookAndFeel)) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            ErrorMsg.reportError("Could not set look and feel", e.getMessage());
            log.warn(e.getMessage(), e);
        } catch (InstantiationException e) {
            ErrorMsg.reportError("Could not set look and feel", e.getMessage());
            log.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            ErrorMsg.reportError("Could not set look and feel", e.getMessage());
            log.warn(e.getMessage(), e);
        } catch (UnsupportedLookAndFeelException e) {
            ErrorMsg.reportError("Could not set look and feel", e.getMessage());
            log.warn(e.getMessage(), e);
        }
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getGuiStyle()
     */
    @Override
    public int getGuiStyle() {
        return preferences.getInt("guiStyle", MacUtils.isMac() ? GUI_SDI
                : GUI_MDI);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setGuiStyle(int)
     */
    @Override
    public void setGuiStyle(int guiStyle) {
        preferences.putInt("guiStyle", guiStyle);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getToolBar()
     */
    @Override
    public boolean getToolBar() {
        return preferences.getBoolean("toolBar", MacUtils.isMac());
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setToolBar(boolean)
     */
    @Override
    public void setToolBar(boolean b) {
        preferences.putBoolean("toolBar", b);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getFaderEnable()
     */
    @Override
    public boolean getFaderEnable() {
        return (midiSettings.isOutputAvailable()
                && midiSettings.getMidiEnable() && preferences.getBoolean(
                "faderEnable", false));
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setFaderEnable(boolean)
     */
    @Override
    public void setFaderEnable(boolean faderEnable) {
        preferences.putBoolean("faderEnable", faderEnable);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getFaderPort()
     */
    @Override
    public int getFaderPort() {
        return preferences.getInt("faderPort", 0);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setFaderPort(int)
     */
    @Override
    public void setFaderPort(int faderPort) {
        preferences.putInt("faderPort", faderPort);
    }

    // int[] faderChannel (0 <= channel < 16, 16:off)
    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getFaderChannel(int)
     */
    @Override
    public int getFaderChannel(int i) {
        return preferences.getInt("faderChannel" + i, 0);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setFaderChannel(int, int)
     */
    @Override
    public void setFaderChannel(int i, int faderChannel) {
        preferences.putInt("faderChannel" + i, faderChannel);
    }

    // int[] faderControl (0 <= controller < 120, 120:off)
    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getFaderControl(int)
     */
    @Override
    public int getFaderControl(int i) {
        int n = preferences.getInt("faderControl" + i, 0);
        return n > 120 ? 120 : n; // for old JSynthLib bug
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setFaderControl(int, int)
     */
    @Override
    public void setFaderControl(int i, int faderControl) {
        preferences.putInt("faderControl" + i, faderControl);
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#getMainWindowBounds(javax.swing.JFrame)
     */
    @Override
    public boolean getMainWindowBounds(JFrame frame) {
        String val = preferences.get("mainWindow", null);
        if (val == null) {
            return false;
        } else {
            String[] ints = val.split("\\s+");
            try {
                // fully parse string first
                int x = Integer.parseInt(ints[0]);
                int y = Integer.parseInt(ints[1]);
                int width = Integer.parseInt(ints[2]);
                int height = Integer.parseInt(ints[3]);
                boolean isMax = Boolean.parseBoolean(ints[4]);

                // parsing succeeded, now update frame
                frame.setBounds(x, y, width, height);
                if (isMax) {
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }

                AWTUtils.moveOnScreen(frame);
                return true;
            } catch (Throwable t) {
                // On any error, don't touch frame.
                return false;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.AppConfig#setMainWindowBounds(javax.swing.JFrame)
     */
    @Override
    public void setMainWindowBounds(JFrame frame) {
        boolean isMax = (frame.getExtendedState() & JFrame.MAXIMIZED_BOTH) != 0;

        // TODO figure out how to get the un-maximized bounds of a window even
        // when it's maximized
        String val =
                String.format("%d %d %d %d %s", frame.getX(), frame.getY(),
                        frame.getWidth(), frame.getHeight(), isMax);
        preferences.put("mainWindow", val);
    }

    @Override
    public String getJSLVersion() {
        return properties.getProperty("jsl.version");
    }
}
