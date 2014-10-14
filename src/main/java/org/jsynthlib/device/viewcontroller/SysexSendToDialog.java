/*
 * SysexSendToDialog.java
 */

package org.jsynthlib.device.viewcontroller;

import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Dialog to choose a Device and Driver to send the patch into an Edit buffer.
 * More than one of each device is supported, but only devices/drivers are
 * selectable, which support the patch.
 * @author Torsten Tittmann
 * @version $Id: SysexSendToDialog.java 747 2004-10-09 04:14:28Z hayashi $
 */
public class SysexSendToDialog extends DevDrvPatchSelector {
    /**
     * Constructor
     * @param patch
     *            The Patch to 'send to...'
     */
    public SysexSendToDialog(Patch patch) {
        // super(PatchEdit.getInstance(),
        // "Send Sysex Data into Edit Buffer of a specified device", true);
        super(patch, "Send Sysex Data into Edit Buffer of a specified device",
                "Send To...");
    }

    /**
     * Makes the actual work after pressing the 'Send to...' button
     */
    @Override
    protected void doit() {
        p.setDriver((IPatchDriver) driverComboBox.getSelectedItem());
        if (p.isSinglePatch()) {
            p.send();
        }

        setVisible(false);
        dispose();
    }
}
