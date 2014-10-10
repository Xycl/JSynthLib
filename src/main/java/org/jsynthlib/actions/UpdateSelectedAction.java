package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

public class UpdateSelectedAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public UpdateSelectedAction(Map<Action, Integer> mnemonics) {
        super("Update", null);
        setEnabled(false);
        mnemonics.put(this, new Integer('U'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ((SceneFrame) getSelectedFrame()).updateSelected();
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patches to update must be highlighted\nin the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
