package org.jsynthlib.utils.ctrlr.config;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.CtrlrSynthGenerator;
import org.jsynthlib.utils.ctrlr.CtrlrSynthGeneratorFactory;
import org.jsynthlib.utils.ctrlr.controller.PanelController;
import org.jsynthlib.utils.ctrlr.controller.PanelLuaManagerController;
import org.jsynthlib.utils.ctrlr.service.CtrlrImageConverter;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.PanelResourceManager;
import org.jsynthlib.utils.ctrlr.service.impl.CtrlrImageConverterImpl;
import org.jsynthlib.utils.ctrlr.service.impl.EditorLuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.impl.PanelResourceManagerImpl;
import org.jsynthlib.utils.ctrlr.service.impl.RootLuaMethodProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

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
        bind(LuaMethodProvider.class).annotatedWith(Names.named("root")).to(
                RootLuaMethodProvider.class);
        install(new FactoryModuleBuilder().implement(CtrlrSynthGenerator.class,
                CtrlrSynthGenerator.class).build(
                        CtrlrSynthGeneratorFactory.class));

        install(new FactoryModuleBuilder().implement(PanelController.class,
                PanelController.class).build(PanelController.Factory.class));
        install(new FactoryModuleBuilder().implement(LuaMethodProvider.class,
                EditorLuaMethodProvider.class).build(
                        EditorLuaMethodProvider.Factory.class));
        install(new FactoryModuleBuilder().implement(
                PanelLuaManagerController.class,
                PanelLuaManagerController.class).build(
                PanelLuaManagerController.Factory.class));
    }

    @Provides
    @Named("midiReceivedMethodName")
    String provideMidiRecMethod() {
        return midiReceivedMethodName;
    }
}
