/*
 * Scene.java
 *
 * Created on 18. April 2002, 20:51
 *
 * Refactored from Performance.java
 */

package org.jsynthlib.patch.model.impl;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.sound.midi.SysexMessage;

import org.apache.log4j.Logger;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.Device;
import org.jsynthlib.device.model.IDriver;

/**
 * A scene is a container for all patches and their explicite bank/patch
 * locations at the synths for a concrete singular song/sound. You can put all
 * needed patches of all used synths in one scene and transfer the whole stuff
 * in one step to your synths.
 * @version $Id: Scene.java 1079 2007-09-19 22:50:29Z billzwicky $
 * @author Gerrit Gehnen
 */
public class Scene extends Patch {

    private final transient Logger log = Logger.getLogger(getClass());

    private Patch patch;

    private int bankNumber;

    private int patchNumber;

    private final String comment;

    // This is used by java to maintain backwards compatibility.
    private static final long serialVersionUID = 1L;

    /** Creates a new instance of Scene */
    /*
     * public Scene() { patch=new Patch(new byte[1024], (Driver) null);
     * bankNumber=0; patchNumber=0; comment=new StringBuffer(); }
     */
    public Scene(Patch p) {
        patch = p;
        bankNumber = 0;
        patchNumber = 0;
        comment = p.getComment();

    }

    public Scene(Patch p, int bankNum, int patchNum) {// wirski@op.pl
        patch = p;
        bankNumber = bankNum;
        patchNumber = patchNum;
        comment = p.getComment();

    }

    /**
     * Getter for property bankNumber.
     * @return Value of property bankNumber.
     */
    public int getBankNumber() {
        return bankNumber;
    }

    /**
     * Setter for property bankNumber.
     * @param bankNumber
     *            New value of property bankNumber.
     */
    public void setBankNumber(int bankNumber) {
        this.bankNumber = bankNumber;
    }

    /**
     * Getter for property patchNumber.
     * @return Value of property patchNumber.
     */
    public int getPatchNumber() {
        return patchNumber;
    }

    /**
     * Setter for property patchNumber.
     * @param patchNumber
     *            New value of property patchNumber.
     */
    public void setPatchNumber(int patchNumber) {
        this.patchNumber = patchNumber;
    }

    /**
     * Setter for property patch.
     * @param patch
     *            New value of property patch.
     */
    public void setPatch(Patch patch) {
        this.patch = patch;
    }

    /**
     * Getter for property comment.
     * @return Value of property comment.
     */

    // Transferable interface methods
    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.match(PatchTransferHandler.SCENE_FLAVOR)) {
            return this;
        } else if (flavor.match(PatchTransferHandler.PATCH_FLAVOR)) {
            return patch;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    @Override
    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        log.info("Scene.isDataFlavorSupported " + flavor);
        return (flavor.match(PatchTransferHandler.SCENE_FLAVOR) || flavor
                .match(PatchTransferHandler.PATCH_FLAVOR));
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
                PatchTransferHandler.SCENE_FLAVOR,
                PatchTransferHandler.PATCH_FLAVOR };
    }

    // end of Transferable interface methods

    // Clone interface method
    @Override
    public Object clone() {
        return super.clone();
    }

    // end of Clone interface method

    @Override
    public String getDate() {
        return patch.getDate();
    }

    @Override
    public void setDate(String date) {
        patch.setDate(date);
    }

    @Override
    public String getAuthor() {
        return patch.getAuthor();
    }

    @Override
    public void setAuthor(String author) {
        patch.setAuthor(author);
    }

    @Override
    public Device getDevice() {
        return patch.getDevice();
    }

    @Override
    public IDriver getDriver() {
        return patch.getDriver();
    }

    @Override
    public void setDriver(IDriver driver) {
        patch.setDriver(driver);
    }

    @Override
    public void setDriver() {
        patch.setDriver();
    }

    @Override
    public boolean hasNullDriver() {
        return patch.hasNullDriver();
    }

    @Override
    public String getPatchHeader() {
        return patch.getPatchHeader();
    }

    @Override
    public String getName() {
        return patch.getName();
    }

    @Override
    public void setName(String name) {
        patch.setName(name);
    }

    @Override
    public boolean hasEditor() {
        return patch.hasEditor();
    }

    @Override
    public JSLFrame edit() {
        return patch.edit();
    }

    @Override
    public void send(int bankNum, int patchNum) {
        patch.send(bankNum, patchNum);
    }

    @Override
    public SysexMessage[] getMessages() {
        return patch.getMessages();
    }

    @Override
    public byte[] export() {
        return patch.export();
    }

    @Override
    public byte[] getByteArray() {
        return patch.getByteArray();
    }

    @Override
    public int getSize() {
        return patch.getSize();
    }

    @Override
    public String getType() {
        return patch.getType();
    }

    @Override
    public int getNameSize() {
        return patch.getNameSize();
    }

    @Override
    public String lookupManufacturer() {
        return patch.lookupManufacturer();
    }

    @Override
    public boolean isSinglePatch() {
        return patch.isSinglePatch();
    }

    @Override
    public boolean isBankPatch() {
        return patch.isBankPatch();
    }

    @Override
    public void useSysexFromPatch(Patch p) {
        patch.useSysexFromPatch(p);
    }

}
