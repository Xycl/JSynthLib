package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.viewcontroller.AbstractLibraryFrame;

public class ExtractAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public ExtractAction(Map<Action, Integer> mnemonics) {
        super("Extract", null);
        mnemonics.put(this, new Integer('E'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ((AbstractLibraryFrame) getSelectedFrame())
                    .extractSelectedPatch();
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Can not Extract (Maybe its not a bank?)");
            log.warn(ex.getMessage(), ex);
        }
    }
}
