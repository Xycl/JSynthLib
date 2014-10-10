package org.jsynthlib.synthdrivers.RolandD50;

import org.jsynthlib.device.model.IPatchStringSender;
import org.jsynthlib.patch.model.impl.Patch;

public class D50PatchStringSender implements IPatchStringSender {

    private Patch patch;
    private int base;

    public D50PatchStringSender(Patch patch, int base) {
        super();
        this.patch = patch;
        this.base = base;
    }

    public D50PatchStringSender() {
        super();
    }

    @Override
    public void send(String name) {
        D50StringHandler.setName(patch, name, base,
                D50Constants.PARTIAL_NAME_SIZE);
        patch.calculateChecksum();
        patch.send();
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }
}
