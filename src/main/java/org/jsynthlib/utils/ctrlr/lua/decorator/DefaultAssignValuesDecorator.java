package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultAssignValuesDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultAssignValuesDecorator newDefaultAssignValuesDecorator(
                DriverLuaHandler decoratedHandler);
    }

    @Inject
    public DefaultAssignValuesDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
    }

    @Override
    protected String getAssignValuesMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getAssignValuesMethodBody());
        codeBuilder.append(indent(indent++))
                .append("midiSize = data:getSize()").append(newLine());
        codeBuilder.append(indent(indent++))
        .append("for i = 0,midiSize do -- ")
                .append("gets the voice parameter values").append(newLine());
        codeBuilder.append(indent(indent))
                .append("midiParam = data:getByte(i)").append(newLine());
        codeBuilder.append(indent(indent)).append("name = &quot;")
                .append(getDriverPrefix()).append("&quot;..i")
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
        .append(getGetNameCall("data")).append(newLine());
        codeBuilder.append(indent(indent))
        .append("panel:getModulatorByName(&quot;")
        .append(getNameModulator())
        .append("&quot;):getComponent():setText(v_char)")
        .append(newLine());
        return codeBuilder.toString();
    }

}
