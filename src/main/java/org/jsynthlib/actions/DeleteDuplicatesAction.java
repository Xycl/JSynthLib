package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JOptionPane;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.LibraryFrame;

public class DeleteDuplicatesAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public DeleteDuplicatesAction(Map<Action, Integer> mnemonics) {
        super("Delete Dups...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('D'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (JOptionPane
                .showConfirmDialog(
                        null,
                        "This Operation will change the ordering of the Patches. Continue?",
                        "Delete Duplicate Patches",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
            return;
        }

        int numDeleted = 0;
        PatchEdit.showWaitDialog("Deleting duplicates...");
        try {
            numDeleted =
                    ((LibraryFrame) getSelectedFrame()).deleteDuplicates();
        } catch (Exception ex) {
            PatchEdit.hideWaitDialog();
            ErrorMsg.reportError("Error",
                    "Library to Delete Duplicates in must be Focused");
            log.warn(ex.getMessage(), ex);
            return;
        }
        PatchEdit.hideWaitDialog();

        JOptionPane.showMessageDialog(null, numDeleted
                + " Patches and Scenes were deleted", "Delete Duplicates",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
