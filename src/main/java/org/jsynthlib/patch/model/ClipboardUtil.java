package org.jsynthlib.patch.model;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.patch.model.impl.PatchTransferHandler;

public class ClipboardUtil implements ClipboardOwner {
    private static final Logger LOG = Logger.getLogger(ClipboardUtil.class);
    protected static final ClipboardUtil instance = new ClipboardUtil();

    protected static final Clipboard c = Toolkit.getDefaultToolkit()
            .getSystemClipboard();

    public static void storePatch(Patch p) {
        try {
            c.setContents(p, instance);
        } catch (IllegalStateException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public static Patch getPatch() { // not used
        try {
            Transferable t = c.getContents(instance);
            return (Patch) t
                    .getTransferData(PatchTransferHandler.PATCH_FLAVOR);
        } catch (IllegalStateException e) {
            LOG.warn(e.getMessage(), e);
        } catch (ClassCastException e) {
            LOG.warn(e.getMessage(), e);
        } catch (UnsupportedFlavorException e) {
            LOG.warn(e.getMessage(), e);
        } catch (IOException e) {
            LOG.warn(e.getMessage(), e);
        }
        return null;
    }

    // ClipboardOwner method
    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

}
