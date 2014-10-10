package org.jsynthlib.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ExtensionFilter;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.viewcontroller.CompatibleFileDialog;
import org.jsynthlib.patch.viewcontroller.LibraryFrame;
import org.jsynthlib.patch.viewcontroller.SceneFrame;

public class OpenAction extends JSLAbstractAction {

    private static final long serialVersionUID = 1L;

    private final FileFilter filter;

    private final AppConfig appConfig;

    public OpenAction(Map<Action, Integer> mnemonics, AppConfig appConfig) {
        super("Open...", null);
        mnemonics.put(this, new Integer('O'));
        this.appConfig = appConfig;

        String lext = LibraryFrame.FILE_EXTENSION;
        String sext = SceneFrame.FILE_EXTENSION;
        filter =
                new ExtensionFilter("JSynthLib Library/Scene Files (*" + lext
                        + ", *" + sext + ")", new String[] {
                        lext, sext });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        CompatibleFileDialog fc = new CompatibleFileDialog();
        fc.setCurrentDirectory(new File(appConfig.getLibPath()));
        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);

        if (fc.showOpenDialog(PatchEdit.getInstance()) == JFileChooser.APPROVE_OPTION) {
            Actions.openFrame(fc.getSelectedFile());
        }
    }
}
