package org.jsynthlib.device.model;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.viewcontroller.BankEditorFrame;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * This is an implementation of IBankDriver and the base class for bank drivers
 * which use <code>Patch<IPatch>.<p>
 */
public abstract class AbstractBankDriver extends AbstractDriver implements
        IBankDriver {
    /**
     * The Number of Patches the Bank holds.
     */
    private final int numPatches;
    /**
     * How many columns to use when displaying the patches as a table.
     */
    private final int numColumns;

    // for default canHoldPatch
    /**
     * The Sysex header for the patches which go in this bank. This should be
     * same value as the <code>sysexID</code> field of the single driver. It can
     * be up to 16 bytes and have wildcards (<code>*</code>). (ex.
     * <code>"F041.*003F12"</code>)
     * @see AbstractPatchDriver#sysexID
     * @see #canHoldPatch
     */
    // This can be "private static final".
    protected String singleSysexID;
    /**
     * The size of the patches which go in this bank.
     * @see #canHoldPatch
     */
    // This can be "private static final".
    protected int singleSize;

    /**
     * Creates a new <code>BankDriver</code> instance.
     * @param patchType
     *            The patch type. eg. "Bank", "Multi Bank", "Drum Bank", etc.
     * @param authors
     *            The names of the authors of this driver.
     * @param numPatches
     *            The Number of Patches the Bank holds.
     * @param numColumns
     *            How many columns to use when displaying the patches as a
     *            table.
     */
    public AbstractBankDriver(String patchType, String authors, int numPatches,
            int numColumns) {
        super(patchType, authors);
        this.numPatches = numPatches;
        this.numColumns = numColumns;
    }

    //
    // IDriver interface methods
    //
    @Override
    public final boolean isSingleDriver() {
        return false;
    }

    @Override
    public final boolean isBankDriver() {
        return true;
    }

    @Override
    public final boolean isConverter() {
        return false;
    }

    // end of IDriver methods

    //
    // IPatchDriver interface methods
    //
    /**
     * Store the bank to a given bank on the synth. Ignores the patchNum
     * parameter. Should probably be overridden in most drivers
     * @see Patch#send(int, int)
     */
    @Override
    public void storePatch(Patch bank, int bankNum, int patchNum) {
        setBankNum(bankNum);
        super.sendPatch(bank);
    }

    /**
     * @see Patch#hasEditor()
     */
    @Override
    public boolean hasEditor() {
        return true;
    }

    /**
     * Creates a default bank editor window to edit this bank.
     * @see Patch#edit()
     */
    @Override
    public JSLFrame editPatch(Patch bank) {
        return new BankEditorFrame((BankPatch) bank);
    }

    //
    // for IPatch interface methods
    //
    /**
     * Get name of the bank.
     * @see Patch#getName()
     */
    @Override
    public String getPatchName(Patch bank) {
        // Most Banks have no name.
        return "-";
    }

    /**
     * Set name of the bank.
     * @see Patch#setName(String)
     */
    @Override
    public void setPatchName(Patch bank, String name) {
        // Most Banks have no name.
    }

    // end of IPatch interface methods
    //
    // for IBankPatch interface methods
    //
    /**
     * @see Patch#getNumPatches()
     */
    @Override
    public final int getNumPatches() {
        return numPatches;
    }

    /**
     * @see Patch#getNumColumns()
     */
    @Override
    public final int getNumColumns() {
        return numColumns;
    }

    /**
     * Compares the header & size of a Single Patch to this driver to see if
     * this bank can hold the patch.
     * @see Patch#put(Patch, int)
     * @see AbstractPatchDriver#supportsPatch
     */
    @Override
    public boolean canHoldPatch(Patch p) {
        if ((singleSize != p.sysex.length) && (singleSize != 0)) {
            return false;
        }

        String patchString = p.getPatchHeader().toString();
        StringBuffer driverString = new StringBuffer(singleSysexID);
        for (int j = 0; j < driverString.length(); j++) {
            if (driverString.charAt(j) == '*') {
                driverString.setCharAt(j, patchString.charAt(j));
            }
        }
        return (driverString.toString().equalsIgnoreCase(patchString.substring(
                0, driverString.length())));
    }

    /**
     * Delete a patch.
     * @see Patch#delete(int)
     */
    @Override
    public void deletePatch(Patch single, int patchNum) {
        setPatchName(single, patchNum, "          ");
    }

    // end of IBankDriver methods

    /**
     * Create a new Patch. Don't override this unless your driver properly
     * implement this method.
     * @see IPatchDriver#createPatch()
     * @see #createPatch()
     */
    @Override
    protected BankPatch createNewPatch() { // overridden by subclass
        return null;
    }

    public String getSingleSysexID() {
        return singleSysexID;
    }

    public void setSingleSysexID(String singleSysexID) {
        this.singleSysexID = singleSysexID;
    }

    public int getSingleSize() {
        return singleSize;
    }

    public void setSingleSize(int singleSize) {
        this.singleSize = singleSize;
    }

}
