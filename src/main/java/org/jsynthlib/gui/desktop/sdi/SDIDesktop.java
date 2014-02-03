package org.jsynthlib.gui.desktop.sdi;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.jsynthlib.gui.desktop.JSLDesktop;
import org.jsynthlib.gui.desktop.JSLFrame;
import org.jsynthlib.gui.desktop.JSLFrameEvent;

/** fake desktop for SDI (Single Document Interface) mode. */
public class SDIDesktop extends JSLDesktop {
    private static final Logger LOG = Logger.getLogger(SDIDesktop.class);

    protected JSLFrame toolbarFrame;
    /** invisible frame to keep menus when no open windows on MacOSX. */
    private JSLFrame invisibleFrame = null;
    private JSLFrame selected = null;
    /** last selected (activated) frame except toolbar nor invisible frame. */
    private JSLFrame lastSelected = null;
    private boolean invisible;

    public SDIDesktop(boolean invisible, String title, JMenuBar menuBar,
            JToolBar toolBar, Action exitAction) {
        super(exitAction);
        this.invisible = invisible;
        if (invisible) {
            createInvisibleWindow(menuBar);
        } else {
            createToolBarWindow(title + " Tool Bar", menuBar, toolBar);
        }

    }

    /** Create invisible window to keep menus when no open windows */
    private void createInvisibleWindow(JMenuBar mb) {
        invisibleFrame = new JSLFrame();
        JFrame frame = invisibleFrame.getJFrame();
        frame.setTitle("Please enable ScreenMenuBar.");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (mb != null)
            frame.setJMenuBar(mb);
        frame.setSize(0, 0);
        frame.setUndecorated(true);
        // frame(0,0x7FFFFFFF);
        frame.pack();
        frame.setVisible(true);
        // frame.addWindowListener(this);
        selected = invisibleFrame;
    }

    /** create a toolbar window */
    private void createToolBarWindow(String title, JMenuBar mb, JToolBar tb) {
        toolbarFrame = new JSLFrame();
        JFrame frame = toolbarFrame.getJFrame();
        toolbarFrame.setTitle(title);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // toolbar.addJSLFrameListener(this);
        if (mb != null)
            toolbarFrame.setJMenuBar(mb);
        tb.setFloatable(false);
        toolbarFrame.getContentPane().add(tb);
        toolbarFrame.pack();

        Dimension gs = frame.getGlassPane().getSize();
        Dimension ts = frame.getSize();
        xdecoration = (int) (ts.getWidth() - gs.getWidth());
        ydecoration = (int) (ts.getHeight() - gs.getHeight());
        toolbarFrame.setLocation(xdecoration / 2, ydecoration);

        this.add(toolbarFrame);
        toolbarFrame.setVisible(true);
    }

    protected void addInternal(JSLFrame f) {
    }

    public void updateLookAndFeel() {
        // update toolbar
        if (toolbarFrame != null) { // wirski@op.pl
            SwingUtilities.updateComponentTreeUI(toolbarFrame.getJFrame());
            toolbarFrame.pack();
        }
        ;
        // update each Frame
        Iterator<JSLFrame> it = windows.iterator();
        while (it.hasNext()) {
            JFrame frame = it.next().getJFrame();
            SwingUtilities.updateComponentTreeUI(frame);
            frame.pack();
        }
    }

    public Dimension getSize() {
        return Toolkit.getDefaultToolkit().getScreenSize();
    }

    public JFrame getSelectedWindow() {
        return selected.getJFrame();
    }

    public JSLFrame getSelectedJSLFrame() {
        return lastSelected;
    }

    public JSLFrame[] getAllJSLFrames() {
        return windows.toArray(new JSLFrame[windows.size()]);
    }

    // JSLFrameListener methods : called for both toolbar and JSLFrame
    private void showState(JSLFrame f, String s) {
        LOG.info("\"" + f.getTitle() + "\" " + s);
    }

    @Override
    public void JSLFrameClosed(JSLFrameEvent e) {
        super.JSLFrameClosed(e);
        if (windows.isEmpty() || (toolbarFrame != null && windows.size() == 1))
            frame_count = 0; // reset frame position

    }

    public void FrameActivated(JSLFrame f) {
        if (f == toolbarFrame) {
            synchronized (in_fake_activation) {
                if (in_fake_activation.booleanValue())
                    return;
                // When toolbar is activated, activate the last selected
                // frame if it is not iconified nor closing.
                if (lastSelected != null
                // && last_selected != toolbar
                        && !lastSelected.isIcon() && !lastSelected.isClosing()) {
                    showState(lastSelected, "FakeActivated");
                    in_fake_activation = Boolean.TRUE;
                    ((SDIFrameProxy) lastSelected.getJFrame()).fakeActivate();
                    in_fake_activation = Boolean.FALSE;
                }
            }
        } else if (f != invisibleFrame)
            lastSelected = f;

        selected = f;
        showState(f, "selected : " + selected);
    }

    public void FrameClosing(JSLFrame f) {
        // if (f == toolbar && confirmExiting())
        // exitAction.actionPerformed(null);
    }

    public void FrameClosed(JSLFrame f) {
        showState(f, "closed. " + (windows.size() - 1) + " windows still open.");
        if (lastSelected == f)
            lastSelected = null;
    }

    @Override
    protected Point getDefaultLocation(Dimension frameSize) {
        int xofs = 0;
        int yofs = 0;
        int xsep = 30;
        int ysep = 30;

        if (invisible) { // no toolbar window
            xofs = 100;
            yofs = 100;
        } else {
            JFrame tb = toolbarFrame.getJFrame();
            if (tb.getLocation().getY() < 100) { // Do we need this check?
                xofs = (int) tb.getLocation().getX();
                yofs =
                        (int) (tb.getLocation().getY() + tb.getSize()
                                .getHeight());
            }
            ysep = ydecoration;
        }

        return getDefaultLoationInternal(frameSize, xofs, yofs, xsep, ysep);
    }

    @Override
    public JFrame getInvisible() {
        return invisibleFrame.getJFrame();
    }

}
