package org.jsynthlib.view.desktop;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.FocusListener;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;

import org.apache.log4j.Logger;
import org.jsynthlib.view.desktop.mdi.MDIFrameProxy;
import org.jsynthlib.view.desktop.sdi.SDIFrameProxy;

// addJSLFrameListener should probably be implemented in JSLFrame
// for dynamic switching of type
/**
 * A Frame class which supports JInternalFrame mothods and also can creates a
 * non-internal frame using JFrame. For the details of each method, refer the
 * documentation of JInternalFrame.
 * @see JSLDesktop
 * @see JSLWindowMenu
 * @see javax.swing.JInternalFrame
 * @see javax.swing.JFrame
 * @author Rib Rdb
 * @author Hiroo Hayashi
 */
public class JSLFrame {

    private JSLFrameProxy proxy;
    /** parent JSLDesktop. */
    private JSLDesktop desktop;

    /**
     * Creates a non-resizable, non-closable, non-maximizable, non-iconifiable
     * JSLFrame with no title.
     */
    public JSLFrame() {
        this.desktop = JSLDesktop.Factory.getDesktop();
        if (JSLDesktop.Factory.useMDI())
            proxy = new MDIFrameProxy(this);
        else
            proxy = new SDIFrameProxy(this);
    }

    /**
     * Creates a JSLFrame with the specified title, resizability, closability,
     * maximizability, and iconifiability.
     */
    public JSLFrame(String title, boolean resizable, boolean closable,
            boolean maximizable, boolean iconifiable) {
        this.desktop = JSLDesktop.Factory.getDesktop();
        if (JSLDesktop.Factory.useMDI())
            proxy =
                    new MDIFrameProxy(this, title, resizable, closable,
                            maximizable, iconifiable);
        else
            proxy = new SDIFrameProxy(this, title);
    }

    // JInternalFrame compatible methods
    /**
     * Convenience method that moves this component to position 0 if its parent
     * is a JLayeredPane.
     */
    public void moveToFront() {
        proxy.moveToFront();
    }

    /** Selects or deselects the frame if it's showing. */
    public void setSelected(boolean b) throws java.beans.PropertyVetoException {
        proxy.setSelected(b);
    }

    /** Closes this internal frame if the argument is true. */
    public void setClosed(boolean b) throws java.beans.PropertyVetoException {
        proxy.setClosed(b);
    }

    /** If the minimum size has been set to a non-null value just returns it. */
    public Dimension getMinimumSize() {
        return proxy.getMinimumSize();
    }

    /** Returns the content pane for this internal frame. */
    public Container getContentPane() {
        return proxy.getContentPane();
    }

    /** Sets the frame title. */
    public void setTitle(String title) {
        proxy.setTitle(title);
    }

    /** Repaints this component. */
    public void repaint() {
        proxy.repaint();
    }

    /** Returns the title of the frame. */
    public String getTitle() {
        return proxy.getTitle();
    }

    /**
     * Adds the specified focus listener to receive focus events from this
     * component when this component gains input focus.
     */
    public void addFocusListener(FocusListener l) {
        proxy.addFocusListener(l);
    }

    /** Resizes this component so that it has width width and height height. */
    public void setSize(int w, int h) {
        proxy.setSize(w, h);
    }

    /** Returns the size of this component in the form of a Dimension object. */
    public Dimension getSize() {
        return proxy.getSize();
    }

    /**
     * Stores the width/height of this component into "return value" rv and
     * returns rv.
     */
    public Dimension getSize(Dimension rv) {
        return proxy.getSize(rv);
    }

    /** Moves this component to a new location. */
    public void setLocation(int x, int y) {
        proxy.setLocation(x, y);
    }

    /** Moves this component to a new location. */
    public void setLocation(Point p) {
        proxy.setLocation(p);
    }

    /** Shows or hides this component depending on the value of parameter b. */
    public void setVisible(boolean b) {
        proxy.setVisible(b);
    }

    /**
     * Determines whether this component should be visible when its parent is
     * visible.
     */
    public boolean isVisible() {
        return proxy.isVisible();
    }

    /**
     * Causes subcomponents of this frame to be laid out at their preferred
     * size.
     */
    public void pack() {
        proxy.pack();
    }

    /** Makes this frame invisible, unselected, and closed. */
    public void dispose() {
        proxy.dispose();
    }

    /** Returns the current x coordinate of the components origin. */
    public int getX() {
        return proxy.getX();
    }

    /** Returns the current y coordinate of the components origin. */
    public int getY() {
        return proxy.getY();
    }

    /** Returns whether the frame is the currently "selected" or active frame. */
    public boolean isSelected() {
        return proxy.isSelected();
    }

    /** Returns whether this Window is iconified. */
    public boolean isIcon() {
        return proxy.isIcon();
    }

    /** Returns whether this Window is closing. */
    public boolean isClosing() {
        return proxy.isClosing();
    }

    /** Adds the specified listener to receive frame events from this frame. */
    public void addJSLFrameListener(JSLFrameListener l) {
        proxy.addJSLFrameListener(l);
    }

    /**
     * Returns an array of all the JSLFrameListener added to this frame with
     * addJSLFrameListener.
     */
    public JSLFrameListener[] getJSLFrameListeners() {
        return proxy.getJSLFrameListeners();
    }

    /**
     * Removes the specified frame listener so that it no longer receives frame
     * events from this frame.
     */
    public void removeJSLFrameListener(JSLFrameListener l) {
        proxy.removeJSLFrameListener(l);
    }

    /** Sets the menuBar property for this JInternalFrame. */
    public void setJMenuBar(JMenuBar m) {
        proxy.setJMenuBar(m);
    }

    // original (non-JInternalFrame compatible) methods
    /** Move the frame to default location. */
    public void moveToDefaultLocation() {
        setLocation(desktop.getDefaultLocation(getSize()));
    }

    // only for Actions.PasteAction
    public boolean canImport(DataFlavor[] flavors) {
        return false;
    }

    // mothods for JSLDeskTop
    /** Returns JFrame object. Only for SDI mode. */
    public JFrame getJFrame() {
        return (JFrame) proxy;
    }

    /** Returns JInternalFrame object. Only for MDI mode. */
    public JInternalFrame getJInternalFrame() {
        return (JInternalFrame) proxy;
    }
}
