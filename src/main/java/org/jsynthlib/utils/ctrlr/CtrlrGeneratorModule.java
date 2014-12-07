package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.impl.CtrlrImageConverterImpl;
import org.jsynthlib.utils.ctrlr.impl.PanelResourceManagerImpl;
import org.jsynthlib.utils.ctrlr.lua.PanelLuaManagerBuilderImpl;
import org.jsynthlib.utils.ctrlr.lua.generator.AssembleValuesGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.AssignValuesGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.GetNameMethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.LoadMenuGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.LoadMethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.MethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.ReceiveMenuGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.ReceiveMethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.SaveMenuGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.SaveMethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.SetNameMethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.WriteMenuGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.WriteMethodGenerator;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Named;

public class CtrlrGeneratorModule extends AbstractModule {

    private static Injector instance;

    private final String midiReceivedMethodName = "midiReceived";

    public static Injector getInjector() {
        if (instance == null) {
            instance =
                    JSynthLibInjector.getInjector().createChildInjector(new CtrlrGeneratorModule());
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

        Multibinder<MethodGenerator> actionBinder =
                Multibinder.newSetBinder(binder(), MethodGenerator.class);
        actionBinder.addBinding().to(AssembleValuesGenerator.class);
        actionBinder.addBinding().to(AssignValuesGenerator.class);
        actionBinder.addBinding().to(GetNameMethodGenerator.class);
        actionBinder.addBinding().to(LoadMenuGenerator.class);
        actionBinder.addBinding().to(LoadMethodGenerator.class);
        actionBinder.addBinding().to(ReceiveMenuGenerator.class);
        actionBinder.addBinding().to(ReceiveMethodGenerator.class);
        actionBinder.addBinding().to(SaveMenuGenerator.class);
        actionBinder.addBinding().to(SaveMethodGenerator.class);
        actionBinder.addBinding().to(SetNameMethodGenerator.class);
        actionBinder.addBinding().to(WriteMenuGenerator.class);
        actionBinder.addBinding().to(WriteMethodGenerator.class);

    }

    @Provides
    @Named("midiReceivedMethodName")
    String provideMidiRecMethod() {
        return midiReceivedMethodName;
    }
}
