package org.jsynthlib.synthdrivers.RolandD50;

import org.jsynthlib.device.model.impl.DefaultPatchStringModel;

public class D50PatchStringModel extends DefaultPatchStringModel {

    private int offset;
    private int length;

    @Override
    public String get() {
        return D50StringHandler.getName(getPatch(), offset, length);
    }

    @Override
    public void set(String s) {
        D50StringHandler.setName(getPatch(), s, offset, length);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
