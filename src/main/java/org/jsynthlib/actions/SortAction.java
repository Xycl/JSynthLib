package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.SortDialog;

public class SortAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public SortAction(Map<Action, Integer> mnemonics) {
        super("Sort...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('R'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            SortDialog sd = new SortDialog(PatchEdit.getInstance());
            sd.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Library to Sort must be Focused");
            log.warn(ex.getMessage(), ex);
        }
    }
}
