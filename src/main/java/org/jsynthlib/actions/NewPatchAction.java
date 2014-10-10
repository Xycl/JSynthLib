package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.NewPatchDialog;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class NewPatchAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public NewPatchAction(Map<Action, Integer> mnemonics) {
        super("New Patch...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('N'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            NewPatchDialog np = new NewPatchDialog(PatchEdit.getInstance());
            np.setVisible(true);
            IPatch p = np.getNewPatch();
            JSLFrame selectedFrame = getSelectedFrame();
            if (p != null && selectedFrame instanceof PatchBasket) {
                PatchBasket patchBasket = (PatchBasket) selectedFrame;
                patchBasket.pastePatch(p);
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Unable to create this new patch.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
