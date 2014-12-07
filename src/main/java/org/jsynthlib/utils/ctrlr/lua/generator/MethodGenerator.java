package org.jsynthlib.utils.ctrlr.lua.generator;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils;

import com.google.inject.Inject;

public abstract class MethodGenerator {

    @Inject
    private LuaMethodUtils utils;

    public abstract LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values);

    protected String newUuid() {
        return utils.newUuid();
    }

    protected void fillMethodData(LuaMethodGroupType methodGroup, String name,
            String code) {
        utils.fillMethodData(methodGroup, name, code);
    }

    protected void fillMethod(LuaMethodType method, String name, String code) {
        utils.fillMethod(method, name, code);
    }

    protected void fillMethodData(LuaManagerMethodsType methods, String name,
            String code) {
        utils.fillMethodData(methods, name, code);
    }

    protected LuaMethodGroupType newMethodGroup(LuaManagerMethodsType methods,
            String name) {
        return utils.newMethodGroup(methods, name);
    }

    protected String getMethodDecl(String name, String... args) {
        return utils.getMethodDecl(name, args);
    }

    protected String indent(int numTabs) {
        return utils.indent(numTabs);
    }

    protected String newLine() {
        return utils.newLine();
    }

    protected LuaMethodType createMethod(LuaMethodGroupType methodGroup,
            String methodName,
            String methodCode) {
        return utils.createMethod(methodGroup, methodName, methodCode);
    }

    protected String getInfoMessageCall(String msg) {
        return utils.getInfoMessageCall(msg);
    }

    protected String getWarnMessageCall(String msg) {
        return utils.getWarnMessageCall(msg);
    }
}
