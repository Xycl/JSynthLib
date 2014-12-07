package org.jsynthlib.utils.ctrlr.lua;

import java.util.UUID;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;

import com.google.inject.Singleton;

@Singleton
public class LuaMethodUtils {

    public String newUuid() {
        return UUID.randomUUID().toString().replace("_", "");
    }

    public LuaMethodType fillMethodData(LuaMethodGroupType methodGroup,
            String name, String code) {
        LuaMethodType method = methodGroup.addNewLuaMethod();
        fillMethod(method, name, code);
        return method;
    }

    public void fillMethod(LuaMethodType method, String name, String code) {
        method.setLuaMethodName(name);
        method.setLuaMethodCode(code);
        method.setUuid(newUuid());
        method.setLuaMethodLinkedProperty("");
        method.setLuaMethodSource(0);
        method.setLuaMethodValid(1);

    }

    public void fillMethodData(LuaManagerMethodsType methods,
            String name, String code) {
        LuaMethodType method = methods.addNewLuaMethod();
        fillMethod(method, name, code);
    }

    public LuaMethodGroupType newMethodGroup(
            LuaManagerMethodsType methods, String name) {
        LuaMethodGroupType methodGroup = methods.addNewLuaMethodGroup();
        methodGroup.setName(name);
        methodGroup.setUuid(newUuid());
        return methodGroup;
    }

    public String getMethodDecl(String name, String... args) {
        StringBuilder sb =
                new StringBuilder().append("function ").append(name)
                .append(" (");
        boolean first = true;
        for (String arg : args) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(arg);
        }
        sb.append(")").append(newLine());
        return sb.toString();
    }

    public String indent(int numTabs) {
        StringBuilder tabBuilder = new StringBuilder();
        for (int i = 0; i < numTabs; i++) {
            tabBuilder.append("&#9;");
        }
        return tabBuilder.toString();
    }

    public String newLine() {
        return "&#13;&#10;";
    }

    public LuaMethodType createMethod(LuaMethodGroupType methodGroup,
            String methodName, String methodCode) {
        return fillMethodData(methodGroup, methodName,
                methodCode);
    }

    String getMessageCall(boolean warning, String title, String msg) {
        StringBuilder codeBuilder = new StringBuilder();

        String escapedMsg =
                StringEscapeUtils.escapeJava(msg).replace("\n", "\\n");
        codeBuilder.append("AlertWindow.showMessageBox(AlertWindow.");
        if (warning) {
            codeBuilder.append("WarningIcon");
        } else {
            codeBuilder.append("InfoIcon");
        }
        codeBuilder.append(", \"").append(title).append("\", \"")
        .append(escapedMsg).append("\", \"OK\")");
        return codeBuilder.toString();
    }

    public String getInfoMessageCall(String msg) {
        return getMessageCall(false, "Information", msg);
    }

    public String getWarnMessageCall(String msg) {
        return getMessageCall(true, "Warning", msg);
    }

}
