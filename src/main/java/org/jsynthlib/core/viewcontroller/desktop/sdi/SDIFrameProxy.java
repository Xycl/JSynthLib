package org.jsynthlib.core.viewcontroller.desktop.sdi;

import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameEvent;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameListener;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameProxy;

/** use JFrame for SDI (Single Document Interface) mode. */
public class SDIFrameProxy extends JFrame implements JSLFrameProxy,
        WindowListener {

    private static final Logger LOG = Logger.getLogger(SDIFrameProxy.class);

    private static final long serialVersionUID = 1L;

    private final WeakReference<JSLFrame> parent;
    protected List<JSLFrameListener> listeners =
            new ArrayList<JSLFrameListener>();
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

    @Override
    public JSLFrame getJSLFrame() {
        return parent.get();
    }

    @Override
    public void moveToFront() {
        toFront();
    }

    @Override
    public void setSelected(boolean b) {
        if (!isFocused() && b) {
            toFront();
        }
    }

    @Override
    public void setClosed(boolean b) {
        if (b) {
            processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void addJSLFrameListener(JSLFrameListener l) {
        listeners.add(l);
    }

    @Override
    public void setVisible(boolean b) {
        // deiconified
        if (b && isIcon()) {
            setExtendedState(getExtendedState() & ~ICONIFIED);
        }
        super.setVisible(b);
    }

    @Override
    public void windowActivated(WindowEvent e) {
        LOG.info("activated (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameActivated(fe);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        LOG.info("closed (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosed(fe);
        }
        closing = false;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        LOG.info("closing (" + this.getExtendedState() + ")");
        closing = true;
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosing(fe);
        }
    }

    @Override
    public boolean isClosing() {
        return closing;
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        LOG.info("deactivated (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeactivated(fe);
        }
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        LOG.info("deiconified (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeiconified(fe);
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
        LOG.info("iconified (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameIconified(fe);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
        LOG.info("opened (" + this.getExtendedState() + ")");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameOpened(fe);
        }
    }

    @Override
    public JSLFrameListener[] getJSLFrameListeners() {
        JSLFrameListener[] type = new JSLFrameListener[0];
        return listeners.toArray(type);
    }

    @Override
    public void removeJSLFrameListener(JSLFrameListener l) {
        listeners.remove(l);
    }

    @Override
    public boolean isSelected() {
        return (JSLDesktop.Factory.getDesktop().getSelectedFrame().getJFrame() == this);
    }

    @Override
    public boolean isIcon() {
        return (getExtendedState() & Frame.ICONIFIED) != 0;
    }

    public void fakeActivate() {
        WindowEvent we =
                new WindowEvent(this, WindowEvent.WINDOW_ACTIVATED, null);
        processWindowEvent(we);
    }
}
