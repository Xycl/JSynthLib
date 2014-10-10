package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.ExtensionFilter;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.CompatibleFileDialog;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class ImportAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private final AppConfig appConfig;

    public ImportAction(Map<Action, Integer> mnemonics, AppConfig appConfig) {
        super("Import...", null);
        mnemonics.put(this, new Integer('I'));
        this.appConfig = appConfig;
        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CompatibleFileDialog fc2 = new CompatibleFileDialog();
        FileFilter type1 =
                new ExtensionFilter("Sysex Files (*.syx)", ".syx");
        // core.ImportMidiFile extracts Sysex Messages from MidiFile
        FileFilter type2 =
                new ExtensionFilter("MIDI Files (*.mid)", ".mid");
        fc2.addChoosableFileFilter(type1);
        fc2.addChoosableFileFilter(type2);
        fc2.setFileFilter(type1);
        fc2.setCurrentDirectory(new File(appConfig.getSysexPath()));
        if (fc2.showOpenDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = fc2.getSelectedFile();
        try {
            if (getSelectedFrame() == null) {
                ErrorMsg.reportError("Error",
                        "Library to Import Patch\n into Must be in Focus");
            } else {
                JSLFrame selectedFrame = getSelectedFrame();
                if (selectedFrame instanceof PatchBasket) {
                    PatchBasket patchBasket = (PatchBasket) selectedFrame;
                    patchBasket.importPatch(file);
                }
            }
        } catch (IOException ex) {
            ErrorMsg.reportError("Error", "Unable to Load Sysex Data");
            log.warn(ex.getMessage(), ex);
        }
    }
}
