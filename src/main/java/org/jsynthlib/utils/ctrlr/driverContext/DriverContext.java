package org.jsynthlib.utils.ctrlr.driverContext;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Injector;

public interface DriverContext {

    XmlDriverDefinition getDriverDefinition();

    XmlDeviceDefinition getDeviceDefinition();

    PanelType getPanel();

    Injector getInjector();

    String getDriverClassName();

    String getDriverPrefix();

    XmlDriverParser getDriverParser();

    <T> T getInstance(Class<T> klass);
}
