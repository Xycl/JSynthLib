package org.jsynthlib.utils.ctrlr.builder.method;

public class SetPatchNameBuilder extends MethodBuilder {

    private static final String CODE_STUB =
            "function setPatchName(midi, v_char)&#13;&#10;end";

    public SetPatchNameBuilder() {
        super("setPatchName");
    }

    @Override
    public String getCode() {
        return CODE_STUB;
    }

}
