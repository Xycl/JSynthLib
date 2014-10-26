package org.jsynthlib.device.model;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.SysexMessage;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * This is an interface for Device.driverList. All of drivers (single driver and
 * bank driver) implement this.
 * @author ribrdb
 * @version $Id: IDriver.java 747 2004-10-09 04:14:28Z hayashi $
 * @see IPatchDriver
 * @see IConverter
 */
public interface IDriver {
    /**
     * return type of patch which the driver handles. eg. "Single", "Bank",
     * "Drumkit", "Converter", etc.
     */
    String getPatchType();

    /** return the names of the authors of this driver. */
    String getAuthors();

    /** Set <code>Device</code> with which this driver go. */
    void setDevice(Device device);

    /** Return <code>Device</code> with which this driver go.. */
    Device getDevice();

    /**
     * Compares the header & size of a Patch to this driver to see if this
     * driver is the correct one to support the patch.
     * @param patchString
     *            the result of {@link Patch#getPatchHeader()
     *            Patch.getPatchHeader()}.
     * @param sysex
     *            a byte array of sysex message
     * @return <code>true</code> if this driver supports the Patch.
     */
    boolean supportsPatch(String patchString, byte[] sysex);

    /**
     * Returns whether this driver is a Single Driver.
     */
    boolean isSingleDriver();

    /**
     * Returns whether this driver is a Bank Driver.
     */
    boolean isBankDriver();

    /**
     * Returns whether this driver is a Converter. Equivalent with
     * <code>instanceof IConverter</code>. Note that there can be a Driver which
     * are both Single Driver and Converter.
     * @see IConverter
     */
    boolean isConverter();

    /**
     * This method trims a patch, containing more than one real patch to a
     * correct size. Useful for files containg more than one bank for example.
     * Some drivers are incompatible with this method so it reqires explicit
     * activation with the trimSize variable.
     * @param patch
     *            the patch, which should be trimmed to the right size
     * @return the size of the (modified) patch
     * @see #fixPatch(Patch, String)
     * @see IPatchDriver#createPatches(SysexMessage[])
     */
    int trimSysex(Patch patch);

    /**
     * Gets the name of the patch from the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver.
     * @see Patch#getName()
     */
    String getPatchName(Patch patch);

    /**
     * Set the name of the patch in the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver.
     * @see Patch#setName(String)
     */
    void setPatchName(Patch patch, String s);

    /**
     * @see Patch#hasEditor()
     */
    boolean hasEditor();

    /**
     * Override this if your driver implement Patch Editor. Don't override this
     * otherwise.
     * @see Patch#edit()
     */
    JSLFrame editPatch(Patch patch);

    /**
     * Calculate check sum of a <code>Patch</code>.
     * <p>
     * Need to be overridden if a patch is consist from multiple SysEX messages.
     * @param p
     *            a <code>Patch</code> value
     */
    void calculateChecksum(Patch patch);

    /**
     * Sends a patch to a set location on a synth.
     * <p>
     * Override this if required.
     * @see Patch#send(int, int)
     */
    void storePatch(Patch patch, int bankNum, int patchNum);

    /** Getter of patchNameSize. */
    int getPatchNameSize();

    /**
     * Play note. plays a MIDI file or a single note depending which preference
     * is set. Currently the MIDI sequencer support isn't implemented!
     * @see Patch#play()
     * @see ISinglePatch#play()
     */
    void playPatch(Patch patch);

    /**
     * Sends a patch to the synth's edit buffer.
     * <p>
     * Override this in the subclass if parameters or warnings need to be sent
     * to the user (aka if the particular synth does not have a edit buffer or
     * it is not MIDI accessable).
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    void sendPatch(Patch patch);

    /**
     * Send a MidiMessage to the MIDI outport for this driver.
     */
    void send(MidiMessage msg);

    /**
     * Returns String[] returns full list of patchNumbers.
     * @see DriverUtil#generateNumbers
     */
    String[] getPatchNumbers();

    /**
     * Return the size of the patch which the driver handles.
     * <code>SysexGetDialog</code> uses this to estimate timeout value.
     * @see SysexGetDialog
     */
    int getPatchSize();

    /**
     * Returns String[] returns full list of bankNumbers.
     * @see DriverUtil#generateNumbers
     */
    String[] getBankNumbers();

    /**
     * Create an array of patches from an array of SysexMessage for the driver.
     * Returns an array of patches because Converter may be used. This is used
     * for SysexMessages received by using
     * <code>requestPatchDump(int, int)</code>.
     * @param msgs
     *            an array of SysexMessage.
     * @return an array of <code>IPatch</code> value.
     * @see #requestPatchDump(int, int)
     * @see SysexGetDialog
     */
    Patch[] createPatches(SysexMessage[] msgs);

    /**
     * Request the synth to send a patch dump.
     */
    void requestPatchDump(int bankNum, int patchNum);

    /**
     * Create a patch from a byte array for the driver. This must be called only
     * when <code>IDriver.supportsPatch()</code> returns <code>true</code>.
     * @param sysex
     *            a byte array of sysex data.
     * @return a array of <code>IPatch</code> object.
     * @see IDriver#supportsPatch(String, byte[])
     * @see DriverUtil#createPatch(byte[])
     */
    Patch createPatch(byte[] sysex);

    /**
     * Check if this driver supports (implements createPatch()) creating a new
     * patch.
     * @see #createPatch()
     */
    boolean canCreatePatch();

    /**
     * Returns full name for referring to this Driver. Used for labels by driver
     * selection comboboxes.
     */
    @Override
    String toString();

    /**
     * Create a new Patch for this driver.
     */
    Patch createPatch();

    /** Return MIDI channel number. */
    int getChannel();

    /**
     * Calculate check sum of a byte array <code>sysex</code>.
     * <p>
     * The checksum calculation method of this method is used by Roland, YAMAHA,
     * etc.</p>
     * @param sysex
     *            a byte array
     * @param start
     *            start offset
     * @param end
     *            end offset
     * @param ofs
     *            offset of the checksum data
     * @see AbstractPatchDriver#calculateChecksum(Patch)
     */
    void calculateChecksum(byte[] sysex, int start, int end, int ofs);


    /** Return MIDI devide ID. */
    int getDeviceID();

    String getSysexID();

    void setSysexID(String sysexID);

    int getDeviceIDoffset();

    void setDeviceIDoffset(int deviceIDoffset);

    SysexHandler getSysexRequestDump();

    void setSysexRequestDump(SysexHandler sysexRequestDump);

    int getChecksumOffset();

    void setChecksumOffset(int checksumOffset);

    int getChecksumStart();

    void setChecksumStart(int checksumStart);

    int getChecksumEnd();

    void setChecksumEnd(int checksumEnd);

    int getPatchNameStart();

    void setPatchNameStart(int patchNameStart);

    int getTrimSize();

    void setTrimSize(int trimSize);

    void setPatchSize(int patchSize);

    void setPatchNumbers(String[] patchNumbers);

    void setBankNumbers(String[] bankNumbers);

    void setPatchNameSize(int patchNameSize);

}
