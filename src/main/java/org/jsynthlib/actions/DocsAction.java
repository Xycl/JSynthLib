package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.DocumentationWindow;

public class DocsAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private DocumentationWindow docWin;

    public DocsAction(Map<Action, Integer> mnemonics) {
        super("Help", null);
        this.setEnabled(true);
        mnemonics.put(this, new Integer('H'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (docWin == null) {
                docWin =
                        new DocumentationWindow("text/html",
                                "documentation.html");
            }
            docWin.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Unable to show Documentation)");
            log.warn(ex.getMessage(), ex);
        }
    }
}
