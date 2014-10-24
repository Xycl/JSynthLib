package org.jsynthlib.midi.service.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;

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

        boolean empty = tested.getMidiDeviceReferenceMap().isEmpty();
        assertTrue("Reference map is empty at start", empty);
        Transmitter transmitter = tested.getTransmitter(transmitterName);

        int deviceSize = tested.getMidiDeviceReferenceMap().size();
        assertEquals("Device size", 2, deviceSize);

        int txSize = tested.getMidiTransmitterMap().size();
        assertEquals("TX size", 1, txSize);

        String txResult = tested.getMidiTransmitterMap().get(transmitter);
        assertEquals(transmitterName, txResult);

        tested.getTransmitter(transmitterName);

        deviceSize = tested.getMidiDeviceReferenceMap().size();
        assertEquals(2, deviceSize);

        txSize = tested.getMidiTransmitterMap().size();
        assertEquals(1, tested.getMidiTransmitterMap().size());

        txResult = tested.getMidiTransmitterMap().get(transmitter);
        assertEquals(transmitterName, txResult);

        tested.releaseTransmitter(transmitter);
        assertEquals(2, tested.getMidiDeviceReferenceMap().size());
        assertEquals(1, tested.getMidiTransmitterMap().size());
        assertEquals(transmitterName, tested.getMidiTransmitterMap().get(transmitter));

        tested.releaseTransmitter(transmitter);
        assertTrue(tested.getMidiDeviceReferenceMap().isEmpty());
        assertTrue(tested.getMidiTransmitterMap().isEmpty());

        verifyAll();

    }
}
