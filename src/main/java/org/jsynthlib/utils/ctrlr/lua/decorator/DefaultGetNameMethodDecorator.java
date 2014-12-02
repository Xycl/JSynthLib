package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import java.util.Arrays;
import java.util.List;

import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultGetNameMethodDecorator extends
        DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultGetNameMethodDecorator newDefaultGetNameDecorator(
                DriverLuaHandler decoratedHandler, String[] chars);
    }

    @Inject
    private XmlDriverDefinition driverDef;
    private final String[] chars;

    @Inject
    public DefaultGetNameMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler,
            @Assisted String[] chars) {
        super(driverPrefix, decoratedHandler);
        this.chars = chars;
    }

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
    protected String getGetNameMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getGetNameMethodBody());
        codeBuilder.append(indent(indent)).append("local patchNameStart = ")
                .append(driverDef.getPatchNameStart()).append(newLine());
        codeBuilder.append(indent(indent)).append("local patchNameSize = ")
        .append(driverDef.getPatchNameSize()).append(newLine());
        codeBuilder.append(indent(indent)).append("local name = &quot;&quot;")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("local symbols = ")
        .append(getStringArray(Arrays.asList(chars))).append(newLine());
        codeBuilder.append(indent(indent++)).append(
                "for i = patchNameStart,(patchNameStart + patchNameSize - 1) ");
        codeBuilder.append("do -- gets the voice name").append(newLine());
        codeBuilder.append(indent(indent))
        .append("midiParam = data:getByte(i)").append(newLine());
        codeBuilder
                .append(indent(indent))
        .append("name = name..symbols[midiParam + 1] -- Lua tables are base 1 indexed")
                .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(indent)).append("return name")
        .append(newLine());
        return codeBuilder.toString();
    }

}
