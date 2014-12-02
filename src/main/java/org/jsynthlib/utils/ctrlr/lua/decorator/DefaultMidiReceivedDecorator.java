package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultMidiReceivedDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultMidiReceivedDecorator newDefaultMidiReceivedDecorator(
                DriverLuaHandler decoratedHandler);
    }

    @Inject
    private XmlDriverDefinition driverDef;
    private final String driverPrefix;

    @Inject
    public DefaultMidiReceivedDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
        this.driverPrefix = driverPrefix;
    }

    @Override
    public String getMidiReceivedPart() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getMidiReceivedPart());
        codeBuilder.append(indent(indent++)).append("if midiSize == ")
        .append(driverDef.getPatchSize()).append(" then")
        .append(newLine());
        codeBuilder.append("-------------------- process ")
        .append(driverPrefix)
        .append(" data ----------------------------------------")
        .append(newLine());
        codeBuilder.append(indent(indent))
                .append(getAssignValuesCall("midi:getData()", "false"))
                .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append("---------------------------------------------")
        .append("------------------------------------")
        .append(newLine());
        return codeBuilder.toString();
    }

}
