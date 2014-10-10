package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.Actions;

public class SaveAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public SaveAction(Map<Action, Integer> mnemonics) {
        super("Save", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('S'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Actions.saveFrame();
    }
}
