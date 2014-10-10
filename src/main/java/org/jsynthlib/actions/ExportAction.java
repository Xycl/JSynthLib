package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.ExtensionFilter;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.CompatibleFileDialog;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class ExportAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private final AppConfig appConfig;

    public ExportAction(Map<Action, Integer> mnemonics, AppConfig appConfig) {
        super("Export...", null);
        mnemonics.put(this, new Integer('O'));
        this.setEnabled(false);
        this.appConfig = appConfig;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CompatibleFileDialog fc3 = new CompatibleFileDialog();
        FileFilter type1 =
                new ExtensionFilter("Sysex Files (*.syx)", ".syx");
        fc3.addChoosableFileFilter(type1);
        fc3.setFileFilter(type1);
        fc3.setCurrentDirectory(new File(appConfig.getSysexPath()));
        if (fc3.showSaveDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc3.getSelectedFile();
        try {
            if (getSelectedFrame() == null) {
                ErrorMsg.reportError("Error",
                        "Patch to export must be hilighted\n"
                                + "in the currently focuses Library");
            } else {
                if (!file.getName().toUpperCase().endsWith(".SYX")) {
                    file = new File(file.getPath() + ".syx");
                }
                if (file.exists() && JOptionPane.showConfirmDialog(null,
                        "Are you sure?", "File Exists",
                        JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                }

                JSLFrame selectedFrame = getSelectedFrame();
                if (selectedFrame instanceof PatchBasket) {
                    PatchBasket patchBasket = (PatchBasket) selectedFrame;
                    patchBasket.exportPatch(file);
                }
            }
        } catch (IOException ex) {
            ErrorMsg.reportError("Error", "Unable to Save Exported Patch");
            log.warn(ex.getMessage(), ex);
        }
    }
}
