package org.jsynthlib.device.model.handler;

import org.jsynthlib.patch.model.impl.Patch;

/**
 * This class does not send the entire patch when its name changes
 * @author pascalc
 */
public class LegacyPatchNameSender extends PatchNameSender implements
IPatchStringSender {

    public LegacyPatchNameSender(Patch patch) {
        super(patch);
    }

    public LegacyPatchNameSender() {
        super();
    }

    @Override
    public void send(String name) {
        getPatch().setName(name);
    }
}
