package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchHandler;

public class EditAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    public EditAction(Map<Action, Integer> mnemonics) {
        super("Edit...", null);
        mnemonics.put(this, new Integer('E'));
        this.setEnabled(false);
    }

    public EditAction() {
        super();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSLFrame selectedFrame = getSelectedFrame();
                    if (selectedFrame instanceof PatchHandler) {
                        PatchHandler patchHandler = (PatchHandler) selectedFrame;
                        JSLFrame frm = patchHandler.editSelectedPatch();
                        if (frm != null) {
                            JSLDesktop.Factory.getDesktop().add(frm);
                            frm.moveToDefaultLocation();
                            frm.setVisible(true);

                            try {
                                frm.setSelected(true);
                            } catch (PropertyVetoException e) {
                                log.warn(e.getMessage(), e);
                            }
                        }

                    }
                } catch (Exception ex) {
                    log.warn(ex.getMessage(), ex);
                    ErrorMsg.reportError("Error", "Error in PatchEditor.", ex);
                }
            }
        });
        thread.run();
    }
}
