package org.jsynthlib.utils.ctrlr;

import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.device.model.XMLDevice;
import org.jsynthlib.device.model.XMLSingleDriver;
import org.jsynthlib.synthdrivers.EmuProteus2.EmuProteus2Device;
import org.jsynthlib.synthdrivers.RolandD50.D50Device;
import org.jsynthlib.synthdrivers.RolandD50.D50SingleDriver;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public final class XmlUtils {

    private XmlUtils() {
    }

    public static XmlSingleDriverDefinition getD50SingleDriverDef()
            throws XmlException, IOException {
        return getSingleDriverDef(D50SingleDriver.class);
    }

    public static XmlDeviceDefinition getRolandD50DeviceDef() throws XmlException,
    IOException {
        return getDeviceDef(D50Device.class);
    }

    public static XmlBankDriverDefinition getBankDriverDef(
            Class<? extends XMLBankDriver> klass) throws XmlException,
            IOException {
        String resName = klass.getName().replace('.', '/') + ".xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlBankDriverDefinitionDocument doc =
                XmlBankDriverDefinitionDocument.Factory.parse(stream);
        return doc.getXmlBankDriverDefinition();
    }

    public static XmlSingleDriverDefinition getSingleDriverDef(
            Class<? extends XMLSingleDriver> klass) throws XmlException,
            IOException {
        String resName = klass.getName().replace('.', '/') + ".xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlSingleDriverDefinitionDocument doc =
                XmlSingleDriverDefinitionDocument.Factory.parse(stream);
        return doc.getXmlSingleDriverDefinition();
    }

    public static XmlDeviceDefinition getDeviceDef(
            Class<? extends XMLDevice> klass) throws XmlException, IOException {
        String resName = klass.getName().replace('.', '/') + ".xml";
        InputStream stream =
                XmlUtils.class.getClassLoader().getResourceAsStream(resName);
        XmlDeviceDefinitionDocument doc =
                XmlDeviceDefinitionDocument.Factory.parse(stream);
        return doc.getXmlDeviceDefinition();
    }

    public static XmlDeviceDefinition getEmuProteus2DeviceDef()
            throws XmlException, IOException {
        return getDeviceDef(EmuProteus2Device.class);
    }
}
