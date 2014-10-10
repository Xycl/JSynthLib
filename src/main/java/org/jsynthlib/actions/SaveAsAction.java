package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.patch.viewcontroller.AbstractLibraryFrame;

/** Save and specify a file name */
public class SaveAsAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public SaveAsAction(Map<Action, Integer> mnemonics) {
        super("Save As...", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('A'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            AbstractLibraryFrame oFrame =
                    (AbstractLibraryFrame) getSelectedFrame();
            File fn = Actions.showSaveDialog(oFrame);
            if (fn != null) {
                oFrame.save(fn);
            }
        } catch (IOException ex) {
            ErrorMsg.reportError("Error", "Unable to Save Library");
            log.warn(ex.getMessage(), ex);
        }

    }
}
