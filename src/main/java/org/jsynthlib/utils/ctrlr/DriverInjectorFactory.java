package org.jsynthlib.utils.ctrlr;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.driverContext.DriverModule;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class DriverInjectorFactory {

    @Inject
    private DriverModule.Factory moduleFactory;

    public Injector newDriverinjector(XmlDeviceDefinition deviceDef,
            XmlDriverReference driverRef, PanelType panel,
            Injector panelInjector) throws XmlException, IOException {
        DriverModule driverModule =
                moduleFactory.newDriverModule(deviceDef, driverRef, panel);
        return panelInjector.createChildInjector(driverModule);
    }

}
