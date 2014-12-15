package org.jsynthlib.utils.ctrlr.service.impl;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsynthlib.utils.ctrlr.service.PopupManager;

import com.google.inject.Singleton;

@Singleton
public class PopupManagerImpl implements PopupManager {

    private final Map<PopupSession, List<String>> sessionMap;

    public PopupManagerImpl() {
        sessionMap = new HashMap<PopupManager.PopupSession, List<String>>();
    }

    @Override
    public void showMessage(Component parent, Object message, String title,
            int msgType) {
        notifyListeners(message);
    }

    @Override
    public void showMessage(Component parent, Object message) {
        notifyListeners(message);
    }

    synchronized void notifyListeners(Object msg) {
        for (List<String> list : sessionMap.values()) {
            list.add(msg.toString());
        }
    }

    @Override
    public synchronized PopupSession openSession() {
        ArrayList<String> list = new ArrayList<String>();
        PopupSession session = new PopupSession();
        sessionMap.put(session, list);
        return session;
    }

    @Override
    public synchronized List<String> closeSession(PopupSession session) {
        return sessionMap.remove(session);
    }

}
