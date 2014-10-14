/*
 * ReassignPatchDialog.java
 */
package org.jsynthlib.core.viewcontroller;

import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.viewcontroller.DevDrvPatchSelector;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * If more than two devices are loaded which supports the given patch, show this
 * Dialog to choose a new Device/Driver combination for the patch. The internal
 * patch assignment is used to send/play a patch.
 * @author Torsten Tittmann
 * @version $Id: ReassignPatchDialog.java 667 2004-08-14 22:27:38Z ribrdb $
 */
public class ReassignPatchDialog extends DevDrvPatchSelector {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * @param patch
     *            The Patch to reassign
     */
    public ReassignPatchDialog(Patch patch) {
        super(patch, "Reassign Patch to another Device/Driver", "Reassign...");
    }

    /**
     * Makes the actual work after pressing the 'Reassign' button
     */
    @Override
    protected void doit() {
        p.setDriver((IDriver) driverComboBox.getSelectedItem());

        setVisible(false);
        dispose();
    }
}
