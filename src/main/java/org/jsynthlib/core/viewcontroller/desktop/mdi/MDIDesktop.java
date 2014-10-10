package org.jsynthlib.core.viewcontroller.desktop.mdi;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameProxy;

/** use JDesktopPane for MDI (Multiple Document Interface) mode. */
public class MDIDesktop extends JSLDesktop {

    private final JDesktopPane desktopPane;
    private final JFrame frame;
    private static final int INSET = 100;

    public MDIDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {
        super(exitAction);
        desktopPane = new JDesktopPane();
        frame = new JFrame(title);
        // Emenaker - 2006-02-02
        // TODO: Move the actual filename to some central config location
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/images/JSLIcon48x48.png")));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (!getAppConfig().getMainWindowBounds(frame)) {
            frame.setBounds(INSET, INSET, screenSize.width - INSET * 2,
                    screenSize.height - INSET * 2);
        }

        // Quit this app when the big window closes.
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                getAppConfig().setMainWindowBounds(frame);
                closingProc();
            }
        });

        Container c = frame.getContentPane();
        if (tb != null) {
            c.add(tb, BorderLayout.NORTH);
            tb.setVisible(true);
        }
        c.add(desktopPane, BorderLayout.CENTER);
        if (mb != null) {
            frame.setJMenuBar(mb);
        }
        desktopPane.setOpaque(false);
        desktopPane.putClientProperty("JDesktopPane.dragMode", "outline");

        frame.setVisible(true);
    }

    @Override
    protected void addInternal(JSLFrame f) {
        desktopPane.add(f.getJInternalFrame());
    }

    @Override
    public void updateLookAndFeel() {
        SwingUtilities.updateComponentTreeUI(frame);
        // selected.pack();
    }

    @Override
    public JFrame getSelectedWindow() {
        return frame;
    }

    @Override
    public JSLFrame getSelectedFrame() {
        try {
            return ((JSLFrameProxy) desktopPane.getSelectedFrame())
                    .getJSLFrame();
        } catch (NullPointerException e) {
            return null; // This is normal.
        }
    }

    @Override
    public JSLFrame[] getAllJSLFrames() {
        JInternalFrame[] ifs = desktopPane.getAllFrames();
        JSLFrame[] a = new JSLFrame[ifs.length];

        for (int i = 0; i < ifs.length; i++) {
            if (ifs[i] instanceof JSLFrameProxy) {
                a[i] = ((JSLFrameProxy) ifs[i]).getJSLFrame();
            }
        }
        return a;
    }

    @Override
    public void FrameActivated(JSLFrame f) {
    }

    @Override
    public void FrameClosing(JSLFrame f) {
    }

    @Override
    public void FrameClosed(JSLFrame f) {
    }

    @Override
    protected Point getDefaultLocation(Dimension frameSize) {
        int xofs = 0;
        int yofs = 0;
        int xsep = 30;
        int ysep = 30;
        return getDefaultLoationInternal(frameSize, xofs, yofs, xsep, ysep);
    }

    @Override
    public Dimension getSize() {
        return desktopPane.getSize();
    }

    @Override
    public JFrame getRootFrame() {
        return frame;
    }
}
