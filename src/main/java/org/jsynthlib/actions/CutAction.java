package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class CutAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public CutAction(Map<Action, Integer> mnemonics) {
        super("Cut", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('T'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchBasket) {
                PatchBasket patchBasket = (PatchBasket) selectedFrame;
                patchBasket.copySelectedPatch();
                patchBasket.deleteSelectedPatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to cut must be hilighted\nin the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
