package org.jsynthlib.view.desktop.mdi;

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

import org.jsynthlib.view.desktop.JSLDesktop;
import org.jsynthlib.view.desktop.JSLFrame;
import org.jsynthlib.view.desktop.JSLFrameProxy;

import core.AppConfig;

/** use JDesktopPane for MDI (Multiple Document Interface) mode. */
public class MDIDesktop extends JSLDesktop {

    private JDesktopPane desktopPane;
    private JFrame frame;
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
        if (!AppConfig.getMainWindowBounds(frame))
            frame.setBounds(INSET, INSET, screenSize.width - INSET * 2,
                    screenSize.height - INSET * 2);

        // Quit this app when the big window closes.
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AppConfig.setMainWindowBounds(frame);
                closingProc();
            }
        });

        Container c = frame.getContentPane();
        if (tb != null) {
            c.add(tb, BorderLayout.NORTH);
            tb.setVisible(true);
        }
        c.add(desktopPane, BorderLayout.CENTER);
        if (mb != null)
            frame.setJMenuBar(mb);
        desktopPane.setOpaque(false);
        desktopPane.putClientProperty("JDesktopPane.dragMode", "outline");

        frame.setVisible(true);
    }

    protected void addInternal(JSLFrame f) {
        desktopPane.add(f.getJInternalFrame());
    }

    public void updateLookAndFeel() {
        SwingUtilities.updateComponentTreeUI(frame);
        // selected.pack();
    }

    public JFrame getSelectedWindow() {
        return frame;
    }

    public JSLFrame getSelectedJSLFrame() {
        try {
            return ((JSLFrameProxy) desktopPane.getSelectedFrame())
                    .getJSLFrame();
        } catch (NullPointerException e) {
            return null; // This is normal.
        }
    }

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

    public void FrameActivated(JSLFrame f) {
    }

    public void FrameClosing(JSLFrame f) {
    }

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
