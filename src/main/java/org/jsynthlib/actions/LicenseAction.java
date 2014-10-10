package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.DocumentationWindow;

public class LicenseAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private DocumentationWindow licWin;

    public LicenseAction(Map<Action, Integer> mnemonics) {
        super("License", null);
        this.setEnabled(true);
        mnemonics.put(this, new Integer('L'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (licWin == null) {
                licWin =
                        new DocumentationWindow("text/plain",
                                "LICENSE");
            }
            licWin.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Unable to show Documentation)");
            log.warn(ex.getMessage(), ex);
        }
    }
}
