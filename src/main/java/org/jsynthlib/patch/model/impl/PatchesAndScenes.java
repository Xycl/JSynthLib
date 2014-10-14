package org.jsynthlib.patch.model.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

/**
 * The PatchesAndScenes class is merely a holder for multiple classes for
 * drag-n-drop and cut-n-paste operations. Internally, it's just an array of
 * patches. User: jemenake Date: Feb 24, 2006 Time: 12:59:27 AM
 */
public class PatchesAndScenes implements Transferable {

    private final DataFlavor[] flavors = new DataFlavor[] {
        PatchTransferHandler.PATCHES_FLAVOR };
    private final Vector<Patch> patches = new Vector<Patch>();

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return (flavors);
    }

    public void add(Patch patch) {
        patches.add(patch);
    }

    public void add(Scene scene) {
        patches.add(scene);
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor dataFlavor) {
        for (int i = 0; i < flavors.length; i++) {
            if (dataFlavor.equals(flavors[i])) {
                return (true);
            }
        }
        return (false);
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor)
            throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(dataFlavor)) {
            throw new UnsupportedFlavorException(dataFlavor);
        }
        return (patches);
    }
}
