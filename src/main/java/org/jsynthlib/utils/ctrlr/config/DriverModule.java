package org.jsynthlib.utils.ctrlr.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.LuaFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssembleValuesFromBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignBankController;
import org.jsynthlib.utils.ctrlr.controller.lua.AssignValuesController;
import org.jsynthlib.utils.ctrlr.controller.lua.GetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.JavaParsedMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.LoadPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceiveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.ReceivePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveBankMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SaveMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.SavePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SelectPatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.SetNameMethodController;
import org.jsynthlib.utils.ctrlr.controller.lua.WriteMenuController;
import org.jsynthlib.utils.ctrlr.controller.lua.WritePatchMethodController;
import org.jsynthlib.utils.ctrlr.controller.modulator.NameCharSliderController;
import org.jsynthlib.utils.ctrlr.controller.modulator.PatchNameController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiCombinedGroupController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiComboController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiEnvelopeController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGlobalButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGroupController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiImageButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiIncDecButtonsController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiKnobController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiLabelController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiSliderController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiTabController;
import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.domain.DriverTypeModel;
import org.jsynthlib.utils.ctrlr.service.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.ParameterOffsetParser;
import org.jsynthlib.utils.ctrlr.service.PopupManager;
import org.jsynthlib.utils.ctrlr.service.SysexFormulaParser;
import org.jsynthlib.utils.ctrlr.service.XmlDriverParser;
import org.jsynthlib.utils.ctrlr.service.codeparser.BankDriverParserModel;
import org.jsynthlib.utils.ctrlr.service.codeparser.CalculateChecksumMethodVisitor;
import org.jsynthlib.utils.ctrlr.service.codeparser.DefaultMethodVisitor;
import org.jsynthlib.utils.ctrlr.service.codeparser.GetPatchMethodVisitor;
import org.jsynthlib.utils.ctrlr.service.codeparser.PutPatchMethodVisitor;
import org.jsynthlib.utils.ctrlr.service.codeparser.VisitorFactoryFacade;
import org.jsynthlib.utils.ctrlr.service.codeparser.VisitorFactoryFacadeImpl;
import org.jsynthlib.utils.ctrlr.service.impl.ConverterDeviceFactoryImpl;
import org.jsynthlib.utils.ctrlr.service.impl.EditorLuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.impl.ParameterOffsetParserImpl;
import org.jsynthlib.utils.ctrlr.service.impl.PopupManagerImpl;
import org.jsynthlib.utils.ctrlr.service.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.service.impl.XmlBankDriverParser;
import org.jsynthlib.utils.ctrlr.service.impl.XmlSingleDriverParser;
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
import com.google.inject.name.Names;

public class DriverModule extends AbstractModule {

    public static class Factory {

        @Inject
        private CtrlrPanelModel panelModel;

        @Inject
        @Named("root")
        private LuaMethodProvider rootLuaMethodProvider;

        @Inject
        private EditorLuaMethodProvider.Factory editorMethodProviderFactory;

        private final Map<String, EditorLuaMethodProvider> editorMethodProviders =
                new HashMap<String, EditorLuaMethodProvider>();

        private final Map<String, DriverTypeModel> driverTypeModels =
                new HashMap<String, DriverTypeModel>();

        public DriverModule newDriverModule(XmlDeviceDefinition deviceDef,
                XmlDriverReference driverRef) throws XmlException, IOException {

            DriverModuleBuilder builder = new DriverModuleBuilder();
            builder.deviceDef = deviceDef;
            builder.driverRef = driverRef;
            builder.driverClassName = driverRef.getDriverClass();
            builder.panelModel = panelModel;

            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadStripWhitespace();
            Enum driverType = driverRef.getDriverType();
            switch (driverType.intValue()) {
            case XmlDriverReference.DriverType.INT_PATCH:
                InputStream stream =
                DriverModuleBuilder.class.getClassLoader()
                .getResourceAsStream(
                        getXmlfilePath(driverRef
                                .getDriverClass().trim()));
                XmlSingleDriverDefinitionDocument singleDocument =
                        XmlSingleDriverDefinitionDocument.Factory.parse(stream,
                                xmlOptions);
                builder.driverDef =
                        singleDocument.getXmlSingleDriverDefinition();
                builder.driverPrefix =
                        singleDocument.getXmlSingleDriverDefinition()
                        .getPatchType();
                break;
            case XmlDriverReference.DriverType.INT_BANK:
                stream =
                DriverModuleBuilder.class.getClassLoader()
                .getResourceAsStream(
                        getXmlfilePath(driverRef
                                .getDriverClass().trim()));
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
            LuaMethodGroupType methodGroup =
                    rootLuaMethodProvider
                    .getLuaMethodGroup(builder.driverPrefix);

            if (driverTypeModels.containsKey(builder.driverPrefix)) {
                builder.driverTypeModel =
                        driverTypeModels.get(builder.driverPrefix);
            } else {
                DriverTypeModel driverTypeModel = new DriverTypeModel();
                driverTypeModels.put(builder.driverPrefix, driverTypeModel);
                builder.driverTypeModel = driverTypeModel;
            }

            if (editorMethodProviders.containsKey(methodGroup.getName())) {
                builder.luaMethodProvider =
                        editorMethodProviders.get(methodGroup.getName());
            } else {
                builder.luaMethodProvider =
                        editorMethodProviderFactory
                        .newLuaMethodProvider(methodGroup);
                editorMethodProviders.put(methodGroup.getName(),
                        builder.luaMethodProvider);
            }
            return new DriverModule(builder);
        }

        String getXmlfilePath(String name) {
            return name.replace('.', '/') + ".xml";
        }
    }

    public static class DriverModuleBuilder {

        public DriverTypeModel driverTypeModel;
        public EditorLuaMethodProvider luaMethodProvider;
        public CtrlrPanelModel panelModel;
        public String driverClassName;
        public String driverPrefix;
        public XmlDriverDefinition driverDef;
        public XmlDeviceDefinition deviceDef;
        public XmlDriverReference driverRef;

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
    }

    private final XmlDeviceDefinition deviceDef;
    private final String driverClassName;
    private final XmlDriverDefinition driverDef;
    private final String driverPrefix;
    private final XmlDriverReference driverRef;
    private final PanelType panel;
    private final EditorLuaMethodProvider luaMethodProvider;
    private final DriverTypeModel driverTypeModel;

    public DriverModule(DriverModuleBuilder builder) {
        deviceDef = builder.getDeviceDef();
        driverClassName = builder.getDriverClassName();
        driverDef = builder.getDriverDef();
        driverPrefix = builder.getDriverPrefix();
        driverRef = builder.getDriverRef();
        luaMethodProvider = builder.luaMethodProvider;
        this.driverTypeModel = builder.driverTypeModel;

        if (builder.panelModel.getPanel() == null) {
            throw new IllegalStateException(
                    "You are trying to create modulators when there is no panel");
        } else {
            panel = builder.panelModel.getPanel();
        }
    }

    @Override
    protected void configure() {
        bind(SysexFormulaParser.class).to(SysexFormulaParserImpl.class);
        bind(ParameterOffsetParser.class).to(ParameterOffsetParserImpl.class);
        bind(ModulatorFactoryFacade.class).to(ModulatorFactoryFacadeImpl.class);
        bind(LuaFactoryFacade.class).to(LuaFactoryFacadeImpl.class);
        bind(VisitorFactoryFacade.class).to(VisitorFactoryFacadeImpl.class);
        bind(ConverterDeviceFactory.class).to(ConverterDeviceFactoryImpl.class);
        bind(PopupManager.class).to(PopupManagerImpl.class);
        bind(LuaMethodProvider.class).annotatedWith(Names.named("editor"))
        .toInstance(luaMethodProvider);
        bind(BankDriverParserModel.class).toInstance(
                new BankDriverParserModel());

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

        // Controller factories
        installModulatorFactories();

        installLuaFactories();

        installVisitorFactories();
    }

    void installVisitorFactories() {
        install(new FactoryModuleBuilder().implement(
                DefaultMethodVisitor.class, DefaultMethodVisitor.class).build(
                        DefaultMethodVisitor.Factory.class));
        install(new FactoryModuleBuilder().implement(
                GetPatchMethodVisitor.class, GetPatchMethodVisitor.class)
                .build(GetPatchMethodVisitor.Factory.class));
        install(new FactoryModuleBuilder().implement(
                PutPatchMethodVisitor.class, PutPatchMethodVisitor.class)
                .build(PutPatchMethodVisitor.Factory.class));
        install(new FactoryModuleBuilder().implement(
                CalculateChecksumMethodVisitor.class,
                CalculateChecksumMethodVisitor.class).build(
                        CalculateChecksumMethodVisitor.Factory.class));
    }

    void installLuaFactories() {
        install(new FactoryModuleBuilder().implement(
                AssembleValuesController.class, AssembleValuesController.class)
                .build(AssembleValuesController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                SelectPatchMethodController.class,
                SelectPatchMethodController.class).build(
                        SelectPatchMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                AssignValuesController.class, AssignValuesController.class)
                .build(AssignValuesController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                GetNameMethodController.class, GetNameMethodController.class)
                .build(GetNameMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(LoadMenuController.class,
                LoadMenuController.class).build(
                        LoadMenuController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                LoadPatchMethodController.class,
                LoadPatchMethodController.class).build(
                        LoadPatchMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                ReceiveMenuController.class, ReceiveMenuController.class)
                .build(ReceiveMenuController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                ReceivePatchMethodController.class,
                ReceivePatchMethodController.class).build(
                        ReceivePatchMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(SaveMenuController.class,
                SaveMenuController.class).build(
                        SaveMenuController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                SavePatchMethodController.class,
                SavePatchMethodController.class).build(
                        SavePatchMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                SetNameMethodController.class, SetNameMethodController.class)
                .build(SetNameMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(WriteMenuController.class,
                WriteMenuController.class).build(
                        WriteMenuController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                WritePatchMethodController.class,
                WritePatchMethodController.class).build(
                        WritePatchMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                AssignBankController.class, AssignBankController.class).build(
                        AssignBankController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                LoadBankMethodController.class, LoadBankMethodController.class)
                .build(LoadBankMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                AssembleValuesFromBankController.class,
                AssembleValuesFromBankController.class).build(
                        AssembleValuesFromBankController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                SaveBankMethodController.class, SaveBankMethodController.class)
                .build(SaveBankMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                ReceiveBankMethodController.class,
                ReceiveBankMethodController.class).build(
                        ReceiveBankMethodController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                JavaParsedMethodController.class,
                JavaParsedMethodController.class).build(
                        JavaParsedMethodController.Factory.class));
    }

    void installModulatorFactories() {
        install(new FactoryModuleBuilder().implement(UiComboController.class,
                UiComboController.class).build(UiComboController.Factory.class));
        install(new FactoryModuleBuilder().implement(PatchNameController.class,
                PatchNameController.class).build(
                        PatchNameController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiImageButtonController.class, UiImageButtonController.class)
                .build(UiImageButtonController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiButtonController.class,
                UiButtonController.class).build(
                        UiButtonController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiKnobController.class,
                UiKnobController.class).build(UiKnobController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiGroupController.class,
                UiGroupController.class).build(UiGroupController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiTabController.class,
                UiTabController.class).build(UiTabController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiEnvelopeController.class, UiEnvelopeController.class).build(
                        UiEnvelopeController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiCombinedGroupController.class,
                UiCombinedGroupController.class).build(
                        UiCombinedGroupController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiIncDecButtonsController.class,
                UiIncDecButtonsController.class).build(
                        UiIncDecButtonsController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiLabelController.class,
                UiLabelController.class).build(UiLabelController.Factory.class));
        install(new FactoryModuleBuilder().implement(UiSliderController.class,
                UiSliderController.class).build(
                        UiSliderController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                NameCharSliderController.class, NameCharSliderController.class)
                .build(NameCharSliderController.Factory.class));
        install(new FactoryModuleBuilder().implement(
                UiGlobalButtonController.class, UiGlobalButtonController.class)
                .build(UiGlobalButtonController.Factory.class));
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

    @Provides
    DriverTypeModel provideDriverTypeModel() {
        return driverTypeModel;
    }
}
