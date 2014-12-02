package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultAssembleValuesDecorator extends
        DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultAssembleValuesDecorator newDefaultAssembleValuesDecorator(
                DriverLuaHandler decoratedHandler);
    }

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    public DefaultAssembleValuesDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
    }

    @Override
    protected String getAssembleValuesMethodBody() {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getAssembleValuesMethodBody());
        String sysexID = driverDef.getSysexID().replaceAll("\\*{2}", "00");
        byte[] header = SysexUtils.stringToSysex(sysexID);
        byte[] buf = new byte[driverDef.getPatchSize()];
        System.arraycopy(header, 0, buf, 0, header.length);
        buf[buf.length - 1] = (byte) 0xF7;

        int indent = 1;
        codeBuilder.append(indent(indent)).append("data:createFromTable({")
        .append(SysexUtils.byteToHexStringArray(buf)).append("})")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("local headerSize = ")
                .append(header.length).append(newLine());
        codeBuilder.append(indent(indent)).append("local patchSize = ")
                .append(driverDef.getPatchSize()).append(newLine());

        codeBuilder.append(indent(indent++))
                .append("for i = headerSize, patchSize do")
        .append(" -- run through all modulators and fetch their value")
                .append(newLine());
        codeBuilder.append(indent(indent)).append("name = &quot;")
        .append(getDriverPrefix()).append("&quot;..i")
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

        return codeBuilder.toString();
    }

}
