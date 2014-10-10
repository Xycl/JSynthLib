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
import org.jsynthlib.patch.model.IPatch;

/**
 * A scene is a container for all patches and their explicite bank/patch
 * locations at the synths for a concrete singular song/sound. You can put all
 * needed patches of all used synths in one scene and transfer the whole stuff
 * in one step to your synths.
 * @version $Id: Scene.java 1079 2007-09-19 22:50:29Z billzwicky $
 * @author Gerrit Gehnen
 */
public class Scene implements IPatch {
    
    private final transient Logger log = Logger.getLogger(getClass());

    private IPatch patch;

    private int bankNumber;

    private int patchNumber;

    private String comment;

    // This is used by java to maintain backwards compatibility.
    private static final long serialVersionUID = 1L;

    /** Creates a new instance of Scene */
    /*
     * public Scene() { patch=new Patch(new byte[1024], (Driver) null);
     * bankNumber=0; patchNumber=0; comment=new StringBuffer(); }
     */
    public Scene(IPatch p) {
        patch = p;
        bankNumber = 0;
        patchNumber = 0;
        comment = p.getComment();

    }

    public Scene(IPatch p, int bankNum, int patchNum) {// wirski@op.pl
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
    public void setPatch(IPatch patch) {
        this.patch = patch;
    }

    /**
     * Getter for property comment.
     * @return Value of property comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Setter for property comment.
     * @param comment
     *            New value of property comment.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    // Transferable interface methods
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException {
        if (flavor.match(PatchTransferHandler.SCENE_FLAVOR))
            return this;
        else if (flavor.match(PatchTransferHandler.PATCH_FLAVOR))
            return patch;
        else
            throw new UnsupportedFlavorException(flavor);
    }

    public boolean isDataFlavorSupported(final DataFlavor flavor) {
        log.info("Scene.isDataFlavorSupported " + flavor);
        return (flavor.match(PatchTransferHandler.SCENE_FLAVOR) || flavor
                .match(PatchTransferHandler.PATCH_FLAVOR));
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] {
                PatchTransferHandler.SCENE_FLAVOR,
                PatchTransferHandler.PATCH_FLAVOR };
    }

    // end of Transferable interface methods

    // Clone interface method
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // Cannot happen -- we support clone, and so do arrays
            throw new InternalError(e.toString());
        }
    }
    // end of Clone interface method

    public String getDate() {
        return patch.getDate();
    }

    public void setDate(String date) {
        patch.setDate(date);
    }

    public String getAuthor() {
        return patch.getAuthor();
    }

    public void setAuthor(String author) {
        patch.setAuthor(author);
    }

    public Device getDevice() {
        return patch.getDevice();
    }

    public IDriver getDriver() {
        return patch.getDriver();
    }

    public void setDriver(IDriver driver) {
        patch.setDriver(driver);
    }

    public void setDriver() {
        patch.setDriver();
    }

    public boolean hasNullDriver() {
        return patch.hasNullDriver();
    }

    public String getPatchHeader() {
        return patch.getPatchHeader();
    }

    public String getName() {
        return patch.getName();
    }

    public void setName(String name) {
        patch.setName(name);
    }

    public boolean hasEditor() {
        return patch.hasEditor();
    }

    public JSLFrame edit() {
        return patch.edit();
    }

    public void send(int bankNum, int patchNum) {
        patch.send(bankNum, patchNum);
    }

    public SysexMessage[] getMessages() {
        return patch.getMessages();
    }

    public byte[] export() {
        return patch.export();
    }

    public byte[] getByteArray() {
        return patch.getByteArray();
    }

    public int getSize() {
        return patch.getSize();
    }

    public String getType() {
        return patch.getType();
    }

    public int getNameSize() {
        return patch.getNameSize();
    }

    public String lookupManufacturer() {
        return patch.lookupManufacturer();
    }

    public boolean isSinglePatch() {
        return patch.isSinglePatch();
    }

    public boolean isBankPatch() {
        return patch.isBankPatch();
    }

    public void useSysexFromPatch(IPatch p) {
        patch.useSysexFromPatch(p);
    }

    
}
