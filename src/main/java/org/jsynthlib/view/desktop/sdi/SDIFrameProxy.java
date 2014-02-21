package org.jsynthlib.view.desktop.sdi;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jsynthlib.view.desktop.JSLDesktop;
import org.jsynthlib.view.desktop.JSLFrame;
import org.jsynthlib.view.desktop.JSLFrameEvent;
import org.jsynthlib.view.desktop.JSLFrameListener;
import org.jsynthlib.view.desktop.JSLFrameProxy;

/** use JFrame for SDI (Single Document Interface) mode. */
public class SDIFrameProxy extends JFrame implements JSLFrameProxy, WindowListener {
    
    private static final Logger LOG = Logger.getLogger(SDIFrameProxy.class);

    private static final long serialVersionUID = 8208502459693393026L;

    private WeakReference<JSLFrame> parent;
    protected List<JSLFrameListener> listeners = new ArrayList<JSLFrameListener>();
    private boolean closing = false;

    public SDIFrameProxy(JSLFrame p) {
        super();
        addWindowListener(this);
        parent = new WeakReference<JSLFrame>(p);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public SDIFrameProxy(JSLFrame p, String title) {
        super(title);
        addWindowListener(this);
        parent = new WeakReference<JSLFrame>(p);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    public JSLFrame getJSLFrame() {
        return (JSLFrame) parent.get();
    }

    public void moveToFront() {
        toFront();
    }

    public void setSelected(boolean b) {
        if (!isFocused() && b)
            toFront();
    }

    public void setClosed(boolean b) {
        if (b)
            processWindowEvent(new WindowEvent(this,
                    WindowEvent.WINDOW_CLOSING));
    }

    public void addJSLFrameListener(JSLFrameListener l) {
        listeners.add(l);
    }

    public void setVisible(boolean b) {
        // deiconified
        if (b && isIcon())
            setExtendedState(getExtendedState() & ~ICONIFIED);
        super.setVisible(b);
    }

    private void showState(String s) {
        LOG.info("\"" + getTitle() + "\" " + s + " ("
                + this.getExtendedState() + ")");
    }

    public void windowActivated(WindowEvent e) {
        showState("activated");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameActivated(fe);
        }
    }

    public void windowClosed(WindowEvent e) {
        showState("closed");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosed(fe);
        }
        closing = false;
    }

    public void windowClosing(WindowEvent e) {
        showState("closing");
        closing = true;
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosing(fe);
        }
        // if (!fe.isConsumed())
        // proxy.dispose();
    }

    public boolean isClosing() {
        return closing;
    }

    public void windowDeactivated(WindowEvent e) {
        showState("deactivated");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeactivated(fe);
        }
    }

    public void windowDeiconified(WindowEvent e) {
        showState("deiconified");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeiconified(fe);
        }
    }

    public void windowIconified(WindowEvent e) {
        showState("iconified");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameIconified(fe);
        }
    }

    public void windowOpened(WindowEvent e) {
        showState("opened");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameOpened(fe);
        }
    }

    public JSLFrameListener[] getJSLFrameListeners() {
        JSLFrameListener[] type = new JSLFrameListener[0];
        return (JSLFrameListener[]) listeners.toArray(type);
    }

    public void removeJSLFrameListener(JSLFrameListener l) {
        listeners.remove(l);
    }

    public boolean isSelected() {
        return (JSLDesktop.Factory.getDesktop().getSelectedFrame().getJFrame() == this);
    }

    public boolean isIcon() {
        return (getExtendedState() & Frame.ICONIFIED) != 0;
    }

    public void fakeActivate() {
        WindowEvent we =
                new WindowEvent(this, WindowEvent.WINDOW_ACTIVATED, null);
        processWindowEvent(we);
    }
}
