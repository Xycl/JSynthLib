package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class SaveMethodControllerBase extends
        EditorLuaMethodControllerBase {

    private final String type;
    private String bankVarName;

    public SaveMethodControllerBase(String methodName, String type) {
        super(methodName);
        this.type = type;
    }

    protected String getMethodBegin(AtomicInteger indent, boolean checkBank) {
        StringBuilder code = new StringBuilder();

        code.append(indent(indent.getAndIncrement())).append(getMethodDecl());

        code.append(getPanelInitCheck(indent)).append(newLine());

        if (checkBank) {

            code.append(indent(indent.getAndIncrement())).append("if ")
            .append(bankVarName).append(" == nil then")
            .append(newLine());
            code.append(indent(indent))
            .append("utils.warnWindow (\"No bank loaded\", \"You must load a bank in order to perform this action.\")")
            .append(newLine());
            code.append(indent(indent)).append("return").append(newLine());
            code.append(indent(indent.decrementAndGet())).append("end")
            .append(newLine());
        }

        code.append(indent(indent)).append("f = utils.saveFileWindow (\"Save ")
        .append(type).append("\", File(\"\"), \"*.syx\", true)")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:isValid() == false then").append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent)).append("f:create()").append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:existsAsFile() then").append(newLine());

        return code.toString();
    }

    protected String getMethodEnd(AtomicInteger indent) {
        StringBuilder code = new StringBuilder();

        code.append(indent(indent)).append("-- Check if the file exists")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:existsAsFile() == false then").append(newLine());
        code.append(indent(indent))
        .append("-- If file does not exist, then create it")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:create() == false then").append(newLine());
        code.append(indent(indent))
        .append("-- If file cannot be created, then fail here")
        .append(newLine());
        code.append(indent(indent))
        .append("utils.warnWindow (\"\\n\\nSorry, the Editor failed to\\nsave the ")
        .append(type)
        .append(" to disk!\", \"The file does not exist.\")")
        .append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        code.append(indent(indent))
        .append("-- If we reached this point, we have a valid file we can try to write to")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:replaceWithData (PatchDataCurrent) == false then")
        .append(newLine());
        code.append(indent(indent))
        .append("utils.warnWindow (\"File write\", \"Sorry, the Editor failed to\\nwrite the data to file!\")")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent))
        .append("console (\"File save complete, Editor patch saved to disk\")")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        return code.toString();
    }

    protected String getBankVarName() {
        return bankVarName;
    }

    protected void setBankVarName(String bankVarName) {
        this.bankVarName = bankVarName;
    }
}
