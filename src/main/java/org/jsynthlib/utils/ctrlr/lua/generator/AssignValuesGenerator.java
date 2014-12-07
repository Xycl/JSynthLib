package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class AssignValuesGenerator extends MethodGenerator {

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        String dataAttrName = "data";
        String sendMidiAttrName = "midi";

        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getAssignValuesMethodName(), dataAttrName,
                        sendMidiAttrName));
        codeBuilder.append(indent(indent)).append("midiSize = data:getSize()")
        .append(newLine());
        codeBuilder.append(indent(indent++))
        .append("for i = 0,midiSize do -- ")
        .append("gets the voice parameter values").append(newLine());
        codeBuilder.append(indent(indent))
        .append("midiParam = data:getByte(i)").append(newLine());
        codeBuilder.append(indent(indent)).append("name = &quot;")
                .append(values.getDriverPrefix()).append("&quot;..i")
                .append(newLine());
        codeBuilder.append(indent(indent))
        .append("mod = panel:getModulatorByName(name)")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if mod ~= nil then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("mod:setModulatorValue(midiParam, false, midi, false)")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(indent)).append("v_char = ")
        .append(values.getGetNameCall("data")).append(newLine());
        codeBuilder.append(indent(indent))
        .append("panel:getModulatorByName(&quot;")
        .append(values.getNameModulatorName())
        .append("&quot;):getComponent():setText(v_char)")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getAssignValuesMethodName(),
                codeBuilder.toString());
    }

}
