package org.jsynthlib.synthdrivers.YamahaMotif;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.Patch;

/** Driver for Yamaha Motif Normal Voices */
public class YamahaMotifNormalVoiceDriver extends YamahaMotifSingleDriver {

    public YamahaMotifNormalVoiceDriver() {
        super("Normal Voice", "Rib Rdb (ribrdb at yahoo.com)");
        // This is a hack until getting built in voices is supported.
        bankNumbers = new String[] {
                "User", "User" };
        patchNumbers = new String[128];
        defaults_filename = "normal.syx";
        base_address = "08";
        edit_buffer_base_address = "0F";
        parameter_base_address = "40";
        patchSize = 1250;
        trimSize = 1250;
        numSysexMsgs = 35;
        yamaha_init();
    }

    public JSLFrame editPatch(Patch p) {
        return new YamahaMotifNormalVoiceEditor((Patch) p);
    }

    public Patch createNewPatch() {
        return super.createNewPatch();
    }

}
