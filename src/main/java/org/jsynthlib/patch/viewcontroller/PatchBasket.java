package org.jsynthlib.patch.viewcontroller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.jsynthlib.patch.model.impl.Patch;

/**
 * This interface should be implemented by any window which serves as a holder
 * or "basket" for patches.
 * @version $Id: PatchBasket.java 1079 2007-09-19 22:50:29Z billzwicky $
 */
public interface PatchBasket {
    /** Import a patch from a file. */
    void importPatch(File file) throws IOException;

    /** Export a patch to a file. */
    void exportPatch(File file) throws IOException;

    /** Delete the selected patch. */
    void deleteSelectedPatch();

    /** Copy the selected patch. */
    void copySelectedPatch();

    /** Paste a patch from system clipboard or drag&drop buffer. */
    void pastePatch();

    /** Add a patch into the table of patches. */
    void pastePatch(Patch p); // XXX Shall we rename?

    /** Add a patch into the table of patches including bank and patch numbers. */
    void pastePatch(Patch p, int bankNum, int patchNum); // wirski@op.pl

    /** Get the selected patch. */
    Patch getSelectedPatch();

    /** Return collection of all patches in basket. */
    List<Patch> getPatchCollection();
}
