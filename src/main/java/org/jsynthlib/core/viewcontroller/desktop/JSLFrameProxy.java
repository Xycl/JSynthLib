package org.jsynthlib.core.viewcontroller.desktop;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusListener;

import javax.swing.JMenuBar;

public interface JSLFrameProxy {
    void moveToFront();

    void setSelected(boolean b)
            throws java.beans.PropertyVetoException;

    void setClosed(boolean b)
            throws java.beans.PropertyVetoException;

    Container getContentPane();

    void setTitle(String title);

    void repaint();

    String getTitle();

    void addJSLFrameListener(JSLFrameListener l);

    void setJMenuBar(JMenuBar m);

    JSLFrame getJSLFrame();

    void addFocusListener(FocusListener l);

    void setSize(int w, int h);

    Dimension getSize();

    Dimension getSize(Dimension rv);

    Dimension getMinimumSize();

    void pack();

    // void show();
    void dispose();

    int getX();

    int getY();

    boolean isVisible();

    void setVisible(boolean b);

    void setLocation(int x, int y);

    void setLocation(Point p);

    JSLFrameListener[] getJSLFrameListeners();

    void removeJSLFrameListener(JSLFrameListener l);

    // void setPreferredSize(Dimension d);
    boolean isSelected();

    boolean isShowing();

    boolean isIcon();

    boolean isClosing();

}
