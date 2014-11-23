package org.jsynthlib.utils.ctrlr.builder.method;

public abstract class MethodBuilder {

    private final String name;

    protected MethodBuilder(String name) {
        this.name = name;
    }

    public abstract String getCode();

    public String getName() {
        return name;
    }
}
