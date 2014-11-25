package org.jsynthlib.utils.ctrlr.driverContext;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacadeImpl;
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
import org.jsynthlib.utils.ctrlr.impl.CtrlrConverterDeviceFactoryImpl;
import org.jsynthlib.utils.ctrlr.impl.GlobalPatchMethodParserImpl;
import org.jsynthlib.utils.ctrlr.impl.ParameterOffsetParserImpl;
import org.jsynthlib.utils.ctrlr.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.impl.XmlSingleDriverParserImpl;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class CtrlrDriverModule extends AbstractModule {

    private final XmlDeviceDefinition deviceDef;
    private final String driverClassName;
    private final XmlDriverDefinition driverDef;
    private final String driverPrefix;
    private final XmlDriverReference driverRef;
    private final PanelType panel;

    public CtrlrDriverModule(DriverModuleBuilder builder) {
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
        bind(CtrlrConverterDeviceFactory.class).to(
                CtrlrConverterDeviceFactoryImpl.class);
        bind(GlobalPatchMethodParser.class).to(
                GlobalPatchMethodParserImpl.class);

        switch (driverRef.getDriverType().intValue()) {
        case XmlDriverReference.DriverType.INT_PATCH:
            bind(XmlDriverParser.class).to(XmlSingleDriverParserImpl.class);
            break;
        default:
            break;
        }
        // install(new FactoryModuleBuilder().implement(XmlDriverParser.class,
        // XmlSingleDriverParserImpl.class).build(
        // XmlSingleDriverParserFactory.class));

        // install(new FactoryModuleBuilder().implement(XmlDriverParser.class,
        // XmlSingleDriverParserImpl.class).build(
        // XmlBankDriverParserFactory.class));

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
