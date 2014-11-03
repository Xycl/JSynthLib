package org.jsynthlib.device.model;

import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.StringModelReference;

public interface ParamModelFactory {

    IParamModel newParamModel(ParamModelReference ref, Patch patch);

    PatchStringModel newPatchStringModel(StringModelReference ref, Patch patch);
}
