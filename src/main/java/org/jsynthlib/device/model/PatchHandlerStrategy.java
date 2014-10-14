package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.impl.Patch;

public interface PatchHandlerStrategy {

    /**
     * Puts a patch into the bank, converting it as needed. <code>single</code>
     * is already checked by <code>canHoldPatch</code>, although it was not.
     * @see Patch#put(Patch, int)
     */
    void putPatch(Patch bank, Patch single, int patchNum);

    /**
     * Gets a patch from the bank, converting it as needed.
     * @see Patch#get(int)
     */
    Patch getPatch(Patch bank, int patchNum);

}
