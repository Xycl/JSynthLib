/*
 * AbstractLibraryFrame.java
 *
 * Created on 24. September 2002, 10:52
 */
package org.jsynthlib.patch.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.MaskFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsynthlib.actions.EditAction;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.ProxyImportHandler;
import org.jsynthlib.core.XMLFileUtils;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.core.viewcontroller.ReassignPatchDialog;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameEvent;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameListener;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;
import org.jsynthlib.device.viewcontroller.PatchEditorFrame;
import org.jsynthlib.device.viewcontroller.SysexSendToDialog;
import org.jsynthlib.device.viewcontroller.SysexStoreDialog;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.MultiPatchImporter;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;
import org.jsynthlib.patch.model.impl.PatchHandler;
import org.jsynthlib.patch.model.impl.PatchTableModel;
import org.jsynthlib.patch.model.impl.PatchTransferHandler;

/**
 * Abstract class for unified handling of Library and Scene frames.
 * @author Gerrit.Gehnen
 * @version $Id: AbstractLibraryFrame.java 1160 2011-09-23 00:40:25Z frankster $
 */
public abstract class AbstractLibraryFrame extends JSLFrame implements
        PatchBasket, PatchHandler {
    private final transient Logger log = Logger.getLogger(getClass());
    protected JTable table;
    protected PatchTableModel model;

    private final String TYPE;
    private final PatchTransferHandler pth;
    /** Has the library been altered since it was last saved? */
    protected boolean changed = false; // wirski@op.pl
    private final JLabel statusBar;
    private File filename;
    private final MultiPatchImporter patchImporter;

    protected AbstractLibraryFrame(String title, String type,
            PatchTransferHandler pth) {
        super(title);
        TYPE = type;
        this.pth = pth;

        // ...Create the GUI and put it in the window...
        addJSLFrameListener(new MyFrameListener());

        // create Table
        model = createTableModel();
        createTable();

        patchImporter =
                JSynthLibInjector
                        .getInstance(MultiPatchImporter.class);

        // Create the scroll pane and add the table to it.
        final JScrollPane scrollPane = new JScrollPane(table);
        // Enable drop on scrollpane
        scrollPane.getViewport().setTransferHandler(
                new ProxyImportHandler(table, pth));

        // Add the scroll pane to this window.
        JPanel statusPanel = new JPanel();
        statusBar = new JLabel(model.getRowCount() + " Patches");
        statusPanel.add(statusBar);

        // getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(statusPanel, BorderLayout.SOUTH);

        // ...Then set the window size or call pack...
        setSize(800, 300); // wirski@op.pl
    }

    protected abstract PatchTableModel createTableModel();

    /** Before calling this method, table and myModel is setup. */
    protected abstract void setupColumns();

    protected abstract void frameActivated();

    protected abstract void enableActions();

    private void createTable() {
        table = new JTable(model);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                    table.setRowSelectionInterval(
                            table.rowAtPoint(new Point(e.getX(), e.getY())),
                            table.rowAtPoint(new Point(e.getX(), e.getY())));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                    table.setRowSelectionInterval(
                            table.rowAtPoint(new Point(e.getX(), e.getY())),
                            table.rowAtPoint(new Point(e.getX(), e.getY())));
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Patch patch = getSelectedPatch();
                    String name = patch.getName();
                    int nameSize = patch.getNameSize();
                    if (patch.hasEditor()) {
                        ActionEvent ae =
                                new ActionEvent(e.getSource(), e.getID(), e
                                        .paramString());
                        EditAction editAction = new EditAction();
                        editAction.actionPerformed(ae);
                        changed();
                    } else if (nameSize != 0) {
                        final JOptionPane optionPane;
                        String maskStr = "";
                        for (int i = 0; i < nameSize; i++) {
                            maskStr += "*";
                        }
                        MaskFormatter Mask = new MaskFormatter();
                        try {
                            Mask.setMask(maskStr);
                        } catch (ParseException ex) {
                            log.info(ex.getMessage(), ex);
                        }
                        JFormattedTextField patchName =
                                new JFormattedTextField(Mask);
                        patchName.setValue(name);
                        Object[] options = {
                                new String("OK"), new String("Cancel") };
                        optionPane =
                                new JOptionPane(patchName,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.YES_NO_OPTION, null,
                                        options, options[0]);
                        JDialog dialog =
                                optionPane.createDialog(table,
                                        "Edit patch name");
                        dialog.setVisible(true);
                        if (optionPane.getValue() == options[0]) {
                            String newName = (String) patchName.getValue();
                            patch.setName(newName);
                            changed();
                        }
                    }
                }
            }
        });

        // table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setTransferHandler(pth);
        table.setDragEnabled(true);

        setupColumns();

        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                changed = true;
                statusBar.setText(model.getRowCount() + " Patches");
                enableActions();
            }
        });

        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        enableActions();
                    }
                });
    }

    private class MyFrameListener implements JSLFrameListener {
        @Override
        public void JSLFrameClosing(JSLFrameEvent e) {
            if (!changed) {
                return;
            }

            // close Patch/Bank Editor editing a patch in this frame.
            JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
            Iterator<JSLFrame> iterator = desktop.getJSLFrameIterator();
            while (iterator.hasNext()) {
                JSLFrame jslFrame = iterator.next();
                if (jslFrame instanceof BankEditorFrame) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (((BankEditorFrame) (jslFrame)).getBankData() == model
                                .getPatchAt(i)) {
                            jslFrame.moveToFront();
                            try {
                                jslFrame.setSelected(true);
                                jslFrame.setClosed(true);
                            } catch (Exception e1) {
                                log.info(e1.getMessage(), e1);
                            }
                            break;
                        }
                    }
                }
                if (jslFrame instanceof PatchEditorFrame) {
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (((PatchEditorFrame) (jslFrame)).getPatch() == model
                                .getPatchAt(i)) {
                            jslFrame.moveToFront();
                            try {
                                jslFrame.setSelected(true);
                                jslFrame.setClosed(true);
                            } catch (Exception e1) {
                                log.info(e1.getMessage(), e1);
                            }
                            break;
                        }
                    }
                }
            }

            if (JOptionPane.showConfirmDialog(null, "This " + TYPE
                    + " may contain unsaved data.\nSave before closing?",
                    "Unsaved Data", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                return;
            }

            moveToFront();
            Actions.saveFrame();
        }

        @Override
        public void JSLFrameOpened(JSLFrameEvent e) {
        }

        @Override
        public void JSLFrameActivated(JSLFrameEvent e) {
            frameActivated();
        }

        @Override
        public void JSLFrameClosed(JSLFrameEvent e) {
        }

        @Override
        public void JSLFrameDeactivated(JSLFrameEvent e) {
            Actions.setEnabled(false, Actions.EN_ALL);
        }

        @Override
        public void JSLFrameDeiconified(JSLFrameEvent e) {
        }

        @Override
        public void JSLFrameIconified(JSLFrameEvent e) {
        }
    }

    // begin PatchBasket methods
    @Override
    public void importPatch(File file) throws IOException {
        if (doImport(file)) {
            return;
        }

        byte[] buffer = FileUtils.readFileToByteArray(file);

        log.debug("Buffer length: " + buffer.length);
        List<Patch> patarray = patchImporter.createPatches(buffer);
        for (Patch patch : patarray) {
            if (table.getSelectedRowCount() == 0) {
                model.addPatch(patch);
            } else {
                model.setPatchAt(patch, table.getSelectedRow());
            }
        }

        changed();
    }

    public boolean doImport(java.io.File file) {
        Sequence seq;
        Track[] tr;

        try {
            seq = MidiSystem.getSequence(file);
        } catch (Exception ex) {
            // If we fall in an exception the file was not a Midifile....
            return false;
        }
        tr = seq.getTracks();
        log.debug("Track Count " + tr.length);

        for (int j = 0; j < tr.length; j++) {
            log.debug("Track " + j + ":size " + tr[j].size());
            for (int i = 0; i < tr[j].size(); i++) {
                PatchBasket frame =
                        (PatchBasket) JSLDesktop.Factory.getDesktop()
                                .getSelectedFrame();
                if (tr[j].get(i).getMessage() instanceof SysexMessage) {
                    log.debug("Track " + j + " Event " + i + " SYSEX!!");
                    List<Patch> patarray =
                            patchImporter.createPatches(tr[j].get(i)
                                    .getMessage().getMessage());
                    for (Patch patch : patarray) {
                        frame.pastePatch(patch);
                    }
                }
            }
        }
        return true;
    }

    protected void changed() {
        model.fireTableDataChanged();
        // This is done in tableChanged for the TableModelListener
        // changed = true;
    }

    /**
     * @param the
     *            row that was inserted. TODO: may want to add a different
     *            version for modifying a row
     */
    protected void changed(int row) {
        model.fireTableRowsInserted(row, row);
    }

    public boolean isChanged() {
        return (changed);
    }

    @Override
    public void exportPatch(File file) throws IOException,
            FileNotFoundException {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().export());
        fileOut.close();
    }

    @Override
    public void deleteSelectedPatch() {
        log.info("delete patch : " + table.getSelectedRowCount());
        int[] ia = table.getSelectedRows();
        // Without this we cannot delete the patch at the bottom.
        table.clearSelection();
        // delete from bottom not to change indices to be removed
        for (int i = ia.length; i > 0; i--) {
            log.info("i = " + ia[i - 1]);
            model.removeAt(ia[i - 1]);
        }
        changed();
    }

    @Override
    public void copySelectedPatch() {
        pth.exportToClipboard(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard(), TransferHandler.COPY);
    }

    @Override
    public void pastePatch() {
        if (pth.importData(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(this))) {
            changed();
        } else {
            Actions.setEnabled(false, Actions.EN_PASTE);
        }
    }

    @Override
    public void pastePatch(Patch p) {
        int row = model.addPatch(p);
        changed(row);
    }

    @Override
    public void pastePatch(Patch p, int bankNum, int patchNum) {// added by R.
                                                                 // Wirski
        int row = model.addPatch(p, bankNum, patchNum);
        changed(row);
    }

    @Override
    public Patch getSelectedPatch() {
        return model.getPatchAt(table.getSelectedRow());
    }

    @Override
    public void sendSelectedPatch() {
        getSelectedPatch().send();
    }

    @Override
    public void sendToSelectedPatch() {
        new SysexSendToDialog(getSelectedPatch());
    }

    @Override
    public void reassignSelectedPatch() {
        new ReassignPatchDialog(getSelectedPatch());
        changed();
    }

    @Override
    public void playSelectedPatch() {
        Patch myPatch = getSelectedPatch();
        myPatch.send();
        myPatch.play();
    }

    @Override
    public void storeSelectedPatch() {
        new SysexStoreDialog(getSelectedPatch(), 0, 0); // wirski@op.pl
    }

    @Override
    public JSLFrame editSelectedPatch() {
        // TODO: "changed" should only be set to true if the patch was modified.
        changed = true;
        return getSelectedPatch().edit();
    }

    @Override
    public List<Patch> getPatchCollection() {
        return model.getList();
    }

    // end of PatchBasket methods

    /**
     * @return The abstractPatchListModel as unified source of patches in all
     *         types of Libraryframes
     */
    public PatchTableModel getPatchTableModel() {
        return model;
    }

    /**
     * @return The visual table component for this Frame.
     */
    public JTable getTable() { // for SearchDialog
        return table;
    }

    public void extractSelectedPatch() {
        if (table.getSelectedRowCount() == 0) {
            ErrorMsg.reportError("Error", "No Patch Selected.");
            return;
        }
        BankPatch myPatch = (BankPatch) getSelectedPatch();
        for (int i = 0; i < myPatch.getNumPatches(); i++) {
            Patch p = myPatch.get(i);
            if (p != null) {
                model.addPatch(p);
            }
        }
        changed();
    }

    // for open/save/save-as actions
    public void save() throws IOException {
        PatchEdit.showWaitDialog("Saving " + filename + "...");
        try {
            FileOutputStream f = new FileOutputStream(filename);
            ObjectOutputStream s = new ObjectOutputStream(f);
            s.writeObject(model.getList());
            s.flush();
            s.close();
            f.close();
            changed = false;

            XMLFileUtils.writePatchBasket(this, filename + ".xml");

        } catch (IOException e) {
            throw e;
        } finally {
            PatchEdit.hideWaitDialog();
        }
    }

    public void save(File file) throws IOException {
        filename = file;
        setTitle(file.getName());
        save();
        changed = false;
    }

    public void open(File file) throws IOException, ClassNotFoundException {
        boolean readXMLFile = true;
        boolean readOldFile = true;

        setTitle(file.getName());
        filename = file;

        if (readOldFile) {
            FileInputStream f = new FileInputStream(file);
            ObjectInputStream s = new ObjectInputStream(f);
            List<Patch> list = (List<Patch>) s.readObject();
            for (Patch iPatch : list) {
                JSynthLibInjector.getInjector().injectMembers(iPatch);
            }
            model.setList(list);
            s.close();
            f.close();
            if (model.getList().size() > 0) {
                // Don't attempt XML if readObject succeeded.
                readXMLFile = false;
            }
        }
        if (readXMLFile) {
            XMLFileUtils.readPatchBasket(this, file.getName() + ".xml");
        }
        revalidateDrivers();
        model.fireTableDataChanged();
        changed = false;

    }

    public abstract FileFilter getFileFilter();

    public abstract String getFileExtension();

    /**
     * Re-assigns drivers to all patches in libraryframe. Called after new
     * drivers are added or or removed
     */
    public void revalidateDrivers() {
        for (int i = 0; i < model.getRowCount(); i++) {
            chooseDriver(model.getPatchAt(i));
        }
        model.fireTableDataChanged();
    }

    private void chooseDriver(Patch patch) {
        patch.setDriver();
        if (patch.hasNullDriver()) {
            // Unkown patch, try to guess at least the manufacturer
            patch.setComment("Probably a " + patch.lookupManufacturer()
                    + " Patch, Size: " + patch.getSize());
        }
    }

    // JSLFrame method
    @Override
    public boolean canImport(DataFlavor[] flavors) {
        return pth.canImport(table, flavors);
    }

    int getSelectedRowCount() { // not used now
        return table.getSelectedRowCount();
    }
}