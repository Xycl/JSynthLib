package org.jsynthlib.utils.ctrlr.lua.decorator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class EmptyDriverLuaHandler extends DriverLuaHandler {

    @Inject
    public EmptyDriverLuaHandler(@Named("prefix") String driverPrefix) {
        super(driverPrefix);
    }

    @Override
    protected String getAssignValuesMethodBody() {
        return "";
    }

    @Override
    protected String getAssembleValuesMethodBody() {
        return "";
    }

    @Override
    protected String getMidiReceivedMethodContents() {
        return "";
    }

    @Override
    protected String getGetMethodBody() {
        return "";
    }

    @Override
    protected String getSendMethodBody() {
        return "";
    }

    @Override
    protected String getLoadMethodBody() {
        return "";
    }

    @Override
    protected String getSaveMethodBody() {
        return "";
    }

    @Override
    protected String getGetNameMethodBody() {
        return "";
    }

    @Override
    protected String getSetNameMethodBody() {
        return "";
    }

    @Override
    public String getMidiReceivedPart() {
        return "";
    }

}
