package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.IPatch;

public class PatchNameSender implements IPatchStringSender {

    private IPatch patch;

    public PatchNameSender(IPatch patch) {
        super();
        this.patch = patch;
    }

    public PatchNameSender() {
        super();
    }

    @Override
    public void send(String name) {
        patch.setName(name);
    }

    public IPatch getPatch() {
        return patch;
    }

    public void setPatch(IPatch patch) {
        this.patch = patch;
    }

}
