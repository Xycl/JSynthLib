package org.jsynthlib.device.model;


/**
 * Interface for methods for both single drivers and bank drivers.
 * @author ribrdb
 * @version $Id: IPatchDriver.java 995 2005-03-17 19:59:31Z ribrdb $
 * @see IDriver
 */
public interface IPatchDriver extends IDriver {

    /**
     * Returns String[] list of patch numbers for writable patches. This can be
     * overridden if some patch locations are read only. e.g. the Waldorf Pulse
     * has 100 patches, but only 0 to 39 are writable. Currently writable
     * patches are assumed to start at patch location 0. (This has nothing to
     * with the "Storable" class in JSynthLib.)
     * @see DriverUtil#generateNumbers
     */
    String[] getPatchNumbersForStore();
}
