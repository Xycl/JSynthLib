package org.jsynthlib.utils.ctrlr;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;

import org.jsynthlib.core.AppConfig;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiMonitorService;
import org.jsynthlib.midi.service.impl.MidiServiceImpl;
import org.jsynthlib.utils.SingletonMidiDeviceProvider;
import org.jsynthlib.utils.SingletonMidiDeviceProvider.MidiRecordSession;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class CtrlrMidiService extends MidiServiceImpl {

    private Receiver receiver;
    private final SingletonMidiDeviceProvider midiDeviceProvider;

    @Inject
    public CtrlrMidiService(MidiSettings midiSettings,
            MidiMonitorService midiMonitorService, AppConfig appConfig)
            throws MidiUnavailableException {
        super(midiSettings, midiMonitorService, appConfig);
        midiDeviceProvider = SingletonMidiDeviceProvider.getInstance();
        Info[] deviceInfo = midiDeviceProvider.getDeviceInfo();
        for (Info info : deviceInfo) {
            MidiDevice midiDevice = midiDeviceProvider.getDevice(info);
            if (midiDevice.getMaxReceivers() == -1) {
                receiver = midiDevice.getReceiver();
                break;
            }
        }
    }

    @Override
    public Receiver getReceiver(String portName)
            throws MidiUnavailableException {
        return receiver;
    }

    @Override
    public void releaseReceiver(Receiver rec) throws MidiUnavailableException {
    }

    public MidiRecordSession openSession() {
        return midiDeviceProvider.openSession();
    }

    public String closeSession(MidiRecordSession session) {
        return midiDeviceProvider.closeSession(session);
    }
}
