package org.jsynthlib.synthdrivers.YamahaDX7.common;

import org.jsynthlib.device.model.handler.SysexSender;
import org.jsynthlib.patch.model.impl.Patch;

/*
 * SysexSender - Performance Parameter DX1, DX5, TX7 (g=1; h=0) DX7 (g=2;
 * h=0) Since the DX7 doesn't support the performance patch directly, this
 * SysexSender makes also the necessary translations between DX7 Function
 * parameter change commands and DX Performance parameter change commands.
 * So we don't need to write an own editor for the DX7!
 */
public class PerformanceSender extends SysexSender {

    private Patch patch;
    private int parameter;
    private final byte[] b = new byte[7];
    // translation table TX7->DX7 for Sensitivity parameters
    // (ModulationWheel, FootCtrl, BreathCtrl, AfterTouch)
    private static final byte[] TX2DX_SENSITIVITY = new byte[] {
        0x00, 0x06, 0x0d, 0x13, 0x1A, 0x21, 0x27, 0x2E, 0x35, 0x3B, 0x42,
        0x48, 0x4F, 0x56, 0x5C, 0x63 };

    public PerformanceSender() {
        b[0] = (byte) 0xF0;
        b[1] = (byte) 0x43;
        b[6] = (byte) 0xF7;
    }

    @Override
    public byte[] generate(int value) {
        b[2] = (byte) (0x10 + getChannel() - 1);
        b[5] = (byte) value;

        if (isDX7(patch)) {
            if (parameter == 0x46 || parameter == 0x48 || parameter == 0x4A
                    || parameter == 0x4C) {
                b[5] = TX2DX_SENSITIVITY[value];
            }
        }

        return b;
    }

    boolean isDX7(Patch p) {
        return p.getDevice().getModelName().equals("DX7");
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
        if (isDX7(patch)) {
            b[3] = (byte) 0x08;
        } else {
            b[3] = (byte) 0x04;
        }

    }

    public int getParameter() {
        return parameter;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
        b[4] = (byte) parameter;
    }

}
