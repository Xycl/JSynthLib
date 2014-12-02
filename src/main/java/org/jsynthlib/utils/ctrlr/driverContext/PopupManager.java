package org.jsynthlib.utils.ctrlr.driverContext;

import java.util.List;

public interface PopupManager {

    class PopupSession {
    }

    PopupSession openSession();

    List<String> closeSession(PopupSession session);
}
