package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

/**
 * @author wirski@op.pl
 */
public class UpdateSceneAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public UpdateSceneAction(Map<Action, Integer> mnemonics) {
        super("Update Scene", null);
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            ((SceneFrame) getSelectedFrame()).updateScene();
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Scene Library must be the selected window.");
            log.warn(ex.getMessage(), ex);
        }
    }
}
