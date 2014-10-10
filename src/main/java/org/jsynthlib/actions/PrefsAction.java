package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.patch.model.impl.PatchEdit;

public class PrefsAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public PrefsAction(Map<Action, Integer> mnemonics) {
        super("Preferences...", null);
        mnemonics.put(this, new Integer('P'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PatchEdit.showPrefsDialog();
    }
}
