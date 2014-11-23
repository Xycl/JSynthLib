package org.jsynthlib.utils.ctrlr.driverContext;

import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public interface XmlSingleDriverParserFactory {

    XmlDriverParser newSingleDriverParser(XmlSingleDriverDefinition driverDef);
}
