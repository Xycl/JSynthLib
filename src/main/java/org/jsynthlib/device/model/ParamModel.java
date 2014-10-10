package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.impl.Patch;

/**
 * An implementation of IParamModel for Patch class.
 * @see IParamModel
 * @see Patch
 */
public class ParamModel implements IParamModel {

    /** <code>Patch</code> data. */
    protected Patch patch;
    /** Offset of the data for which this model is. */
    protected int offset;

    public ParamModel() {
    }

    /**
     * Creates a new <code>ParamModel</code> instance.
     * @param patch
     *            a <code>Patch</code> value
     * @param offset
     *            an offset in <code>patch.sysex</code>.
     * @deprecated use the empty constructor to let bean initializers fill the
     *             fields automatically.
     */
    @Deprecated
    public ParamModel(Patch patch, int offset) {
        this.offset = offset;
        this.patch = patch;
    }

    // SysexWidget.IParamModel interface methods
    /** Set a parameter value <code>value</code>. */
    @Override
    public void set(int value) {
        patch.sysex[offset] = (byte) value;
    }

    /** Get a parameter value. */
    @Override
    public int get() {
        return patch.sysex[offset];
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int ofs) {
        this.offset = ofs;
    }
}
