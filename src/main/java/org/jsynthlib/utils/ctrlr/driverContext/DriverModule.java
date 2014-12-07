package org.jsynthlib.utils.ctrlr.driverContext;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
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
import org.jsynthlib.utils.ctrlr.driverContext.impl.ParameterOffsetParserImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.PopupManagerImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.driverContext.impl.XmlBankDriverParser;
import org.jsynthlib.utils.ctrlr.driverContext.impl.XmlSingleDriverParser;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType.Enum;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class DriverModule extends AbstractModule {

    public static class Factory {
        @Inject
        private PanelLuaManagerBuilder luaManager;

        public DriverModule newDriverModule(XmlDeviceDefinition deviceDef,
                XmlDriverReference driverRef, PanelType panel) throws XmlException,
                IOException {

            Builder builder = new Builder();
            builder.deviceDef = deviceDef;
            builder.driverRef = driverRef;
            builder.panel = panel;
            builder.driverClassName = driverRef.getDriverClass();

            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadStripWhitespace();
            Enum driverType = driverRef.getDriverType();
            switch (driverType.intValue()) {
            case XmlDriverReference.DriverType.INT_PATCH:
                InputStream stream =
                Builder.class.getClassLoader()
                .getResourceAsStream(
                        getXmlfilePath(driverRef.getDriverClass()
                                .trim()));
                XmlSingleDriverDefinitionDocument singleDocument =
                        XmlSingleDriverDefinitionDocument.Factory.parse(stream,
                                xmlOptions);
                builder.driverDef = singleDocument.getXmlSingleDriverDefinition();
                builder.driverPrefix =
                        singleDocument.getXmlSingleDriverDefinition()
                                .getPatchType();
                break;
            case XmlDriverReference.DriverType.INT_BANK:
                stream =
                Builder.class.getClassLoader()
                .getResourceAsStream(
                        getXmlfilePath(driverRef.getDriverClass()
                                .trim()));
                XmlBankDriverDefinitionDocument bankDocument =
                        XmlBankDriverDefinitionDocument.Factory.parse(stream,
                                xmlOptions);
                builder.driverDef = bankDocument.getXmlBankDriverDefinition();
                builder.driverPrefix =
                        bankDocument.getXmlBankDriverDefinition()
                                .getPatchType();
                break;
            default:
                throw new IllegalArgumentException("Unsupported driver type");
            }
            builder.luaBean = luaManager.getDriverLuaBean(builder.driverPrefix);
            builder.luaBean.setDriverPrefix(builder.driverPrefix);
            return new DriverModule(builder);
        }

        String getXmlfilePath(String name) {
            return name.replace('.', '/') + ".xml";
        }
    }

    static class Builder {

        private DriverLuaBean luaBean;
        private String driverClassName;
        private String driverPrefix;
        private XmlDriverDefinition driverDef;
        private XmlDeviceDefinition deviceDef;
        private XmlDriverReference driverRef;
        private PanelType panel;

        public String getDriverClassName() {
            return driverClassName;
        }

        public String getDriverPrefix() {
            return driverPrefix;
        }

        public XmlDriverDefinition getDriverDef() {
            return driverDef;
        }

        public XmlDeviceDefinition getDeviceDef() {
            return deviceDef;
        }

        public XmlDriverReference getDriverRef() {
            return driverRef;
        }

        public PanelType getPanel() {
            return panel;
        }

        public DriverLuaBean getLuaBean() {
            return luaBean;
        }

    }

    private final XmlDeviceDefinition deviceDef;
    private final String driverClassName;
    private final XmlDriverDefinition driverDef;
    private final String driverPrefix;
    private final XmlDriverReference driverRef;
    private final PanelType panel;
    private final DriverLuaBean luaBean;

    public DriverModule(Builder builder) {
        deviceDef = builder.getDeviceDef();
        driverClassName = builder.getDriverClassName();
        driverDef = builder.getDriverDef();
        driverPrefix = builder.getDriverPrefix();
        driverRef = builder.getDriverRef();
        panel = builder.getPanel();
        this.luaBean = builder.getLuaBean();
    }

    @Override
    protected void configure() {
        bind(SysexFormulaParser.class).to(SysexFormulaParserImpl.class);
        bind(ParameterOffsetParser.class).to(ParameterOffsetParserImpl.class);
        bind(BuilderFactoryFacade.class).to(BuilderFactoryFacadeImpl.class);
        bind(ConverterDeviceFactory.class).to(ConverterDeviceFactoryImpl.class);
        bind(PopupManager.class).to(PopupManagerImpl.class);
        bind(DriverLuaBean.class).toInstance(luaBean);

        switch (driverRef.getDriverType().intValue()) {
        case XmlDriverReference.DriverType.INT_PATCH:
            bind(XmlDriverParser.class).to(XmlSingleDriverParser.class);
            break;
        case XmlDriverReference.DriverType.INT_BANK:
            bind(XmlDriverParser.class).to(XmlBankDriverParser.class);
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
