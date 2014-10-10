package org.jsynthlib.midi.service.impl;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;
import javax.sound.midi.MidiDevice.Info;

import org.easymock.internal.MockBuilder;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiMonitorService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MidiServiceImplTest {

    private MidiSettings midiSettingsMock;
    private MidiMonitorService midiMonitorServiceMock;
    private AppConfig appConfigMock;
    private MidiServiceImpl tested;
    private Transmitter transmitterMock;
    private Info infoMock;
    private MidiDevice midiDeviceMock;

    @Before
    public void setUp() throws Exception {
        midiSettingsMock = createMock(MidiSettings.class);
        midiMonitorServiceMock = createMock(MidiMonitorService.class);
        appConfigMock = createMock(AppConfig.class);
        MockBuilder<MidiServiceImpl> mockBuilder = new MockBuilder<>(MidiServiceImpl.class);
        mockBuilder.addMockedMethod("getInputMidiDevice", int.class);
        mockBuilder.addMockedMethod("getOutputMidiDevice", int.class);
//        mockBuilder.withConstructor(MidiSettings.class, MidiMonitorService.class, AppConfig.class);
        mockBuilder.withConstructor(midiSettingsMock, midiMonitorServiceMock, appConfigMock);
        tested = mockBuilder.createMock();
        transmitterMock = createMock(Transmitter.class);
        infoMock = createMock(Info.class);
        midiDeviceMock = createMock(MidiDevice.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    void replayAll() {
        replay(midiSettingsMock);
        replay(midiMonitorServiceMock);
        replay(appConfigMock);
        replay(tested);
        replay(transmitterMock);
        replay(infoMock);
        replay(midiDeviceMock);
    }

    void verifyAll() {
        verify(midiSettingsMock);
        verify(midiMonitorServiceMock);
        verify(appConfigMock);
        verify(tested);
        verify(transmitterMock);
        verify(infoMock);
        verify(midiDeviceMock);
    }

    @Test
    public void testEntityRef() throws Exception {
        transmitterMock.close();
        expectLastCall().times(2);

        replayAll();

        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());
        tested.incrementEntityRef(transmitterMock);
        assertEquals(1, tested.getMidiDeviceReferenceMap().size());
        tested.decrementEntityRef(transmitterMock);
        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());

        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());
        tested.incrementEntityRef(transmitterMock);
        assertEquals(1, tested.getMidiDeviceReferenceMap().size());
        tested.incrementEntityRef(transmitterMock);
        assertEquals(1, tested.getMidiDeviceReferenceMap().size());
        tested.decrementEntityRef(transmitterMock);
        assertEquals(1, tested.getMidiDeviceReferenceMap().size());
        tested.decrementEntityRef(transmitterMock);
        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());

        verifyAll();
    }

    @Test
    public void testGetReleaseTransmitter() throws MidiUnavailableException {
        String transmitterName = "name";

        expect(midiSettingsMock.getInputMidiDeviceInfo()).andReturn(new Info[]{infoMock});
        expect(midiSettingsMock.getInPort(transmitterName)).andReturn(0);
        expect(tested.getInputMidiDevice(0)).andReturn(midiDeviceMock);

        midiDeviceMock.open();
        expectLastCall().times(1);

        expect(midiDeviceMock.getTransmitter()).andReturn(transmitterMock);

        expect(midiSettingsMock.getInputMidiDeviceInfo()).andReturn(new Info[]{infoMock});

        expect(midiSettingsMock.getInPort(transmitterName)).andReturn(0);
        expect(tested.getInputMidiDevice(0)).andReturn(midiDeviceMock);

        transmitterMock.close();
        expectLastCall().times(1);

        midiDeviceMock.close();
        expectLastCall().times(1);

        replayAll();

        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());
        Transmitter transmitter = tested.getTransmitter(transmitterName);
        assertEquals(2, tested.getMidiDeviceReferenceMap().size());
        assertEquals(1, tested.getMidiTransmitterMap().size());
        assertEquals(transmitterMock, tested.getMidiTransmitterMap().get(transmitterName));

        tested.getTransmitter(transmitterName);
        assertEquals(2, tested.getMidiDeviceReferenceMap().size());
        assertEquals(1, tested.getMidiTransmitterMap().size());
        assertEquals(transmitterMock, tested.getMidiTransmitterMap().get(transmitterName));

        tested.releaseTransmitter(transmitter);
        assertEquals(2, tested.getMidiDeviceReferenceMap().size());
        assertEquals(1, tested.getMidiTransmitterMap().size());
        assertEquals(transmitterMock, tested.getMidiTransmitterMap().get(transmitterName));

        tested.releaseTransmitter(transmitter);
        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());
        assertTrue(tested.getMidiTransmitterMap().isEmpty());

        verifyAll();

    }
}
