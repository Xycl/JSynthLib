package org.jsynthlib.midi.service;

import java.util.concurrent.TimeoutException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

public interface MidiService {

    /**
     * Setup an input queue for MIDI System Exclusive Message input. The input
     * queue is shared. If the input queue is already opened, nothing is done.
     * @see #clearSysexInputQueue
     */
    void setSysexInputQueue(int port);

    /**
     * clear MIDI input queue specified. Internally setSysexInputQueue(port) is
     * called.
     * @see #setSysexInputQueue
     */
    void clearSysexInputQueue(int port);

    /**
     * return <code>true</code> when MIDI input queue is empty.
     * @see #clearSysexInputQueue
     */
    boolean isSysexInputQueueEmpty(int port);

    /**
     * get Sysex Message from MIDI input queue.
     * @see #clearSysexInputQueue
     */
    MidiMessage getMessage(int port, long timeout) throws TimeoutException,
            InvalidMidiDataException;

    /**
     * Converts a byte array into an array of SysexMessages. Each SysexMessage
     * must be terminated by END_OF_EXCLUSIVE.
     * <p>
     * This method is provided to keep compatibility with the old MIDI layer
     * which handled MIDI data in byte array. It is more efficient to create
     * SysexMessages directly because a synth driver knows the start index and
     * length of each Sysex data in an array.
     */
    SysexMessage[] byteArrayToSysexMessages(byte[] d)
            throws InvalidMidiDataException;

    /**
     * Convert an array of SysexMessage to a byte array.
     * @param msgs
     *            an array of SysexMessage.
     * @return byte array of System Exclusive data.
     */
    byte[] sysexMessagesToByteArray(SysexMessage[] msgs);

    /**
     * Send a <code>MidiMessage</code>. A Sysex Message is divided into several
     * Sysex Messages whose size is <code>bufSize</code>.
     * @param outPortName
     *            MIDI out port name
     * @param msg
     *            MIDI Message
     * @param bufSize
     *            MIDI message size. If zero, whole MIDI message is passed to
     *            lower MIDI driver.
     * @param delay
     *            delay (msec) after every MIDI message transfer.
     * @throws MidiUnavailableException
     * @throws InvalidMidiDataException
     */
    void send(String outPortName, MidiMessage msg, int bufSize, int delay)
            throws MidiUnavailableException, InvalidMidiDataException;

    /**
     * Maybe these methods should be in an own class, but currently I put them
     * here. ttittmann 01.Aug.2004
     */
    void startSequencer(String portName);

    Receiver getReceiver(String portName) throws MidiUnavailableException;

    void releaseReceiver(Receiver receiver) throws MidiUnavailableException;

    Transmitter getTransmitter(String portName) throws MidiUnavailableException;

    void releaseTransmitter(Transmitter transmitter) throws MidiUnavailableException;

    void closeAllMidiConnections();
}
