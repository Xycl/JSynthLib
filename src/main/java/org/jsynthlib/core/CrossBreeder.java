package org.jsynthlib.core;

import java.util.List;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.viewcontroller.CrossBreedDialog;
import org.jsynthlib.patch.viewcontroller.PatchBasket;

/*
 * Generates a patch with random combinations of the patches for a
 * driver in a library.
 *
 * As of version 0.14 the actual functionality of the crossbreeder
 * dialog is hidden away in this file. It seems like a good idea to be
 * seperating functionality from GUI code, something I didn't do when
 * I first started JSynthLib.
 */
/**
 * @author bklock
 * @version $Id: CrossBreeder.java 913 2005-02-12 16:01:31Z hayashi $
 * @see CrossBreedDialog
 */
public class CrossBreeder {
    private final transient Logger log = Logger.getLogger(getClass());
    /** The patch we are working on. */
    private Patch patch;
    /** The patch library we are working on. */
    private List<Patch> lib;
    /** The number of patches in the patch library */
    private int libSize;

    public void generateNewPatch(PatchBasket library) {
        lib = library.getPatchCollection();
        libSize = lib.size();

        // get a base patch.
        Patch base = library.getSelectedPatch();
        log.info("base : " + base);
        int sysexSize = base.getSize();
        log.info("length : " + sysexSize);
        IDriver drv = base.getDriver();

        byte[] dsysex = new byte[sysexSize];
        dsysex[0] = (byte) SysexMessage.SYSTEM_EXCLUSIVE;
        for (int i = 1; i < sysexSize - 1; i++) {
            Patch source;
            byte[] ssysex;
            // look for a patch with the same Driver and enough length
            do {
                source = getRandomPatch();
                ssysex = source.getByteArray();
            } while (source.getDriver() != drv || ssysex.length - 1 < i);
            dsysex[i] = ssysex[i];
        }
        dsysex[dsysex.length - 1] = (byte) ShortMessage.END_OF_EXCLUSIVE; // EOX

        patch = (drv.createPatch(dsysex));
        log.info("done : " + patch);
    }

    public Patch getCurrentPatch() {
        return patch;
    }

    private Patch getRandomPatch() {
        int num = (int) (Math.random() * libSize);
        // log.info("num : " + num + " / " + libSize);
        return lib.get(num);
    }
}
