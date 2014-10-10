package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;

public class ExitAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public ExitAction(Map<Action, Integer> mnemonics) {
        super("Exit", null);
        mnemonics.put(this, new Integer('X'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JSLDesktop.Factory.getDesktop().closingProc();
    }
}
