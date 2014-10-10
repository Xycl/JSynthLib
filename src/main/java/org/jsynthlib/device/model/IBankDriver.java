/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author Pascal Collberg
 */
public interface IBankDriver extends IDriver {

    /**
     * Compares the header & size of a Single Patch to this driver to see if
     * this bank can hold the patch.
     * @see Patch#put(IPatch, int)
     * @see AbstractPatchDriver#supportsPatch
     */
    boolean canHoldPatch(Patch p);

    /**
     * Puts a patch into the bank, converting it as needed. <code>single</code>
     * is already checked by <code>canHoldPatch</code>, although it was not.
     * @see Patch#put(IPatch, int)
     */
    void putPatch(Patch bank, Patch single, int patchNum);

    /**
     * Delete a patch.
     * @see Patch#delete(int)
     */
    void deletePatch(Patch single, int patchNum);

    /**
     * Gets a patch from the bank, converting it as needed.
     * @see Patch#get(int)
     */
    Patch getPatch(Patch bank, int patchNum);

    /**
     * Get the name of the patch at the given number <code>patchNum</code>.
     * @see Patch#getName(int)
     */
    String getPatchName(Patch bank, int patchNum);

    /**
     * Set the name of the patch at the given number <code>patchNum</code>.
     * @see Patch#setName(int, String)
     */
    void setPatchName(Patch bank, int patchNum, String name);

    int getNumPatches();

    /**
     * @see Patch#getNumColumns()
     */
    int getNumColumns();

}
