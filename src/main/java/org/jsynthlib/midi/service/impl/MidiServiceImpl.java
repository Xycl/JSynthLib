/*
 * Copyright 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.midi.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiMonitorService;
import org.jsynthlib.midi.service.MidiService;

/**
 * MIDI Utility Routines. This class contains methods and inner classes for Java
 * Sound API.
 * <p>
 * Examples:
 * <p>
 * MIDI output
 *
 * <pre>
 * Receiver rcvr = MidiUtil.getReceiver(outport);
 * MidiUtil.send(rcvr, msg);
 * </pre>
 *
 * MIDI input (System Exclusive Message)
 *
 * <pre>
 * MidiUtil.clearSysexInputQueue(inport);
 * SysexMessage msg = MidiUtil.getMessage(inport, 1000);
 * </pre>
 *
 * The example above uses a shared input data queue. See the description for
 * each method and code of Master-In and Fader-In for more details and MIDI
 * short message input.
 * @author Hiroo Hayashi
 * @version $Id: MidiUtil.java 1213 2013-10-14 19:10:42Z packe01 $
 * @see <a
 *      href="http://java.sun.com/j2se/1.4.2/docs/guide/sound/programmer_guide/contents.html">
 *      Java Sound Progremmer Guide</a>
 */
@Singleton
public class MidiServiceImpl implements MidiService {

    private final transient Logger log = Logger.getLogger(getClass());

    private final Map<Integer, SysexInputQueue> sysexInputQueue;

    private final MidiMonitorService midiMonitorService;
    private final MidiSettings midiSettings;

    private final Map<Receiver, String> midiReceiverMap;
    private final Map<Transmitter, String> midiTransmitterMap;

    private final AppConfig appConfig;

    private final HashMap<AutoCloseable, Integer> midiDeviceReferenceMap;

    @Inject
    public MidiServiceImpl(MidiSettings midiSettings,
            MidiMonitorService midiMonitorService, AppConfig appConfig) {
        this.midiSettings = midiSettings;
        this.midiMonitorService = midiMonitorService;
        this.appConfig = appConfig;
        sysexInputQueue = new HashMap<Integer, SysexInputQueue>();
        midiReceiverMap = new HashMap<Receiver, String>();
        midiTransmitterMap = new HashMap<Transmitter, String>();
        midiDeviceReferenceMap = new HashMap<AutoCloseable, Integer>();
    }

    boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    @Override
    public void setSysexInputQueue(int port) {
        if (sysexInputQueue.containsKey(port)) {
            // already ready
            return;
        }

        String inputName = midiSettings.getInputName(port);
        SysexInputQueue rcvr = new SysexInputQueue(port);
        Transmitter trns;
        try {
            trns = getTransmitter(inputName);
            trns.setReceiver(rcvr);
            sysexInputQueue.put(port, rcvr);
        } catch (MidiUnavailableException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void clearSysexInputQueue(int port) {
        setSysexInputQueue(port);
        sysexInputQueue.get(port).clearQueue();
    }

    @Override
    public boolean isSysexInputQueueEmpty(int port) {
        return sysexInputQueue.get(port).isEmpty();
    }

    @Override
    public MidiMessage getMessage(int port, long timeout)
            throws TimeoutException, InvalidMidiDataException {
        return sysexInputQueue.get(port).getMessage(timeout);
    }

    @Override
    public SysexMessage[] byteArrayToSysexMessages(byte[] d)
            throws InvalidMidiDataException {
        List<SysexMessage> list = new ArrayList<SysexMessage>();

        for (int i = 0; i < d.length; i++) {
            if ((d[i] & 0xFF) == SysexMessage.SYSTEM_EXCLUSIVE) {
                int j = i + 1;
                // let cause exception if there is no END_OF_EXCLUSIVE
                while (j < d.length
                        && (d[j] & 0xff) != ShortMessage.END_OF_EXCLUSIVE) {
                    j++;
                }
                if (j == d.length) {
                    throw new InvalidMidiDataException("Missing EOX");
                }
                // here d[j] is EOX.
                int l = j - i + 1;
                byte[] b = new byte[l];
                System.arraycopy(d, i, b, 0, l);
                SysexMessage m = new SysexMessage();
                m.setMessage(b, l);
                list.add(m);
                i = j;
            }
        }
        return list.toArray(new SysexMessage[0]);
    }

    @Override
    public byte[] sysexMessagesToByteArray(SysexMessage[] msgs) {
        int totalSize = 0;
        for (int i = 0; i < msgs.length; i++) {
            totalSize += msgs[i].getLength();
        }
        byte[] sysex = new byte[totalSize];
        for (int size, ofst = 0, i = 0; i < msgs.length; ofst += size, i++) {
            size = msgs[i].getLength();
            byte[] d = msgs[i].getMessage();
            System.arraycopy(d, 0, sysex, ofst, size);
        }
        return sysex;
    }

    /**
     * get MidiDevice for Output.
     * @param port
     *            an index in an array returned by
     *            <code>getOutputMidiDeviceInfo()</code>.
     * @return a <code>MidiDevice</code> object for MIDI output. The MidiDevice
     *         is already opened.
     * @see #getOutputMidiDeviceInfo()
     * @see #getReceiver
     * @see #send
     */
    MidiDevice getOutputMidiDevice(int port) throws MidiUnavailableException {
        if (midiSettings.getOutputMidiDeviceInfo().length == 0) {
            return null;
        }
        MidiDevice dev =
                MidiSystem
                        .getMidiDevice(midiSettings.getOutputMidiDeviceInfo()[port]);
        if (!dev.isOpen()) {
            log.info("open outport: " + dev.getDeviceInfo().getName()
                    + ", port: " + port);
            dev.open();
        }
        return dev;
    }

    /**
     * get MidiDevice for Input.
     * @param port
     *            an index in an array returned by
     *            <code>getInputMidiDeviceInfo()</code>.
     * @return a <code>MidiDevice</code> object for MIDI input. The MidiDevice
     *         is already opened.
     * @see #getInputMidiDeviceInfo()
     * @see #clearSysexInputQueue
     * @see #getMessage
     */
    MidiDevice getInputMidiDevice(int port) {
        MidiDevice dev = null;
        if (midiSettings.getInputMidiDeviceInfo().length == 0) {
            return null;
        }
        try {
            dev =
                    MidiSystem.getMidiDevice(midiSettings
                            .getInputMidiDeviceInfo()[port]);
            if (!dev.isOpen()) {
                log.info("open inport: " + dev.getDeviceInfo().getName()
                        + ", port: " + port);
                dev.open();
            }
        } catch (MidiUnavailableException e) {
            log.warn(e.getMessage(), e);
        }
        return dev;
    }

    @Override
    public Receiver getReceiver(String portName)
            throws MidiUnavailableException {
        if (midiSettings.getOutputMidiDeviceInfo().length == 0) {
            return null;
        }

        Receiver receiver = null;
        Iterator<Entry<Receiver, String>> iterator =
                midiReceiverMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Receiver, String> entry = iterator.next();
            if (entry.getValue().equals(portName)
                    && entry.getKey() instanceof Receiver) {
                receiver = entry.getKey();
                break;
            }
        }

        if (receiver == null) {
            int outPortIndex = midiSettings.getOutPort(portName);
            MidiDevice dev = getOutputMidiDevice(outPortIndex);
            receiver = dev.getReceiver();
            midiReceiverMap.put(receiver, portName);

            dev.open();
        }

        incrementEntityRef(receiver);
        return receiver;
    }

    @Override
    public void releaseReceiver(Receiver receiver)
            throws MidiUnavailableException {
        try {
            if (decrementEntityRef(receiver)) {
                String portName = midiReceiverMap.remove(receiver);
                int outPortIndex = midiSettings.getOutPort(portName);
                MidiDevice dev = getOutputMidiDevice(outPortIndex);
                decrementEntityRef(dev);
            }
        } catch (Exception e) {
            throw new MidiUnavailableException(e.getMessage());
        }
    }

    @Override
    public Transmitter getTransmitter(String portName)
            throws MidiUnavailableException {
        if (midiSettings.getInputMidiDeviceInfo().length == 0) {
            return null;
        }

        Transmitter transmitter = null;
        Iterator<Entry<Transmitter, String>> iterator =
                midiTransmitterMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Transmitter, String> entry = iterator.next();
            if (entry.getValue().equals(portName) && entry.getKey() instanceof Transmitter) {
                transmitter = entry.getKey();
                break;
            }
        }

        if (transmitter == null) {
            int inPortIndex = midiSettings.getInPort(portName);

            // Transmitter cannot be shared.
            MidiDevice dev = getInputMidiDevice(inPortIndex);
            incrementEntityRef(dev);
            transmitter = dev.getTransmitter();
            midiTransmitterMap.put(transmitter, portName);
        }
        incrementEntityRef(transmitter);
        return transmitter;
    }

    @Override
    public void releaseTransmitter(Transmitter transmitter)
            throws MidiUnavailableException {
        try {
            if (decrementEntityRef(transmitter)) {
                String portName = midiTransmitterMap.remove(transmitter);
                int inPortIndex = midiSettings.getInPort(portName);
                MidiDevice dev = getInputMidiDevice(inPortIndex);
                decrementEntityRef(dev);
            }
        } catch (Exception e) {
            throw new MidiUnavailableException(e.getMessage());
        }
    }

    void incrementEntityRef(AutoCloseable midiEntity)
            throws MidiUnavailableException {
        Integer refCounter = midiDeviceReferenceMap.get(midiEntity);
        if (refCounter == null) {
            midiDeviceReferenceMap.put(midiEntity, 1);
            if (midiEntity instanceof MidiDevice) {
                MidiDevice midiDevice = (MidiDevice) midiEntity;
                midiDevice.open();
            }
        } else {
            refCounter = new Integer(refCounter.intValue() + 1);
            midiDeviceReferenceMap.put(midiEntity, refCounter);
        }
    }

    /**
     * @param midiEntity
     * @return true if this entity was closed
     * @throws Exception
     */
    boolean decrementEntityRef(AutoCloseable midiEntity) throws Exception {
        boolean retval = false;
        if (midiEntity == null) {
            return retval;
        }
        Integer refCounter = midiDeviceReferenceMap.get(midiEntity);
        if (refCounter == null) {
            retval = true;
        } else {
            refCounter = new Integer(refCounter.intValue() - 1);
            midiDeviceReferenceMap.put(midiEntity, refCounter);
            if (refCounter.equals(0)) {
                retval = true;
            }
        }

        if (retval) {
            midiDeviceReferenceMap.remove(midiEntity);
            midiEntity.close();
        }
        return retval;
    }

    @Override
    public void send(String outPortName, MidiMessage msg, int bufSize, int delay)
            throws MidiUnavailableException, InvalidMidiDataException {
        int size = msg.getLength();
        Receiver rcv = getReceiver(outPortName);

        if (bufSize == 0 || size <= bufSize) {
            rcv.send(msg, -1);
            midiMonitorService.logOut(msg);
            // always include the delay after sending in case a driver uses
            // multiple calls to send
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // do nothing
            }
        } else {
            // divide large System Exclusive Message into multiple
            // small messages.
            byte[] sysex = msg.getMessage();
            byte[] tmpArray = new byte[bufSize + 1];
            for (int i = 0; size > 0; i += bufSize, size -= bufSize) {
                int s = Math.min(size, bufSize);

                if (i == 0) {
                    System.arraycopy(sysex, i, tmpArray, 0, s);
                    ((SysexMessage) msg).setMessage(tmpArray, s);
                } else {
                    tmpArray[0] = (byte) SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE;
                    System.arraycopy(sysex, i, tmpArray, 1, s);
                    ((SysexMessage) msg).setMessage(tmpArray, ++s);
                }
                rcv.send(msg, -1);
                midiMonitorService.logOut(msg);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // do nothing
                }
            }
        }
    }

    /*
     * Maybe these methods should be in an own class, but currently I put them
     * here. ttittmann 01.Aug.2004
     */

    @Override
    public void startSequencer(String myport) {
        try {
            Sequencer sequencer = MidiSystem.getSequencer();

            Transmitter seqTrans = sequencer.getTransmitter();
            int outPortName = midiSettings.getOutPort(myport);
            MidiDevice outPort = getOutputMidiDevice(outPortName);
            Receiver outRcvr = outPort.getReceiver();
            seqTrans.setReceiver(outRcvr);

            sequencer.open();

            File myMidiFile = new File(appConfig.getSequencePath());
            Sequence mySeq = MidiSystem.getSequence(myMidiFile);
            sequencer.setSequence(mySeq);

            sequencer.start();
        } catch (MidiUnavailableException e) {
            ErrorMsg.reportError("MidiSystem Error",
                    "Can't access sequencer properly");
            log.warn(e.getMessage(), e);
        } catch (IOException e) {
            ErrorMsg.reportError("MidiSystem Error",
                    "Can't access MIDI file for sequencer");
            log.warn(e.getMessage(), e);
        } catch (InvalidMidiDataException e) {
            ErrorMsg.reportError("MidiSystem Error",
                    "Can't access sequencer properly");
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void closeAllMidiConnections() {
        Set<Entry<AutoCloseable, Integer>> entrySet =
                midiDeviceReferenceMap.entrySet();
        Iterator<Entry<AutoCloseable, Integer>> iterator = entrySet.iterator();
        while (iterator.hasNext()) {
            try {
                Entry<AutoCloseable, Integer> entry = iterator.next();
                entry.getKey().close();
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    protected Map<Receiver, String> getMidiReceiverMap() {
        return midiReceiverMap;
    }

    protected Map<Transmitter, String> getMidiTransmitterMap() {
        return midiTransmitterMap;
    }

    protected HashMap<AutoCloseable, Integer> getMidiDeviceReferenceMap() {
        return midiDeviceReferenceMap;
    }

}
