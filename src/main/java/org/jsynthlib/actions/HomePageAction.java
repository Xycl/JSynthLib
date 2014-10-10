package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.DocumentationWindow;

public class HomePageAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private DocumentationWindow hpWin;

    public HomePageAction(Map<Action, Integer> mnemonics) {
        super("JSynthLib Home Page", null);
        this.setEnabled(true);
        mnemonics.put(this, new Integer('P'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (hpWin == null) {
                hpWin =
                        new DocumentationWindow("text/html",
                                "http://www.jsynthlib.org/");
            }
            hpWin.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Unable to show Documentation)");
            log.warn(ex.getMessage(), ex);
        }
    }
}
