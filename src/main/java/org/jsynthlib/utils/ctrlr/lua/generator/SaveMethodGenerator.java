package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class SaveMethodGenerator extends MethodGenerator {

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getSaveMethodName()));

        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("f = utils.saveFileWindow (\"Save Patch\", File(\"\"), \"*.syx\", true)")
        .append(newLine());
        codeBuilder.append(indent(indent++))
        .append("if f:isValid() == false then").append(newLine());
        codeBuilder.append(indent(indent)).append("return").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(indent)).append("f:create()")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if f:existsAsFile() then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("PatchDataCurrent = MemoryBlock()").append(newLine());
        codeBuilder.append(indent(indent))
        .append(values.getAssembleValuesCall("PatchDataCurrent"))
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("-- Check if the file exists").append(newLine());
        codeBuilder.append(indent(indent++))
        .append("if f:existsAsFile() == false then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("-- If file does not exist, then create it")
        .append(newLine());
        codeBuilder.append(indent(indent++))
        .append("if f:create() == false then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("-- If file cannot be created, then fail here")
        .append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("utils.warnWindow (\"\\n\\nSorry, the Editor failed to\\nsave the patch to disk!\", \"The file does not exist.\")")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("return").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        codeBuilder
        .append(indent(indent))
        .append("-- If we reached this point, we have a valid file we can try to write to")
        .append(newLine());
        codeBuilder
        .append(indent(indent++))
        .append("if f:replaceWithData (PatchDataCurrent) == false then")
        .append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("utils.warnWindow (\"File write\", \"Sorry, the Editor failed to\\nwrite the data to file!\")")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("console (\"File save complete, Editor patch saved to disk\")")
        .append(newLine());

        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getSaveMethodName(),
                codeBuilder.toString());
    }
}
