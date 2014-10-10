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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DriverIdentifier;
import org.jsynthlib.device.model.IBankDriver;
import org.jsynthlib.device.model.IConverter;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.MultiPatchImporter;
import org.jsynthlib.patch.model.PatchFactory;

/**
 * @author Pascal Collberg
 */
public class PatchFactoryImpl implements PatchFactory, MultiPatchImporter {

    private final DriverIdentifier driverIdentifier;
    private final DeviceManager deviceManager;

    @Inject
    public PatchFactoryImpl(DeviceManager deviceManager,
            DriverIdentifier driverIdentifier) {
        this.deviceManager = deviceManager;
        this.driverIdentifier = driverIdentifier;
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.patch.model.IPatchFactory#createPatch(byte[])
     */
    @Override
    public IPatch createPatch(byte[] sysex) {
        IDriver driver = driverIdentifier.chooseDriver(sysex);
        if (driver == null) {
            return null;
        } else {
            return createNewPatch(sysex, driver);
        }
    }

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
    @Override
    public IPatch createNewPatch(IDriver driver, String fileName, int size) {
        try {
            byte[] buffer = getBuffer(driver, fileName, size);
            return createNewPatch(buffer, driver);
        } catch (IOException e) {
            ErrorMsg.reportError("Error", "Unable to open " + fileName, e);
            return null;
        }
    }

    byte[] getBuffer(IDriver driver, String fileName, int size)
            throws IOException {
        byte[] buffer = new byte[size];
        InputStream fileIn = driver.getClass().getResourceAsStream(fileName);

        if (fileIn != null) {
            fileIn.read(buffer);
            fileIn.close();
        } else {
            throw new FileNotFoundException("File: " + fileName
                    + " does not exist!");
        }
        return buffer;
    }

    /**
     * Constructor - Driver is known. This is often used by a Single Driver and
     * its subclass.
     * @param gsysex
     *            The MIDI SysEx message.
     * @param driver
     *            a <code>Driver</code> instance. If <code>null</code>, a null
     *            driver (Generic Driver) is used.
     */
    @Override
    public Patch createNewPatch(byte[] gsysex, IDriver driver) {
        Patch patch = JSynthLibInjector.getInstance(Patch.class);
        patch.sysex = gsysex;
        patch.setDriver(driver);
        // commented out not to break backward compatibility
        // driver.trimSysex(this);
        return patch;
    }

    /**
     * Constructor - Device is known but Driver is not. This is often used by a
     * Bank Driver and its subclass.
     * @param gsysex
     *            The MIDI SysEx message.
     * @param device
     *            a <code>Device</code> instance.
     */
    @Override
    public Patch createNewPatch(byte[] gsysex, Device device) {
        Patch patch = JSynthLibInjector.getInstance(Patch.class);
        patch.sysex = gsysex;
        patch.setDriver(driverIdentifier.chooseDriver(patch.sysex, device));
        patch.getDriver().trimSysex(patch);
        return patch;
    }

    /**
     * Constructor - Either Device nor Driver is not known. Consider using
     * <code>Patch(byte[], Driver)</code> or <code>Patch(byte[],
     * Device)</code>. If you know that the patch you are creating does not
     * correspond to any driver, use <code>Patch(byte[],
     * (Driver) null)</code>, since it is much more efficient than this.
     * @param gsysex
     *            The MIDI SysEx message.
     */
    @Override
    public Patch createNewPatch(byte[] gsysex) {
        Patch patch = JSynthLibInjector.getInstance(Patch.class);
        patch.sysex = gsysex;
        patch.setDriver(driverIdentifier.chooseDriver(patch.sysex));
        patch.getDriver().trimSysex(patch);
        return patch;
    }

    @Override
    public BankPatch newBankPatch(IBankDriver iBankDriver, String iID) {
        BankPatch bankPatch = JSynthLibInjector.getInstance(BankPatch.class);
        bankPatch.setIID(iID);
        bankPatch.setDriver(iBankDriver);
        return bankPatch;
    }

    @Override
    public BankPatch newBankPatch(byte[] sysex, IBankDriver iBankDriver) {
        BankPatch bankPatch = JSynthLibInjector.getInstance(BankPatch.class);
        bankPatch.setDriver(iBankDriver);
        bankPatch.sysex = sysex;
        return bankPatch;
    }

    @Override
    public BankPatch newBankPatch(byte[] sysex) {
        BankPatch bankPatch = JSynthLibInjector.getInstance(BankPatch.class);
        bankPatch.sysex = sysex;
        bankPatch.setDriver(driverIdentifier.chooseDriver(bankPatch.sysex));
        bankPatch.getDriver().trimSysex(bankPatch);
        bankPatch.sysex = sysex;
        return bankPatch;
    }

    @Override
    public BankPatch newBankPatch(IBankDriver driver, String fileName, int size) {
        try {
            byte[] buffer = getBuffer(driver, fileName, size);
            return newBankPatch(buffer, driver);
        } catch (IOException e) {
            ErrorMsg.reportError("Error", "Unable to open " + fileName, e);
            return null;
        }
    }


    /*
     * (non-Javadoc)
     * @see org.jsynthlib.patch.model.MultiPatchImporter#createPatches(byte[])
     */
    @Override
    public List<IPatch> createPatches(byte[] sysex) {
        IDriver drv = null;
        int i;

        // find first sysex that is supported by one of active drivers
        while (drv == null) {
            if ((sysex[0] & 0xff) != 0xf0) {
                for (i = 0; i < sysex.length; i++) {
                    if ((sysex[i] & 0xff) == 0xf0) {
                        break;
                    }
                }
                if (i == sysex.length) {
                    break;
                }
                sysex = Arrays.copyOfRange(sysex, i, sysex.length);
            }
            drv = driverIdentifier.chooseDriver(sysex);
            if (drv == deviceManager.getNullDriver()) {
                drv = null;
                sysex = Arrays.copyOfRange(sysex, 1, sysex.length);
            }
        }
        if (drv != null) {
            return createPatches(sysex, drv);
        } else {
            return new ArrayList<IPatch>();
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.patch.model.MultiPatchImporter#createPatches(byte[],
     * org.jsynthlib.device.model.Device)
     */
    @Override
    public List<IPatch> createPatches(byte[] sysex, Device device) {
        return createPatches(sysex,
                driverIdentifier.chooseDriver(sysex, device));
    }

    List<IPatch> createPatches(byte[] sysex, IDriver driver) {
        if (driver == null) {
            return null;
        } else if (driver.isConverter()) {
            IPatch[] patches = ((IConverter) driver).createPatches(sysex);
            return Arrays.asList(patches);
        } else {
            ArrayList<IPatch> list = new ArrayList<IPatch>();
            if (driver instanceof IBankDriver) {
                IBankDriver bankDriver = (IBankDriver) driver;
                list.add(newBankPatch(sysex, bankDriver));
            } else {
                list.add(createNewPatch(sysex, driver));
            }
            return list;
        }

    }
}
