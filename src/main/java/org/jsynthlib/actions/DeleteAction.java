package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class DeleteAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public DeleteAction(Map<Action, Integer> mnemonics) {
        super("Delete", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('D'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchBasket) {
                PatchBasket patchBasket = (PatchBasket) selectedFrame;
                patchBasket.deleteSelectedPatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to delete must be hilighted\nin the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
