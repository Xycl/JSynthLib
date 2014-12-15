package org.jsynthlib.utils.ctrlr.service.impl;

import javax.sound.midi.MidiMessage;

import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.utils.SysexUtils;
import org.jsynthlib.utils.ctrlr.service.ConverterDeviceFactory;
import org.jsynthlib.utils.ctrlr.service.SysexFormulaParser;
import org.jsynthlib.xmldevice.MidiSenderReference;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SysexFormulaParserImpl implements SysexFormulaParser {

    private static final byte CS_HOLDER = (byte) 0xFF;
    private static final byte VALUE_HOLDER = (byte) 0xFE;

    @Inject
    private ConverterDeviceFactory factory;

    @Override
    public String parseSysexFormula(MidiSenderReference ref, int min, int max) {
        DriverMock driverMock = new DriverMock();
        driverMock.setDevice(factory.getDevice());
        Patch patch = new Patch();
        patch.setDriver(driverMock);

        ISender sender = factory.newSender(ref, patch);

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
}
