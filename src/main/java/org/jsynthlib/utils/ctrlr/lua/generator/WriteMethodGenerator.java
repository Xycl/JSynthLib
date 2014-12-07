package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean.WritePatchMessage;

public class WriteMethodGenerator extends MethodGenerator {

    private static final String PATCH_NBR = "patchNbr";
    private static final String BANK_NBR = "bankNbr";

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        String[] args = new String[0];
        if (values.isVariableBanks()) {
            if (values.isVariablePatches()) {
                args = new String[2];
                args[0] = BANK_NBR;
                args[1] = PATCH_NBR;
            } else {
                args = new String[1];
                args[0] = BANK_NBR;
            }
        } else if (values.isVariablePatches()) {
            args = new String[1];
            args[0] = PATCH_NBR;
        }
        codeBuilder.append(indent(indent++)).append(
                getMethodDecl(values.getWriteMethodName(), args));
        for (WritePatchMessage msg : values.getWriteMsgList()) {
            byte[] bytes = SysexUtils.stringToSysex(msg.getMessage());
            if (msg.isPatchDataMsg()) {
                codeBuilder.append(indent(indent))
                .append("PatchDataCurrent = MemoryBlock()")
                .append(newLine());
                codeBuilder
                .append(indent(indent))
                .append(values
                        .getAssembleValuesCall("PatchDataCurrent"))
                        .append(newLine());
                codeBuilder.append(indent(indent))
                .append("m = CtrlrMidiMessage(PatchDataCurrent)")
                .append(newLine());

            } else {
                String string = SysexUtils.byteToHexStringArray(bytes);
                codeBuilder.append(indent(indent))
                .append("m = CtrlrMidiMessage({").append(string)
                .append("})").append(newLine());
            }

            if (values.isVariableBanks() && msg.containsBankNbr()) {
                codeBuilder.append(indent(indent))
                .append("PatchDataCurrent:setByte(")
                .append(msg.getBankNbrOffset()).append(", ")
                .append(BANK_NBR).append(")").append(newLine());
            }

            if (values.isVariablePatches() && msg.containsPatchNbr()) {
                codeBuilder.append(indent(indent))
                .append("PatchDataCurrent:setByte(")
                .append(msg.getPatchNbrOffset()).append(", ")
                .append(PATCH_NBR).append(")").append(newLine());
            }
            codeBuilder.append(indent(indent))
            .append("panel:sendMidiMessageNow(m)").append(newLine());

        }
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        return createMethod(group, values.getWriteMethodName(),
                codeBuilder.toString());
    }
}
