package org.jsynthlib.utils.ctrlr.impl;

import java.util.Map;

import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.MidiSenderFactory;
import org.jsynthlib.device.model.ParamModelFactory;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.model.impl.DeviceModule;
import org.jsynthlib.device.model.impl.HandlerBindingMap;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.utils.ctrlr.driverContext.HandlerReferenceFactory;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class HandlerReferenceFactoryImpl implements HandlerReferenceFactory {

    private final XmlDeviceDefinition deviceDefinition;
    private final Device device;
    private final ParamModelFactory modelFactory;
    private final MidiSenderFactory senderFactory;

    @Inject
    public HandlerReferenceFactoryImpl(DeviceManager deviceManager,
            DriverContext context) throws DeviceException {
        this.deviceDefinition = context.getDeviceDefinition();
        HandlerBindingMap bindingMap = new HandlerBindingMap();
        String devicePath = bindingMap.addDevice(deviceDefinition);
        Map<String, HandlerDefinitionBase> deviceBindingsMap =
                bindingMap.getDeviceBindings(devicePath);
        DeviceModule deviceModule = new DeviceModule(deviceBindingsMap);
        Injector driverInjector =
                JSynthLibInjector.getInjector().createChildInjector(
                        deviceModule);
        modelFactory = driverInjector.getInstance(ParamModelFactory.class);
        senderFactory = driverInjector.getInstance(MidiSenderFactory.class);

        StringBuilder devNameBuilder = new StringBuilder();
        devNameBuilder.append(deviceDefinition.getManufacturer()).append(" ")
        .append(deviceDefinition.getModelName()).append(" Driver");
        DeviceDescriptor deviceDescriptor =
                deviceManager.getDescriptorForDeviceName(devNameBuilder
                        .toString());
        for (int i = 0; i < deviceManager.deviceCount(); i++) {
            deviceManager.removeDevice(i);
        }
        device = deviceManager.addDevice(deviceDescriptor);
    }

    @Override
    public ISender newSender(MidiSenderReference ref, Patch patch) {
        return senderFactory.newSender(ref, patch);
    }

    @Override
    public IParamModel newParamModel(ParamModelReference ref, Patch patch) {
        return modelFactory.newParamModel(ref, patch);
    }

    @Override
    public Device getDevice() {
        return device;
    }

}
