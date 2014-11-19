package org.jsynthlib.utils.ctrlr;

import java.io.File;
import java.io.IOException;

import org.jsynthlib.xmldevice.IntParamSpec;

public interface CtrlrImageConverter {

    ResourceContainer convertJslImageResources(IntParamSpec paramSpec, File outdir)
            throws IOException;
}
