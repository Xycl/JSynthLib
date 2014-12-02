package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultSetNameMethodDecorator extends
DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultSetNameMethodDecorator newDefaultSetNameDecorator(
                DriverLuaHandler decoratedHandler, String[] chars);
    }

    @Inject
    private XmlDriverDefinition driverDef;

    private final String prefix;

    private final Map<String, Integer> charsMap;

    @Inject
    public DefaultSetNameMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler,
            @Assisted String[] chars) {
        super(driverPrefix, decoratedHandler);
        this.prefix = driverPrefix;
        this.charsMap = new HashMap<String, Integer>();
        for (int i = 0; i < chars.length; i++) {
            String string = chars[i];
            charsMap.put(string, i);
        }
    }

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
    protected String getSetNameMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getGetNameMethodBody());
        codeBuilder.append(indent(indent))
                .append("local b = panel:getModulatorByName(\"")
                .append(getNameModulator()).append("\")").append(newLine());
        codeBuilder.append(indent(indent)).append("local c = b:getComponent()")
                .append(newLine());
        codeBuilder.append(indent(indent))
                .append("local patchName = c:getProperty (\"uiLabelText\")")
                .append(newLine());
        codeBuilder.append(indent(indent))
                .append("local patchNameLength = string.len(patchName)")
                .append(newLine());
        codeBuilder.append(indent(indent)).append("local symbols = ")
                .append(getStringArray(charsMap)).append(newLine());
        codeBuilder.append(indent(indent))
                .append("local emptyChar = symbols[\" \"]").append(newLine());

        for (int i = 0; i < driverDef.getPatchNameSize(); i++) {
            codeBuilder.append(indent(indent++))
            .append("if patchNameLength > ").append(i).append(" then")
            .append(newLine());
            codeBuilder.append(indent(indent))
                    .append("mod = panel:getModulatorByName(\"").append(prefix)
            .append(driverDef.getPatchNameStart() + i).append("\")")
                    .append(newLine());
            codeBuilder.append(indent(indent))
                    .append("caracter = string.sub(patchName, ").append(i + 1)
            .append(", ").append(i + 1).append(")").append(newLine());
            codeBuilder.append(indent(indent))
                    .append("mod:setValue(symbols[caracter], true)")
                    .append(newLine());
            codeBuilder.append(indent(--indent)).append("else")
            .append(newLine());
            indent++;
            codeBuilder.append(indent(indent))
            .append("mod = panel:getModulatorByName(\"").append(prefix)
            .append(driverDef.getPatchNameStart() + i).append("\")")
                    .append(newLine());
            codeBuilder.append(indent(indent))
            .append("mod:setValue(emptyChar, true)").append(newLine());
            codeBuilder.append(indent(--indent)).append("end")
            .append(newLine());
        }
        return codeBuilder.toString();
    }

}
