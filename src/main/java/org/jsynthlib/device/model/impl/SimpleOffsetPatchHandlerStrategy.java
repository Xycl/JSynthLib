package org.jsynthlib.device.model.impl;

import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.PatchHandlerStrategy;
import org.jsynthlib.patch.model.impl.Patch;

public class SimpleOffsetPatchHandlerStrategy implements PatchHandlerStrategy {

    private int headerSize;
    private int patchSize;
    private IPatchDriver singleDriver;

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        System.arraycopy(single.sysex, headerSize, bank.sysex, getPatchStart(patchNum),
                patchSize);
    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        Patch patch = (Patch) singleDriver.createPatch();
        System.arraycopy(bank.sysex, getPatchStart(patchNum), patch.sysex, headerSize,
                patchSize);
        return patch;
    }

    public int getPatchStart(int patchNum) {
        return headerSize + (patchSize * patchNum);
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public int getPatchSize() {
        return patchSize;
    }

    public void setPatchSize(int patchSize) {
        this.patchSize = patchSize;
    }

    public IPatchDriver getSingleDriver() {
        return singleDriver;
    }

    public void setSingleDriver(IPatchDriver singleDriver) {
        this.singleDriver = singleDriver;
    }

}
