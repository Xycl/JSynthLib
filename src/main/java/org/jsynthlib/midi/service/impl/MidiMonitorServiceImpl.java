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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Singleton;
import javax.sound.midi.MidiMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.midi.domain.MidiLogEntry;
import org.jsynthlib.midi.service.MidiLogListener;
import org.jsynthlib.midi.service.MidiMonitorService;

/**
 * Service class handling MIDI log dispatching.
 * @author Pascal Collberg
 */
@Singleton
public class MidiMonitorServiceImpl implements MidiMonitorService {

    /**
     * Timeout in millisecs between each log dispatch.
     */
    private static final int LOG_UPDATE_TIMEOUT = 5;

    /**
     * Maximum log lines cached between dispatches.
     */
    private static final int LOG_SIZE = 100;

    private final transient Logger log = Logger.getLogger(getClass());

    private final transient Lock lock = new ReentrantLock();

    private final BlockingQueue<MidiLogEntry> logQueue;

    private final List<MidiLogListener> listeners;
    private ScheduledExecutorService executor;

    public MidiMonitorServiceImpl() {
        logQueue = new ArrayBlockingQueue<MidiLogEntry>(LOG_SIZE);
        listeners = new ArrayList<MidiLogListener>();
        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    lock.lock();
                    for (MidiLogListener listener : listeners) {
                        try {
                            for (MidiLogEntry entry : logQueue) {
                                listener.log(entry);
                            }
                        } catch (Exception e) {
                            log.warn(e.getMessage(), e);
                        }
                    }
                    logQueue.clear();
                } finally {
                    lock.unlock();
                }
            }
        }, 0, LOG_UPDATE_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void addLogListener(MidiLogListener l) {
        try {
            lock.lock();
            listeners.add(l);
        } finally {
            lock.unlock();
        }
    }

    public void removeLogListener(MidiLogListener l) {
        try {
            lock.lock();
            listeners.remove(l);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Dump output MidiMessage <code>msg</code> on the MIDI Monitor Window with
     * port number information.
     * @param port
     *            port number
     * @param msg
     *            MidiMessage
     */
    public void logIn(int port, MidiMessage msg) {
        logQueue.offer(new MidiLogEntry(port, true, msg));
    }

    /**
     * Dump input MidiMessage <code>msg</code> on the MIDI Monitor Window with
     * port number information.
     * @param port
     *            port number
     * @param msg
     *            MidiMessage
     */
    public void logOut(int port, MidiMessage msg) {
        logQueue.offer(new MidiLogEntry(port, false, msg));
    }

    public void logOut(MidiMessage msg) {
        logQueue.offer(new MidiLogEntry(-1, false, msg));
    }

}
