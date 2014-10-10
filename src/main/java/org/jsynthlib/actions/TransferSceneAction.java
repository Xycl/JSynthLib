package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

public class TransferSceneAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public TransferSceneAction(Map<Action, Integer> mnemonics) {
        super("Transfer Scene", null); // show a dialog frame???
        // mnemonics.put(this, new Integer('S'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ((SceneFrame) getSelectedFrame()).sendScene();
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Scene Library must be the selected window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
