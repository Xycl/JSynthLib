package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AssignValuesController extends EditorLuaMethodControllerBase
implements Observer {

    public interface Factory {
        AssignValuesController newAssignValuesController();
    }

    private final DriverModel model;
    private final String prefix;
    private String patchNameModulatorId;

    /**
     * @param group
     * @param methodName
     */
    @Inject
    public AssignValuesController(DriverModel model,
            @Named("prefix") String prefix) {
        super(model.getAssignValuesMethodName());
        this.prefix = prefix;
        this.model = model;
        model.addObserver(this);
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (patchNameModulatorId == null) {
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
    public void update(Observable o, Object arg) {
        patchNameModulatorId = model.getNameModulatorName();
        init();
    }

    @Override
    protected void writeLuaMethodCode() {
        String dataAttrName = "data";
        String sendMidiAttrName = "midi";

        int indent = 0;
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method assigns patch data from a memory block ")
        .append(newLine());
        code.append(indent(indent)).append("-- to all modulators in the panel")
        .append(newLine());
        code.append(indent(indent++)).append(
                getMethodDecl(dataAttrName, sendMidiAttrName));
        code.append(indent(indent)).append("midiSize = data:getSize()")
        .append(newLine());
        code.append(indent(indent++)).append("for i = 0,midiSize do -- ")
        .append("gets the voice parameter values").append(newLine());
        code.append(indent(indent)).append("midiParam = data:getByte(i)")
        .append(newLine());
        code.append(indent(indent)).append("name = &quot;").append(prefix)
        .append("&quot;..i").append(newLine());
        code.append(indent(indent))
        .append("mod = panel:getModulatorByName(name)")
        .append(newLine());
        code.append(indent(indent++)).append("if mod ~= nil then")
        .append(newLine());
        code.append(indent(indent))
        .append("mod:setModulatorValue(midiParam, false, midi, false)")
        .append(newLine());
        code.append(indent(--indent)).append("end").append(newLine());
        code.append(indent(--indent)).append("end").append(newLine());

        code.append(indent(indent)).append("v_char = ")
        .append(getMethodCall(model.getGetNameMethodName(), "data"))
        .append(newLine());

        code.append(indent(indent)).append("panel:getModulatorByName(&quot;")
        .append(patchNameModulatorId)
        .append("&quot;):getComponent():setText(v_char)")
        .append(newLine());

        code.append(indent(--indent)).append("end").append(newLine());

        setLuaMethodCode(code.toString());
    }

}
