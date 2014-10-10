/*
 * GenericDevice.java
 */

package org.jsynthlib.synthdrivers.Generic;

import java.util.prefs.Preferences;

import org.jsynthlib.core.LookupManufacturer;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.AbstractPatchDriver;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.SysexHandler;
import org.jsynthlib.midi.service.MidiMessageFormatter;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * A Null Synth Driver.
 * @author Brian Klock
 * @version $Id: GenericDevice.java 1041 2006-02-04 12:25:58Z jemenake $
 */
public class GenericDevice extends Device {
    public GenericDevice() {
        super("Generic", "Unknown", null, null, "Brian Klock");
    }

    public void setup(Preferences prefs) {
        super.setup(prefs);

        addDriver(new GenericDriver());
        addDriver(new IdentityDriver());
    }

    private class GenericDriver extends AbstractPatchDriver {
        private GenericDriver() {
            super("-", "Brian Klock");
            patchNumbers = new String[] {
                "0" };
        }

        @Override
        public boolean hasEditor() {
            return false;
        }
    }

    private class IdentityDriver extends AbstractPatchDriver {
        private IdentityDriver() {
            super("Identity", "Joe Emenaker");
            patchNumbers = new String[] {
                "0" };
            sysexRequestDump = new SysexHandler("F0 7E 7F 06 01 F7");
            sysexID = "F07E**0602"; // Match sysex identity reply messages
        }

        public JSLFrame editPatch(Patch p) {
            int lengthOfID = LookupManufacturer.lengthOfID(p.sysex, 5);
            String manuf = LookupManufacturer.get(p.sysex, 5);

            SingleTextAreaFrame f =
                    new SingleTextAreaFrame("Identity Reply Details");
            f.append("MIDI Channel         : " + p.sysex[2] + "\n");
            f.append("Manuf ID             : "
                    + MidiMessageFormatter.hexDump(p.sysex, 5, lengthOfID, -1, true) + " ("
                    + manuf + ")\n");
            f.append("Family (LSB First)   : "
                    + MidiMessageFormatter.hexDump(p.sysex, 5 + lengthOfID, 2, -1, true)
                    + "\n");
            f.append("Product (LSB First)  : "
                    + MidiMessageFormatter.hexDump(p.sysex, 7 + lengthOfID, 2, -1, true)
                    + "\n");
            f.append("Software (LSB First) : "
                    + MidiMessageFormatter.hexDump(p.sysex, 9 + lengthOfID, 4, -1, true)
                    + "\n");
            return (f);
            // return new HexDumpEditorFrame(p);
        }
    }
}
