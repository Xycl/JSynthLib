package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SavePatchMethodController extends SaveMethodControllerBase {

    public interface Factory {
        SavePatchMethodController newSavePatchMethodController();
    }

    private final DriverModel model;

    @Inject
    public SavePatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_SavePatch", "patch");
        this.model = model;
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();

        code.append(indent(indent))
        .append("-- This method saves the current patch to file")
        .append(newLine());

        code.append(getMethodBegin(indent, false)).append(newLine());

        code.append(indent(indent))
        .append("local PatchDataCurrent = MemoryBlock()")
        .append(newLine());
        code.append(indent(indent))
        .append(getMethodCall(model.getAssembleValuesMethodName(),
                "PatchDataCurrent")).append(newLine());

        code.append(getMethodEnd(indent)).append(newLine());

        setLuaMethodCode(code.toString());
    }
}
