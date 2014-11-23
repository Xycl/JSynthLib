package org.jsynthlib.utils.ctrlr.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.CtrlrGeneratorModule;
import org.jsynthlib.utils.ctrlr.driverContext.CtrlrDriverModule;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.utils.ctrlr.driverContext.XmlDriverParser;
import org.jsynthlib.utils.ctrlr.driverContext.XmlSingleDriverParserFactory;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType.Enum;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;

public class DriverContextImpl implements DriverContext {

    private final XmlDeviceDefinition deviceDef;
    private final XmlSingleDriverDefinition driverDef;
    private final PanelType panel;
    private final Injector injector;
    private final String driverClassName;
    private XmlDriverParser driverParser;
    private final String namePrefix;

    @Inject
    public DriverContextImpl(@Assisted XmlDeviceDefinition deviceDef,
            @Assisted XmlDriverReference driverRef, @Assisted PanelType panel)
            throws XmlException, IOException {
        this.injector =
                CtrlrGeneratorModule.getInjector().createChildInjector(
                        new CtrlrDriverModule(this));
        this.deviceDef = deviceDef;
        this.panel = panel;

        this.driverClassName = driverRef.getDriverClass();
        namePrefix =
                driverClassName.substring(driverClassName.lastIndexOf(".") + 1);
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadStripWhitespace();
        Enum driverType = driverRef.getDriverType();
        switch (driverType.intValue()) {
        case XmlDriverReference.DriverType.INT_PATCH:
            InputStream stream =
            getClass().getClassLoader().getResourceAsStream(
                    getXmlfilePath(driverRef.getDriverClass().trim()));
            XmlSingleDriverDefinitionDocument driverDocument =
                    XmlSingleDriverDefinitionDocument.Factory.parse(stream,
                            xmlOptions);
            this.driverDef = driverDocument.getXmlSingleDriverDefinition();
            driverParser =
                    injector.getInstance(XmlSingleDriverParserFactory.class)
                            .newSingleDriverParser(driverDef);
            break;
        default:
            throw new IllegalArgumentException("Unsupported driver type");
        }
    }

    final String getXmlfilePath(String name) {
        return name.replace('.', '/') + ".xml";
    }

    @Override
    public XmlDriverDefinition getDriverDefinition() {
        return driverDef;
    }

    @Override
    public XmlDeviceDefinition getDeviceDefinition() {
        return deviceDef;
    }

    @Override
    public PanelType getPanel() {
        return panel;
    }

    @Override
    public Injector getInjector() {
        return injector;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public XmlDriverParser getDriverParser() {
        return driverParser;
    }

    @Override
    public String getDriverPrefix() {
        return namePrefix;
    }

    @Override
    public <T> T getInstance(Class<T> klass) {
        return injector.getInstance(klass);
    }

}
