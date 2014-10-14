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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.viewcontroller.AbstractDriverEditor;
import org.jsynthlib.device.viewcontroller.DefaultFxmlDriverEditor;
import org.jsynthlib.device.viewcontroller.JSLDriverEditorFrame;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.RolandD50.D50Constants;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.StringArray.GeneratedBy;
import org.jsynthlib.xmldevice.XmlDriverDefs.XmlDriverDef.DriverType;
import org.jsynthlib.xmldevice.XmlDriverSpec;
import org.jsynthlib.xmldevice.XmlPatchDriverSpecDocument.XmlPatchDriverSpec;

/**
 * @author Pascal Collberg
 */
public class XMLPatchDriver extends AbstractPatchDriver implements IPatchDriver {

    private final XmlDriverSpec driverSpec;
    private byte[] initPatch;

    public XMLPatchDriver(XmlPatchDriverSpec driverSpec) {
        super(driverSpec.getPatchType(), driverSpec.getAuthors());
        this.driverSpec = driverSpec;
    }

    @Override
    public void setDevice(Device d) {
        super.setDevice(d);
    }

    @Override
    public Patch createNewPatch() {
        byte[] sysex = new byte[getPatchSize()];
        if (initPatch == null || initPatch.length == 0) {
            System.arraycopy(D50Constants.SYSEX_HEADER, 0, sysex, 0,
                    D50Constants.SYSEX_HEADER_SIZE);
            sysex[sysex.length - 1] = (byte) 0xF7;
        } else {
            int len = initPatch.length;
            System.arraycopy(initPatch, 0, sysex, 0, len);
        }
        Patch p = getPatchFactory().createNewPatch(sysex, this);
        setPatchName(p, "New Patch");
        calculateChecksum(p);
        return p;
    }

    public BankPatch createNewBankPatch() {
        return null;
    }

    String[] getStringArray(StringArray xmlSpec) {
        if (xmlSpec == null) {
            return null;
        }
        GeneratedBy generatedBy = xmlSpec.getGeneratedBy();
        if (generatedBy == null) {
            return xmlSpec.getStringArray();
        } else {
            String format = generatedBy.getFormat();
            int min = generatedBy.getMin();
            int max = generatedBy.getMax();
            return generateNumbers(min, max, format);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#isConverter()
     */
    @Override
    public boolean isConverter() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.IDriver#getPatchName(org.jsynthlib.patch.model
     * .impl.Patch)
     */
    /**
     * Gets the name of the patch from the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver.
     * @see Patch#getName()
     */
    @Override
    public String getPatchName(Patch p) {
        if (patchNameSize == 0) {
            return ("-");
        }
        try {
            return new String(p.sysex, patchNameStart, patchNameSize,
                    "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.IDriver#setPatchName(org.jsynthlib.patch.model
     * .impl.Patch, java.lang.String)
     */
    @Override
    public void setPatchName(Patch patch, String s) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#hasEditor()
     */
    @Override
    public boolean hasEditor() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.IDriver#editPatch(org.jsynthlib.patch.model
     * .impl.Patch)
     */
    @SuppressWarnings("unchecked")
    @Override
    public JSLFrame editPatch(Patch patch) {
        try {
            String className = getClass().getName() + "Editor";
            InputStream stream =
                    getClass().getClassLoader().getResourceAsStream(className);
            Class<? extends AbstractDriverEditor> editorClass = null;
            if (stream == null) {
                editorClass = DefaultFxmlDriverEditor.class;
            } else {
                editorClass =
                        (Class<AbstractDriverEditor>) Class.forName(className);
            }

            Constructor<? extends AbstractDriverEditor> constructor =
                    editorClass.getConstructor(IDriver.class,
                            XmlPatchDriverSpec.class, Patch.class);
            AbstractDriverEditor driverEditor =
                    constructor.newInstance(this, driverSpec, patch);
            String frameName = driverSpec.getName() + " Editor";
            return new JSLDriverEditorFrame(driverEditor, frameName, patch);
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            log.warn(e.getMessage(), e);
            return null;
        } catch (NoSuchMethodException e) {
            log.warn(e.getMessage(), e);
            return null;
        } catch (SecurityException e) {
            log.warn(e.getMessage(), e);
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.model.IDriver#storePatch(org.jsynthlib.patch.model
     * .impl.Patch, int, int)
     */
    @Override
    public void storePatch(Patch patch, int bankNum, int patchNum) {
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.device.model.IPatchDriver#getPatchNumbersForStore()
     */
    @Override
    public String[] getPatchNumbersForStore() {
        return patchNumbers;
    }

    public byte[] getInitPatch() {
        return initPatch;
    }

    public void setInitPatch(byte[] initPatch) {
        this.initPatch = initPatch;
    }

    @Override
    public String toString() {
        return getManufacturerName() + " " + getModelName() + " "
                + getPatchType() + " " + DriverType.PATCH.toString();
    }

}
