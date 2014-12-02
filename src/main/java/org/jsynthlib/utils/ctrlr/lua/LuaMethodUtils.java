package org.jsynthlib.utils.ctrlr.lua;

import java.util.UUID;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;

public final class LuaMethodUtils {

    private LuaMethodUtils() {
    }

    public static String newUuid() {
        return UUID.randomUUID().toString().replace("_", "");
    }

    public static void fillMethodData(LuaMethodGroupType methodGroup,
            String name, String code) {
        LuaMethodType method = methodGroup.addNewLuaMethod();
        fillMethod(method, name, code);
    }

    static void fillMethod(LuaMethodType method, String name, String code) {
        method.setLuaMethodName(name);
        method.setLuaMethodCode(code);
        method.setUuid(newUuid());
        method.setLuaMethodLinkedProperty("");
        method.setLuaMethodSource(0);
        method.setLuaMethodValid(1);

    }

    public static void fillMethodData(LuaManagerMethodsType methods,
            String name, String code) {
        LuaMethodType method = methods.addNewLuaMethod();
        fillMethod(method, name, code);
    }

    public static LuaMethodGroupType newMethodGroup(
            LuaManagerMethodsType methods, String name) {
        LuaMethodGroupType methodGroup = methods.addNewLuaMethodGroup();
        methodGroup.setName(name);
        methodGroup.setUuid(newUuid());
        return methodGroup;
    }

    public static String getMethodDecl(String name, String... args) {
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

    public static String indent(int numTabs) {
        StringBuilder tabBuilder = new StringBuilder();
        for (int i = 0; i < numTabs; i++) {
            tabBuilder.append("&#9;");
        }
        return tabBuilder.toString();
    }

    public static String newLine() {
        return "&#13;&#10;";
    }

    public static void createMethod(LuaMethodGroupType methodGroup,
            String methodName, String methodBody, String... methodArgs) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(getMethodDecl(methodName, methodArgs));
        codeBuilder.append(methodBody);
        codeBuilder.append("end").append(newLine());
        LuaMethodUtils.fillMethodData(methodGroup, methodName,
                codeBuilder.toString());
    }
}
