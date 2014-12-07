package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class LoadMethodGenerator extends MethodGenerator {

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getLoadMethodName(), "modulator",
                        "newValue"));
        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        // codeBuilder.append(indent(indent++)).append("if newValue == 1 then")
        // .append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("f = utils.openFileWindow (\"Open Patch\", File(\"\"), \"*.syx\", true)")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if f:existsAsFile() then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("PatchDataLoaded = MemoryBlock()").append(newLine());
        codeBuilder.append(indent(indent))
        .append("f:loadFileAsData(PatchDataLoaded)").append(newLine());
        codeBuilder.append(indent(indent))
                .append(values.getAssignValuesCall("PatchDataLoaded", "true"))
        .append(newLine());
        // Display Patch Loaded
        // codeBuilder.append(getInfoMessageCall("Patch Loaded", indent));
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        // codeBuilder.append(indent(--indent)).append("end").append(newLine());
        // codeBuilder.append(indent(indent)).append("modulator:setValue(0,false)")
        // .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getLoadMethodName(),
                codeBuilder.toString());
    }
}
