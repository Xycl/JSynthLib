/*
 * SysexStoreDialog.java
 */

package org.jsynthlib.device.viewcontroller;

import org.jsynthlib.device.model.IDriver;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Dialog to choose the Device, Driver, BankNumber and PatchNumber of the
 * location, where a Patch should be stored. More than one of each device is
 * supported, but only devices/drivers are selectable, which support the patch.
 * @author Torsten Tittmann
 * @version $Id: SysexStoreDialog.java 1079 2007-09-19 22:50:29Z billzwicky $
 */
public class SysexStoreDialog extends DevDrvPatchSelector {

    /**
     * Constructor with choosable default patchNumber.
     * @param patch
     *            The Patch to store.
     * @param patchnum
     *            The default patchNumber selected in the patch Combobox.
     */
    public SysexStoreDialog(Patch patch, int banknum, int patchnum) {
        super(patch, banknum, patchnum, "Store Sysex Data", "Store...");
    }

    /**
     * getPatchNumbers is overridden for SystexStoreDialog. Only storable
     * patches are displayed.
     */
    @Override
    protected String[] getPatchNumbers(IPatchDriver driver) {
        return driver.getPatchNumbersForStore();
    }

    /**
     * Makes the actual work after pressing the 'Store' button
     */
    @Override
    protected void doit() {
        p.setDriver((IDriver) driverComboBox.getSelectedItem());
        int bankNum = bankComboBox.getSelectedIndex();
        int patchNum = patchNumComboBox.getSelectedIndex();
        p.send(bankNum, patchNum);

        setVisible(false);
        dispose();
    }
}
