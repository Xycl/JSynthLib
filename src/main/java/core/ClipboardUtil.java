package core;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import org.apache.log4j.Logger;

public class ClipboardUtil implements ClipboardOwner {
    private static final Logger LOG = Logger.getLogger(ClipboardUtil.class);
    protected final static ClipboardUtil instance = new ClipboardUtil();

    protected final static Clipboard c = Toolkit.getDefaultToolkit()
            .getSystemClipboard();

    public static void storePatch(IPatch p) {
        try {
            c.setContents(p, instance);
        } catch (IllegalStateException e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public static IPatch getPatch() { // not used
        try {
            Transferable t = c.getContents(instance);
            return (IPatch) t
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
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

}
