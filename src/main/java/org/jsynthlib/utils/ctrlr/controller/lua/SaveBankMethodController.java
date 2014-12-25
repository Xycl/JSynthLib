package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SaveBankMethodController extends SaveMethodControllerBase
{

    public interface Factory {
        SaveBankMethodController newSaveBankMethodController();
    }

    private final DriverModel model;

    @Inject
    public SaveBankMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_SaveBank", "Bank");
        this.model = model;
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();

        code.append(indent(indent))
        .append("-- This method saves the current bank to file")
        .append(newLine());

        code.append(getMethodBegin(indent, true)).append(newLine());

        // Write active patch to its bank location
        // use the global bank variable to save into the file

        code.append(indent(indent))
        .append("local PatchDataCurrent = MemoryBlock()")
        .append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(model.getAssembleValuesMethodName(),
                "PatchDataCurrent")).append(newLine());

        // TODO: get patch number!
        code.append(indent(indent))
        .append(getMethodCall(model.getPutPatchMethodName(),
                "PatchDataCurrent", "1")).append(newLine());

        code.append(getMethodEnd(indent)).append(newLine());

        setLuaMethodCode(code.toString());

        if (model.getSaveMenuName() == null) {
            model.setSaveMenuName(getMethodName());
        }
    }
}
