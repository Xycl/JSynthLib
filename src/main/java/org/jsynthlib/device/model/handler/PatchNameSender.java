package org.jsynthlib.device.model.handler;

import org.jsynthlib.patch.model.impl.Patch;

public class PatchNameSender implements IPatchStringSender {

    private Patch patch;

    public PatchNameSender(Patch patch) {
        super();
        this.patch = patch;
    }

    public PatchNameSender() {
        super();
    }

    @Override
    public void send(String name) {
        patch.setName(name);
        patch.send();
    }

    public Patch getPatch() {
        return patch;
    }

    public void setPatch(Patch patch) {
        this.patch = patch;
    }

}
