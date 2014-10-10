package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.UploadPatchDialog;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class UploadAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public UploadAction(Map<Action, Integer> mnemonics) {
        super("Upload...", null);
        mnemonics.put(this, new Integer('U'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UploadPatchDialog myDialog =
                new UploadPatchDialog(PatchEdit.getInstance());
        myDialog.setVisible(true);
    }
}
