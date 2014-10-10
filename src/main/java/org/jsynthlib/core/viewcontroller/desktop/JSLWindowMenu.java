package org.jsynthlib.core.viewcontroller.desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.apache.log4j.Logger;

/**
 * A Window menu for JSLDesktop and JSLFrame. A JSLFrame is added by
 * <code>add</code> method or is removed when the JSLFrame is closed. A JSLFrame
 * can be activated by the menu entry added.
 * @see JSLDesktop
 * @see JSLFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLWindowMenu extends JMenu implements JSLFrameListener,
        ActionListener {

    private static final long serialVersionUID = 1L;
    private final transient Logger log = Logger.getLogger(getClass());
    private final ButtonGroup bg = new ButtonGroup();
    private final HashMap<JSLFrame, JSLWindowMenuItem> windows;
    private final JSLWindowMenuItem none = new JSLWindowMenuItem();
    private Boolean doing_selection = Boolean.FALSE;

    public JSLWindowMenu(String title) {
        super(title);
        windows =
                new HashMap<JSLFrame, JSLWindowMenuItem>();
    }

    /**
     * Appends a menu item to the end of this menu.
     * @param f
     *            JSLFrame to be added.
     * @return the menu item added.
     */
    public JMenuItem add(JSLFrame f) {
        // if (f == null) return;
        f.addJSLFrameListener(this);
        JSLWindowMenuItem i = new JSLWindowMenuItem(f);
        i.addActionListener(this);
        windows.put(f, i);
        bg.add(i);
        return add(i);
    }

    /**
     * Removes the specified menu item from this menu. If there is no popup
     * menu, this method will have no effect.
     * @param f
     *            JSLFrame to be added.
     */
    private void remove(JSLFrame f) {
        // if (f == null) return;
        log.info("JSLWindowMenu.remove : " + f.getTitle());
        if (windows.containsKey(f)) {
            JSLWindowMenuItem i = windows.remove(f);
            bg.remove(i);
            remove(i);
        }
    }

    private void setSelectedWindow(JSLFrame f) {
        if (windows.containsKey(f)) {
            bg.setSelected(windows.get(f), true);
        } else {
            bg.setSelected(none, true); // XXX Why this is required?
        }
    }

    // JSLFrameListener methods
    @Override
    public void JSLFrameActivated(JSLFrameEvent e) {
        setSelectedWindow(e.getJSLFrame());
    }

    @Override
    public void JSLFrameClosed(JSLFrameEvent e) {
        remove(e.getJSLFrame());
    }

    @Override
    public void JSLFrameClosing(JSLFrameEvent e) {
    }

    @Override
    public void JSLFrameDeactivated(JSLFrameEvent e) {
    }

    @Override
    public void JSLFrameDeiconified(JSLFrameEvent e) {
    }

    @Override
    public void JSLFrameIconified(JSLFrameEvent e) {
    }

    @Override
    public void JSLFrameOpened(JSLFrameEvent e) {
    }

    // end of JSLFrameListener methods

    // ActionListener method
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JSLWindowMenuItem) {
            JSLFrame f = ((JSLWindowMenuItem) e.getSource()).getJSLFrame();
            synchronized (doing_selection) {
                if (doing_selection.booleanValue()) {
                    return;
                }
                doing_selection = Boolean.TRUE;
                f.setVisible(true);
                // f.toFront();
                doing_selection = Boolean.FALSE;
            }
        }
    }

    private class JSLWindowMenuItem extends JRadioButtonMenuItem implements
            ButtonModel {

        private static final long serialVersionUID = 1L;

        private final WeakReference<JSLFrame> f;
        private boolean roll = false;
        private boolean press = false;

        JSLWindowMenuItem() {
            super();
            f = null;
        }

        public JSLWindowMenuItem(JSLFrame frame) {
            super(frame.getTitle());
            f = new WeakReference<JSLFrame>(frame);
        }

        @Override
        public void setGroup(ButtonGroup g) {
        }

        @Override
        public void setRollover(boolean b) {
            roll = b;
        }

        @Override
        public void setPressed(boolean b) {
            press = b;
        }

        @Override
        public boolean isRollover() {
            return roll;
        }

        @Override
        public boolean isPressed() {
            return press;
        }

        public JSLFrame getJSLFrame() {
            return f.get();
        }
    }
}
