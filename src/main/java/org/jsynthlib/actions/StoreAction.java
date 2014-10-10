package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchHandler;

public class StoreAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public StoreAction(Map<Action, Integer> mnemonics) {
        super("Store...", null);
        mnemonics.put(this, new Integer('R'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchHandler) {
                PatchHandler patchHandler = (PatchHandler) selectedFrame;
                patchHandler.storeSelectedPatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to Store must be highlighted in the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
