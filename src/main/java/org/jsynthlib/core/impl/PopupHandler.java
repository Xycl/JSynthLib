package org.jsynthlib.core.impl;

import java.awt.Component;

public interface PopupHandler {

    void showMessage(Component parent, Object message, String title, int msgType);

    void showMessage(Component parent, Object message);
}
