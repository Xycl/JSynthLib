package org.jsynthlib.device.model;

import org.jsynthlib.patch.model.impl.Patch;

/**
 * This is an interface of a driver which simply converts a patch, which is
 * imported from a file or MIDI input, into it's associated with to another
 * format.
 * <p>
 * <code>Converter</code> class is an implementation of this interface. It is an
 * independent driver implementing IDriver. An implementation of ISingleDriver
 * (ex. Driver class) may also implement this interface.
 * @version $Id: IConverter.java 747 2004-10-09 04:14:28Z hayashi $
 * @see IDriver
 * @see Converter
 */
public interface IConverter {
    /**
     * Create an array of patches from a byte array of SysexMessage for the
     * driver. Used for a byte array read from a Sysex file.
     * @param sysex
     *            a byte array of SysexMessage.
     * @return an array of <code>IPatch</code> value.
     * @see DriverUtil#createPatches(byte[])
     * @see DriverUtil#createPatches(byte[], Device)
     */
    Patch[] createPatches(byte[] sysex);
}
