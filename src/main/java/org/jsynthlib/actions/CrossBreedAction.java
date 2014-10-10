package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.CrossBreedDialog;

public class CrossBreedAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public CrossBreedAction(Map<Action, Integer> mnemonics) {
        super("Cross Breed...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('B'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            CrossBreedDialog xbd =
                    new CrossBreedDialog(PatchEdit.getInstance());
            xbd.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Unable to perform Crossbreed. (No Library selected?)");
            log.warn(ex.getMessage(), ex);
        }
    }
}
