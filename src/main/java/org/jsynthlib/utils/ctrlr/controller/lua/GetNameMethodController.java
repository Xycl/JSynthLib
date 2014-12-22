package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

public class GetNameMethodController extends EditorLuaMethodControllerBase {

    public interface Factory {
        GetNameMethodController newGetNameMethodController(String[] chars);

        GetNameMethodController newGetNameMethodController();
    }

    @Inject
    private XmlDriverDefinition driverDef;
    private final String[] chars;

    @AssistedInject
    public GetNameMethodController(@Assisted String[] chars,
            @Named("prefix") String prefix, DriverModel model) {
        super(model.getGetNameMethodName());
        this.chars = chars;
    }

    @AssistedInject
    public GetNameMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        this(null, prefix, model);
    }

    String getStringArray(List<String> array) {
        StringBuilder code = new StringBuilder();
        code.append("{");
        for (int i = 0; i < array.size(); i++) {
            if (i > 0) {
                code.append(",");
            }
            code.append("\"").append(array.get(i)).append("\"");
        }
        code.append("}");
        return code.toString();
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method fetches the patch name from the hidden ")
        .append(newLine());
        code.append(indent(indent))
        .append("-- char modulators and returns it as a string")
        .append(newLine());
        code.append(indent(indent.getAndIncrement())).append(
                getMethodDecl("data"));
        code.append(getPanelInitCheck(indent)).append(newLine());
        code.append(indent(indent)).append("local patchNameStart = ")
        .append(driverDef.getPatchNameStart()).append(newLine());
        code.append(indent(indent)).append("local patchNameSize = ")
        .append(driverDef.getPatchNameSize()).append(newLine());
        code.append(indent(indent)).append("local name = &quot;&quot;")
        .append(newLine());

        if (chars != null) {
            code.append(indent(indent)).append("local symbols = ")
            .append(getStringArray(Arrays.asList(chars)))
            .append(newLine());
        }

        code.append(indent(indent.getAndIncrement())).append(
                "for i = patchNameStart,(patchNameStart + patchNameSize - 1) ");
        code.append("do -- gets the voice name").append(newLine());
        code.append(indent(indent)).append("midiParam = data:getByte(i)")
        .append(newLine());

        if (chars == null) {
            code.append(indent(indent))
            .append("name = name..string.char(midiParam)")
            .append(newLine());
        } else {
            code.append(indent(indent))
            .append("name = name..symbols[midiParam + 1] -- Lua tables are base 1 indexed")
            .append(newLine());
        }
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent)).append("return name").append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        setLuaMethodCode(code.toString());
    }
}
