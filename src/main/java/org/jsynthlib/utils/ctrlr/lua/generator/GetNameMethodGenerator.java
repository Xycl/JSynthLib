package org.jsynthlib.utils.ctrlr.lua.generator;

import java.util.Arrays;
import java.util.List;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class GetNameMethodGenerator extends MethodGenerator {

    String getStringArray(List<String> array) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("{");
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) {
                codeBuilder.append(",");
            }
            codeBuilder.append("\"").append(array.get(i)).append("\"");
        }
        codeBuilder.append("}");
        return codeBuilder.toString();
    }

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        int indent = 0;
        String dataAttrName = "data";
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getGetNameMethodName(), dataAttrName));
        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        codeBuilder.append(indent(indent)).append("local patchNameStart = ")
                .append(values.getPatchNameStart()).append(newLine());
        codeBuilder.append(indent(indent)).append("local patchNameSize = ")
                .append(values.getPatchNameSize()).append(newLine());
        codeBuilder.append(indent(indent)).append("local name = &quot;&quot;")
        .append(newLine());

        String[] chars = values.getPatchNameChars();
        if (chars != null) {
            codeBuilder.append(indent(indent)).append("local symbols = ")
            .append(getStringArray(Arrays.asList(chars)))
            .append(newLine());
        }

        codeBuilder.append(indent(indent++)).append(
                "for i = patchNameStart,(patchNameStart + patchNameSize - 1) ");
        codeBuilder.append("do -- gets the voice name").append(newLine());
        codeBuilder.append(indent(indent))
        .append("midiParam = data:getByte(i)").append(newLine());

        if (chars == null) {
            codeBuilder
            .append(indent(indent))
            .append("name = name..string.char(midiParam) -- Lua tables are base 1 indexed")
            .append(newLine());
        } else {
            codeBuilder
            .append(indent(indent))
            .append("name = name..symbols[midiParam + 1] -- Lua tables are base 1 indexed")
            .append(newLine());
        }
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(indent)).append("return name")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getGetNameMethodName(),
                codeBuilder.toString());
    }

}
