package org.jsynthlib.device.model;

public class EnvelopeYParam extends AbstractEnvelopeParam {

    private final int base;

    /**
     * @param min
     * @param max
     * @param name
     * @param basey
     *            The value will be added to all Y values. This doesn't
     *            change the function of the EnvelopeWidget, but makes it
     *            look nicer and possibly be more intuitive to use.
     *            Sometimes you don't want zero on a Y-axis-riding-parameter
     *            to be all the way down at the bottom. This gives it a
     *            little bit of rise.
     */
    public EnvelopeYParam(int min, int max, String name, int base) {
        super(min, min == EnvelopeNode.SAME ? min : max, name);
        this.base = base;
    }

    public int getBase() {
        return base;
    }

}