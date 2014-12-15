package org.jsynthlib.utils.ctrlr.config;

import java.io.IOException;

import org.apache.xmlbeans.XmlException;
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
            XmlDriverReference driverRef,
            Injector panelInjector) throws XmlException, IOException {
        DriverModule driverModule =
                moduleFactory.newDriverModule(deviceDef, driverRef);
        return panelInjector.createChildInjector(driverModule);
    }

}
