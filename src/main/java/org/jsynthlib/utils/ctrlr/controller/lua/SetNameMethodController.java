package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

public class SetNameMethodController extends EditorLuaMethodControllerBase
implements Observer {

    public interface Factory {
        SetNameMethodController newSetNameMethodController(
                String[] patchNameChars);

        SetNameMethodController newSetNameMethodController();
    }

    private final DriverModel model;
    private final String prefix;

    @Inject
    private XmlDriverDefinition driverDef;
    private String nameModulatorName;
    private HashMap<String, Integer> charsMap;

    @AssistedInject
    public SetNameMethodController(@Assisted String[] patchNameChars,
            @Named("prefix") String prefix, DriverModel model) {
        super(model.getSetNameMethodName());
        this.model = model;
        this.prefix = prefix;
        if (patchNameChars != null) {
            charsMap = new HashMap<String, Integer>();
            for (int i = 0; i < patchNameChars.length; i++) {
                String string = patchNameChars[i];
                charsMap.put(string, i);
            }
        }
        model.addObserver(this);
    }

    @AssistedInject
    public SetNameMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        this(null, prefix, model);
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
    public void update(Observable o, Object arg) {
        nameModulatorName = model.getNameModulatorName();
        initialize();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (nameModulatorName == null) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        String dataAttrName = "data";
        String nameAttrName = "name";
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method set the values of the hidden char modulators ")
        .append(newLine());
        code.append(indent(indent)).append("-- to match the given name")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append(getMethodDecl(dataAttrName, nameAttrName))
        .append(newLine());
        code.append(getPanelInitCheck(indent)).append(newLine());
        code.append(indent(indent))
        .append("local b = panel:getModulatorByName(\"")
        .append(nameModulatorName).append("\")").append(newLine());
        code.append(indent(indent)).append("local c = b:getComponent()")
        .append(newLine());
        code.append(indent(indent))
        .append("local patchName = c:getProperty (\"uiLabelText\")")
        .append(newLine());
        code.append(indent(indent))
        .append("local patchNameLength = string.len(patchName)")
        .append(newLine());

        if (charsMap != null) {
            code.append(indent(indent)).append("local symbols = ")
            .append(getStringArray(charsMap)).append(newLine());
            code.append(indent(indent))
            .append("local emptyChar = symbols[\" \"]")
            .append(newLine());
        }

        for (int i = 0; i < driverDef.getPatchNameSize(); i++) {
            code.append(indent(indent.getAndIncrement()))
            .append("if patchNameLength > ").append(i).append(" then")
            .append(newLine());
            code.append(indent(indent))
            .append("mod = panel:getModulatorByName(\"").append(prefix)
            .append(driverDef.getPatchNameStart() + i).append("\")")
            .append(newLine());
            if (charsMap == null) {
                code.append(indent(indent))
                .append("caracter = string.byte(patchName, ")
                .append(i + 1).append(")").append(newLine());
                code.append(indent(indent))
                .append("mod:setValue(caracter, true)")
                .append(newLine());
            } else {
                code.append(indent(indent))
                .append("caracter = string.sub(patchName, ")
                .append(i + 1).append(", ").append(i + 1).append(")")
                .append(newLine());
                code.append(indent(indent))
                .append("mod:setValue(symbols[caracter], true)")
                .append(newLine());
            }

            code.append(indent(indent.decrementAndGet())).append("else")
            .append(newLine());
            indent.getAndIncrement();
            code.append(indent(indent))
            .append("mod = panel:getModulatorByName(\"").append(prefix)
            .append(driverDef.getPatchNameStart() + i).append("\")")
            .append(newLine());

            if (charsMap == null) {
                code.append(indent(indent)).append("mod:setValue(32, true)")
                .append(newLine());
            } else {
                code.append(indent(indent))
                .append("mod:setValue(emptyChar, true)")
                .append(newLine());
            }

            code.append(indent(indent.decrementAndGet())).append("end")
            .append(newLine());
        }
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }

}
