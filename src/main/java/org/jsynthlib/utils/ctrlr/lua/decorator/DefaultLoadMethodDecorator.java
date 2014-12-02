package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultLoadMethodDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultLoadMethodDecorator newDefaultLoadMethodDecorator(
                DriverLuaHandler decoratedHandler);
    }

    @Inject
    public DefaultLoadMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
    }

    @Override
    protected String getLoadMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getLoadMethodBody());
        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        // codeBuilder.append(indent(indent++)).append("if newValue == 1 then")
        // .append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("f = utils.openFileWindow (\"Open Patch\", File(\"\"), \"*.syx\", true)")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if f:existsAsFile() then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("PatchDataLoaded = MemoryBlock()").append(newLine());
        codeBuilder.append(indent(indent))
        .append("f:loadFileAsData(PatchDataLoaded)").append(newLine());
        codeBuilder.append(indent(indent))
                .append(getAssignValuesCall("PatchDataLoaded", "true"))
                .append(newLine());
        // Display Patch Loaded
        // codeBuilder.append(getInfoMessageCall("Patch Loaded", indent));
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        // codeBuilder.append(indent(--indent)).append("end").append(newLine());
        // codeBuilder.append(indent(indent)).append("modulator:setValue(0,false)")
        // .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        return codeBuilder.toString();
    }
}
