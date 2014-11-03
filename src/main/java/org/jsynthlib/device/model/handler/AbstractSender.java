package org.jsynthlib.device.model.handler;

public abstract class AbstractSender implements ISender {

    private int offset;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
