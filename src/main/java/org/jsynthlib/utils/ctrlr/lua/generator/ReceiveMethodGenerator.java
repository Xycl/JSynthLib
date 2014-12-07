package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;

public class ReceiveMethodGenerator extends MethodGenerator {

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getReceiveMethodName(), "modulator",
                        "newValue"));
        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        // codeBuilder.append(indent(indent++)).append("if newValue == 1 then")
        // .append(newLine());

        for (String msg : values.getReceiveMidiMessages()) {
            byte[] sysex = SysexUtils.stringToSysex(msg);
            String hexStringArray = SysexUtils.byteToHexStringArray(sysex);
            codeBuilder.append(indent(indent)).append("m = CtrlrMidiMessage({")
            .append(hexStringArray).append("})").append(newLine());
            codeBuilder.append(indent(indent))
            .append("panel:sendMidiMessageNow(m)").append(newLine());
        }

        for (String popString : values.getReceivePopupList()) {
            codeBuilder.append(indent(indent))
            .append(getInfoMessageCall(popString)).append(newLine());
        }

        codeBuilder.append(indent(indent)).append("dump_send = 1")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("modulator:setValue(0,false)").append(newLine());
        // codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getReceiveMethodName(),
                codeBuilder.toString());
    }

}
