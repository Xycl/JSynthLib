package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;

public class CloseAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public CloseAction(Map<Action, Integer> mnemonics) {
        super("Close", null);
    }

    @Override
    public void actionPerformed(ActionEvent ex) {
        try {
            JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
            JSLFrame frame = desktop.getSelectedFrame();
            if (frame != null) {
                frame.setClosed(true);
            } else {
                JSLDesktop.Factory.getDesktop().closingProc();
            }
        } catch (PropertyVetoException e) {
            // don't know how to handle this.
            log.warn(e.getMessage(), e);
        }
    }

}
