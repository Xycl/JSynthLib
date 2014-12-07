package org.jsynthlib.core.impl;


public final class PopupHandlerProvider {
    private static PopupHandler injector;

    private PopupHandlerProvider() {
    }

    public static void setInjector(PopupHandler inj) {
        injector = inj;
    }

    public static PopupHandler get() {
        return injector;
    }
}
