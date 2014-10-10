package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.device.viewcontroller.PatchEditorFrame;

public class NextFaderAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public NextFaderAction(Map<Action, Integer> mnemonics) {
        super("Go to Next Fader Bank", null);
        mnemonics.put(this, new Integer('F'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(getSelectedFrame() instanceof PatchEditorFrame)) {
            return;
        }
        PatchEditorFrame pf = (PatchEditorFrame) getSelectedFrame();
        pf.nextFader();
        return;
    }
}
