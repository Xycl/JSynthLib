package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

public interface XmlBankDriverParserFactory {

    XmlDriverParser newBankDriverParser(XmlBankDriverDefinition driverDef);
}
