package org.jsynthlib.utils.ctrlr.lua.generator;

import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MidiReceivedGenerator implements MidiReceivedPartGenerator {

    @Inject
    private LuaMethodUtils utils;

    @Inject
    private DriverLuaBean values;

    @Inject
    private XmlDriverDefinition driverDef;

    @Inject
    @Named("prefix")
    private String driverPrefix;

    @Override
    public String getMidiReceivedPart() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(utils.indent(indent++)).append("if midiSize == ")
        .append(driverDef.getPatchSize()).append(" then")
        .append(utils.newLine());
        codeBuilder.append("-------------------- process ")
        .append(driverPrefix)
        .append(" data ----------------------------------------")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent))
        .append(values.getAssignValuesCall("midi:getData()", "false"))
        .append(utils.newLine());
        codeBuilder.append(utils.indent(--indent)).append("end")
        .append(utils.newLine());
        codeBuilder.append("---------------------------------------------")
        .append("------------------------------------")
        .append(utils.newLine());
        return codeBuilder.toString();
    }

}
