package org.jsynthlib.device.model.impl;

import org.jsynthlib.device.model.PatchStringModel;
import org.jsynthlib.patch.model.impl.Patch;

public class DefaultPatchStringModel implements PatchStringModel {

    private Patch patch;

    @Override
    public String get() {
        return patch.getName();
    }

    @Override
    public void set(String s) {
        patch.setName(s);
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

}
