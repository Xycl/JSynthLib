package org.jsynthlib.view.desktop.mdi;

import java.beans.PropertyVetoException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import org.apache.log4j.Logger;
import org.jsynthlib.view.desktop.JSLFrame;
import org.jsynthlib.view.desktop.JSLFrameEvent;
import org.jsynthlib.view.desktop.JSLFrameListener;
import org.jsynthlib.view.desktop.JSLFrameProxy;

/** use JInternalFrame for MDI (Multiple Document Interface) mode. */
public class MDIFrameProxy extends JInternalFrame implements JSLFrameProxy,
        InternalFrameListener {
    
    private static final Logger LOG = Logger.getLogger(MDIFrameProxy.class);

    private static final long serialVersionUID = -385738971246114737L;

    private WeakReference<JSLFrame> parent;
    private List<JSLFrameListener> listeners;

    public MDIFrameProxy(JSLFrame p) {
        super();
        init(p);
    }

    public MDIFrameProxy(JSLFrame p, String title, boolean resizable,
            boolean closable, boolean maximizable, boolean iconifiable) {
        super(title, resizable, closable, maximizable, iconifiable);
        init(p);
    }
    
    final void init(JSLFrame p) {
        listeners = new ArrayList<JSLFrameListener>();
        parent = new WeakReference<JSLFrame>(p);
        addInternalFrameListener(this);
    }

    public void setJMenuBar(JMenuBar m) {
    }

    public JSLFrame getJSLFrame() {
        return (JSLFrame) parent.get();
    }

    public void addJSLFrameListener(JSLFrameListener l) {
        listeners.add(l);
    }

    public void setVisible(boolean b) {
        LOG.info("setVisible : " + getTitle());
        super.setVisible(b);
        try {
            this.setSelected(b);
        } catch (PropertyVetoException e) {
            // don't know how this exception occurs
            e.printStackTrace();
        }
    }

    public void internalFrameActivated(InternalFrameEvent e) {
        LOG.info("\"" + getTitle() + "\" activated.");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameActivated(fe);
        }
    }

    public void internalFrameClosed(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosed(fe);
        }
    }

    public void internalFrameClosing(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosing(fe);
        }
        if (!fe.isConsumed())
            dispose();
    }

    public void internalFrameDeactivated(InternalFrameEvent e) {
        LOG.info("\"" + this.getTitle() + "\" deactivated.");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeactivated(fe);
        }
    }

    public void internalFrameDeiconified(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeiconified(fe);
        }
    }

    public void internalFrameIconified(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameIconified(fe);
        }
    }

    public void internalFrameOpened(InternalFrameEvent e) {
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

    public boolean isClosing() {
        return false;
    }

}
