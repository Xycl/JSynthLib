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
package org.jsynthlib.patch.model;

import java.util.List;

import org.jsynthlib.device.model.Device;

/**
 * @author Pascal Collberg
 *
 */
public interface MultiPatchImporter {
    /**
     * Factory method of Patch. Look up the driver for sysex byte array, and
     * create a patch by using the driver found. This is used for a byte array
     * read from a Sysex file, for which a Driver is not known.
     */
    List<IPatch> createPatches(byte[] sysex);

    /**
     * Factory method of Patch. Look up the driver of the specified Device for
     * sysex byte array, and create a patch by using the driver found.
     * @param device
     *            Device whose driver is looked up.
     */
    List<IPatch> createPatches(byte[] sysex, Device device);
}
