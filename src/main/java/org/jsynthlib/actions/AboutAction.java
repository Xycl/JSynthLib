package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.inject.JSynthLibInjector;

public class AboutAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private final AppConfig appConfig;

    public AboutAction(Map<Action, Integer> mnemonics) {
        super("About");
        mnemonics.put(this, new Integer('A'));
        appConfig = JSynthLibInjector.getInstance(AppConfig.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        StringBuilder text = new StringBuilder();
        text.append("JSynthLib Version ").append(appConfig.getJSLVersion())
        .append("\nCopyright (C) 2000-2014 Brian Klock et al.\n")
        .append("See 'Help -> License' for more info.");
        PopupHandlerProvider.get().showMessage(null, text.toString(),
                "About JSynthLib", JOptionPane.INFORMATION_MESSAGE);
    }

}
