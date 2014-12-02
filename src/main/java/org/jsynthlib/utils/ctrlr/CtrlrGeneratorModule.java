package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.impl.CtrlrImageConverterImpl;
import org.jsynthlib.utils.ctrlr.impl.PanelResourceManagerImpl;
import org.jsynthlib.utils.ctrlr.lua.PanelLuaManagerBuilderImpl;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class CtrlrGeneratorModule extends AbstractModule {

    private static Injector instance;

    private final String midiReceivedMethodName = "midiReceived";

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
        bind(PanelLuaManagerBuilder.class).to(PanelLuaManagerBuilderImpl.class);
        install(new FactoryModuleBuilder().implement(CtrlrSynthGenerator.class,
                CtrlrSynthGenerator.class).build(
                        CtrlrSynthGeneratorFactory.class));
        bind(PanelLuaManagerBuilder.class).to(PanelLuaManagerBuilderImpl.class);
    }

    @Provides
    @Named("midiReceivedMethodName")
    String provideMidiRecMethod() {
        return midiReceivedMethodName;
    }
}
