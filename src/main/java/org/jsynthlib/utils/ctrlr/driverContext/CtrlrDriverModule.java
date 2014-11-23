package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.utils.ctrlr.builder.CtrlrComponentBuilderFactory;
import org.jsynthlib.utils.ctrlr.impl.CtrlrComponentBuilderFactoryImpl;
import org.jsynthlib.utils.ctrlr.impl.HandlerReferenceFactoryImpl;
import org.jsynthlib.utils.ctrlr.impl.ParameterOffsetParserImpl;
import org.jsynthlib.utils.ctrlr.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.impl.XmlSingleDriverParserImpl;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CtrlrDriverModule extends AbstractModule {

    private final DriverContext context;

    public CtrlrDriverModule(DriverContext context) {
        this.context = context;
    }

    @Override
    protected void configure() {
        bind(DriverContext.class).toInstance(context);
        bind(SysexFormulaParser.class).to(SysexFormulaParserImpl.class);
        bind(ParameterOffsetParser.class).to(ParameterOffsetParserImpl.class);
        bind(CtrlrComponentBuilderFactory.class).to(
                CtrlrComponentBuilderFactoryImpl.class);
        bind(HandlerReferenceFactory.class).to(
                HandlerReferenceFactoryImpl.class);
        bind(GlobalPatchMethodParser.class).to(GlobalPatchMethodParserImpl.class);
        install(new FactoryModuleBuilder().implement(XmlDriverParser.class,
                XmlSingleDriverParserImpl.class).build(
                        XmlSingleDriverParserFactory.class));
        // install(new FactoryModuleBuilder().implement(XmlDriverParser.class,
        // XmlSingleDriverParserImpl.class).build(
        // XmlBankDriverParserFactory.class));
    }

}
