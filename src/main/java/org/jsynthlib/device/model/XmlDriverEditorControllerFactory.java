package org.jsynthlib.device.model;

import org.jsynthlib.device.viewcontroller.XmlDriverEditorController;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.XmlSingleDriverDefinitionDocument.XmlSingleDriverDefinition;

public interface XmlDriverEditorControllerFactory {

    XmlDriverEditorController create(IDriver d,
            XmlSingleDriverDefinition xmlDriverDef, Patch p);
}
