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

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * @author Pascal Collberg
 */
public interface PatchFactory {

    Patch createPatch(byte[] sysex);

    /**
     * Create new patch using a patch file <code>patchFileName</code>.
     * @param driver
     *            IPatchDriver object
     * @param fileName
     *            file name (relative path to driver directory)
     * @param size
     *            Sysex data size
     * @return IPatch object
     * @see IPatchDriver#createPatch()
     */
    Patch createNewPatch(IDriver driver, String fileName, int size);

    /**
     * Constructor - Driver is known. This is often used by a Single Driver and
     * its subclass.
     * @param gsysex
     *            The MIDI SysEx message.
     * @param driver
     *            a <code>Driver</code> instance. If <code>null</code>, a null
     *            driver (Generic Driver) is used.
     */
    Patch createNewPatch(byte[] gsysex, IDriver driver);

    /**
     * Constructor - Device is known but Driver is not. This is often used by a
     * Bank Driver and its subclass.
     * @param gsysex
     *            The MIDI SysEx message.
     * @param device
     *            a <code>Device</code> instance.
     */
    Patch createNewPatch(byte[] gsysex, Device device);

    /**
     * Constructor - Either Device nor Driver is not known. Consider using
     * <code>Patch(byte[], Driver)</code> or <code>Patch(byte[],
     * Device)</code>. If you know that the patch you are creating does not
     * correspond to any driver, use <code>Patch(byte[],
     * (Driver) null)</code>, since it is much more efficient than this.
     * @param gsysex
     *            The MIDI SysEx message.
     */
    Patch createNewPatch(byte[] gsysex);

    BankPatch newBankPatch(IBankDriver iBankDriver, String iID);

    BankPatch newBankPatch(byte[] sysex, IBankDriver iBankDriver);

    BankPatch newBankPatch(byte[] sysex);

    BankPatch newBankPatch(IBankDriver driver, String fileName, int size);
}