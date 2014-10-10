package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchHandler;

public class PlayAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public PlayAction(Map<Action, Integer> mnemonics) {
        super("Play", null);
        mnemonics.put(this, new Integer('P'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchHandler) {
                PatchHandler patchHandler = (PatchHandler) selectedFrame;
                patchHandler.playSelectedPatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to Play must be highlighted in the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
