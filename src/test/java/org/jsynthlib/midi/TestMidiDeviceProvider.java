package org.jsynthlib.midi;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.sound.midi.spi.MidiDeviceProvider;

public class TestMidiDeviceProvider extends MidiDeviceProvider {

    private static final Info[] INFOS = {
            new TestDeviceInfo(true), new TestDeviceInfo(false) };

    private static TestMidiDeviceProvider instance;

    public static TestMidiDeviceProvider getInstance() {
        return instance;
    }

    private final char[] hexArray;

    private StringBuilder messageBuilder;

    private final Lock lock;

    public TestMidiDeviceProvider() {
        instance = this;
        messageBuilder = new StringBuilder();
        lock = new ReentrantLock();
        hexArray = "0123456789ABCDEF".toCharArray();
    }

    @Override
    public MidiDevice getDevice(Info info) {
        if (info instanceof TestDeviceInfo) {
            TestDeviceInfo deviceInfo = (TestDeviceInfo) info;
            return new TestMidiDevice(deviceInfo);
        }
        return null;
    }

    @Override
    public Info[] getDeviceInfo() {
        return INFOS;
    }

    public String getAndClearReceivedMessages() {
        try {
            lock.lock();
            String string = messageBuilder.toString();
            messageBuilder = new StringBuilder();
            return string;
        } finally {
            lock.unlock();
        }
    }

    static class TestDeviceInfo extends MidiDevice.Info {

        private boolean input;

        protected TestDeviceInfo(boolean input) {
            super(input ? "Midi Input" : "Midi Output", "JSynthLib",
                    "Test driver", "");
            this.input = input;
        }

        public boolean isInput() {
            return input;
        }
    }

    class TestMidiDevice implements MidiDevice {

        private TestDeviceInfo info;

        public TestMidiDevice(TestDeviceInfo info) {
            super();
            this.info = info;
        }

        @Override
        public void close() {
        }

        @Override
        public Info getDeviceInfo() {
            return info;
        }

        @Override
        public int getMaxReceivers() {
            if (info.isInput()) {
                return 0;
            } else {
                return -1;
            }
        }

        @Override
        public int getMaxTransmitters() {
            if (info.isInput()) {
                return -1;
            } else {
                return 0;
            }
        }

        @Override
        public long getMicrosecondPosition() {
            return 0;
        }

        String bytesToHex(byte[] bytes) {
            char[] hexChars = new char[bytes.length * 2];
            int v;
            for (int j = 0; j < bytes.length; j++) {
                v = bytes[j] & 0xFF;
                hexChars[j * 2] = hexArray[v >>> 4];
                hexChars[j * 2 + 1] = hexArray[v & 0x0F];
            }
            return new String(hexChars);
        }

        @Override
        public Receiver getReceiver() throws MidiUnavailableException {
            if (info.isInput()) {
                throw new MidiUnavailableException(
                        "TestMidiDevice currently has no Receivers");
            } else {
                return new Receiver() {

                    @Override
                    public void send(MidiMessage arg0, long arg1) {
                        try {
                            lock.lock();
                            byte[] msg = arg0.getMessage();
                            messageBuilder.append(bytesToHex(msg));
                            messageBuilder.append(";");
                        } finally {
                            lock.unlock();
                        }
                    }

                    @Override
                    public void close() {
                    }
                };
            }
        }

        @Override
        public List<Receiver> getReceivers() {
            return null;
        }

        @Override
        public Transmitter getTransmitter() throws MidiUnavailableException {
            if (info.isInput()) {
                return new Transmitter() {

                    private Receiver rec;

                    @Override
                    public void setReceiver(Receiver arg0) {
                        this.rec = arg0;
                    }

                    @Override
                    public Receiver getReceiver() {
                        return rec;
                    }

                    @Override
                    public void close() {
                    }
                };
            } else {
                throw new MidiUnavailableException(
                        "TestMidiDevice currently has no Receivers");
            }
        }

        @Override
        public List<Transmitter> getTransmitters() {
            return null;
        }

        @Override
        public boolean isOpen() {
            return true;
        }

        @Override
        public void open() throws MidiUnavailableException {
        }

    }
}
