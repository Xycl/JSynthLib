package org.jsynthlib.utils.ctrlr.impl;

import java.util.Map;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceException;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.MidiSenderFactory;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.model.impl.DeviceModule;
import org.jsynthlib.device.model.impl.HandlerBindingMap;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.HandlerDefinitionBase;
import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.XmlDeviceDefinitionDocument.XmlDeviceDefinition;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class SysexFormulaParserImpl implements SysexFormulaParser {

    private static final byte CS_HOLDER = (byte) 0xFF;
    private static final byte VALUE_HOLDER = (byte) 0xFE;

    private Injector driverInjector;
    private Device device;
    private XmlDeviceDefinition deviceDefinition;
    private final DeviceManager deviceManager;

    @Inject
    public SysexFormulaParserImpl(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    @Override
    public String parseSysexFormula(MidiSenderReference ref, int min, int max) {
        MidiSenderFactory senderFactory =
                driverInjector.getInstance(MidiSenderFactory.class);

        DriverMock driverMock = new DriverMock();
        driverMock.setDevice(device);
        Patch patch = new Patch();
        patch.setDriver(driverMock);

        ISender sender = senderFactory.newSender(ref, patch);

        sender.send(driverMock, min);
        byte[] minBytes = driverMock.getSentBytes();

        sender.send(driverMock, max);
        byte[] maxBytes = driverMock.getSentBytes();

        byte[] buf = new byte[minBytes.length];

        int csOffset = driverMock.getCsOffset();
        for (int i = 0; i < buf.length; i++) {
            if (i == csOffset) {
                buf[i] = CS_HOLDER;
            } else if (minBytes[i] == maxBytes[i]) {
                buf[i] = minBytes[i];
            } else {
                // Value byte
                buf[i] = VALUE_HOLDER;
            }
        }

        String sysexString = SysexUtils.sysexToString(buf);
        sysexString = sysexString.replaceAll(".{2}(?=.)", "$0 ");

        if (csOffset >= 0) {
            int csBytes = driverMock.getCsEnd() - driverMock.getCsStart() + 1;
            String cs = "z" + csBytes;
            sysexString = sysexString.replace("FF", cs);
        }

        sysexString = sysexString.replace("FE", "xx");
        return sysexString;
    }

    static class DriverMock extends AbstractPatchDriver {

        private byte[] message;
        private int csOffset = -1;
        private int csStart;
        private int csEnd;

        public DriverMock() {
            super("");
        }

        @Override
        public void send(MidiMessage msg) {
            message = msg.getMessage();
        }

        @Override
        public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
            this.csOffset = ofs;
            this.csStart = start;
            this.csEnd = end;
        }

        byte[] getSentBytes() {
            return message;
        }

        public int getCsOffset() {
            return csOffset;
        }

        public int getCsStart() {
            return csStart;
        }

        public int getCsEnd() {
            return csEnd;
        }
    }

    @Override
    public XmlDeviceDefinition getDeviceDefinition() {
        return deviceDefinition;
    }

    @Override
    public void setDeviceDefinition(XmlDeviceDefinition deviceDefinition)
            throws DeviceException {
        this.deviceDefinition = deviceDefinition;
        HandlerBindingMap bindingMap = new HandlerBindingMap();
        String devicePath = bindingMap.addDevice(deviceDefinition);
        Map<String, HandlerDefinitionBase> deviceBindingsMap =
                bindingMap.getDeviceBindings(devicePath);
        DeviceModule deviceModule = new DeviceModule(deviceBindingsMap);
        driverInjector =
                JSynthLibInjector.getInjector().createChildInjector(
                        deviceModule);
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
}
