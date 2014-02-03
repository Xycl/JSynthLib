package core;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.util.Iterator;

import org.jsynthlib.gui.BankEditorFrame;
import org.jsynthlib.gui.LibraryFrame;
import org.jsynthlib.gui.PatchEditorFrame;
import org.jsynthlib.gui.desktop.JSLDesktop;
import org.jsynthlib.gui.desktop.JSLFrame;

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

    // ----- Start Joe Emenaker
    /**
     * convert a byte array into a hexa-dump string, with or without spaces
     * between the bytes.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of bytes per line. If equal or less than 0, no newlines
     *            are inserted.
     * @param wantspaces
     *            whether or not to insert spaces between bytes
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes,
            boolean wantspaces) {
        StringBuilder buf = new StringBuilder();
        if (len == -1 || offset + len > d.length)
            len = d.length - offset;
        for (int i = 0; i < len; i++) {
            int c = (d[offset + i] & 0xff);
            if (c < 0x10)
                buf.append("0");
            buf.append(Integer.toHexString(c));
            if (bytes > 0 && (i % bytes == bytes - 1 && i != len - 1))
                buf.append("\n");
            else if (i != len - 1 && wantspaces)
                buf.append(" ");
        }
        return buf.toString();
    }

    public static String hexDump(byte[] d, int offset, int len, int bytes,
            int addressLength, boolean characters) {
        String output = "";
        if (offset > d.length) {
            return ("offset out of bounds of byte array");
        }
        if (offset + len > d.length) {
            len = d.length - offset;
        }

        if (addressLength > 0) {
            String addressstring = Integer.toHexString(offset);
            while (addressstring.length() < addressLength) {
                addressstring = "0" + addressstring;
            }
            output += addressstring + " - ";
        }
        String hexdigits = hexDump(d, offset, len, bytes, true);
        // If hexdigits is too short, pad with spaces so that the last line
        // lines up with the others.
        if (characters) {
            while (hexdigits.length() < 3 * bytes - 1) {
                hexdigits += "   ";
            }
        }
        output += hexdigits;
        if (characters) {
            output += " - ";
            for (int i = offset; i < offset + len; i++) {
                if (d[i] >= 32 && d[i] <= 126) {
                    output += new Character((char) d[i]).toString();
                } else {
                    output += ".";
                }
            }
        }
        return (output);
    }

    /**
     * convert a byte array into a string of hex values, optionally including
     * their character equivalents and/or the address/offset of the beginning of
     * the line. With character equivalents, unprintable characters are replaced
     * by spaces. For example: 65 78 61 6D 70 6C 65 01 EF 31 38 F3 F1 -
     * EXAMPLE..18.. If <code>addresses</code> is true, then the hex
     * representation of the offset will be put at the beginning of each line.
     * For example: 013A0 - 65 78 61 6D 70 6C 65 01 - EXAMPLE. 013A8 - EF 31 38
     * F3 F1 65 64 63 - .18..EDC 013B0 - 38 34 36 37 38 31 37 37 - 84678177 If
     * <code>len</code> is positive non-zero, only <code>len</code> bytes will
     * be represented on each line.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of columns (bytes) to put in each line. If less than or
     *            equal to zero, no line-breaks are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes,
            boolean addresses, boolean characters) {
        // Is offset beyond the end of array?
        if (offset >= d.length) {
            return ("");
        }
        // Is offset+len beyond the end of the array?
        if (offset + len >= d.length || len < 0) {
            len = d.length - offset; // Set len to get remaining bytes
        }
        // If bytes <=0, set it to len so that we get the remaining bytes on one
        // line
        if (bytes <= 0) {
            bytes = len;
        }

        // How many digits to use for address?
        int addresslen = 0;
        // Keep adding 2 digits until we can repesent the highest number we need
        if (addresses) {
            int maxnumber = 1;
            while (maxnumber < offset + len) {
                addresslen += 2;
                maxnumber *= 256;
            }
        }

        String output = "";
        if (len <= bytes) {
            output = hexDump(d, offset, len, bytes, addresslen, characters);
        } else {
            // The bytes won't fit. Split them up and call hexDump for each one
            for (int i = 0; i < len; i += bytes) {
                output =
                        output
                                + hexDump(d, offset + i, bytes, bytes,
                                        addresslen, characters) + "\n";
            }
        }
        return (output);
    }

    // ----- End Joe Emenaker

    // ----- Start Hiroo Hayashi
    //
    // convert MidiMessage or byte array into String
    //
    /**
     * convert a byte array into a hexa-dump string.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of bytes per line. If equal or less than 0, no newlines
     *            are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes) {
        return hexDump(d, offset, len, bytes, true);
    }

    /**
     * convert a byte array into a one-line hexa-dump string. If it's longer
     * than <code>len</code>, it will have the inner portion removed and
     * replaced with dots, for example: "00 01 03 04 05 .. 7d 7e 7f"
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of columns (bytes) to put in the one-line string.
     * @return hexa-dump string.
     */
    public static String hexDumpOneLine(byte[] d, int offset, int len, int bytes) {
        if (len == -1 || len > d.length - offset)
            len = d.length - offset;

        if (len <= bytes || len < 8)
            return hexDump(d, offset, len, 0);

        return (hexDump(d, offset, bytes - 4, 0) + ".." + hexDump(d, offset
                + len - 3, 3, 0));
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

    /**
     * Revalidate Library. Internally this calls <code>revalidateDriver()</code>
     * method of each frame.
     */
    public static void revalidateLibraries() {
        JSLDesktop desktop = JSLDesktop.Factory.getDesktop();
        Iterator<JSLFrame> iterator = desktop.getJSLFrameIterator();
        boolean first = true;
        while (iterator.hasNext()) {
            // Before first iteration
            if (first) {
            PatchEdit.showWaitDialog();
            }

            JSLFrame jslFrame = iterator.next();
            if (jslFrame instanceof LibraryFrame) {
                ((LibraryFrame) (jslFrame)).revalidateDrivers();
            } else if (jslFrame instanceof BankEditorFrame) {
                ((BankEditorFrame) (jslFrame)).revalidateDriver();
            } else if (jslFrame instanceof PatchEditorFrame) {
                ((PatchEditorFrame) (jslFrame)).revalidateDriver();
            }

            // After last iteration
            if (!iterator.hasNext()) {
            PatchEdit.hideWaitDialog();
            }
        }
    }
}
