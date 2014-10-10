package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.device.viewcontroller.SysexGetDialog;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class GetAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public GetAction(Map<Action, Integer> mnemonics) {
        super("Get...", null);
        mnemonics.put(this, new Integer('G'));
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SysexGetDialog myDialog =
                new SysexGetDialog(PatchEdit.getInstance());
        myDialog.setVisible(true);
    }

}
