package org.jsynthlib.utils.ctrlr;

import org.jsynthlib.utils.ctrlr.impl.CtrlrComponentBuilderFactoryImpl;
import org.jsynthlib.utils.ctrlr.impl.CtrlrImageConverterImpl;
import org.jsynthlib.utils.ctrlr.impl.PanelResourceManagerImpl;
import org.jsynthlib.utils.ctrlr.impl.SysexFormulaParserImpl;
import org.jsynthlib.utils.ctrlr.impl.XmlDriverEditorParser;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class CtrlrGeneratorModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CtrlrComponentBuilderFactory.class).to(
                CtrlrComponentBuilderFactoryImpl.class);
        install(new FactoryModuleBuilder().implement(
                XmlDriverEditorParser.class, XmlDriverEditorParser.class)
                .build(XmlDriverEditorParserFactory.class));
        bind(SysexFormulaParser.class).to(SysexFormulaParserImpl.class);
        bind(CtrlrImageConverter.class).to(CtrlrImageConverterImpl.class);
        bind(PanelResourceManager.class).to(PanelResourceManagerImpl.class);
    }

}
