package org.jsynthlib.midi.domain;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import org.apache.log4j.Logger;
import org.jsynthlib.core.JSynthLib;
import org.jsynthlib.midi.service.MidiService;

@Singleton
public class MidiSettings {

    private final boolean testShortMessage = false;
    private final boolean testSysexMessage = true;


    private final List<PropertyChangeListener> listeners;
    private final Preferences prefences;
    private MidiService midiService;

    /** emulate the no MIDI device condition for debugging */
    private static final boolean EMULATE_NO_MIDI_IN = false;
    private static final boolean EMULATE_NO_MIDI_OUT = false;

    private boolean outputAvailable;
    private boolean inputAvailable;

    private final transient Logger log = Logger.getLogger(getClass());

    private MidiDevice.Info[] outputMidiDeviceInfo;
    private MidiDevice.Info[] inputMidiDeviceInfo;
    private String[] outputNames; // wirski@op.pl
    private String[] inputNames; // wirski@op.pl

    public MidiSettings() throws BackingStoreException {
        prefences = Preferences.userNodeForPackage(JSynthLib.class);
        prefences.sync();
        listeners = new ArrayList<PropertyChangeListener>();
        inputAvailable = true;
        outputAvailable = true;
        try {
            this.outputMidiDeviceInfo = getMidiDeviceInfoArray(false);
            if (outputMidiDeviceInfo.length == 0) {
                outputAvailable = false;
            }

            this.inputMidiDeviceInfo = getMidiDeviceInfoArray(true);
            if (inputMidiDeviceInfo.length == 0) {
                inputAvailable = false;
            }
        } catch (MidiUnavailableException e) {
            log.warn(e.getMessage(), e);
            outputAvailable = false;
            inputAvailable = false;
        }
        createNames(); // wirski@op.pl
    }

    /**
     * Parses all MIDI in/out devices and returns their Info object in an array.
     */
    final MidiDevice.Info[] getMidiDeviceInfoArray(boolean input)
            throws MidiUnavailableException {
        List<Info> list = new ArrayList<Info>();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
            if (!(device instanceof Synthesizer)
                    && !(device instanceof Sequencer)) {
                if (input && device.getMaxTransmitters() != 0
                        && !EMULATE_NO_MIDI_IN) {
                    if (isMac()) {
                        if ("com.github.osxmidi4j.CoreMidiDeviceInfo"
                                .equals(infos[i].getClass().getName())) {
                            list.add(infos[i]);
                        }
                    } else {
                        list.add(infos[i]);
                    }
                } else if (!input && device.getMaxReceivers() != 0
                        && !EMULATE_NO_MIDI_OUT) {
                    if (isMac()) {
                        if ("com.github.osxmidi4j.CoreMidiDeviceInfo"
                                .equals(infos[i].getClass().getName())) {
                            list.add(infos[i]);
                        }
                    } else {
                        list.add(infos[i]);
                    }
                }
            }
        }

        return list.toArray(new MidiDevice.Info[0]);
    }

    final void makeNamesUnique(String[] iNames) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < iNames.length; i++) {
            Integer val = map.get(iNames[i]);
            if (val == null) {
                val = -i;
            } else {
                if (val < 1) { // first duplicate
                    iNames[-val] += ":1";
                    val = 1;
                }
                iNames[i] += ":" + (++val);
            }
            map.put(iNames[i], val);
        }
    }

    /**
     * Fills outputNames and inputNames, with the names of devices made only
     * from their names. Description often contains "no details available" and
     * vendor often contains "unknown vendor"
     */
    final void createNames() { // wirski@op.pl
        outputNames = new String[outputMidiDeviceInfo.length];
        for (int i = 0; i < outputMidiDeviceInfo.length; i++) {
            outputNames[i] = outputMidiDeviceInfo[i].getName();
        }
        makeNamesUnique(outputNames);

        inputNames = new String[inputMidiDeviceInfo.length];
        for (int i = 0; i < inputMidiDeviceInfo.length; i++) {
            inputNames[i] = inputMidiDeviceInfo[i].getName();
        }
        makeNamesUnique(inputNames);
    }

    public String getInputName(int port) { // wirski@op.pl
        return inputNames[port];
    }

    public String getOutputName(int port) { // wirski@op.pl
        return outputNames[port];
    }

    public int getOutPort(String info) {
        for (int i = 0; i < outputMidiDeviceInfo.length; i++) {
            if (outputNames[i].equals(info)) { // wirski@op.pl
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find out port named " + info);
    }

    public int getInPort(String info) {
        for (int i = 0; i < inputMidiDeviceInfo.length; i++) {
            if (inputNames[i] == info) { // wirski@op.pl
                return i;
            }
        }
        throw new IllegalArgumentException("Could not find out port named " + info);
    }

    public boolean isOutputAvailable() {
        return outputAvailable;
    }

    public void setOutputAvailable(boolean outputAvailable) {
        this.outputAvailable = outputAvailable;
    }

    public boolean isInputAvailable() {
        return inputAvailable;
    }

    public void setInputAvailable(boolean inputAvailable) {
        this.inputAvailable = inputAvailable;
    }

    /**
     * return an array of MidiDevice.Info for MIDI output.
     * @see #getOutputMidiDeviceInfo(int)
     */
    public MidiDevice.Info[] getOutputMidiDeviceInfo() {
        return outputMidiDeviceInfo;
    }

    public MidiDevice.Info[] getInputMidiDeviceInfo() {
        return inputMidiDeviceInfo;
    }

    public MidiDevice.Info getOutputMidiDeviceInfo(int i) {
        return outputMidiDeviceInfo[i];
    }

    public MidiDevice.Info getInputMidiDeviceInfo(int i) {
        return inputMidiDeviceInfo[i];
    }

    boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return (os.indexOf("mac") >= 0);
    }

    public String[] getOutputNames() {
        return outputNames;
    }

    public void setOutputNames(String[] outputNames) {
        this.outputNames = outputNames;
    }

    public String[] getInputNames() {
        return inputNames;
    }

    public void setInputNames(String[] inputNames) {
        this.inputNames = inputNames;
    }

    public boolean isTestShortMessage() {
        return testShortMessage;
    }

    public boolean isTestSysexMessage() {
        return testSysexMessage;
    }

    /**
     * Getter for midiEnable. Returns false if either MIDI input nor output is
     * not available.
     */
    public boolean getMidiEnable() {
        return ((outputAvailable || inputAvailable) && prefences.getBoolean(
                "midiEnable", false));
    }

    /** Setter for midiEnable */
    public void setMidiEnable(boolean midiEnable) {
        prefences.putBoolean("midiEnable", midiEnable);
    }

    // ff: store the midi in/out devices as a string, in case the order of
    // devices
    // changes between loads.

    /** Getter for initPortIn */
    public int getInitPortIn() {
        String uniqueName = prefences.get("initPortIn", "");
        for (int i = 0; i < inputNames.length; ++i) {
            if (inputNames[i].equals(uniqueName)) {
                return i;
            }
        }
        return 0;
    }

    /** Setter for initPortIn */
    public void setInitPortIn(int initPortIn) {
        if (initPortIn >= 0) {
            prefences.put("initPortIn", inputNames[initPortIn]);
        } else {
            log.warn("Tried to set Midi in port " + initPortIn);
        }
    }

    /** Getter for initPortOut */
    public int getInitPortOut() {
        String uniqueName = prefences.get("initPortOut", "");

        for (int i = 0; i < outputNames.length; ++i) {
            if (outputNames[i].equals(uniqueName)) {
                return i;
            }
        }
        return 0;
    }

    /** Setter for initPortOut */
    public void setInitPortOut(int initPortOut) {
        if (initPortOut < 0) {
            initPortOut = 0;
        }

        prefences.put("initPortOut", outputNames[initPortOut]);
    }

    /**
     * Getter for masterInEnable. Returns false if either MIDI input or output
     * is unavailable.
     */
    public boolean getMasterInEnable() {
        return (outputAvailable && inputAvailable && getMidiEnable() && prefences
                .getBoolean("masterInEnable", false));
    }

    /** Setter for masterInEnable */
    public void setMasterInEnable(boolean masterInEnable) {
        boolean oldValue = prefences.getBoolean("masterInEnable", false);
        prefences.putBoolean("masterInEnable", masterInEnable);
        notifyListeners("masterInEnable", Boolean.toString(oldValue),
                Boolean.toString(masterInEnable));
    }

    /** Getter for masterController */
    public int getMasterController() {
        return prefences.getInt("masterController", 0);
    }

    /** Setter for masterController */
    public void setMasterController(int masterController) {
        prefences.putInt("masterController", masterController);
    }

    /** Getter for MIDI Output Buffer size. */
    public int getMidiOutBufSize() {
        return prefences.getInt("midiOutBufSize", 0);
    }

    /** Setter for MIDI Output Buffer size. */
    public void setMidiOutBufSize(int size) {
        prefences.putInt("midiOutBufSize", size);
    }

    /** Getter for MIDI Output delay time (msec). */
    public int getMidiOutDelay() {
        return prefences.getInt("midiOutDelay", 0);
    }

    /** Setter for MIDI Output delay time (msec). */
    public void setMidiOutDelay(int msec) {
        prefences.putInt("midiOutDelay", msec);
    }

    void notifyListeners(String property, String oldValue, String newValue) {
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(new PropertyChangeEvent(this, property,
                    oldValue, newValue));
        }
    }

    public void addChangeListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    public MidiService getMidiService() {
        return midiService;
    }

    @Inject
    public void setMidiService(MidiService midiService) {
        this.midiService = midiService;
    }

    /** Getter for Multiple MIDI Interface enable */
    public boolean getMultiMIDI() {
        return prefences.getBoolean("multiMIDI", false);
    }

    /** Setter for midiEnable */
    public void setMultiMIDI(boolean enable) {
        prefences.putBoolean("multiMIDI", enable);
    }
}
