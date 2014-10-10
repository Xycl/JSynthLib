package org.jsynthlib.device.model;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.inject.JSynthLibInjector;
import org.jsynthlib.patch.model.IPatch;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * An implementation of IConverter interface for Patch class.
 * @author ???
 * @version $Id: Converter.java 738 2004-09-26 22:00:28Z hayashi $
 * @see IDriver
 * @see Device#addDriver(IDriver)
 */
public abstract class Converter extends AbstractDriver implements
        IConverter {
    private final DriverIdentifier driverIdentifier;

    public Converter(String patchType, String authors) {
        super(patchType, authors);
        driverIdentifier =
                JSynthLibInjector.getInstance(DriverIdentifier.class);
    }

    // public Converter() {
    // this("Converter", "JSynthLib"); // Who is the auther?
    // }

    // If extractPatch returns an array of Patches whose drivers are set
    // properly, override this by;
    // public IPatch[] createPatch(byte[] sysex) {
    // return extractPatch(new Patch(sysex, this));
    // }
    @Override
    public IPatch[] createPatches(byte[] sysex) {
        Patch patch = getPatchFactory().createNewPatch(sysex, this);
        Patch[] patarray = extractPatch(patch);
        if (patarray == null) {
            return new Patch[] {
                patch };
        }

        // Conversion was sucessfull, we have at least one
        // converted patch. Assign a proper driver to each patch of patarray
        Device dev = getDevice();
        for (int i = 0; i < patarray.length; i++) {
            byte[] d = patarray[i].sysex;
            patarray[i].setDriver(driverIdentifier.chooseDriver(d, dev));
        }
        return patarray;
    }

    /**
     * Convert a bulk patch into an array of single and/or bank patches.
     */
    public abstract Patch[] extractPatch(Patch p);

    @Override
    public final boolean isSingleDriver() {
        return false;
    }

    @Override
    public final boolean isBankDriver() {
        return false;
    }

    @Override
    public final boolean isConverter() {
        return true;
    }
    @Override
    public String getPatchName(Patch patch) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setPatchName(Patch patch, String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean hasEditor() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public JSLFrame editPatch(Patch patch) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storePatch(Patch patch, int bankNum, int patchNum) {
        // TODO Auto-generated method stub

    }

}
