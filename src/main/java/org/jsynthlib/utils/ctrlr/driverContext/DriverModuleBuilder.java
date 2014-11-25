package org.jsynthlib.utils.ctrlr.driverContext;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference.DriverType.Enum;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;

public final class DriverModuleBuilder {

    private String driverClassName;
    private String driverPrefix;
    private XmlDriverDefinition driverDef;
    private XmlDeviceDefinition deviceDef;
    private XmlDriverReference driverRef;
    private PanelType panel;

    private DriverModuleBuilder() {
    }

    public static CtrlrDriverModule newDriverModule(
            XmlDeviceDefinition deviceDef, XmlDriverReference driverRef,
            PanelType panel) throws XmlException, IOException {

        DriverModuleBuilder builder = new DriverModuleBuilder();

        builder.deviceDef = deviceDef;
        builder.driverRef = driverRef;
        builder.panel = panel;
        builder.driverClassName = driverRef.getDriverClass();
        builder.driverPrefix =
                builder.driverClassName.substring(builder.driverClassName
                        .lastIndexOf(".") + 1);
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setLoadStripWhitespace();
        Enum driverType = driverRef.getDriverType();
        switch (driverType.intValue()) {
        case XmlDriverReference.DriverType.INT_PATCH:
            InputStream stream =
            DriverModuleBuilder.class.getClassLoader()
            .getResourceAsStream(
                    getXmlfilePath(driverRef.getDriverClass()
                            .trim()));
            XmlSingleDriverDefinitionDocument driverDocument =
                    XmlSingleDriverDefinitionDocument.Factory.parse(stream,
                            xmlOptions);
            builder.driverDef = driverDocument.getXmlSingleDriverDefinition();
            break;
        default:
            throw new IllegalArgumentException("Unsupported driver type");
        }
        return new CtrlrDriverModule(builder);
    }

    static String getXmlfilePath(String name) {
        return name.replace('.', '/') + ".xml";
    }

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

}
