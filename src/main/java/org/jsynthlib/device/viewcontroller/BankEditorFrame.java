/* $Id: BankEditorFrame.java 1164 2011-09-24 22:22:51Z billzwicky $ */
package org.jsynthlib.device.viewcontroller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.jsynthlib.core.BankPrinter;
import org.jsynthlib.core.ProxyImportHandler;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameEvent;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrameListener;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.IBankPatch;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.ISinglePatch;
import org.jsynthlib.patch.model.PatchFactory;
import org.jsynthlib.patch.model.impl.PatchHandler;
import org.jsynthlib.patch.model.impl.PatchTransferHandler;
import org.jsynthlib.patch.model.impl.PatchesAndScenes;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

public class BankEditorFrame extends JSLFrame implements PatchBasket, PatchHandler {

    private final transient Logger log = Logger.getLogger(getClass());
    /** This is the patch we are working on. */
    protected IBankPatch bankData;
    /** This BankEditorFrame instance. */
    protected final BankEditorFrame instance;
    protected Dimension preferredScrollableViewportSize =
            new Dimension(500, 70);
    protected int autoResizeMode = JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
    protected int preferredColumnWidth = 75;

    private JTable table;
    private PatchGridModel myModel;
    private static PatchTransferHandler pth = new PatchGridTransferHandler();
    private final PatchFactory patchFactory;

    /**
     * Creates a new <code>BankEditorFrame</code> instance.
     * @param bankPatch
     *            a <code>Patch</code> value
     */
    public BankEditorFrame(IBankPatch bankPatch) {
        super(bankPatch.getDriver().toString() + " Window");
        instance = this;
        bankData = bankPatch;
        initBankEditorFrame();

        patchFactory = JSynthLibInjector.getInstance(PatchFactory.class);
    }

    /** Initialize the bank editor frame. */
    protected void initBankEditorFrame() {
        // ...Create the GUI and put it in the window...
        myModel = new PatchGridModel();
        table = new JTable(myModel);
        table.setTransferHandler(pth);
        table.setDragEnabled(true);
        // Only one patch can be handled.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Select index (0, 0) to ensure a patch is selected.
        table.changeSelection(0, 0, false, false);
        table.setPreferredScrollableViewportSize(preferredScrollableViewportSize);
        // table.setRowSelectionAllowed(true);
        // table.setColumnSelectionAllowed(true);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(autoResizeMode);
        ListSelectionListener lsl = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                enableActions();
            }
        };
        table.getSelectionModel().addListSelectionListener(lsl);
        table.getColumnModel().getSelectionModel()
                .addListSelectionListener(lsl);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Actions.showMenuPatchPopup(table, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    playSelectedPatch();
                }
            }
        });

        this.addJSLFrameListener(new JSLFrameListener() {
            @Override
            public void JSLFrameOpened(JSLFrameEvent e) {
            }

            @Override
            public void JSLFrameClosed(JSLFrameEvent e) {
            }

            @Override
            public void JSLFrameDeiconified(JSLFrameEvent e) {
            }

            @Override
            public void JSLFrameIconified(JSLFrameEvent e) {
            }

            @Override
            public void JSLFrameActivated(JSLFrameEvent e) {
                frameActivated();
            }

            @Override
            public void JSLFrameClosing(JSLFrameEvent e) {
                JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
                Iterator<JSLFrame> iterator = desktop.getJSLFrameIterator();
                while (iterator.hasNext()) {
                    JSLFrame jslFrame = iterator.next();
                    if (jslFrame instanceof PatchEditorFrame) {
                        if (((PatchEditorFrame) (jslFrame)).bankFrame == instance) {
                            jslFrame.moveToFront();
                            try {
                                jslFrame.setSelected(true);
                                jslFrame.setClosed(true);
                            } catch (Exception e1) {
                            }
                        }
                    }
                }
            }

            @Override
            public void JSLFrameDeactivated(JSLFrameEvent e) {
                Actions.setEnabled(false, Actions.EN_ALL);
            }
        });

        // Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setTransferHandler(
                new ProxyImportHandler(table, pth));

        // Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            column.setPreferredWidth(preferredColumnWidth);
        }

        // ...Then set the window size or call pack...
        setSize(600, 300);
    }

    private void frameActivated() {
        Actions.setEnabled(false, Actions.EN_ALL);

        // always enabled
        Actions.setEnabled(true, Actions.EN_IMPORT | Actions.EN_NEW_PATCH);

        enableActions();
    }

    /** change state of Actions based on the state of the table. */
    private void enableActions() {
        // one or more patches are included.
        Actions.setEnabled(table.getRowCount() > 0, Actions.EN_SEARCH);

        // sort is not supported yet.
        // Actions.setEnabled(table.getRowCount() > 1, Actions.EN_SORT);

        // only one valid patch is selected.
        boolean selectedOne =
                (table.getSelectedRowCount() == 1
                        && table.getSelectedColumnCount() == 1 && getSelectedPatch() != null);

        Actions.setEnabled(selectedOne, Actions.EN_COPY | Actions.EN_CUT
                | Actions.EN_DELETE | Actions.EN_EXPORT | Actions.EN_PLAY
                | Actions.EN_SEND | Actions.EN_STORE);

        // All entries are of the same type, so we can check the first one....
        Actions.setEnabled(selectedOne && myModel.getPatchAt(0, 0).hasEditor(),
                Actions.EN_EDIT);

        // enable paste if the clipboard has contents.
        Actions.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard()
                .getContents(this) != null, Actions.EN_PASTE);
    }

    private int getPatchNum(int row, int col) {
        return col * bankData.getNumPatches() / bankData.getNumColumns() + row;
    }

    private int getSelectedPatchNum() {
        return getPatchNum(table.getSelectedRow(), table.getSelectedColumn());
    }

    // PatchBasket methods

    // This needs to use some sort of factory so correct IPatch can be created.
    @Override
    public void importPatch(File file) throws IOException,
            FileNotFoundException {
        FileInputStream fileIn = new FileInputStream(file);
        byte[] buffer = new byte[(int) file.length()];
        fileIn.read(buffer);
        fileIn.close();

        IPatch p = patchFactory.createPatch(buffer);
        bankData.put(p, getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    @Override
    public void exportPatch(File file) throws IOException,
            FileNotFoundException {
        /*
         * Almost the same thing occurs in LibraryFrame and SceneFrame also.
         * Maybe we should have something like static final
         * writePatch(OutputStream, IPatch) in Patch.
         */
        FileOutputStream fileOut = new FileOutputStream(file);
        fileOut.write(getSelectedPatch().export());
        fileOut.close();
    }

    @Override
    public void deleteSelectedPatch() {
        bankData.delete(getSelectedPatchNum());
        myModel.fireTableDataChanged();
    }

    @Override
    public void copySelectedPatch() {
        pth.exportToClipboard(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard(), TransferHandler.COPY);
    }

    @Override
    public IPatch getSelectedPatch() {
        return bankData.get(getSelectedPatchNum());
    }

    @Override
    public void sendSelectedPatch() {
        // A Bank Patch consists from Single Patches.
        ((ISinglePatch) getSelectedPatch()).send();
    }

    @Override
    public void sendToSelectedPatch() {
    }

    @Override
    public void reassignSelectedPatch() {
    }

    @Override
    public void playSelectedPatch() {
        // A Bank Patch consists from Single Patches.
        ISinglePatch p = (ISinglePatch) getSelectedPatch();
        p.send();
        p.play();
    }

    public void printPatch() {
        BankPrinter.print(table);
    }

    @Override
    public void storeSelectedPatch() {
        new SysexStoreDialog(getSelectedPatch(), 0, getSelectedPatchNum());// wirski@op.pl
    }

    @Override
    public JSLFrame editSelectedPatch() {
        PatchEditorFrame pf = (PatchEditorFrame) getSelectedPatch().edit();
        pf.setBankEditorInformation(this, table.getSelectedRow(),
                table.getSelectedColumn());
        return pf;
    }

    @Override
    public void pastePatch() {
        if (!pth.importData(table, Toolkit.getDefaultToolkit()
                .getSystemClipboard().getContents(this))) {
            Actions.setEnabled(false, Actions.EN_PASTE);
        }
    }

    @Override
    public void pastePatch(IPatch p) {
        myModel.setPatchAt(p, table.getSelectedRow(), table.getSelectedColumn());
    }

    @Override
    public void pastePatch(IPatch p, int bankNum, int patchNum) {// wirski@op.pl
        myModel.setPatchAt(p, table.getSelectedRow(), table.getSelectedColumn());
    }

    @Override
    public List<IPatch> getPatchCollection() {
        return null; // for now bank doesn't support this feature. Need to
                     // extract single and place in collection.
    }

    // end of PatchBasket methods

    // for PatchEditorFrame
    void setPatchAt(IPatch p, int row, int col) {
        myModel.setPatchAt(p, row, col);
    }

    public void revalidateDriver() {
        bankData.setDriver();
        if (bankData.hasNullDriver()) {
            try {
                setClosed(true);
            } catch (PropertyVetoException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    // JSLFrame method
    @Override
    public boolean canImport(DataFlavor[] flavors) {
        // changed by Hiroo July 5th, 2004
        // XXX Do we still need this check? Hiroo
        return (table.getSelectedRowCount() != 0
                && table.getSelectedColumnCount() != 0 && pth.canImport(table,
                flavors));
    }

    private static class PatchGridTransferHandler extends PatchTransferHandler {
        @Override
        protected Transferable createTransferable(JComponent c) {
            IPatch patch;
            PatchesAndScenes patchesAndScenes = new PatchesAndScenes();
            JTable t = (JTable) c;
            PatchGridModel m = (PatchGridModel) t.getModel();
            patch = m.getPatchAt(t.getSelectedRow(), t.getSelectedColumn());
            patchesAndScenes.add(patch);
            return patchesAndScenes;
        }

        @Override
        protected boolean storePatch(IPatch p, JComponent c) {
            JTable t = (JTable) c;
            PatchGridModel m = (PatchGridModel) t.getModel();
            m.setPatchAt(p, t.getSelectedRow(), t.getSelectedColumn());
            return true;
        }
    }

    private class PatchGridModel extends AbstractTableModel {

        PatchGridModel() {
            super();
            log.info("PatchGridModel");
        }

        @Override
        public int getColumnCount() {
            return bankData.getNumColumns();
        }

        @Override
        public int getRowCount() {
            return bankData.getNumPatches() / bankData.getNumColumns();
        }

        @Override
        public String getColumnName(int col) {
            return "";
        }

        @Override
        public Object getValueAt(int row, int col) {
            String[] patchNumbers = bankData.getDriver().getPatchNumbers();
            int i = getPatchNum(row, col);
            return (patchNumbers[i] + " " + bankData.getName(i));
        }

        @Override
        public Class getColumnClass(int c) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            // ----- Start phil@muqus.com (allow patch name editing from a bank
            // edit window)
            // return false;
            return true;
            // ----- End phil@muqus.com
        }

        /**
         * Called when user changes edits a cell in a bank grid. Will change the
         * name of the corresponding patch.
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            // value is formatted like "num - name"
            // we need to pluck out 'name', ignore the rest
            // field editor is not locked, so user can mangle the 'num -'
            // portion
            int patchNum = getPatchNum(row, col);
            String[] patchNumbers = bankData.getDriver().getPatchNumbers();
            String prefix = patchNumbers[patchNum];

            String neu = value.toString();
            if (neu.length() > prefix.length()) {
                // trim prefix only if user left it in
                if (neu.startsWith(prefix)) {
                    neu = neu.substring(prefix.length()).trim();
                } else {
                    neu = neu.trim();
                }
            } else {
                neu = "";
            }

            bankData.setName(patchNum, neu);
            log.info(neu);
        }

        IPatch getPatchAt(int row, int col) {
            return bankData.get(getPatchNum(row, col));
        }

        void setPatchAt(IPatch p, int row, int col) {
            bankData.put(p, getPatchNum(row, col));
        }
    }

    /**
     * @return
     */
    public IPatch getBankData() {
        return bankData;
    }
}