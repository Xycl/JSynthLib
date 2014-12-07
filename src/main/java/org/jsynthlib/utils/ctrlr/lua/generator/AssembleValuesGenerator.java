package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class AssembleValuesGenerator extends MethodGenerator {

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {

        String sysexID = values.getSysexID().replaceAll("\\*{2}", "00");
        byte[] header = SysexUtils.stringToSysex(sysexID);
        byte[] buf = new byte[values.getPatchSize()];
        System.arraycopy(header, 0, buf, 0, header.length);
        buf[buf.length - 1] = (byte) 0xF7;

        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++))
        .append(getMethodDecl(values.getAssembleValuesMethodName(),
                "data"))
                .append(newLine());
        codeBuilder.append(indent(indent)).append("data:createFromTable({")
        .append(SysexUtils.byteToHexStringArray(buf)).append("})")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("local headerSize = ")
        .append(header.length).append(newLine());
        codeBuilder.append(indent(indent)).append("local patchSize = ")
                .append(values.getPatchSize()).append(newLine());

        codeBuilder.append(indent(indent++))
        .append("for i = headerSize, patchSize do")
        .append(" -- run through all modulators and fetch their value")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("name = &quot;")
                .append(values.getDriverPrefix()).append("&quot;..i")
                .append(newLine());
        codeBuilder.append(indent(indent))
        .append("mod = panel:getModulatorByName(name)")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if mod ~= nil then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("data:setByte(i, mod:getValue())").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getAssembleValuesMethodName(),
                codeBuilder.toString());
    }

}
