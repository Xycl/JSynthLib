package org.jsynthlib.utils.ctrlr.service;

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DriverFactory;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;

public interface ConverterDeviceFactory {

    ISender newSender(MidiSenderReference ref, Patch patch);

    IParamModel newParamModel(ParamModelReference ref, Patch patch);

    Device getDevice();

    DriverFactory getDriverFactory();
}
