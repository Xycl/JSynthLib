package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;

// Added by Joe Emenaker - 2005-10-24
public class PrintAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public PrintAction(Map<Action, Integer> mnemonics) {
        super("Print", null);
        // mnemonics.put(this, new Integer(''));
        this.setEnabled(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (getSelectedFrame() instanceof BankEditorFrame) {
                ((BankEditorFrame) getSelectedFrame()).printPatch();
            } else {
                ErrorMsg.reportError("Error",
                        "You can only print a Bank window.");
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to Play must be highlighted in the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
