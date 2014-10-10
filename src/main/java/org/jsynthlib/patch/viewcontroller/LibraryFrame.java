package org.jsynthlib.patch.viewcontroller;

import java.awt.Toolkit;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ExtensionFilter;
import org.jsynthlib.core.viewcontroller.Actions;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.impl.LibraryColumns;
import org.jsynthlib.patch.model.impl.PatchListModel;
import org.jsynthlib.patch.model.impl.PatchListTransferHandler;
import org.jsynthlib.patch.model.impl.PatchTableModel;
import org.jsynthlib.patch.model.impl.PatchTransferHandler;

/**
 * @version $Id: LibraryFrame.java 1160 2011-09-23 00:40:25Z frankster $
 */
public class LibraryFrame extends AbstractLibraryFrame {
    private static int openFrameCount = 0;
    public static final String FILE_EXTENSION = ".patchlib";
    private static final FileFilter FILE_FILTER =
            new ExtensionFilter("PatchEdit Library Files (*" + FILE_EXTENSION
                    + ")", FILE_EXTENSION);
    private static final PatchTransferHandler TRANSFER_HANDLER =
            new PatchListTransferHandler();

    final transient Logger log = Logger.getLogger(getClass());

    public LibraryFrame(File file) {
        super(file.getName(), "Library", TRANSFER_HANDLER);
    }

    public LibraryFrame() {
        super("Unsaved Library #" + (++openFrameCount), "Library", TRANSFER_HANDLER);
    }

    @Override
    protected PatchTableModel createTableModel() {
        return new PatchListModel(/* false */);
    }

    @Override
    protected void setupColumns() {
        TableColumnModel columnModel = table.getColumnModel();
        TableColumn column =
                columnModel.getColumn(LibraryColumns.SYNTH.ordinal());
        column.setPreferredWidth(50);
        column = columnModel.getColumn(LibraryColumns.TYPE.ordinal());
        column.setPreferredWidth(50);
        column = columnModel.getColumn(LibraryColumns.PATCH_NAME.ordinal());
        column.setPreferredWidth(100);
        column = columnModel.getColumn(LibraryColumns.FIELD_1.ordinal());
        column.setPreferredWidth(50);
        column = columnModel.getColumn(LibraryColumns.FIELD_2.ordinal());
        column.setPreferredWidth(50);
        column = columnModel.getColumn(LibraryColumns.COMMENT.ordinal());
        column.setPreferredWidth(200);
    }

    @Override
    protected void frameActivated() {
        Actions.setEnabled(false, Actions.EN_ALL);
        // always enabled
        Actions.setEnabled(true, Actions.EN_GET | Actions.EN_IMPORT
                | Actions.EN_IMPORT_ALL | Actions.EN_NEW_PATCH);
        enableActions();
    }

    /** change state of Actions based on the state of the table. */
    @Override
    protected void enableActions() {
        // one or more patches are included.
        Actions.setEnabled(table.getRowCount() > 0, Actions.EN_SAVE
                | Actions.EN_SAVE_AS | Actions.EN_SEARCH);

        // more than one patches are included.
        Actions.setEnabled(table.getRowCount() > 1,
                Actions.EN_DELETE_DUPLICATES | Actions.EN_SORT);

        // one or more patches are selected
        Actions.setEnabled(table.getSelectedRowCount() > 0, Actions.EN_DELETE);

        // one patch is selected
        Actions.setEnabled(table.getSelectedRowCount() == 1, Actions.EN_COPY
                | Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN
                | Actions.EN_STORE | Actions.EN_UPLOAD | Actions.EN_CROSSBREED);

        // one signle patch is selected
        Actions.setEnabled(table.getSelectedRowCount() == 1
                && model.getPatchAt(table.getSelectedRow()).isSinglePatch(),
                Actions.EN_SEND | Actions.EN_SEND_TO | Actions.EN_PLAY);

        // one bank patch is selected
        Actions.setEnabled(table.getSelectedRowCount() == 1
                && model.getPatchAt(table.getSelectedRow()).isBankPatch(),
                Actions.EN_EXTRACT);

        // one patch is selected and it implements patch
        Actions.setEnabled(table.getSelectedRowCount() == 1
                && model.getPatchAt(table.getSelectedRow()).hasEditor(),
                Actions.EN_EDIT);

        // enable paste if the clipboard has contents.
        Actions.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard()
                .getContents(this) != null, Actions.EN_PASTE);
    }

    public int deleteDuplicates() {
        Collections.sort(model.getList(), new SysexSort());
        int numDeleted = 0;
        Iterator<IPatch> it = model.getList().iterator();
        byte[] p = it.next().getByteArray();
        while (it.hasNext()) {
            byte[] q = it.next().getByteArray();
            if (Arrays.equals(p, q)) {
                it.remove();
                numDeleted++;
            } else {
                p = q;
            }
        }
        // bug 3410743
        // fix symptoms of issue where apparently the selected row
        // can be no longer in the table
        if (numDeleted > 0) {
            table.clearSelection();
        }

        changed();
        return numDeleted;
    }

    // This is a comparator class used by the delete duplicated action
    // to sort based on the sysex data
    // Sorting this way makes the Dups search much easier, since the
    // dups must be next to each other
    private static class SysexSort implements Comparator<IPatch> {
        @Override
        public int compare(IPatch a1, IPatch a2) {
            String s1 = new String(a1.getByteArray());
            String s2 = new String(a2.getByteArray());
            return s1.compareTo(s2);
        }
    }

    // for SortDialog
    public void sortPatch(Comparator<IPatch> c) {
        Collections.sort(model.getList(), c);
        changed();
    }

    @Override
    public FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    @Override
    public String getFileExtension() {
        return FILE_EXTENSION;
    }
}
