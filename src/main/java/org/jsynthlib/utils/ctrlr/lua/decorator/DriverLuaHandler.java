package org.jsynthlib.utils.ctrlr.lua.decorator;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.createMethod;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;

public abstract class DriverLuaHandler {

    @Inject
    private XmlDriverDefinition driverDef;

    private final String driverPrefix;
    private final String nameModulator;
    private final String getMethod;
    private final String sendMethod;
    private final String loadMethod;
    private final String saveMethod;
    private final String getNameMethod;
    private final String setNameMethod;
    private final String assignValuesMethod;
    private final String assembleValuesMethod;

    private final String infoLabel;

    public DriverLuaHandler(String driverPrefix) {
        this.driverPrefix = driverPrefix;
        nameModulator = driverPrefix + "_patchNameModulator";
        infoLabel = driverPrefix + "_infoModulator";
        getMethod = driverPrefix + "_GetPatch";
        sendMethod = driverPrefix + "_SendPatch";
        loadMethod = driverPrefix + "_LoadPatch";
        saveMethod = driverPrefix + "_SavePatch";

        getNameMethod = driverPrefix + "_GetPatchName";
        setNameMethod = driverPrefix + "_SetPatchName";
        assignValuesMethod = driverPrefix + "_AssignValues";
        assembleValuesMethod = driverPrefix + "_AssembleValues";
    }

    public void createDriverMethodGroup(LuaManagerMethodsType methods) {
        LuaMethodGroupType methodGroup =
                LuaMethodUtils.newMethodGroup(methods, driverPrefix);
        createAssembleValuesMethod(methodGroup);
        createAssignValuesMethod(methodGroup);
        createGetNameMethod(methodGroup);
        createSetNameMethod(methodGroup);
        createGetMethod(methodGroup);
        createSendMethod(methodGroup);
        createLoadMethod(methodGroup);
        createSaveMethod(methodGroup);
    }

    public abstract String getMidiReceivedPart();

    void createAssembleValuesMethod(LuaMethodGroupType methodGroup) {
        createMethod(methodGroup, assembleValuesMethod,
                getAssembleValuesMethodBody(), "data");
    }

    void createAssignValuesMethod(LuaMethodGroupType methodGroup) {
        String dataAttrName = "data";
        String sendMidiAttrName = "midi";
        createMethod(methodGroup, assignValuesMethod,
                getAssignValuesMethodBody(), dataAttrName, sendMidiAttrName);
    }

    void createSetNameMethod(LuaMethodGroupType methodGroup) {
        String dataAttrName = "data";
        String nameAttrName = "name";
        createMethod(methodGroup, setNameMethod, getSetNameMethodBody(),
                dataAttrName, nameAttrName);
    }

    void createGetNameMethod(LuaMethodGroupType methodGroup) {
        String dataAttrName = "data";
        createMethod(methodGroup, getNameMethod, getGetNameMethodBody(),
                dataAttrName);
    }

    void createGetMethod(LuaMethodGroupType methodGroup) {
        createMethod(methodGroup, getMethod, getGetMethodBody(), "modulator",
                "newValue");
    }

    void createSendMethod(LuaMethodGroupType methodGroup) {
        createMethod(methodGroup, sendMethod, getSendMethodBody());
    }

    void createLoadMethod(LuaMethodGroupType methodGroup) {
        createMethod(methodGroup, loadMethod, getLoadMethodBody(), "modulator",
                "newValue");
    }

    void createSaveMethod(LuaMethodGroupType methodGroup) {
        createMethod(methodGroup, saveMethod, getSaveMethodBody());
    }

    public String getAssignValuesCall(String dataAttrName,
            String sendMidiAttrName) {
        return new StringBuilder().append(assignValuesMethod).append("(")
                .append(dataAttrName).append(", ").append(sendMidiAttrName)
                .append(")").toString();

    }

    public String getSetNameCall(String dataAttrName, String nameAttrName) {
        return new StringBuilder().append(setNameMethod).append("(")
                .append(dataAttrName).append(", ").append(nameAttrName)
                .append(")").toString();
    }

    public String getGetNameCall(String dataAttrName) {
        return new StringBuilder().append(getNameMethod).append("(")
                .append(dataAttrName).append(")").toString();
    }

    public String getAssembleValuesCall(String dataAttrName) {
        return new StringBuilder().append(assembleValuesMethod).append("(")
                .append(dataAttrName).append(")").toString();
    }

    protected abstract String getAssignValuesMethodBody();

    protected abstract String getAssembleValuesMethodBody();

    protected abstract String getMidiReceivedMethodContents();

    protected abstract String getGetMethodBody();

    protected abstract String getSendMethodBody();

    protected abstract String getLoadMethodBody();

    protected abstract String getSaveMethodBody();

    protected abstract String getGetNameMethodBody();

    protected abstract String getSetNameMethodBody();

    String getMessageCall(String msg, int indent, String color) {
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            codeBuilder.append("&#9;");
        }
        codeBuilder
                .append("panel:getModulatorByName(\"")
                .append(infoLabel)
                .append("\"):getComponent():setPropertyString (\"uiLabelText\", \"")
                .append(msg).append("\")");
        codeBuilder.append("&#13;&#10;");

        for (int i = 0; i < indent; i++) {
            codeBuilder.append("&#9;");
        }
        codeBuilder
                .append("panel:getModulatorByName(\"")
                .append(infoLabel)
                .append("\"):getComponent():setPropertyString (\"uiLabelTextColour\", \"");
        codeBuilder.append(color);
        codeBuilder.append("\")");
        codeBuilder.append("&#13;&#10;");

        return codeBuilder.toString();
    }

    public String getInfoMessageCall(String msg, int indent) {
        return getMessageCall(msg, indent, "00000000");
    }

    public String getWarnMessageCall(String msg, int indent) {
        return getMessageCall(msg, indent, "ff0000ff");
    }

    public final String getNameModulator() {
        return nameModulator;
    }

    public final String getGetMethod() {
        return getMethod;
    }

    public final String getSendMethod() {
        return sendMethod;
    }

    public final String getLoadMethod() {
        return loadMethod;
    }

    public final String getSaveMethod() {
        return saveMethod;
    }

    public final int getPatchSize() {
        return driverDef.getPatchSize();
    }

    public final String getSetNameMethod() {
        return setNameMethod;
    }

    public String getDriverPrefix() {
        return driverPrefix;
    }
}
