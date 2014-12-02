package org.jsynthlib.utils.ctrlr.driverContext;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.core.impl.PopupHandler;
import org.jsynthlib.utils.ctrlr.DriverModuleBuilder;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.builder.component.NameCharSliderBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.PatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiCombinedGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiEnvelopeBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiImageButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiIncDecButtonsBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiKnobBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiLabelBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiSliderBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
import org.jsynthlib.utils.ctrlr.driverContext.impl.ConverterDeviceFactoryImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.DriverMethodParserImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.ParameterOffsetParserImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.PopupManagerImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.XmlSingleDriverParserImpl;
import org.jsynthlib.utils.ctrlr.lua.DecoratorFactoryFacade;
import org.jsynthlib.utils.ctrlr.lua.DecoratorFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssembleValuesDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultAssignValuesDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultGetNameMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultLoadMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultMidiReceivedDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSaveMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSendMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DefaultSetNameMethodDecorator;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;
import org.jsynthlib.utils.ctrlr.lua.decorator.EmptyDriverLuaHandler;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class DriverModule extends AbstractModule {

    private final XmlDeviceDefinition deviceDef;
    private final String driverClassName;
    private final XmlDriverDefinition driverDef;
    private final String driverPrefix;
    private final XmlDriverReference driverRef;
    private final PanelType panel;

    public DriverModule(DriverModuleBuilder builder) {
        deviceDef = builder.getDeviceDef();
        driverClassName = builder.getDriverClassName();
        driverDef = builder.getDriverDef();
        driverPrefix = builder.getDriverPrefix();
        driverRef = builder.getDriverRef();
        panel = builder.getPanel();
    }

    @Override
    protected void configure() {
        bind(SysexFormulaParser.class).to(SysexFormulaParserImpl.class);
        bind(ParameterOffsetParser.class).to(ParameterOffsetParserImpl.class);
        bind(BuilderFactoryFacade.class).to(BuilderFactoryFacadeImpl.class);
        bind(ConverterDeviceFactory.class).to(ConverterDeviceFactoryImpl.class);
        bind(DriverMethodParser.class).to(DriverMethodParserImpl.class);
        bind(PopupHandler.class).to(PopupManagerImpl.class);
        bind(PopupManager.class).to(PopupManagerImpl.class);
        bind(DriverLuaHandler.class).to(EmptyDriverLuaHandler.class);
        bind(DecoratorFactoryFacade.class).to(DecoratorFactoryFacadeImpl.class);

        switch (driverRef.getDriverType().intValue()) {
        case XmlDriverReference.DriverType.INT_PATCH:
            bind(XmlDriverParser.class).to(XmlSingleDriverParserImpl.class);
            break;
        default:
            break;
        }

        // Builder factories
        install(new FactoryModuleBuilder().implement(PatchNameBuilder.class,
                PatchNameBuilder.class).build(PatchNameBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiImageButtonBuilder.class, UiImageButtonBuilder.class).build(
                UiImageButtonBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiButtonBuilder.class,
                UiButtonBuilder.class).build(UiButtonBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiKnobBuilder.class,
                UiKnobBuilder.class).build(UiKnobBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiGroupBuilder.class,
                UiGroupBuilder.class).build(UiGroupBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiTabBuilder.class,
                UiTabBuilder.class).build(UiTabBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiEnvelopeBuilder.class,
                UiEnvelopeBuilder.class).build(UiEnvelopeBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiCombinedGroupBuilder.class, UiCombinedGroupBuilder.class)
                .build(UiCombinedGroupBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiIncDecButtonsBuilder.class, UiIncDecButtonsBuilder.class)
                .build(UiIncDecButtonsBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiLabelBuilder.class,
                UiLabelBuilder.class).build(UiLabelBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(UiSliderBuilder.class,
                UiSliderBuilder.class).build(UiSliderBuilder.Factory.class));
        install(new FactoryModuleBuilder().implement(
                NameCharSliderBuilder.class, NameCharSliderBuilder.class)
                .build(NameCharSliderBuilder.Factory.class));

        // Decorator factories
        install(new FactoryModuleBuilder().implement(
                DefaultAssembleValuesDecorator.class,
                DefaultAssembleValuesDecorator.class).build(
                DefaultAssembleValuesDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultAssignValuesDecorator.class,
                DefaultAssignValuesDecorator.class).build(
                        DefaultAssignValuesDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultGetMethodDecorator.class,
                DefaultGetMethodDecorator.class).build(
                        DefaultGetMethodDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultGetNameMethodDecorator.class,
                DefaultGetNameMethodDecorator.class).build(
                        DefaultGetNameMethodDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultLoadMethodDecorator.class,
                DefaultLoadMethodDecorator.class).build(
                        DefaultLoadMethodDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultSendMethodDecorator.class,
                DefaultSendMethodDecorator.class).build(
                        DefaultSendMethodDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultSaveMethodDecorator.class,
                DefaultSaveMethodDecorator.class).build(
                        DefaultSaveMethodDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultMidiReceivedDecorator.class,
                DefaultMidiReceivedDecorator.class).build(
                        DefaultMidiReceivedDecorator.Factory.class));
        install(new FactoryModuleBuilder().implement(
                DefaultSetNameMethodDecorator.class,
                DefaultSetNameMethodDecorator.class).build(
                        DefaultSetNameMethodDecorator.Factory.class));

    }

    @Provides
    XmlDriverDefinition provideXmlDriverDefinition() {
        return driverDef;
    }

    @Provides
    XmlDeviceDefinition provideXmlDeviceDefinition() {
        return deviceDef;
    }

    @Provides
    XmlDriverReference provideXmlDriverReference() {
        return driverRef;
    }

    @Provides
    PanelType providePanelType() {
        return panel;
    }

    @Provides
    @Named("className")
    String provideClassName() {
        return driverClassName;
    }

    @Provides
    @Named("prefix")
    String providePrefix() {
        return driverPrefix;
    }
}
