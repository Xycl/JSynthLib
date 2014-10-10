package org.jsynthlib.device.model.impl;

import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.PatchHandlerStrategy;
import org.jsynthlib.patch.model.impl.Patch;

public class MultiMessagePatchHandlerStrategy implements PatchHandlerStrategy {

    private int maxBytesPerMessage;
    private int sysexFrameSize;
    private int headerSize;
    private IPatchDriver patchDriver;

    public void copyPatchData(int patchNum, byte[] bankData,
            byte[] patchData, boolean fromBank) {
        int sizeOfPatch = patchData.length - sysexFrameSize;

        int startLinearOffset = patchNum * sizeOfPatch;
        int startsInMessage = startLinearOffset / maxBytesPerMessage;

        int endLinearOffset = startLinearOffset + sizeOfPatch;
        int endsInMessage = (endLinearOffset - 1) / maxBytesPerMessage;

        int startOffsetInMessage =
                startLinearOffset - (startsInMessage * maxBytesPerMessage);
        int startOffset =
                (sysexFrameSize + maxBytesPerMessage) * startsInMessage
                        + headerSize + startOffsetInMessage;
        if (startsInMessage == endsInMessage) {
            // Message in one piece.
            if (fromBank) {
                System.arraycopy(bankData, startOffset, patchData,
                        headerSize, sizeOfPatch);
            } else {
                System.arraycopy(patchData, headerSize, bankData,
                        startOffset, sizeOfPatch);
            }
        } else {
            final int firstPartLength =
                    maxBytesPerMessage - startOffsetInMessage;
            final int secondPartLength = sizeOfPatch - firstPartLength;
            if (fromBank) {
                System.arraycopy(bankData, startOffset, patchData,
                        headerSize, firstPartLength);
                System.arraycopy(bankData, startOffset + firstPartLength
                        + sysexFrameSize, patchData, headerSize
                        + firstPartLength, secondPartLength);
            } else {
                System.arraycopy(patchData, headerSize, bankData,
                        startOffset, firstPartLength);
                System.arraycopy(patchData, headerSize + firstPartLength,
                        bankData, startOffset + firstPartLength
                                + sysexFrameSize, secondPartLength);
            }
        }
    }

    @Override
    public void putPatch(Patch bank, Patch single, int patchNum) {
        copyPatchData(patchNum, bank.sysex, single.sysex, false);

    }

    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        Patch patch = (Patch) patchDriver.createPatch();
        copyPatchData(patchNum, bank.sysex, patch.sysex, true);
        return patch;
    }

    public int getMaxBytesPerMessage() {
        return maxBytesPerMessage;
    }

    public void setMaxBytesPerMessage(int maxBytesPerMessage) {
        this.maxBytesPerMessage = maxBytesPerMessage;
    }

    public int getSysexFrameSize() {
        return sysexFrameSize;
    }

    public void setSysexFrameSize(int sysexFrameSize) {
        this.sysexFrameSize = sysexFrameSize;
    }

    public int getHeaderSize() {
        return headerSize;
    }

    public void setHeaderSize(int headerSize) {
        this.headerSize = headerSize;
    }

    public IPatchDriver getPatchDriver() {
        return patchDriver;
    }

    public void setPatchDriver(IPatchDriver patchDriver) {
        this.patchDriver = patchDriver;
    }

}
