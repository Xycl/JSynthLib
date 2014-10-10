package org.jsynthlib.device.model;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * This is an implementation of ISingleDriver and the base class for single
 * drivers which use <code>Patch<IPatch>.<p>
 *
 * Compatibility Note: The following fields are now
 * <code>private</code>. Use setter/getter method to access them.
 *
 * <pre>
 *   device, patchType, authors
 * </pre>
 *
 * Compatibility Note: The following fields are now obsoleted. Use a getter
 * method to access them. The getter method queries parent Device object.
 *
 * <pre>
 *   deviceNum, driverNum,
 *   channel, port, inPort, manufacturer, model, inquiryID, id
 * </pre>
 *
 * Compatibility Note: SysexHandler.send(getPort(), sysex); or
 * PatchEdit.MidiOut.writeLongMessage(getPort(), sysex); was replaced by
 * send(sysex);
 * @author Brian Klock
 * @version $Id: Driver.java 1085 2007-10-01 23:47:28Z billzwicky $
 * @see Patch
 */
public abstract class AbstractPatchDriver extends AbstractDriver implements
        IPatchDriver {

    /**
     * Creates a new <code>Driver</code> instance.
     * @param patchType
     *            The patch type. eg. "Single", "Bank", "Drumkit", etc.
     * @param authors
     *            The names of the authors of this driver.
     */
    public AbstractPatchDriver(String patchType, String authors) {
        super(patchType, authors);
    }

    public AbstractPatchDriver(String authors) {
        this("Single", authors);
    }

    //
    // IDriver interface methods
    //

    // These are not 'final' because BankDriver and Converter class override
    // them.
    // Synth drivers should not override these.
    @Override
    public final boolean isSingleDriver() {
        return true;
    }

    @Override
    public final boolean isBankDriver() {
        return false;
    }

    @Override
    public boolean isConverter() {
        return false;
    }

    // end of IDriver interface methods
    //
    // IPatchDriver interface methods
    //

    @Override
    public String[] getPatchNumbersForStore() {
        // All patches assumed to be writable by default
        return patchNumbers;
    }

    // end of IPatchDriver interface methods
    //
    // mothods for Patch class
    //
    /**
     * Gets the name of the patch from the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver.
     * @see Patch#getName()
     */
    @Override
    public String getPatchName(Patch p) {
        if (patchNameSize == 0) {
            return ("-");
        }
        try {
            return new String(p.sysex, patchNameStart, patchNameSize,
                    "US-ASCII");
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    /**
     * Set the name of the patch in the sysex. If the patch uses some weird
     * format or encoding, this needs to be overidden in the particular driver.
     * @see Patch#setName(String)
     */
    @Override
    public void setPatchName(Patch p, String name) {
        if (patchNameSize == 0) {
            ErrorMsg.reportError("Error",
                    "The Driver for this patch does not support Patch Name Editing.");
            return;
        }

        while (name.length() < patchNameSize) {
            name = name + " ";
        }

        byte[] namebytes = new byte[patchNameSize];
        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < patchNameSize; i++) {
                p.sysex[patchNameStart + i] = namebytes[i];
            }
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    /**
     * Sends a patch to a set location on a synth.
     * <p>
     * Override this if required.
     * @see Patch#send(int, int)
     */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        setBankNum(bankNum);
        setPatchNum(patchNum);
        sendPatch(p);
    }

    /**
     * @see Patch#hasEditor()
     */
    @Override
    public boolean hasEditor() {
        try {
            getClass().getDeclaredMethod("editPatch", new Class[] {
                Patch.class });
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Override this if your driver implement Patch Editor. Don't override this
     * otherwise.
     * @see Patch#edit()
     */
    @Override
    public JSLFrame editPatch(Patch p) {
        ErrorMsg.reportError("Error",
                "The Driver for this patch does not support Patch Editing.");
        return null;
    }

    //
    // For debugging.
    //
    /**
     * Returns String .. full name for referring to this patch for debugging
     * purposes.
     */
    protected String getFullPatchName(Patch p) {
        return getManufacturerName() + " | " + getModelName() + " | "
                + p.getType() + " | " + getSynthName() + " | "
                + getPatchName(p);
    }
}
