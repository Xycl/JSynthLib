package org.jsynthlib.core.impl;

import com.google.inject.Inject;


public final class PopupHandlerProvider {
    @Inject
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
