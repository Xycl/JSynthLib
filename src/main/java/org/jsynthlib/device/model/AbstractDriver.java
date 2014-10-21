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
package org.jsynthlib.device.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jsynthlib.core.AppConfig;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.midi.service.MidiService;
import org.jsynthlib.patch.model.MultiPatchImporter;
import org.jsynthlib.patch.model.PatchFactory;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchEdit;

/**
 * @author Pascal Collberg
 */
public abstract class AbstractDriver implements IDriver {

    protected final transient Logger log = Logger.getLogger(getClass());

    private final PatchFactory patchFactory;
    /**
     * Which device does this driver go with?
     */
    private Device device;

    /**
     * The patch type. eg. "Single", "Bank", "Drumkit", etc.
     */
    private final String patchType;

    /**
     * The names of the authors of this driver.
     */
    private final String authors;

    // for default supportsPatch method
    /**
     * The size of the patch this Driver supports (or 0 for variable).
     * @see #supportsPatch
     */
    protected int patchSize;
    /**
     * The hex header that sysex files of the format this driver supports will
     * have. The program will attempt to match loaded sysex drivers with the
     * sysexID of a loaded driver. It can be up to 16 bytes and have wildcards (
     * <code>*</code>). (ex. <code>"F041**003F12"</code>)
     * @see #supportsPatch
     */
    protected String sysexID;

    /**
     * Array holding names/numbers for all patches. Used for comboBox selection.
     * @see #getPatchNumbers
     * @see #getPatchNumbersForStore
     * @see DriverUtil#generateNumbers
     */
    protected String[] patchNumbers;
    /**
     * Array holding names or numbers for all banks. Used for comboBox
     * selection.
     * @see #getBankNumbers
     * @see DriverUtil#generateNumbers
     */
    protected String[] bankNumbers;

    // for sendPatchWorker method
    /**
     * Offset of deviceID in sysex. Used by <code>sendPatchWorker</code> method.
     * @see #sendPatchWorker
     */
    protected int deviceIDoffset; // array index of device ID

    /**
     * SysexHandler object to request dump. You don't have to use this field if
     * you override <code>requestPatchDump</code> method.
     * @see #requestPatchDump
     * @see SysexHandler
     */
    // - phil@muqus.com
    protected SysexHandler sysexRequestDump = null;

    // for default calculateCheckSum(Patch) method
    /**
     * Offset of checksum byte.
     * <p>
     * Need to be set if default <code>calculateChecksum(Patch)</code> method is
     * used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumOffset;
    /**
     * Start of range that Checksum covers.
     * <p>
     * Need to be set if default <code>calculateChecksum(Patch)</code> method is
     * used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumStart;
    /**
     * End of range that Checksum covers.
     * <p>
     * Need to be set if default <code>calculateChecksum(Patch)</code> method is
     * used.
     * @see #calculateChecksum(Patch)
     */
    protected int checksumEnd;

    /*
     * The following fields are used by default methods defined in this file. If
     * your extending driver can use a default method as is, set the
     * corresponding fields. Otherwise override the method.
     */
    // for default set/getPatchName methods
    /**
     * The offset in the patch where the patchname starts. '0' if patch is not
     * named -- remember all offsets are zero based.
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameStart;
    /**
     * Number of characters in the patch name. (0 if no name)
     * @see #setPatchName
     * @see #getPatchName
     */
    protected int patchNameSize;

    // for default trimSysex method
    /**
     * The size of the patch for trimming purposes.
     * @see #trimSysex
     */
    protected int trimSize = 0;

    /** Number of sysex messages in patch dump. Not used now. */
    protected int numSysexMsgs;

    private final MidiService midiService;

    private final MultiPatchImporter patchImporter;

    private final AppConfig appConfig;

    private final DeviceManager deviceManager;

    /**
     * Creates a new <code>Driver</code> instance.
     * @param patchType
     *            The patch type. eg. "Single", "Bank", "Drumkit", etc.
     * @param authors
     *            The names of the authors of this driver.
     */
    public AbstractDriver(String patchType, String authors) {
        this.patchType = patchType;
        this.authors = authors;

        patchFactory = JSynthLibInjector.getInstance(PatchFactory.class);
        midiService = JSynthLibInjector.getInstance(MidiService.class);
        patchImporter = JSynthLibInjector.getInstance(MultiPatchImporter.class);
        appConfig = JSynthLibInjector.getInstance(AppConfig.class);
        deviceManager = JSynthLibInjector.getInstance(DeviceManager.class);
    }

    @Override
    public final String getPatchType() {
        return patchType;
    }

    @Override
    public final String getAuthors() {
        return authors;
    }

    @Override
    public void setDevice(Device d) {
        device = d;
    }

    @Override
    public final Device getDevice() {
        return device;
    }

    /**
     * Compares the header & size of a Patch to this driver to see if this
     * driver is the correct one to support the patch.
     * @param patchString
     *            the result of <code>p.getPatchHeader()</code>.
     * @param sysex
     *            a byte array of sysex message
     * @return <code>true</code> if this driver supports the Patch.
     * @see #patchSize
     * @see #sysexID
     */
    @Override
    public boolean supportsPatch(String patchString, byte[] sysex) {
        // check the length of Patch
        if ((patchSize != sysex.length) && (patchSize != 0)) {
            return false;
        }

        if (sysexID == null || patchString.length() < sysexID.length()) {
            return false;
        }

        StringBuffer compareString = new StringBuffer();
        for (int i = 0; i < sysexID.length(); i++) {
            switch (sysexID.charAt(i)) {
            case '*':
                compareString.append(patchString.charAt(i));
                break;
            default:
                compareString.append(sysexID.charAt(i));
            }
        }
        log.debug(toString());
        log.debug("Comp.String: " + compareString);
        log.debug("PatchString: " + patchString);
        return (compareString.toString().equalsIgnoreCase(patchString
                .substring(0, sysexID.length())));
    }

    @Override
    public int getPatchSize() {
        return patchSize;
    }

    @Override
    public String[] getPatchNumbers() {
        return patchNumbers;
    }

    @Override
    public String[] getBankNumbers() {
        return bankNumbers;
    }

    /**
     * Sends a patch to the synth's edit buffer.
     * <p>
     * Override this in the subclass if parameters or warnings need to be sent
     * to the user (aka if the particular synth does not have a edit buffer or
     * it is not MIDI accessable).
     * @see Patch#send()
     * @see ISinglePatch#send()
     */
    @Override
    public void sendPatch(Patch p) {
        sendPatchWorker(p);
    }

    /**
     * Set Device ID and send the sysex data to MIDI output.
     * @see #sendPatch(Patch)
     */
    protected final void sendPatchWorker(Patch p) {
        if (deviceIDoffset > 0) {
            p.sysex[deviceIDoffset] = (byte) (getDeviceID() - 1);
        }

        send(p.sysex);
    }

    /**
     * Send Sysex byte array data to MIDI outport.
     * @param sysex
     *            a byte array of Sysex data. If it has checksum, the checksum
     *            must be calculated before calling this method.
     */
    public final void send(byte[] sysex) {
        try {
            SysexMessage[] a = midiService.byteArrayToSysexMessages(sysex);
            for (int i = 0; i < a.length; i++) {
                getDevice().send(a[i]);
            }
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Play note. plays a MIDI file or a single note depending which preference
     * is set. Currently the MIDI sequencer support isn't implemented!
     * @see Patch#play()
     * @see ISinglePatch#play()
     */
    @Override
    public void playPatch(Patch p) {
        if (appConfig.getSequencerEnable()) {
            playSequence();
        } else {
            playNote();
        }
    }

    private void playNote() {
        try {
            // sendPatch(p);
            Thread.sleep(100);
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
                    appConfig.getNote(), appConfig.getVelocity());
            send(msg);

            Thread.sleep(appConfig.getDelay());

            msg.setMessage(ShortMessage.NOTE_ON, getChannel() - 1,
                    appConfig.getNote(), 0); // expecting running status
            send(msg);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    // MIDI in/out mothods to encapsulate lower MIDI layer
    @Override
    public final void send(MidiMessage msg) {
        getDevice().send(msg);
    }

    private void playSequence() {
        midiService.startSequencer(getDevice().getOutPortName());
    }

    @Override
    public Patch[] createPatches(SysexMessage[] msgs) {
        byte[] sysex = midiService.sysexMessagesToByteArray(msgs);
        List<Patch> patarray = patchImporter.createPatches(sysex, getDevice());

        // Maybe you don't get the expected patch!
        // Check all devices/drivers again! Call fixpatch() if supportsPatch
        // returns false.
        // XXX Why don't we simply cause error? Hiroo
        for (int k = 0; k < patarray.size(); k++) {
            Patch pk = patarray.get(k);
            String patchString = pk.getPatchHeader();
            if (!(pk.getDriver().supportsPatch(patchString, pk.getByteArray()))) {
                patarray.set(k, fixPatch(pk, patchString));
            }
        }
        return patarray.toArray(new Patch[patarray.size()]);
    }

    @Override
    public Patch createPatch(byte[] sysex) {
        return patchFactory.createNewPatch(sysex, this);
    }

    /**
     * Look for a proper driver and trim the patch.
     * @see #createPatches(SysexMessage[])
     * @see IPatchDriver#createPatches(SysexMessage[])
     */
    private Patch fixPatch(Patch pk, String patchString) {
        byte[] sysex = pk.getByteArray();
        for (int i = 0; i < deviceManager.deviceCount(); i++) {
            // first check the device for the patch requested.
            // then starting index '1'. (index 0 is 'generic driver')
            Device fixPatchDevice = null;
            if (i == 0) {
                fixPatchDevice = pk.getDevice();
            } else {
                fixPatchDevice = deviceManager.getDevice(i);
            }

            for (int j = 0; j < fixPatchDevice.driverCount(); j++) {
                IDriver d = fixPatchDevice.getDriver(j);
                if (d instanceof AbstractPatchDriver
                        && d.supportsPatch(patchString, sysex)) {
                    // driver found
                    AbstractPatchDriver driver = (AbstractPatchDriver) d;
                    pk.setDriver(driver);
                    driver.trimSysex(pk);
                    JOptionPane
                            .showMessageDialog(null, "You requested a "
                                    + driver.toString() + " patch!"
                                    + "\nBut you got a "
                                    + pk.getDriver().toString() + " patch.",
                                    "Warning", JOptionPane.WARNING_MESSAGE);
                    return pk;
                }
            } // end of driver (j) loop
        } // end of device (i) loop

        // driver not found
        pk.setDriver(null); // reset
        pk.setComment("Probably a " + pk.lookupManufacturer()
                + " Patch, Size: " + pk.getByteArray().length);
        JOptionPane.showMessageDialog(
                null,
                "You requested a " + this.toString() + " patch!"
                        + "\nBut you got a not supported patch!\n"
                        + pk.getComment(), "Warning",
                JOptionPane.WARNING_MESSAGE);
        return pk;
    }

    //
    // Driver class utility methods
    //
    /** Return the name of manufacturer of synth. */
    protected final String getManufacturerName() {
        return getDevice().getManufacturerName();
    }

    /** Return the name of model of synth. */
    protected final String getModelName() {
        return getDevice().getModelName();
    }

    /** Return the personal name of the synth. */
    protected final String getSynthName() {
        return getDevice().getSynthName();
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getDeviceID()
     */
    @Override
    public final int getDeviceID() {
        if (device == null) {
            return 0;
        } else {
            return getDevice().getDeviceID();
        }
    }

    /** Return MIDI channel number. */
    @Override
    public final int getChannel() {
        if (device == null) {
            return 0;
        } else {
            return getDevice().getChannel();
        }
    }

    @Override
    public int getPatchNameSize() {
        return patchNameSize;
    }

    /**
     * Calculate check sum of a <code>Patch</code>.
     * <p>
     * Need to be overridden if a patch is consist from multiple SysEX messages.
     * @param p
     *            a <code>Patch</code> value
     */
    @Override
    public void calculateChecksum(Patch p) {
        calculateChecksum(p, checksumStart, checksumEnd, checksumOffset);
    }

    /**
     * Calculate check sum of a <code>Patch</code>.
     * <p>
     * This method is called by calculateChecksum(Patch). The checksum
     * calculation method of this method is used by Roland, YAMAHA, etc.
     * Override this for different checksum calculation method.
     * <p>
     * Compatibility Note: This method became 'static' method.
     * @param patch
     *            a <code>Patch</code> value
     * @param start
     *            start offset
     * @param end
     *            end offset
     * @param offset
     *            offset of the checksum data
     * @see #calculateChecksum(Patch)
     */
    protected final void calculateChecksum(Patch patch, int start, int end, int offset) {
        calculateChecksum(patch.sysex, start, end, offset);
    }

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
    @Override
    public int trimSysex(Patch patch) { // no driver overrides this now.
        if (trimSize > 0 && patch.sysex.length > trimSize
                && patch.sysex[trimSize - 1] == (byte) 0xf7) {
            byte[] sysex = new byte[trimSize];
            System.arraycopy(patch.sysex, 0, sysex, 0, trimSize);
            patch.sysex = sysex;
        }
        return patch.sysex.length; // == trimSize
    }

    /**
     * Send Control Change (Bank Select) MIDI message.
     * @see #storePatch(Patch, int, int)
     */
    protected void setBankNum(int bankNum) {
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1, 0x00, // Bank
                                                                                // Select
                                                                                // (MSB)
                    bankNum / 128); // Bank Number (MSB)
            send(msg);
            msg.setMessage(ShortMessage.CONTROL_CHANGE, getChannel() - 1, 0x20, // Bank
                                                                                // Select
                                                                                // (LSB)
                    bankNum % 128); // Bank Number (MSB)
            send(msg);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /**
     * Request the synth to send a patch dump. If <code>sysexRequestDump</code>
     * is not <code>null</code>, a request dump message is sent. Otherwise a
     * dialog window will prompt users.
     * @see IPatchDriver#requestPatchDump(int, int)
     * @see SysexHandler
     */
    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        // clearMidiInBuffer(); now done by SysexGetDialog.GetActionListener.
        setBankNum(bankNum);
        setPatchNum(patchNum);
        if (sysexRequestDump == null) {
            JOptionPane.showMessageDialog(PatchEdit.getInstance(), "The "
                    + toString()
                    + " driver does not support patch getting.\n\n"
                    + "Please start the patch dump manually...", "Get Patch",
                    JOptionPane.WARNING_MESSAGE);
        } else {
            send(sysexRequestDump.toSysexMessage(getDeviceID(),
                    new SysexHandler.NameValue("bankNum", bankNum),
                    new SysexHandler.NameValue("patchNum", patchNum)));
        }
    }

    /**
     * Send Program Change MIDI message.
     * @see #storePatch(Patch, int, int)
     */
    protected void setPatchNum(int patchNum) {
        try {
            ShortMessage msg = new ShortMessage();
            msg.setMessage(ShortMessage.PROGRAM_CHANGE, getChannel() - 1,
                    patchNum, 0); // Program Number
            send(msg);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
    }

    /** Send ShortMessage to MIDI outport. */
    public final void send(int status, int d1, int d2) {
        ShortMessage msg = new ShortMessage();
        try {
            msg.setMessage(status, d1, d2);
        } catch (InvalidMidiDataException e) {
            log.warn(e.getMessage(), e);
        }
        send(msg);
    }

    /** Send ShortMessage to MIDI outport. */
    public final void send(int status, int d1) {
        send(status, d1, 0);
    }

    /**
     * Check if this driver supports creating a new patch. By default it uses
     * reflection to test if the method createNewPatch() is overridden by the
     * subclass of Driver.
     */
    @Override
    public boolean canCreatePatch() {
        try {
            getClass().getDeclaredMethod("createNewPatch", (Class[]) null);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return getManufacturerName() + " " + getModelName() + " "
                + getPatchType();
    }

    @Override
    public final Patch createPatch() {
        return createNewPatch();
    }

    /**
     * Create a new Patch. Don't override this unless your driver properly
     * implement this method.
     * @see IPatchDriver#createPatch()
     * @see #createPatch()
     */
    protected Patch createNewPatch() { // overridden by subclass
        return null;
    }

    protected PatchFactory getPatchFactory() {
        return patchFactory;
    }

    /**
     * A utility method to generates an array of formatted numbers. For example,
     *
     * <pre>
     * patchNumbers = generateNumbers(1, 10, &quot;Patch 00&quot;);
     * </pre>
     *
     * setups the following array,
     *
     * <pre>
     *   {
     *     "Patch 01", "Patch 02", "Patch 03", "Patch 04", "Patch 05"
     *     "Patch 06", "Patch 07", "Patch 08", "Patch 09", "Patch 10"
     *   }
     * </pre>
     * @param min
     *            minumux value
     * @param max
     *            maximum value
     * @param format
     *            pattern String for java.text.DecimalFormat
     * @return an array of formatted numbers.
     * @see java.text.DecimalFormat
     * @see IPatchDriver#getPatchNumbers
     * @see IPatchDriver#getPatchNumbersForStore
     * @see IPatchDriver#getBankNumbers
     */
    protected String[] generateNumbers(int min, int max, String format) {
        String[] retval = new String[max - min + 1];
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance().clone();
        df.applyPattern(format);
        while (max >= min) {
            retval[max - min] = df.format(max--);
        }
        return retval;
    }

    @Override
    public void calculateChecksum(byte[] sysex, int start, int end, int ofs) {
        int sum = 0;
        if (end < 0) {
            end = sysex.length + end;
        }

        for (int i = start; i <= end; i++) {
            sum += sysex[i];
        }
        sysex[ofs] = (byte) (-sum & 0x7f);
        /*
         * Equivalent with above. p.sysex[ofs] = (byte) (sum & 0x7f);
         * p.sysex[ofs] = (byte) (p.sysex[ofs] ^ 0x7f); p.sysex[ofs] = (byte)
         * (p.sysex[ofs] + 1); p.sysex[ofs] = (byte) (p.sysex[ofs] & 0x7f);
         */
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getSysexID()
     */
    @Override
    public String getSysexID() {
        return sysexID;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setSysexID(java.lang.String)
     */
    @Override
    public void setSysexID(String sysexID) {
        this.sysexID = sysexID;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getDeviceIDoffset()
     */
    @Override
    public int getDeviceIDoffset() {
        return deviceIDoffset;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setDeviceIDoffset(int)
     */
    @Override
    public void setDeviceIDoffset(int deviceIDoffset) {
        this.deviceIDoffset = deviceIDoffset;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getSysexRequestDump()
     */
    @Override
    public SysexHandler getSysexRequestDump() {
        return sysexRequestDump;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setSysexRequestDump(org.jsynthlib.device.model.SysexHandler)
     */
    @Override
    public void setSysexRequestDump(SysexHandler sysexRequestDump) {
        this.sysexRequestDump = sysexRequestDump;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getChecksumOffset()
     */
    @Override
    public int getChecksumOffset() {
        return checksumOffset;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setChecksumOffset(int)
     */
    @Override
    public void setChecksumOffset(int checksumOffset) {
        this.checksumOffset = checksumOffset;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getChecksumStart()
     */
    @Override
    public int getChecksumStart() {
        return checksumStart;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setChecksumStart(int)
     */
    @Override
    public void setChecksumStart(int checksumStart) {
        this.checksumStart = checksumStart;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getChecksumEnd()
     */
    @Override
    public int getChecksumEnd() {
        return checksumEnd;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setChecksumEnd(int)
     */
    @Override
    public void setChecksumEnd(int checksumEnd) {
        this.checksumEnd = checksumEnd;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getPatchNameStart()
     */
    @Override
    public int getPatchNameStart() {
        return patchNameStart;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setPatchNameStart(int)
     */
    @Override
    public void setPatchNameStart(int patchNameStart) {
        this.patchNameStart = patchNameStart;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#getTrimSize()
     */
    @Override
    public int getTrimSize() {
        return trimSize;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setTrimSize(int)
     */
    @Override
    public void setTrimSize(int trimSize) {
        this.trimSize = trimSize;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setPatchSize(int)
     */
    @Override
    public void setPatchSize(int patchSize) {
        this.patchSize = patchSize;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setPatchNumbers(java.lang.String[])
     */
    @Override
    public void setPatchNumbers(String[] patchNumbers) {
        this.patchNumbers = patchNumbers;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setBankNumbers(java.lang.String[])
     */
    @Override
    public void setBankNumbers(String[] bankNumbers) {
        this.bankNumbers = bankNumbers;
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.device.model.IDriver#setPatchNameSize(int)
     */
    @Override
    public void setPatchNameSize(int patchNameSize) {
        this.patchNameSize = patchNameSize;
    }
}
