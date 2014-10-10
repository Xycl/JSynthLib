package org.jsynthlib.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.util.Map;

import javax.swing.Action;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class PasteAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private final Clipboard cb;

    public PasteAction(Map<Action, Integer> mnemonics) {
        super("Paste", null);
        this.setEnabled(false);
        mnemonics.put(this, new Integer('P'));
        cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JSLFrame selectedFrame = getSelectedFrame();
            if (selectedFrame instanceof PatchBasket) {
                PatchBasket patchBasket = (PatchBasket) selectedFrame;
                patchBasket.pastePatch();
            }
        } catch (Exception ex) {
            ErrorMsg.reportError("Error",
                    "Library to Paste into must be the focused Window.");
            log.warn(ex.getMessage(), ex);
        }
    }

    @Override
    public void setEnabled(boolean b) {
        try {
            JSLFrame f = JSLDesktop.Factory.getDesktop().getSelectedFrame();
            b = b && f.canImport(cb.getContents(this).getTransferDataFlavors());
            super.setEnabled(b);
        } catch (Exception ex) {
            super.setEnabled(false);
        }
    }
}
