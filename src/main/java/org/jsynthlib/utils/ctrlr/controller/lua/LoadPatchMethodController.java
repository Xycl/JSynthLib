package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LoadPatchMethodController extends LoadMethodControllerBase
{

    public interface Factory {
        LoadPatchMethodController newLoadPatchMethodController();
    }

    private final DriverModel model;

    @Inject
    public LoadPatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_LoadPatch");
        this.model = model;
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method loads a single patch from file")
        .append(newLine());
        code.append(indent(indent))
        .append("-- and assigns its data to the panel modulators")
        .append(newLine());
        code.append(getLoadMethodStart(indent, "Open Patch"))
        .append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(model.getAssignValuesMethodName(),
                loadedDataVar,
                "true")).append(newLine());

        code.append(indent(indent)).append(model.getBankDataVarName())
                .append(" = nil")
        .append(newLine());

        // Display Patch Loaded
        // codeBuilder.append(getInfoMessageCall("Patch Loaded", indent));
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }
}
