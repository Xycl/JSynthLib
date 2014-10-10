package org.jsynthlib.core.viewcontroller.desktop.mdi;

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
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameEvent;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameListener;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameProxy;

/** use JInternalFrame for MDI (Multiple Document Interface) mode. */
public class MDIFrameProxy extends JInternalFrame implements JSLFrameProxy,
        InternalFrameListener {

    private static final Logger LOG = Logger.getLogger(MDIFrameProxy.class);

    private static final long serialVersionUID = 1L;

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

    @Override
    public void setJMenuBar(JMenuBar m) {
    }

    @Override
    public JSLFrame getJSLFrame() {
        return parent.get();
    }

    @Override
    public void addJSLFrameListener(JSLFrameListener l) {
        listeners.add(l);
    }

    @Override
    public void setVisible(boolean b) {
        LOG.debug("setVisible : " + getTitle());
        super.setVisible(b);
        try {
            this.setSelected(b);
        } catch (PropertyVetoException e) {
            // don't know how this exception occurs
            LOG.warn(e.getMessage(), e);
        }
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        LOG.debug("\"" + getTitle() + "\" activated.");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameActivated(fe);
        }
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosed(fe);
        }
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameClosing(fe);
        }
        if (!fe.isConsumed()) {
            dispose();
        }
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        LOG.debug("\"" + this.getTitle() + "\" deactivated.");
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeactivated(fe);
        }
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameDeiconified(fe);
        }
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
        JSLFrameEvent fe =
                new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
        Iterator<JSLFrameListener> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().JSLFrameIconified(fe);
        }
    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
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
    public boolean isClosing() {
        return false;
    }

}
