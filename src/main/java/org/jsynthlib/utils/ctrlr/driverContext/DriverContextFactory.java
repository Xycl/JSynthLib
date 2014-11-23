package org.jsynthlib.utils.ctrlr.driverContext;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

public interface DriverContextFactory {

    DriverContext newDriverContext(XmlDeviceDefinition xmldevice,
            XmlDriverReference xmldriver, PanelType panel);
}
