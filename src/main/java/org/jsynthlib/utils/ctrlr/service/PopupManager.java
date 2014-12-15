package org.jsynthlib.utils.ctrlr.service;

import java.util.List;

import org.jsynthlib.core.impl.PopupHandler;

public interface PopupManager extends PopupHandler {

    class PopupSession {
    }

    PopupSession openSession();

    List<String> closeSession(PopupSession session);
}
