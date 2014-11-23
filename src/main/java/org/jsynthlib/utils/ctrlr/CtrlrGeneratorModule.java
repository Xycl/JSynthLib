package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.builder.CtrlrLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.builder.method.CtrlrLuaManagerBuilderImpl;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContextFactory;
import org.jsynthlib.utils.ctrlr.impl.CtrlrImageConverterImpl;
import org.jsynthlib.utils.ctrlr.impl.DriverContextImpl;
import org.jsynthlib.utils.ctrlr.impl.PanelResourceManagerImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CtrlrGeneratorModule extends AbstractModule {

    private static Injector instance;

    public static Injector getInjector() {
        if (instance == null) {
            instance =
                    JSynthLibInjector.getInjector().createChildInjector(
                            new CtrlrGeneratorModule());
        }
        return instance;
    }

    @Override
    protected void configure() {
        bind(CtrlrImageConverter.class).to(CtrlrImageConverterImpl.class);
        bind(PanelResourceManager.class).to(PanelResourceManagerImpl.class);
        bind(CtrlrLuaManagerBuilder.class).to(CtrlrLuaManagerBuilderImpl.class);
        install(new FactoryModuleBuilder().implement(DriverContext.class,
                DriverContextImpl.class).build(DriverContextFactory.class));
    }
}
