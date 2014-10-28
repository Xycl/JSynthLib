/*
 * MidiScan.java
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.SysexMessage;
import javax.swing.JDialog;
import javax.swing.ProgressMonitor;

import org.apache.log4j.Logger;
import org.jsynthlib.core.LookupManufacturer;
import org.jsynthlib.core.viewcontroller.ScanUnkownReportDialog;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceDescriptor;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.midi.domain.MidiSettings;
import org.jsynthlib.midi.service.MidiScanService;
import org.jsynthlib.midi.service.MidiService;

/**
 * Detect MIDI devices by sending out Inquery ID Sysex Message to every MIDI
 * outport port with every DeviceID and poll every MIDI input port.
 * @author Gerrit Gehnen
 * @version $Id: MidiScan.java 1182 2011-12-04 22:07:24Z chriswareham $
 */
@Singleton
public class MidiScanServiceImpl implements MidiScanService {
    // should be 0x7D, but...
    private static final int MAX_DEVICE_ID = 0x7D;
    // Wait at least 100ms for a response from a device
    private static final int WAITFORRESPONSE = 100;

    private final transient Logger log = Logger.getLogger(getClass());

    private boolean anyUnknown;
    private String report;
    private final ExecutorService executor;
    private Future<?> scanFuture;
    private MidiSettings midiSettings;
    private MidiService midiService;
    private DeviceManager deviceManager;

    /**
     * Creates new MidiScan
     */
    @Inject
    public MidiScanServiceImpl() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void scanMidiDevices(final ProgressMonitor pb, final JDialog parent) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                // The magical ID-Request Message
                byte[] idData =
                        {
                                (byte) 0xf0, (byte) 0x7e, (byte) 0,
                                (byte) 0x06, (byte) 0x01, (byte) 0xf7 };

                anyUnknown = false;
                boolean responseRcvd = false;
                report = new String();
                SysexMessage inqMsg = new SysexMessage();

                int maxin = midiSettings.getInputMidiDeviceInfo().length;
                int maxout = midiSettings.getOutputMidiDeviceInfo().length;

                if (pb != null) {
                    // Preparation for a progress bar
                    pb.setMaximum(maxout * (MAX_DEVICE_ID + 1));
                    pb.setNote("Scanning MIDI Devices");
                }
                for (int j = 0; j < maxout; j++) { // For all Outputs
                    log.info("Out port : " + j);
                    try {
                        String outputName = midiSettings.getOutputName(j);
                        for (int devID = 0; devID <= MAX_DEVICE_ID; devID++) {
                            // every devID
                            if (pb != null) {
                                pb.setProgress(j * (MAX_DEVICE_ID + 1) + devID);
                            }
                            log.debug("  device ID : " + devID);
                            idData[2] = (byte) devID; // Set the transmit devID
                            try {
                                inqMsg.setMessage(idData, idData.length);
                            } catch (InvalidMidiDataException e) {
                                log.warn(e.getMessage(), e);
                                continue;
                            }

                            // clear all input queue
                            for (int i = 0; i < maxin; i++) {
                                midiService.clearSysexInputQueue(i);
                            }

                            // send Inquiry ID Sysex Message
                            try {
                                midiService.send(outputName, inqMsg,
                                        midiSettings.getMidiOutBufSize(),
                                        midiSettings.getMidiOutDelay());
                            } catch (InvalidMidiDataException e) {
                                log.warn(e.getMessage(), e);
                                continue;
                            }
                                Thread.sleep(WAITFORRESPONSE);
                            for (int i = 0; i < maxin; i++) {
                                // For all Inputs
                                log.debug("    in port : " + i);
                                if (midiService.isSysexInputQueueEmpty(i)) {
                                    continue;
                                }

                                log.debug("    Message Received");
                                responseRcvd = true;
                                SysexMessage msg;
                                try {
                                    msg =
                                            (SysexMessage) midiService.getMessage(
                                                    i, 1);
                                } catch (InvalidMidiDataException e) {
                                    log.warn(e.getMessage(), e);
                                    continue;
                                } catch (TimeoutException e) {
                                    log.warn(e.getMessage(), e);
                                    continue;
                                }
                                int sysexSize = msg.getLength();
                                if (sysexSize <= 0) {
                                    continue;
                                }
                                byte[] answerData = msg.getMessage();

                                /*
                                 * check, whether it is really an inquiry response.
                                 * The (answerData[2] == 0x06) and (answerData[3] ==
                                 * 0x02) parts are a hack that is needed e.g. for
                                 * the Waldorf Microwave 2 which does not obey the
                                 * MIDI specification
                                 */
                                if (((answerData[0] & 0xff) == 0xf0)
                                        && (answerData[1] == 0x7e)
                                        && ((answerData[2] == 0x06) || (answerData[3] == 0x06))
                                        && ((answerData[3] == 0x02) || (answerData[4] == 0x02))) {
                                    // Look in all loaded modules
                                    checkResponseData(answerData, sysexSize, j, i,
                                            devID, pb);
                                } else if (!Arrays.equals(idData, answerData)) {
                                    // don't show debug messge for the inquiry
                                    // request
                                    log.info("MidiScan : received non inquiry response data. Ingored.");
                                }
                            } // i : input
                        } // devID
                    } catch (InterruptedException e) {
                        log.warn(e.getMessage(), e);
                    } catch (MidiUnavailableException e) {
                        log.warn(e.getMessage(), e);
                    }
                } // j : output
                if (pb != null) {
                    // clear progress bar
                    pb.setProgress(maxout * (MAX_DEVICE_ID + 1));
                }
                if (anyUnknown) {
                    if (parent != null) {
                        ScanUnkownReportDialog surd =
                                new ScanUnkownReportDialog(parent, true);
                        surd.addToReport(report);
                        surd.setVisible(true);
                    } else {
                        log.info(report);
                    }
                } else if (!responseRcvd) {
                    log.info("No scan responses received");
                }
            }
        };
        scanFuture = executor.submit(runnable);
    }

    private void checkResponseData(byte[] answerData, int msgsize, int midiout,
            int midiin, int devID, ProgressMonitor pb) {
        StringBuffer responseString = new StringBuffer("F0");
        for (int k = 1; k < msgsize; k++) {
            if ((answerData[k] < 16) && (answerData[k] >= 0)) {
                responseString.append("0");
            }
            responseString.append(Integer.toHexString(0xff & answerData[k]));
        }
        log.debug("ResponseString " + responseString);

        boolean found = false;
        Iterator<String> synthIDs = deviceManager.getDeviceIds().iterator();
        while (synthIDs.hasNext()) {
            String se = synthIDs.next();
            log.debug("Checking " + se);

            if (checkInquiry(responseString, se)) {
                // Check, whether the driver is already in the list
                boolean dontadd = false;
                for (int checkloop = 0; checkloop < deviceManager.deviceCount(); checkloop++) {
                    String checkDevice =
                            deviceManager.getDevice(checkloop).getClass()
                                    .getName();
                    DeviceDescriptor descriptor =
                            deviceManager.getDescriptorForIDString(se);
                    if (descriptor != null
                            && checkDevice.equalsIgnoreCase(descriptor
                                    .getDeviceClass())) {
                        dontadd = true; // Oh, its already there....
                    }
                }
                if (!dontadd) { // add it only, if it is not in the list
                    DeviceDescriptor descriptor =
                            deviceManager.getDescriptorForIDString(se);
                    Device useDevice = deviceManager.addDevice(descriptor);
                    log.info("MidiOut: " + midiout + ", MidiIn: " + midiin
                            + ", devID: " + devID);

                    String outputName = midiSettings.getOutputName(midiout);
                    useDevice.setOutPortName(outputName);
                    useDevice.setInPort(midiin);
                    useDevice.setDeviceID(devID + 1);
                    if (pb != null) {
                        pb.setNote("Found " + useDevice.getManufacturerName()
                                + " " + useDevice.getModelName());
                    }
                }

                found = true;
            }
        }
        if (!found) {
            anyUnknown = true;
            report +=
                    "Unknown Synthesizer found:\n"
                            + " According to the manufacturer ID it is made by "
                            + LookupManufacturer.get(answerData[5],
                                    answerData[6], answerData[7])
                            + "\n It is connected on the Output Port: "
                            + midiSettings.getOutputMidiDeviceInfo(midiout)
                                    .toString()
                            + " with Device ID: "
                            + devID
                            + 1
                            + "\n The Input Port was: "
                            + midiSettings.getInputMidiDeviceInfo(midiin)
                                    .toString()

                            + "\n Complete answer was: ";

            for (int x = 0; x < msgsize; x++) {
                report += " ";
                if ((0xff & answerData[x]) < 0x10) {
                    report += "0";
                }
                report += Integer.toHexString(0xff & answerData[x]);
                if ((answerData[x] & 0xff) == 0xf7) {
                    break;
                }
            }
            report += "\n";
        }
    }

    private boolean checkInquiry(StringBuffer patchString, String inquiryID) {
        Pattern p = Pattern.compile(inquiryID, Pattern.CASE_INSENSITIVE);
        return p.matcher(patchString).lookingAt();
    }

    @Override
    public void close() {
        if (scanFuture != null) {
            scanFuture.cancel(true);
        }
    }

    public MidiService getMidiService() {
        return midiService;
    }

    @Inject
    public void setMidiService(MidiService midiService) {
        this.midiService = midiService;
    }

    public MidiSettings getMidiSettings() {
        return midiSettings;
    }

    @Inject
    public void setMidiSettings(MidiSettings midiSettings) {
        this.midiSettings = midiSettings;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    @Inject
    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }
}
