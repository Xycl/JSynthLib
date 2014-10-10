package org.jsynthlib.patch.model.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.inject.Inject;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.SysexMessage;
import org.apache.log4j.Logger;
import org.jsynthlib.core.LookupManufacturer;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.DeviceManager;
import org.jsynthlib.device.model.DriverIdentifier;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.midi.service.MidiService;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.ISinglePatch;

/**
 * A class for MIDI System Exclusive Message patch data.
 * <p>
 * There are many kinds of constructors. Driver can use one of the follows (in
 * preferred order).
 * <ol>
 * <li><code>Patch(byte[], Driver)</code>
 * <li><code>Patch(byte[], Device)</code>
 * <li><code>Patch(byte[])</code>
 * </ol>
 * Use <code>Patch(byte[], Driver)</code> form if possible. The latter two
 * constructors <b>guesses </b> the proper driver by using the
 * <code>Driver.supportsPatch</code> method. It is not efficient.
 * <p>
 * Use <code>Patch(byte[])</code> only when you have no idea about either Driver
 * or Device for which your Patch is. If you know that the patch you are
 * creating does not correspond to any driver, use
 * <code>Patch(byte[], (Driver) null)</code>, since it is much more efficient
 * than <code>Patch(byte[])</code>.
 * @author ???
 * @version $Id: Patch.java 951 2005-03-06 05:05:32Z hayashi $
 * @see AbstractPatchDriver#supportsPatch
 */
public class Patch implements ISinglePatch {
    private final transient Logger log = Logger.getLogger(getClass());
    /** Driver for this Patch. */
    private transient IDriver driver;

    private transient MidiService midiService;

    private transient DriverIdentifier driverIdentifier;

    private transient DeviceManager deviceManager;

    /**
     * MIDI System Exclusive Message byte array.
     */
    public byte[] sysex;

    // 'String' is better. But 'StringBuffer' is used to keep
    // the compatibility for serialized files
    /** "Field 1" comment. */
    private StringBuffer date;

    /** "Field 2" comment. */
    private StringBuffer author;

    /** "Comment" comment. */
    private StringBuffer comment;

    // This is used by java to maintain backwards compatibility.
    private static final long serialVersionUID = 1L;

    public Patch() {
        date = new StringBuffer();
        author = new StringBuffer();
        comment = new StringBuffer();
    }

    // IPatch interface methods
    @Override
    public final String getDate() {
        return date.toString();
    }

    @Override
    public final void setDate(String date) {
        this.date = new StringBuffer(date);
    }

    @Override
    public final String getAuthor() {
        return author.toString();
    }

    @Override
    public final void setAuthor(String author) {
        this.author = new StringBuffer(author);
    }

    @Override
    public final String getComment() {
        return comment.toString();
    }

    @Override
    public final void setComment(String comment) {
        this.comment = new StringBuffer(comment);
    }

    @Override
    public final Device getDevice() {
        return driver.getDevice();
    }

    @Override
    public IDriver getDriver() {
        return driver;
    }

    @Override
    public void setDriver(IDriver driver) {
        this.driver = driver;
        if (driver == null) {
            this.driver = deviceManager.getNullDriver();
        }
    }

    @Override
    public final void setDriver() {
        setDriver(driverIdentifier.chooseDriver(sysex));
    }

    @Override
    public final boolean hasNullDriver() {
        return driver == deviceManager.getNullDriver();
    }

    @Override
    public String getPatchHeader() {
        return driverIdentifier.getPatchHeader(sysex);
    }

    @Override
    public final String getName() {
        return driver.getPatchName(this);
    }

    @Override
    public final void setName(String s) {
        driver.setPatchName(this, s);
    }

    @Override
    public final boolean hasEditor() {
        return driver.hasEditor();
    }

    @Override
    public final JSLFrame edit() {
        return driver.editPatch(this);
    }

    @Override
    public final void send(int bankNum, int patchNum) {
        driver.calculateChecksum(this);
        driver.storePatch(this, bankNum, patchNum);
    }

    @Override
    public final SysexMessage[] getMessages() {
        try {
            return midiService.byteArrayToSysexMessages(sysex);
        } catch (InvalidMidiDataException ex) {
            return null;
        }
    }

    @Override
    public final byte[] export() {
        driver.calculateChecksum(this);
        return this.sysex;
    }

    @Override
    public final byte[] getByteArray() {
        return sysex;
    }

    @Override
    public int getSize() {
        return sysex.length;
    }

    @Override
    public String getType() {
        return driver.getPatchType();
    }

    @Override
    public int getNameSize() {
        return driver.getPatchNameSize();
    }

    @Override
    public final String lookupManufacturer() {
        return LookupManufacturer.get(sysex[1], sysex[2], sysex[3]);
    }

    @Override
    public final boolean isSinglePatch() {
        return driver.isSingleDriver();
    }

    @Override
    public final boolean isBankPatch() {
        return driver.isBankDriver();
    }

    @Override
    public void useSysexFromPatch(IPatch ip) {
        if (ip.getSize() != sysex.length) {
            throw new IllegalArgumentException();
        }
        sysex = ip.getByteArray();
    }

    // end of IPatch interface methods

    // ISinglePatch interface methods
    @Override
    public final void play() {
        driver.playPatch(this);
    }

    @Override
    public final void send() {
        driver.calculateChecksum(this);
        driver.sendPatch(this);
    }

    // end of ISinglePatch interface methods

    // Transferable interface methods

    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.match(PatchTransferHandler.PATCH_FLAVOR)) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        log.info("Patch.isDataFlavorSupported " + flavor);
        return flavor.match(PatchTransferHandler.PATCH_FLAVOR);
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
            PatchTransferHandler.PATCH_FLAVOR };
    }

    // end of Transferable interface methods

    // Clone interface method
    @Override
    public final Object clone() {
        try {
            Patch p = (Patch) super.clone();
            p.sysex = sysex.clone();
            return p;
        } catch (CloneNotSupportedException e) {
            // Cannot happen -- we support clone, and so do arrays
            throw new InternalError(e.toString());
        }
    }

    // end of Clone interface method

    //
    // delegation methods
    //
    public final void calculateChecksum() {
        driver.calculateChecksum(this);
    }

    /**
     * Dump byte data array. Only for debugging.
     * @return string like "[2,3] f0 a3 00"
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[" + driver + "] "
                + MidiMessageFormatter.hexDumpOneLine(sysex, 0, -1, 20));
        return buf.toString();
    }

    protected DriverIdentifier getDriverIdentifier() {
        return driverIdentifier;
    }

    public MidiService getMidiService() {
        return midiService;
    }

    @Inject
    public void setMidiService(MidiService midiService) {
        this.midiService = midiService;
    }

    @Inject
    public void setDriverIdentifier(DriverIdentifier driverIdentifier) {
        this.driverIdentifier = driverIdentifier;
    }

    public DeviceManager getDeviceManager() {
        return deviceManager;
    }

    @Inject
    public void setDeviceManager(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }
}
