package org.jsynthlib.utils.ctrlr.lua.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class SetNameMethodGenerator extends MethodGenerator {

    String getStringArray(Map<String, Integer> map) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append("{");
        boolean first = true;
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                codeBuilder.append(",");
            }
            codeBuilder.append("[\"").append(entry.getKey()).append("\"] = ")
            .append(entry.getValue());
        }
        codeBuilder.append("}");
        return codeBuilder.toString();
    }

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        Map<String, Integer> charsMap = null;
        if (values.getPatchNameChars() != null) {
            String[] chars = values.getPatchNameChars();
            charsMap = new HashMap<String, Integer>();
            for (int i = 0; i < chars.length; i++) {
                String string = chars[i];
                charsMap.put(string, i);
            }
        }

        int indent = 0;
        String dataAttrName = "data";
        String nameAttrName = "name";
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder
        .append(indent(indent++))
        .append(getMethodDecl(values.getSetNameMethodName(),
                dataAttrName, nameAttrName)).append(newLine());
        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        codeBuilder.append(indent(indent))
        .append("local b = panel:getModulatorByName(\"")
        .append(values.getNameModulatorName()).append("\")")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("local c = b:getComponent()")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("local patchName = c:getProperty (\"uiLabelText\")")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("local patchNameLength = string.len(patchName)")
        .append(newLine());

        if (charsMap != null) {
            codeBuilder.append(indent(indent)).append("local symbols = ")
            .append(getStringArray(charsMap)).append(newLine());
            codeBuilder.append(indent(indent))
            .append("local emptyChar = symbols[\" \"]")
            .append(newLine());
        }

        for (int i = 0; i < values.getPatchNameSize(); i++) {
            codeBuilder.append(indent(indent++))
            .append("if patchNameLength > ").append(i).append(" then")
            .append(newLine());
            codeBuilder.append(indent(indent))
            .append("mod = panel:getModulatorByName(\"")
            .append(values.getDriverPrefix())
            .append(values.getPatchNameStart() + i).append("\")")
            .append(newLine());
            if (charsMap == null) {
                codeBuilder.append(indent(indent))
                .append("caracter = string.byte(patchName, ")
                .append(i + 1).append(")").append(newLine());
                codeBuilder.append(indent(indent))
                .append("mod:setValue(caracter, true)")
                .append(newLine());
            } else {
                codeBuilder.append(indent(indent))
                .append("caracter = string.sub(patchName, ")
                .append(i + 1).append(", ").append(i + 1).append(")")
                .append(newLine());
                codeBuilder.append(indent(indent))
                .append("mod:setValue(symbols[caracter], true)")
                .append(newLine());
            }

            codeBuilder.append(indent(--indent)).append("else")
            .append(newLine());
            indent++;
            codeBuilder.append(indent(indent))
                    .append("mod = panel:getModulatorByName(\"")
                    .append(values.getDriverPrefix())
                    .append(values.getPatchNameStart() + i).append("\")")
            .append(newLine());

            if (charsMap == null) {
                codeBuilder.append(indent(indent))
                .append("mod:setValue(32, true)").append(newLine());
            } else {
                codeBuilder.append(indent(indent))
                .append("mod:setValue(emptyChar, true)")
                .append(newLine());
            }

            codeBuilder.append(indent(--indent)).append("end")
            .append(newLine());
        }
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getSetNameMethodName(),
                codeBuilder.toString());
    }

}
