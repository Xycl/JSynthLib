package org.jsynthlib.device.model.handler;

import org.jsynthlib.midi.service.ChecksumCalculator;

public class NoChecksumCalculator implements ChecksumCalculator {

    @Override
    public void calculateChecksum(byte[] sysex) {
    }

}
