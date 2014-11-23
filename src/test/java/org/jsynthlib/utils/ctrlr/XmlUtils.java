package org.jsynthlib.utils.ctrlr;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public final class XmlUtils {

    private XmlUtils() {
    }

    public static XmlSingleDriverDefinition getD50SingleDriverDef()
            throws XmlException, IOException {
        String resName =
                "org/jsynthlib/synthdrivers/RolandD50/D50SingleDriver.xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlSingleDriverDefinitionDocument doc =
                XmlSingleDriverDefinitionDocument.Factory.parse(stream);
        return doc.getXmlSingleDriverDefinition();
    }

    public static XmlDeviceDefinition getRolandD50DeviceDef() throws XmlException,
            IOException {
        String resName = "org/jsynthlib/synthdrivers/RolandD50/D50Device.xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlDeviceDefinitionDocument doc =
                XmlDeviceDefinitionDocument.Factory.parse(stream);
        return doc.getXmlDeviceDefinition();
    }

    public static XmlDeviceDefinition getEmuProteus2DeviceDef()
            throws XmlException, IOException {
        String resName =
                "org/jsynthlib/synthdrivers/EmuProteus2/EmuProteus2Device.xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlDeviceDefinitionDocument doc =
                XmlDeviceDefinitionDocument.Factory.parse(stream);
        return doc.getXmlDeviceDefinition();
    }
}
