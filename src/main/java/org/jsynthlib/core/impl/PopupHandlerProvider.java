package org.jsynthlib.core.impl;

import org.jsynthlib.inject.JSynthLibInjector;

import com.google.inject.Injector;

public final class PopupHandlerProvider {
    private static Injector injector = JSynthLibInjector.getInjector();

    private PopupHandlerProvider() {
    }

    public static void setInjector(Injector inj) {
        injector = inj;
    }

    public static PopupHandler get() {
        return injector.getInstance(PopupHandler.class);
    }
}
