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
package org.jsynthlib.patch.model.impl;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.IPatch;

/**
 * @author Pascal Collberg
 *
 */
public interface PatchHandler {

    /** Get the selected patch. */
    IPatch getSelectedPatch();

    /** Copy the selected patch. */
    void copySelectedPatch();

    /**
     * Send the selected patch to the Edit buffer of the synth for the patch.
     * Only for Single Patch.
     */
    void sendSelectedPatch();

    /**
     * Send the selected patch to the Edit buffer of the synth specified by
     * user. Only for Single Patch.
     */
    void sendToSelectedPatch();

    /**
     * Send the selected patch to a buffer of the synth specified by user. Only
     * for Single Patch.
     */
    void storeSelectedPatch();

    /** Reassign the driver of the selected patch. */
    void reassignSelectedPatch();

    /** Play the selected patch. */
    void playSelectedPatch();

    /** Invoke an editor for the selected patch. */
    JSLFrame editSelectedPatch();

}
