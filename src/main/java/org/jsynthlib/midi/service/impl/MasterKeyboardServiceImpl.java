/*
 * Copyright 2014 Pascal Collberg
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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import org.apache.log4j.Logger;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MasterKeyboardService;
import org.jsynthlib.midi.service.MidiMonitorService;
import org.jsynthlib.midi.service.MidiService;

/**
 * @author Pascal Collberg
 */
@Singleton
public class MasterKeyboardServiceImpl implements MasterKeyboardService {

    private final transient Logger log = Logger.getLogger(getClass());

    private Transmitter trns;
    private Receiver rcvr1;
    private MidiSettings midiSettings;
    private MidiService midiService;
    private MidiMonitorService midiMonitorService;

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.midi.service.MasterKeyboardService#masterInEnable()
     */
    @Override
    public void masterInEnable() {
        if (midiSettings.getMasterInEnable()) {
            // disable previous master in port if enabled.
            masterInDisable();

            try {
                // get transmitter
                int masterController = midiSettings.getMasterController();
                String masterCtrlName = midiSettings.getInputName(masterController);
                trns = midiService.getTransmitter(masterCtrlName);

                // create output receiver
                rcvr1 = new MasterReceiver();
                trns.setReceiver(rcvr1);
                log.debug("Receiver: " + rcvr1 + ", Transmitter: " + trns);
            } catch (MidiUnavailableException e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.midi.service.MasterKeyboardService#masterInDisable()
     */
    @Override
    public void masterInDisable() {
        if (trns != null) {
            try {
                midiService.releaseTransmitter(trns);
            } catch (MidiUnavailableException e) {
                log.warn(e.getMessage(), e);
            }
        }
        if (rcvr1 != null) {
            rcvr1.close();
        }
    }

    // //////////////////////////////////////////////////////////////////////
    // MIDI Master Input
    // masterInTrans (trns) -> MasterReceiver (rcvr1) -> initPortOut(rcvr)
    class MasterReceiver implements Receiver {
        private final Receiver rcvr;
        private final int initPortOut;

        MasterReceiver() throws MidiUnavailableException {
            initPortOut = midiSettings.getInitPortOut();
            String outputName = midiSettings.getOutputName(initPortOut);
            this.rcvr = midiService.getReceiver(outputName);
        }

        // Receiver interface
        @Override
        public void close() {
            // don't close a shared Receiver
            try {
                midiService.releaseReceiver(rcvr);
            } catch (MidiUnavailableException e) {
                log.warn(e.getMessage(), e);
            }
        }

        @Override
        public void send(MidiMessage message, long timeStamp) {
            int status = message.getStatus();
            if ((0x80 <= status) && (status < 0xF0)) { // MIDI channel Voice
                                                       // Message
                // I believe Sysex message must be ignored.
                // || status == SysexMessage.SYSTEM_EXCLUSIVE)
                log.info("MasterReceiver: " + message);
                this.rcvr.send(message, timeStamp);
                midiMonitorService.logOut(initPortOut, message);
            }
        }
    }

    public MidiSettings getMidiSettings() {
        return midiSettings;
    }

    @Inject
    public void setMidiSettings(MidiSettings midiSettings) {
        this.midiSettings = midiSettings;
    }

    public MidiMonitorService getMidiMonitorService() {
        return midiMonitorService;
    }

    @Inject
    public void setMidiMonitorService(MidiMonitorService midiMonitorService) {
        this.midiMonitorService = midiMonitorService;
    }

    public MidiService getMidiService() {
        return midiService;
    }

    public void setMidiService(MidiService midiService) {
        this.midiService = midiService;
    }

}
