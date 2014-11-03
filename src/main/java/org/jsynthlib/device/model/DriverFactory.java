package org.jsynthlib.device.model;

import org.jsynthlib.xmldevice.XmlDriverReferences.XmlDriverReference;

public interface DriverFactory {

    IBankDriver newBankDriver(XmlDriverReference driverReference);

    IPatchDriver newSingleDriver(XmlDriverReference driverReference);

}
