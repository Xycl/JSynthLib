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

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.viewcontroller.AbstractDriverEditor;
import org.jsynthlib.device.viewcontroller.JSLDriverEditorFrame;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.xmldevice.StringArray;
import org.jsynthlib.xmldevice.StringArray.GeneratedBy;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public class XMLSingleDriver extends AbstractPatchDriver implements
IPatchDriver {

    private final XmlDriverDefinition driverSpec;
    private XmlDriverEditorControllerFactory editorFactory;
    private byte[] initPatch;

    public XMLSingleDriver(XmlSingleDriverDefinition driverSpec) {
        super(driverSpec.getPatchType(), driverSpec.getAuthors());
        this.driverSpec = driverSpec;
    }

    @Override
    public void setDevice(Device d) {
        super.setDevice(d);
    }

    @Override
    protected Patch createNewPatch() {
        byte[] sysex = new byte[getPatchSize()];
        if (initPatch == null || initPatch.length == 0) {
            if (sysexID == null || sysexID.length() == 0) {
                sysex[0] = (byte) 0xF0;
            } else {
                byte[] header =
                        SysexUtils.stringToSysex(sysexID.replaceAll("\\*{2}",
                                "00"));
                System.arraycopy(header, 0, sysex, 0, header.length);
            }
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
            return "-";
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
    @Override
    public JSLFrame editPatch(Patch patch) {
        AbstractDriverEditor driverEditor =
                editorFactory.create(this,
                        (XmlSingleDriverDefinition) driverSpec, patch);
        String frameName = driverSpec.getName() + " Editor";
        System.out.println(SysexUtils.sysexToString(patch.sysex));
        return new JSLDriverEditorFrame(driverEditor, frameName, patch);
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

    public final XmlDriverEditorControllerFactory getEditorFactory() {
        return editorFactory;
    }

    @Inject
    public final void setEditorFactory(
            XmlDriverEditorControllerFactory editorFactory) {
        this.editorFactory = editorFactory;
    }

}
