package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.device.viewcontroller.PatchEditorFrame;

public class PrevFaderAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public PrevFaderAction(Map<Action, Integer> mnemonics) {
        super("Go to Previous Fader Bank", null);
        // mnemonics.put(this, new Integer('F'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!(getSelectedFrame() instanceof PatchEditorFrame)) {
            return;
        }
        PatchEditorFrame pf = (PatchEditorFrame) getSelectedFrame();
        pf.prevFader();
        return;
    }
}
