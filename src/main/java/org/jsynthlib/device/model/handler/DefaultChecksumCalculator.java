package org.jsynthlib.device.model.handler;

import org.jsynthlib.midi.service.ChecksumCalculator;

public class DefaultChecksumCalculator implements ChecksumCalculator {

    private int start;
    private int end;
    private int offset;

    @Override
    public void calculateChecksum(byte[] sysex) {
        int sum = 0;
        if (end < 0) {
            end = sysex.length + end;
        }

        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[offset] = (byte) (-sum & 0x7f);
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
