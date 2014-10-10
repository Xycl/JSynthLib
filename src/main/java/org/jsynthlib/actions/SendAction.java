package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchHandler;

public class SendAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public SendAction(Map<Action, Integer> mnemonics) {
        super("Send", null);
        mnemonics.put(this, new Integer('S'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchHandler) {
                PatchHandler patchHandler = (PatchHandler) selectedFrame;
                patchHandler.sendSelectedPatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Patch to Send must be highlighted in the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
