package org.jsynthlib.device.model;

import javax.swing.JOptionPane;

import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.xmldevice.Property;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition.CustomProperties;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType;

public class XMLBankDriver extends AbstractBankDriver implements IBankDriver {

    private final XmlBankDriverDefinition driverSpec;
    private byte[] initPatch;
    private PatchHandlerStrategy patchHandlerStrategy;

    public XMLBankDriver(XmlBankDriverDefinition driverSpec) {
        super(driverSpec.getPatchType(), driverSpec.getAuthors(), driverSpec
                .getNumPatches(), driverSpec.getNumColumns());
        this.driverSpec = driverSpec;
    }

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        if (!canHoldPatch(single)) {
            JOptionPane.showMessageDialog(null,
                    "This type of patch does not fit in to this type of bank.",
                    toString() + "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (patchHandlerStrategy == null) {
            int patchOffset = patchNum * singleSize;
            System.arraycopy(single.sysex, 0, bank.sysex, patchOffset,
                    singleSize);
        } else {
            patchHandlerStrategy.putPatch(bank, single, patchNum);
        }
        calculateChecksum(bank);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        IDriver singleDriver = getSingleDriver();
        if (patchHandlerStrategy == null) {
            int patchOffset = patchNum * singleSize;
            byte[] buf = new byte[singleSize];
            System.arraycopy(bank.sysex, patchOffset, buf, 0, singleSize);
            return getPatchFactory().createNewPatch(buf, singleDriver);
        } else {
            Patch patch = patchHandlerStrategy.getPatch(bank, patchNum);
            singleDriver.calculateChecksum(patch);
            return patch;
        }
    }

    @Override
    public String getPatchName(Patch bank, int patchNum) {
        IDriver singleDriver = getSingleDriver();
        Patch singlePatch = getPatch(bank, patchNum);
        return singleDriver.getPatchName(singlePatch);
    }

    @Override
    public void setPatchName(Patch bank, int patchNum, String name) {
        IDriver singleDriver = getSingleDriver();
        Patch singlePatch = getPatch(bank, patchNum);
        singleDriver.setPatchName(singlePatch, name);
    }

    public byte[] getInitPatch() {
        return initPatch;
    }

    public void setInitPatch(byte[] initPatch) {
        this.initPatch = initPatch;
    }

    protected Property getCustomProperty(String name) {
        CustomProperties customProperties = driverSpec.getCustomProperties();
        if (customProperties != null) {
            Property[] properties = customProperties.getCustomPropertyArray();
            for (Property property : properties) {
                if (property.getName().equals(name)) {
                    return property;
                }
            }
        }
        return null;
    }

    protected IDriver getSingleDriver() {
        XMLDevice xmlDevice = (XMLDevice) getDevice();
        IDriver driver = xmlDevice.getDriver(DriverType.PATCH, getPatchType());
        if (driver == null) {
            throw new IllegalStateException(
                    "Has no Patch driver. Hence cannot create any single patches.");
        }
        return driver;
    }

    @Override
    protected BankPatch createNewPatch() {
        byte[] sysex = new byte[trimSize];
        byte[] sysexHeader = SysexUtils.stringToSysex(sysexID);
        System.arraycopy(sysexHeader, 0, sysex, 0, sysexHeader.length);
        sysex[trimSize - 1] = (byte) 0xF7;

        IDriver driver = getSingleDriver();
        Patch singlePatch = (Patch) driver.createPatch();
        // bank sysex
        BankPatch p = getPatchFactory().newBankPatch(sysex, this);

        for (int i = 0; i < getNumPatches(); i++) {
            putPatch(p, singlePatch, i);
        }

        calculateChecksum(p);

        return p;
    }

    @Override
    public String toString() {
        return getManufacturerName() + " " + getModelName() + " "
                + getPatchType() + " " + DriverType.BANK.toString();
    }

    public PatchHandlerStrategy getPatchHandlerStrategy() {
        return patchHandlerStrategy;
    }

    public void setPatchHandlerStrategy(PatchHandlerStrategy patchHandlerStrategy) {
        this.patchHandlerStrategy = patchHandlerStrategy;
    }

}
