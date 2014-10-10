package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

public class NewSceneAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public NewSceneAction(Map<Action, Integer> mnemonics) {
        super("New Scene", null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Actions.addLibraryFrame(new SceneFrame());
    }
}
