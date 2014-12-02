package org.jsynthlib.utils.ctrlr.lua.decorator;

public class DriverLuaHandlerDecoratorBase extends DriverLuaHandler {

    private final DriverLuaHandler decoratedHandler;

    public DriverLuaHandlerDecoratorBase(String driverPrefix,
            DriverLuaHandler decoratedHandler) {
        super(driverPrefix);
        this.decoratedHandler = decoratedHandler;
    }

    @Override
    protected String getAssignValuesMethodBody() {
        return decoratedHandler.getAssignValuesMethodBody();
    }

    @Override
    protected String getAssembleValuesMethodBody() {
        return decoratedHandler.getAssembleValuesMethodBody();
    }

    @Override
    protected String getMidiReceivedMethodContents() {
        return decoratedHandler.getMidiReceivedMethodContents();
    }

    @Override
    protected String getGetMethodBody() {
        return decoratedHandler.getGetMethodBody();
    }

    @Override
    protected String getSendMethodBody() {
        return decoratedHandler.getSendMethodBody();
    }

    @Override
    protected String getLoadMethodBody() {
        return decoratedHandler.getLoadMethodBody();
    }

    @Override
    protected String getSaveMethodBody() {
        return decoratedHandler.getSaveMethodBody();
    }

    @Override
    protected String getGetNameMethodBody() {
        return decoratedHandler.getGetNameMethodBody();
    }

    @Override
    protected String getSetNameMethodBody() {
        return decoratedHandler.getSetNameMethodBody();
    }

    @Override
    public String getMidiReceivedPart() {
        return decoratedHandler.getMidiReceivedPart();
    }

    // @Override
    // public void createDriverMethodGroup(LuaManagerMethodsType methods) {
    // decoratedHandler.createDriverMethodGroup(methods);
    // }

    @Override
    public boolean equals(Object arg0) {
        return decoratedHandler.equals(arg0);
    }

    @Override
    public String getAssignValuesCall(String dataAttrName,
            String sendMidiAttrName) {
        return decoratedHandler.getAssignValuesCall(dataAttrName,
                sendMidiAttrName);
    }

    @Override
    public String getSetNameCall(String dataAttrName, String nameAttrName) {
        return decoratedHandler.getSetNameCall(dataAttrName, nameAttrName);
    }

    @Override
    public String getGetNameCall(String dataAttrName) {
        return decoratedHandler.getGetNameCall(dataAttrName);
    }

    @Override
    public String getAssembleValuesCall(String dataAttrName) {
        return decoratedHandler.getAssembleValuesCall(dataAttrName);
    }

    @Override
    public String getInfoMessageCall(String msg, int indent) {
        return decoratedHandler.getInfoMessageCall(msg, indent);
    }

    @Override
    public String getWarnMessageCall(String msg, int indent) {
        return decoratedHandler.getWarnMessageCall(msg, indent);
    }

    @Override
    public int hashCode() {
        return decoratedHandler.hashCode();
    }

    @Override
    public String toString() {
        return decoratedHandler.toString();
    }

}
