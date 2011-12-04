package core;

import javax.swing.JOptionPane;

/**
 * This class provides utility methods for error, warning and status messages.
 */
public class ErrorMsg {
    /** The bit mask for debug messages. */
    public static final int DEBUG_ENABLED  = 0x1;
    /** The bit mask for stack dump messages. */
    public static final int STACK_DUMP_ENABLED = 0x2;

    /** The debug level. */
    private static int debugLevel = 0;

    /**
     * Utility class is non-instantiable.
     */
    private ErrorMsg() {
        // non-instantiable
    }

    /**
     * Set the debug level.
     *
     * @param lvl the debug level
     */
    public static void setDebugLevel(final int lvl) {
        debugLevel = lvl;
    }

    /**
     * Get whether debug messages are enabled.
     *
     * @return whether debug messages are enabled
     */
    public static boolean isDebugEnabled() {
        return (debugLevel & DEBUG_ENABLED) != 0;
    }

    /**
     * Get whether stack dump messages are enabled.
     *
     * @return whether stack dump messages are enabled
     */
    public static boolean isStackDumpEnabled() {
        return (debugLevel & STACK_DUMP_ENABLED) != 0;
    }

    /**
     * Show a message in an error dialog.
     *
     * @param title the error dialog title
     * @param msg the error message
     */
    public static void reportError(final String title, final String msg) {
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), msg, title, JOptionPane.ERROR_MESSAGE);
        if (isDebugEnabled()) {
            System.err.println("[Error] " + msg);
        }
        if (isStackDumpEnabled()) {
            Thread.dumpStack();
        }
    }

    /**
     * Show a message in an error dialog.
     *
     * @param title the error dialog title
     * @param msg the error message
     * @param exception the error exception
     */
    public static void reportError(final String title, final String msg, final Exception exception) {
        ErrorDialog.showDialog(PatchEdit.getInstance(), title, msg, exception);
        if (isDebugEnabled()) {
    	    System.err.println("[Error] " + msg + " [Exception] " + exception.getMessage());
        }
        if (isStackDumpEnabled()) {
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Show a message in a warning dialog.
     *
     * @param title the warning dialog title
     * @param msg the warning message
     */
    public static void reportWarning(final String title, final String msg) {
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), msg, title, JOptionPane.WARNING_MESSAGE);
        if (isDebugEnabled()) {
            System.err.println("[Warning] " + msg);
        }
        if (isStackDumpEnabled()) {
            Thread.dumpStack();
        }
    }

    /**
     * Show a message in a warning dialog.
     *
     * @param title the warning dialog title
     * @param msg the warning message
     * @param exception the warning exception
     */
    public static void reportWarning(final String title, final String msg, final Exception exception) {
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), msg, title, JOptionPane.WARNING_MESSAGE);
        if (isDebugEnabled()) {
    	    System.err.println("[Warning] " + msg + " [Exception] " + exception.getMessage());
        }
        if (isStackDumpEnabled()) {
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Show a debug message on the console.
     *
     * @param msg the debug message
     */
    public static void reportStatus(final String msg) {
        if (isDebugEnabled()) {
            System.err.println("[Message]" + msg);
        }
    }

    /**
     * Show an exception message and optional stack trace on the console.
     *
     * @param exception the exception
     */
    public static void reportStatus(final Exception exception) {
        if (isDebugEnabled()) {
            System.err.println("[Exception] " + exception.getMessage());
        }
        if (isStackDumpEnabled()) {
            exception.printStackTrace(System.err);
        }
    }

    /**
     * Show a hex dump of data on the console.
     *
     * @param data the data to hex dump
     */
    public static void reportStatus(final byte[] data) {
        if (isDebugEnabled()) {
            reportStatus(Utility.hexDump(data, 0, data.length, 20));
        }
    }
}
