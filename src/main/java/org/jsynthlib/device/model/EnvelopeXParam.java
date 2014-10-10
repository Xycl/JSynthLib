package org.jsynthlib.device.model;

public class EnvelopeXParam extends AbstractEnvelopeParam {

    private final boolean invert;

    /**
     * @param min
     * @param max
     * @param name
     * @param invert
     *            Sometimes on an X-axis-riding attribute 0 is the fastest,
     *            other times it is the slowest. This allows you to choose.
     */
    public EnvelopeXParam(int min, int max, String name, boolean invert) {
        super(min, max, name);
        this.invert = invert;
    }

    public boolean isInvert() {
        return invert;
    }
}