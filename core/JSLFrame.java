package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
import java.lang.ref.WeakReference;

// addJSLFrameListener should probably be implemented in JSLFrame
// for dynamic switching of type
public class JSLFrame {
    protected JSLFrameProxy proxy;
    private static boolean useIFrames =
	(PatchEdit.appConfig.getGuiStyle() == 0);

    // for JSLJFrame
    private static JFrame lastselection = null;


    public JSLFrame() {
	if (useIFrames)
	    proxy = new JSLIFrame(this);
	else
	    proxy = new JSLJFrame(this);
	if (PatchEdit.desktop != null)
	    PatchEdit.desktop.registerFrame(this);
    }
    public JSLFrame(String s, boolean resizable, boolean closable,
		    boolean maximizable, boolean iconifiable) {
	if (useIFrames)
	    proxy = new JSLIFrame(this,s, resizable, closable, maximizable,
				  iconifiable);
	else
	    proxy = new JSLJFrame(this, s);
	if (PatchEdit.desktop != null)
	    PatchEdit.desktop.registerFrame(this);
    }
    JFrame getJFrame() { 
	if (proxy instanceof JFrame)
	    return (JFrame)proxy;
	else
	    return null;
    }
    public void moveToFront() { proxy.moveToFront(); }
    public void setSelected(boolean b)
	    throws java.beans.PropertyVetoException { proxy.setSelected(b); }
    public void setClosed(boolean b)
    	    throws java.beans.PropertyVetoException { proxy.setClosed(b); }
    public void setPreferredSize(Dimension d) { proxy.setPreferredSize(d); }
    public Dimension getMinimumSize() { return proxy.getMinimumSize(); }
    public Container getContentPane() { return proxy.getContentPane(); }
    public void setTitle(String title) { proxy.setTitle(title); }
    public void repaint() { proxy.repaint(); }
    public String getTitle() { return proxy.getTitle(); }
    public void addFocusListener(FocusListener l) {proxy.addFocusListener(l);}
    public void setSize(int w, int h) { proxy.setSize(w, h); }
    public Dimension getSize() { return proxy.getSize(); }
    public Dimension getSize(Dimension rv) { return proxy.getSize(rv); }
    public void setLocation(int x, int y) { proxy.setLocation(x,y); }
    public void setVisible(boolean b) { proxy.setVisible(b); }
    public boolean isVisible() { return proxy.isVisible(); }
    public void show() { proxy.show(); }
    public void pack() { proxy.pack(); }
    public void dispose() { proxy.dispose(); }
    public int getX() { return proxy.getX(); }
    public int getY() { return proxy.getY(); }
    public void reshape(int a, int b, int c, int d) { proxy.reshape(a,b,c,d); }
    public boolean isSelected() { return proxy.isSelected(); }
    // This is probably naughty.
    public JSLDesktop getDesktopPane() { return PatchEdit.desktop; }
    public JSLFrameListener[] getJSLFrameListeners() {
	return proxy.getJSLFrameListeners();
    }
    public void removeJSLFrameListener(JSLFrameListener l) {
	proxy.removeJSLFrameListener(l);
    }
    public void addJSLFrameListener(JSLFrameListener l) {
	proxy.addJSLFrameListener(l);
    }
    public void setJMenuBar(JMenuBar m) { proxy.setJMenuBar(m); }

    static boolean useInternalFrames() { return useIFrames; }

    JSLFrameProxy getProxy() { return proxy; }

    interface JSLFrameProxy {
	public void moveToFront();
	public void setSelected(boolean b)
	    throws java.beans.PropertyVetoException;
	public void setClosed(boolean b)
	    throws java.beans.PropertyVetoException;
	public Container getContentPane();
	public void setTitle(String title);
	public void repaint();
	public String getTitle();
	public void addJSLFrameListener(JSLFrameListener l);
	public void setJMenuBar(JMenuBar m);
	public JSLFrame getJSLFrame();
	public void addFocusListener(FocusListener l);
	public void setSize(int w, int h);
	public Dimension getSize();
	public Dimension getSize(Dimension rv);
	public Dimension getMinimumSize();
	public void pack();
	public void show();
	public void dispose();
	public int getX();
	public int getY();
	public boolean isVisible();
	public void reshape(int a, int b, int c, int d);
	public void setVisible(boolean b);
	public void setLocation(int x, int y);
	public JSLFrameListener[] getJSLFrameListeners();
	public void removeJSLFrameListener(JSLFrameListener l);
	public void setPreferredSize(Dimension d);
	public boolean isSelected();
    }

    private class JSLIFrame extends JInternalFrame implements JSLFrameProxy,
						       InternalFrameListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();
	public JSLIFrame(JSLFrame p) {
	    super();
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public JSLIFrame(JSLFrame p, String title) {
	    super(title);
	    parent = new WeakReference(p);
	    addInternalFrameListener(this);
	}
	public JSLIFrame(JSLFrame p, String s, boolean resizable, boolean			 closable, boolean maximizable, boolean iconifiable) {
	    super(s, resizable, closable, maximizable, iconifiable);
	    parent = new WeakReference(p);
	}
	public void setJMenuBar(JMenuBar m) {}
	public JSLFrame getJSLFrame() {
	    return (JSLFrame)parent.get();
	}
	public void addJSLFrameListener(JSLFrameListener l) {
	    listeners.add(l);
	}
	
	public void internalFrameActivated(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	}
	public void internalFrameClosed(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosed(fe);
	    }
	}
	public void internalFrameClosing(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosing(fe);
	    }
	    if (!fe.isConsumed())
		proxy.dispose();
	}
	public void internalFrameDeactivated(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeactivated(fe);
	    }
	}
	public void internalFrameDeiconified(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeiconified(fe);
	    }
	}
	public void internalFrameIconified(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameIconified(fe);
	    }
	}
	public void internalFrameOpened(InternalFrameEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameOpened(fe);
	    }
	}
	public JSLFrameListener[] getJSLFrameListeners() {
	    JSLFrameListener[] type = new JSLFrameListener[0];
	    return (JSLFrameListener[])listeners.toArray(type);
	}
	public void removeJSLFrameListener(JSLFrameListener l) {
	    listeners.remove(l);
	}
    }
    private class JSLJFrame extends JFrame implements JSLFrameProxy,
						      WindowListener {
	private WeakReference parent;
	protected ArrayList listeners = new ArrayList();
	double takemem[] = null;

	public JSLJFrame(JSLFrame p) {
	    super();
	    addWindowListener(this);
	    parent = new WeakReference(p);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    takemem = new double[1000000];
	}
	public JSLJFrame(JSLFrame p, String title) {
	    super(title);
	    addWindowListener(this);
	    parent = new WeakReference(p);
	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    takemem = new double[1000000];
	}
	public JSLFrame getJSLFrame() { return (JSLFrame)parent.get(); }
	public void moveToFront() { toFront(); }
	public void setSelected(boolean b) { if (!isFocused() && b) toFront(); }
	public void setClosed(boolean b) {
	    if (b)
		processWindowEvent(new WindowEvent(this,
						   WindowEvent.WINDOW_CLOSING));
	}

	public void addJSLFrameListener(JSLFrameListener l) {
	    listeners.add(l);
	}
	
	public void setVisible(boolean b) {
	    if (MacUtils.isMac()) {
		if (b && getJMenuBar() == null) {
		    setJMenuBar(PatchEdit.createMenuBar());
		} else if (!b) {
		    // Remove menubar so frame can be disposed.
		    // http://archives:archives@lists.apple.com/archives/java-dev/2003/Dec/04/disposingofjframesusescr.001.txt
		    JMenuBar mb = getJMenuBar();
		    setJMenuBar(null);
		    
		    PatchEdit.desktop.getInvisible().getJFrame().requestFocus();
		    requestFocus();
		}
	    }
	    super.setVisible(b);
	}
	public void windowActivated(WindowEvent e) {
	    if (e.getWindow() == PatchEdit.desktop.getToolBar().getJFrame()) {
		if (e.getOppositeWindow() instanceof JSLFrame.JSLFrameProxy ) {
		    lastselection = (JFrame)e.getOppositeWindow();
		} else {
		    lastselection = null;
		}
	    } else if (e.getOppositeWindow()  ==
		               PatchEdit.desktop.getToolBar().getJFrame()) {
		if (e.getWindow() == lastselection) {
		    lastselection = null;
		    return;
		} else if (lastselection != null) {
		    WindowEvent ne = 
			new WindowEvent(lastselection,
					WindowEvent.WINDOW_DEACTIVATED,
					e.getWindow());
		    // We don't tell who the opposite window is, so this
		    // isn't necessary.
		    /*
		    e = new WindowEvent(e.getWindow(),
					WindowEvent.WINDOW_ACTIVATED,
					lastselection);
		    */
		    lastselection = null;
		    windowDeactivated(ne);
		}
	    }
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(),
				  JSLFrameEvent.ACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameActivated(fe);
	    }
	}
	public void windowClosed(WindowEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosed(fe);
	    }
	}
	public void windowClosing(WindowEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.CLOSING);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameClosing(fe);
	    }
	    //if (!fe.isConsumed())
	    //	proxy.dispose();
	}
	public void windowDeactivated(WindowEvent e) {
	    if (e.getOppositeWindow() == 
		        PatchEdit.desktop.getToolBar().getJFrame()) {
		// Don't notify if we're focusing the toolbar.
		return;
	    }
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEACTIVATED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeactivated(fe);
	    }
	}
	public void windowDeiconified(WindowEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.DEICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameDeiconified(fe);
	    }
	}
	public void windowIconified(WindowEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.ICONIFIED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameIconified(fe);
	    }
	}
	public void windowOpened(WindowEvent e) {
	    JSLFrameEvent fe = 
		new JSLFrameEvent(getJSLFrame(), JSLFrameEvent.OPENED);
	    Iterator it = listeners.iterator();
	    while (it.hasNext()) {
		((JSLFrameListener)it.next()).JSLFrameOpened(fe);
	    }
	}
	public JSLFrameListener[] getJSLFrameListeners() {
	    JSLFrameListener[] type = new JSLFrameListener[0];
	    return (JSLFrameListener[])listeners.toArray(type);
	}
	public void removeJSLFrameListener(JSLFrameListener l) {
	    listeners.remove(l);
	}
	public void setPreferredSize(Dimension d) {}
	// Pretend to be selected if switched to toolbar from this frame.
	public boolean isSelected() {
	    //return isActive() || lastselection == this;
	    return PatchEdit.desktop.getSelectedWindow() == this;
	}
    }
}

