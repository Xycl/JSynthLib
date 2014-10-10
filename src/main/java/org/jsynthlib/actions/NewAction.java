package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.patch.viewcontroller.LibraryFrame;

public class NewAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public NewAction(Map<Action, Integer> mnemonics) {
        super("New Library", null);
        mnemonics.put(this, new Integer('N'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Actions.addLibraryFrame(new LibraryFrame());
    }
}
