package org.jsynthlib.device.model.handler

import org.jsynthlib.midi.service.ChecksumCalculator

public class ClosureChecksumCalculator extends ClosureHandlerBase implements ChecksumCalculator {

    public static class Builder extends ClosureHandlerBuilderBase<ClosureChecksumCalculator> {

        @Override
        public ClosureChecksumCalculator newInstance() {
            return new ClosureChecksumCalculator();
        }
    }

    @Override
    public void calculateChecksum(byte[] sysex) {
        def c = getClosure()
        c(sysex)
    }
}
