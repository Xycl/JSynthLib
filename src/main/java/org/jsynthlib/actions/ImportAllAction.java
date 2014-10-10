package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.CompatibleFileDialog;
import org.jsynthlib.patch.viewcontroller.ImportAllDialog;

public class ImportAllAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;
    private final AppConfig appConfig;

    public ImportAllAction(Map<Action, Integer> mnemonics, AppConfig appConfig) {
        super("Import All...", null);
        this.setEnabled(false);
        this.appConfig = appConfig;
        mnemonics.put(this, new Integer('A'));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            CompatibleFileDialog fc = new CompatibleFileDialog();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (appConfig.getLibPath() != null) {
                fc.setSelectedFile(new File(appConfig.getLibPath()));
            }
            if (fc.showDialog(PatchEdit.getInstance(),
                    "Choose Import All Directory") != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();

            ImportAllDialog sd =
                    new ImportAllDialog(PatchEdit.getInstance(), file);
            sd.setVisible(true);
        } catch (Exception ex) {
            ErrorMsg.reportError("Error", "Unable to Import Patches");
            log.warn(ex.getMessage(), ex);
        }
    }
}
