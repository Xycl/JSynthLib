package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class DefaultSaveMethodDecorator extends DriverLuaHandlerDecoratorBase {

    public interface Factory {
        DefaultSaveMethodDecorator newDefaultSaveMethodDecorator(
                DriverLuaHandler decoratedHandler);
    }

    @Inject
    public DefaultSaveMethodDecorator(@Named("prefix") String driverPrefix,
            @Assisted DriverLuaHandler decoratedHandler) {
        super(driverPrefix, decoratedHandler);
    }

    @Override
    protected String getSaveMethodBody() {
        int indent = 1;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(super.getAssembleValuesMethodBody());

        codeBuilder.append(indent(indent++))
        .append("if panel_loaded == 1 then").append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("f = utils.saveFileWindow (\"Save Patch\", File(\"\"), \"*.syx\", true)")
        .append(newLine());
        codeBuilder.append(indent(indent)).append("f:create()")
        .append(newLine());
        codeBuilder.append(indent(indent++)).append("if f:existsAsFile() then")
        .append(newLine());
        codeBuilder.append(indent(indent))
        .append("PatchDataCurrent = MemoryBlock()").append(newLine());
        codeBuilder.append(indent(indent))
                .append(getAssembleValuesCall("PatchDataCurrent"))
                .append(newLine());
        codeBuilder.append(indent(indent))
        .append("f:replaceFileContentWithData(PatchDataCurrent)")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        return codeBuilder.toString();
    }
}
