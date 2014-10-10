package org.jsynthlib.core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;

/**
 * This class provides various utility methods.
 * @author phil@muqus.com - 07/2001
 * @author Hiroo Hayashi
 */
public class Utility {
    /**
     * Utility class cannot be instantiated.
     */
    private Utility() {
        // non-instantiable
    }

    /**
     * Get the operating system name.
     * @return the operating system name
     */
    public static String getOSName() {
        return System.getProperty("os.name", "");
    }

    /**
     * Get the operating system version.
     * @return the operating system version
     */
    public static String getOSVersion() {
        return System.getProperty("os.version", "");
    }

    /**
     * Get the Java version.
     * @return the Java version
     */
    public static String getJavaVersion() {
        return System.getProperty("java.version", "");
    }

    /**
     * Return a copy of a source byte array with a region deleted.
     * @param src
     *            the source array to copy
     * @param offset
     *            the offset of the region to delete
     * @param len
     *            the length in bytes of the region to delete
     * @return a copy of the source array with the region deleted
     */
    public static byte[] byteArrayDelete(final byte[] src, final int offset,
            final int len) {
        byte[] dest = new byte[src.length - len];
        System.arraycopy(src, 0, dest, 0, offset);
        System.arraycopy(src, offset + len, dest, offset, src.length
                - (offset + len));
        return dest;
    }

    /**
     * Return a copy of a source byte array with a region inserted.
     * @param src
     *            the source array to copy
     * @param offset
     *            the offset of the region to insert
     * @param insert
     *            the array to insert a region of
     * @param insertOffset
     *            the offset of the region to insert
     * @param insertLen
     *            the length in bytes of the region to insert
     * @return a copy of the source array with the region inserted
     */
    public static byte[] byteArrayInsert(final byte[] src, final int offset,
            final byte[] insert, final int insertOffset, final int insertLen) {
        byte[] dest = new byte[src.length + insertLen];
        System.arraycopy(src, 0, dest, 0, offset);
        System.arraycopy(insert, insertOffset, dest, offset, insertLen);
        System.arraycopy(src, offset, dest, offset + insertLen, src.length
                - offset);
        return dest;
    }

    /**
     * Return a copy of a source byte array with a region replaced.
     * @param src
     *            the source array to copy
     * @param offset
     *            the offset of the region to replace
     * @param len
     *            the length of the region to replace
     * @param insert
     *            the array to insert a region of
     * @param insertOffset
     *            the offset of the region to insert
     * @param insertLen
     *            the length in bytes of the region to insert
     * @return a copy of the source array with the region replaced
     */
    public static byte[] byteArrayReplace(final byte[] src, final int offset,
            final int len, final byte[] insert, final int insertOffset,
            final int insertLen) {
        byte[] dest = new byte[src.length - len + insertLen];
        System.arraycopy(src, 0, dest, 0, offset);
        System.arraycopy(insert, insertOffset, dest, offset, insertLen);
        System.arraycopy(src, offset + len, dest, offset + insertLen,
                src.length - offset - len);
        return dest;
    }


    // ----- End Hiroo Hayashi

    /**
     * Get the parent frame of a component. Returns the component if it is a an
     * instance of the <code>Frame</code> class, the parent frame, or null if
     * the component is not an instance of the <code>Frame</code> class and does
     * not have a parent frame.
     * @param component
     *            the component to get the parent frame of
     * @return the parent frame
     */
    public static Frame getFrame(final Component component) {
        for (Component c = component; c != null; c = c.getParent()) {
            if (c instanceof Frame) {
                return (Frame) c;
            }
        }
        return null;
    }

    /**
     * Request a window to be centered on the display screen.
     * @param window
     *            the window to request be centered on the display screen
     */
    public static void centerWindow(final Window window) {
        Dimension screenSize = window.getToolkit().getScreenSize();
        screenSize.height /= 2;
        screenSize.width /= 2;

        Dimension windowSize = window.getSize();
        windowSize.height /= 2;
        windowSize.width /= 2;

        window.setLocation(screenSize.width - windowSize.width,
                screenSize.height - windowSize.height);
    }
}
