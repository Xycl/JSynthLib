package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringEscapeUtils;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

public abstract class LuaMethodControllerBase {

    private final String methodName;
    private LuaMethodType method;

    public LuaMethodControllerBase(String methodName) {
        this.methodName = methodName;
    }

    protected void initialize() {
        method = getLuaMethodProvider().getLuaMethod(methodName);
        method.setLuaMethodName(methodName);
        method.setLuaMethodLinkedProperty("");
        method.setLuaMethodSource(0);
        method.setLuaMethodValid(1);
        writeLuaMethodCode();
    }

    public void init() {
        try {
            checkPreconditions();
            initialize();
        } catch (PreConditionsNotMetException e) {
            // Skip initialization until all preconditions are met.
        }
    }

    protected void checkPreconditions() throws PreConditionsNotMetException {
        // No pre conditions by default
    }

    protected abstract LuaMethodProvider getLuaMethodProvider();

    protected abstract void writeLuaMethodCode();

    public void setLuaMethodCode(String luaMethodCode) {
        method.setLuaMethodCode(luaMethodCode);
    }

    public String getPanelInitCheck(AtomicInteger indent) {
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This variable stops index issues during panel bootup")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if panel:getRestoreState() == true or panel:getProgramState() == true then")
        .append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
                .append(newLine()).append(newLine());

        code.append(indent(indent.getAndIncrement()))
        .append("if panel_loaded == 0 then").append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        return code.toString();
    }

    public String getMethodCall(String methodName, String... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(methodName).append("(");
        boolean first = true;
        for (String arg : args) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            sb.append(arg);
        }
        return sb.append(")").toString();
    }

    public String getMethodDecl(String... args) {
        StringBuilder sb =
                new StringBuilder().append("function ").append(methodName)
                .append(" (");
        if (args != null) {
            boolean first = true;
            for (String arg : args) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(arg);
            }
        }
        sb.append(")").append(newLine());
        return sb.toString();
    }

    public String indent(AtomicInteger numTabs) {
        return indent(numTabs.get());
    }

    public String indent(int numTabs) {
        StringBuilder tabBuilder = new StringBuilder();
        for (int i = 0; i < numTabs; i++) {
            tabBuilder.append("&#9;");
        }
        return tabBuilder.toString();
    }

    public String newLine() {
        return "&#10;";
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

    public String getInfoOkCancelMessageCall(String title, String msg) {
        StringBuilder codeBuilder = new StringBuilder();

        String escapedMsg =
                StringEscapeUtils.escapeJava(msg).replace("\n", "\\n");
        codeBuilder
        .append("AlertWindow.showOkCancelBox(AlertWindow.InfoIcon, \"")
        .append(title).append("\", \"").append(escapedMsg)
        .append("\", \"OK\", \"Cancel\")");
        return codeBuilder.toString();
    }

    public String getInfoMessageCall(String msg) {
        return getMessageCall(false, "Information", msg);
    }

    public String getWarnMessageCall(String msg) {
        return getMessageCall(true, "Warning", msg);
    }

    public LuaMethodType getMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }
}
