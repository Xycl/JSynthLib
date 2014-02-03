package org.jsynthlib.gui.desktop;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;
import org.jsynthlib.gui.Actions;
import org.jsynthlib.gui.desktop.mdi.MDIDesktop;
import org.jsynthlib.gui.desktop.sdi.SDIDesktop;

import core.AppConfig;
import core.MacUtils;

/**
 * A virtual JDesktopPane class which supports both MDI (Multiple Document
 * Interface: using JInternalFrame) and SDI (Single Document Interface: using
 * JFrame) mothods. In MDI mode JDesktopPane is used. In SDI mode a ToolBar
 * window is created and JDesktopPane methods are emulated. For the details of
 * each method, refer the documentation of JDesktopPane.
 * @see JDesktopPane
 * @see JSLWindowMenu
 * @see JSLFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public abstract class JSLDesktop implements JSLFrameListener {
    private final transient Logger log = Logger.getLogger(getClass());

    public static class Factory {
        /** just for efficiency. */
        private static boolean isMac = MacUtils.isMac();

        private static final Factory INSTANCE = new Factory();

        /**
         * Returns the root JFrame for the <code>owner</code> parameter for
         * <code>JDialog</code> constructor. Use this for a dialog window which
         * does not depend on a frame.
         * @see #getInstance()
         */
        public static JFrame getRootFrame() {
            if (INSTANCE.desktop == null) {
                return null;
            } else {
                return INSTANCE.desktop.getRootFrame();
            }
        }

        public static JSLDesktop getDesktop() {
            return INSTANCE.desktop;
        }

        /**
         * Select GUI mode. This method must be called before the first
         * JSLDesktop constructor call. If this method is not call, MDI is used.
         * @param useMDI
         *            if true MDI (single window mode) is used, otherwise SDI
         *            (multiple window mode) is used.
         */
        public static void setGUIMode(boolean useMDI) {
            INSTANCE.useMDI = useMDI;
        }

        /**
         * @return <code>true</code> in MDI mode, <code>false</code> in SDI
         *         mode.
         */
        public static boolean useMDI() {
            return INSTANCE.useMDI;
        }

        public static JSLDesktop createDesktop(String title) {
            INSTANCE.createDesktopInternal(title);
            return INSTANCE.desktop;
        }

        private JSLDesktop desktop;

        /** @see #setGUIMode(boolean) */
        private boolean useMDI = true;

        private Factory() {
        }

        void createDesktopInternal(String title) {
            JMenuBar menuBar = Actions.createMenuBar();
            JToolBar toolBar = Actions.createToolBar();

            if (useMDI) {
                desktop =
                        new MDIDesktop(title, menuBar, toolBar,
                                Actions.exitAction);
            } else {
                boolean invisible =
                        isMac
                                && "true"
                                        .equals(System
                                                .getProperty("apple.laf.useScreenMenuBar"));
                desktop =
                        new SDIDesktop(invisible, title, menuBar, toolBar,
                                Actions.exitAction);
            }
        }

    }

    protected Boolean in_fake_activation = Boolean.FALSE;
    protected int frame_count = 0;
    protected int xdecoration = 0, ydecoration = 0;
    private Action exitAction;

    /** a list of JSLFrames added to the JSLDesktop. */
    protected List<JSLFrame> windows = new ArrayList<JSLFrame>();

    /** True if we can exit the application */
    private boolean readyToExit = false;

    /** Creates a new JSLDesktop. */
    protected JSLDesktop(Action exitAction) {
        this.exitAction = exitAction;
    }

    /** @see JSLFrame#moveToDefaultLocation() */
    protected abstract Point getDefaultLocation(Dimension frameSize);

    protected Point getDefaultLoationInternal(Dimension frameSize, int xofs,
            int yofs, int xsep, int ysep) {
        Dimension screenSize = getSize();
        int x, xRemain;
        xRemain = (int) (screenSize.getWidth() - frameSize.getWidth() - xofs);
        x = xRemain > 0 ? (xofs + (xsep * frame_count) % xRemain) : xofs;
        if (x + frameSize.getWidth() > screenSize.getWidth()) {
            x = xofs;
            if (x + frameSize.getWidth() > screenSize.getWidth())
                x = 0;
        }
        int y, yRemain;
        yRemain = (int) (screenSize.getHeight() - frameSize.getHeight() - yofs);
        y = yRemain > 0 ? (yofs + (ysep * frame_count) % yRemain) : yofs;
        if (y + frameSize.getHeight() > screenSize.getHeight()) {
            y = yofs;
            if (y + frameSize.getHeight() > screenSize.getHeight())
                y = 0;
        }

        frame_count++;
        return new Point(x, y);

    }

    /**
     * Returns the currently active JSLFrame, or last active JSLFrame if no
     * JSLFrame is currently active, or null if any JSLFrame has never been
     * activated.
     */
    public JSLFrame getSelectedFrame() {
        return getSelectedJSLFrame();
    }

    // original (non-JDesktopPane compatible) methods

    /**
     * Returns the root JFrame for the <code>owner</code> parameter for
     * <code>JDialog</code> constructor to show a dialog window in the center of
     * screen. In MDI mode returns the root JFrame created. In SDI mode returns
     * null.
     * @see #getSelectedWindow()
     */
    protected JFrame getRootFrame() {
        return null;
    }

    /** Returns invisible window. Used only in SDI mode for Mac OS. */
    public JFrame getInvisible() {
        return null;
    }

    /** add a JSLFrame under this JSLDesktop control. */
    public void add(JSLFrame f) {
        if (windows.contains(f)) {
            log.info("JSLDesktop.add : multiple add() call.");
            return;
        }
        addInternal(f);
        f.addJSLFrameListener(this);
        windows.add(f);
    }

    /**
     * @return <code>Iterator</code> of JSLFrame added on the JSLDesktop.
     */
    public Iterator<JSLFrame> getJSLFrameIterator() {
        return windows.iterator();
    }

    public void JSLFrameActivated(JSLFrameEvent e) {
        FrameActivated(e.getJSLFrame());
    }

    public void JSLFrameClosing(JSLFrameEvent e) {
        FrameClosing(e.getJSLFrame());
    }

    public void JSLFrameClosed(JSLFrameEvent e) {
        windows.remove(e.getJSLFrame());
        FrameClosed(e.getJSLFrame());
    }

    public void JSLFrameDeactivated(JSLFrameEvent e) {
    }

    public void JSLFrameDeiconified(JSLFrameEvent e) {
    }

    public void JSLFrameIconified(JSLFrameEvent e) {
    }

    public void JSLFrameOpened(JSLFrameEvent e) {
    }

    public boolean isReadyToExit() { // wirski@op.pl
        return readyToExit;
    }

    public void closingProc() { // wirski@op.pl
        // To avoid concurrent modification issues when closing.
        List<JSLFrame> framesTemp = new ArrayList<JSLFrame>();
        framesTemp.addAll(windows);
        Iterator<JSLFrame> it = framesTemp.iterator();
        while (it.hasNext()) {
            JSLFrame frame = (JSLFrame) it.next();
            try {
                frame.setClosed(true);
            } catch (PropertyVetoException ex) {
                log.warn(ex.getMessage(), ex);
            }
        }
        AppConfig.savePrefs();
        System.exit(0);
    }

    /**
     * Returns the current active JFrame. Used for the <code>owner</code>
     * parameter for <code>JDialog</code> constructor. In MDI mode returns the
     * root JFrame created. In SDI mode returns the current active JFrame (may
     * be the Toolbar frame).
     * @see #getSelectedWindow()
     */
    public abstract JFrame getSelectedWindow();

    protected abstract JSLFrame[] getAllJSLFrames();

    protected abstract JSLFrame getSelectedJSLFrame();

    /** Returns the size of this component in the form of a Dimension object. */
    public abstract Dimension getSize();

    protected abstract void addInternal(JSLFrame f);

    /**
     * Notification from the UIManager that the L&F has changed. Replaces the
     * current UI object with the latest version from the UIManager.
     */
    public abstract void updateLookAndFeel();

    protected abstract void FrameActivated(JSLFrame f);

    protected abstract void FrameClosing(JSLFrame f);

    protected abstract void FrameClosed(JSLFrame f);

    // /** JSLDesktop with Menu support. */
    // public static class MenuDesktop extends JSLDesktop {
    //
    // MenuDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {
    // super(title, mb, tb, exitAction);
    // }
    //
    // public void add(JSLFrame frame) {
    // super.add(frame);
    // Iterator it = windowMenus.iterator();
    // while (it.hasNext()) {
    // ((JSLWindowMenu) it.next()).add(frame);
    // }
    // }
    //
    // public void JSLFrameClosed(JSLFrameEvent e) {
    // super.JSLFrameClosed(e);
    // }
    // }
}
