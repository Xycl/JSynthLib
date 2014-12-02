package org.jsynthlib.core.impl;

import java.awt.Component;

import javax.swing.JOptionPane;

public class PopupHandlerImpl implements PopupHandler {

    @Override
    public void showMessage(Component parent, Object message, String title,
            int msgType) {
        JOptionPane.showMessageDialog(parent, message, title, msgType);
    }

    @Override
    public void showMessage(Component parent, Object message) {
        JOptionPane.showMessageDialog(parent, message);
    }

}
