package org.jsynthlib.midi.service;

public interface ChecksumCalculator {

    /**
     * Calculate check sum of a byte array <code>sysex</code>.
     * @param sysex
     *            a byte array
     */
    void calculateChecksum(byte[] sysex);
}
