package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.SearchDialog;

public class SearchAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private SearchDialog searchDialog;

    public SearchAction(Map<Action, Integer> mnemonics) {
        super("Search...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('E'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if (searchDialog == null) {
                searchDialog = new SearchDialog(PatchEdit.getInstance());
            }
            searchDialog.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Library to Sort must be Focused");
            log.warn(ex.getMessage(), ex);
        }
    }
}
