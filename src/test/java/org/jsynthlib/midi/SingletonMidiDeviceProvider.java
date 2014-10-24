package org.jsynthlib.midi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.sound.midi.spi.MidiDeviceProvider;

import org.apache.log4j.Logger;

public class SingletonMidiDeviceProvider extends MidiDeviceProvider {

    private static final Info[] INFOS = {
            new TestDeviceInfo(true), new TestDeviceInfo(false) };

    private static final SingletonMidiDeviceProvider INSTANCE =
            new SingletonMidiDeviceProvider();

    public static SingletonMidiDeviceProvider getInstance() {
        return INSTANCE;
    }

    private final transient Logger log = Logger.getLogger(getClass());

    private final char[] hexArray;

    private final Lock lock;

    private final Map<MidiDevice.Info, MidiDevice> deviceMap;

    private final List<MidiRecordSession> sessions;

    public SingletonMidiDeviceProvider() {
        lock = new ReentrantLock();
        hexArray = "0123456789ABCDEF".toCharArray();
        deviceMap = new HashMap<MidiDevice.Info, MidiDevice>();
        sessions = new ArrayList<MidiRecordSession>();
    }

    @Override
    public MidiDevice getDevice(Info info) {
        if (info instanceof TestDeviceInfo) {
            if (deviceMap.containsKey(info)) {
                return deviceMap.get(info);
            } else {
                TestDeviceInfo deviceInfo = (TestDeviceInfo) info;
                TestMidiDevice testMidiDevice = new TestMidiDevice(deviceInfo);
                deviceMap.put(deviceInfo, testMidiDevice);
                return testMidiDevice;
            }
        }
        return null;
    }

    public static class MidiRecordSession {
        private final StringBuilder sb = new StringBuilder();

        void onMidiInput(String midi) {
            sb.append(midi);
            sb.append(";");

        }

        public String getString() {
            return sb.toString();
        }
    }

    @Override
    public Info[] getDeviceInfo() {
        return INFOS;
    }

    public MidiRecordSession openSession() {
        try {
            lock.lock();
            MidiRecordSession session = new MidiRecordSession();
            sessions.add(session);
            return session;
        } finally {
            lock.unlock();
        }
    }

    public String closeSession(MidiRecordSession session) {
        try {
            lock.lock();
            sessions.remove(session);
            return session.getString();
        } finally {
            lock.unlock();
        }
    }

    public void sendMidi(MidiMessage msg) {
        log.info("Sending midi: " + Arrays.toString(msg.getMessage()));
        try {
            MidiDevice midiDevice = MidiSystem.getMidiDevice(INFOS[0]);
            midiDevice.getTransmitter().getReceiver().send(msg, 0);
        } catch (MidiUnavailableException e) {
            log.warn(e.getMessage(), e);
        }

    }

    static class TestDeviceInfo extends MidiDevice.Info {

        private final boolean input;

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

        private final TestDeviceInfo info;
        private Transmitter transmitter;

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
                            String bytesToHex = bytesToHex(msg);
                            log.info("Received midi from jsynthlib: "
                                    + bytesToHex);
                            for (MidiRecordSession session : sessions) {
                                session.onMidiInput(bytesToHex);
                            }
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
                if (transmitter == null) {
                    transmitter = new Transmitter() {

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
                }
                return transmitter;
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
