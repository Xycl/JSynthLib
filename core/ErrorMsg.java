package core;
import javax.swing.*;

/**
 * This class handles error conditions and debug messages. It shows
 * error message dialogs to the user and can also log debug
 * information if a flag is set.<p>
 *
 * The Meaning for the <code>debug</code> variable flag is: (Each
 * number does all of preceding as well.)<p>
 * <pre>
 * 0 = No Debugging Info at all
 * 1 = Just print Stack Trace for Exceptions
 * 2 = Print Debug Status Messages & Error Messages
 * 3 = Print Stack Trace for all for non-exception Errors
 * </pre>
 * @author ???
 * @version $Id$
 */
public class ErrorMsg {
    /** @see ErrorMsg */
    public static int debug;

    /**
     * Show a message in an error dialog window.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     */
    public static void reportError(String errorTitle, String errorMSG) {
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Show a message in an error dialog window with an
     * <code>Exception</code> information.
     *
     * @param errorTitle title for error dialog.
     * @param errorMSG error message.
     * @param e an <code>Exception</code> value
     */
    public static void reportError(String errorTitle, String errorMSG,
				   Exception e) {
	ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.ERROR_MESSAGE, e);
	if (debug > 1)
	    System.out.println("ERR> '" + errorMSG + "' reported.");
	if (debug > 0) {
	    System.out.println("ERR> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Show a message in a warning dialog window.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     */
    public static void reportWarning(String errorTitle, String errorMSG) {
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if (debug > 1)
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Show a message in a warning dialog window with an
     * <code>Exception</code> information.
     *
     * @param errorTitle title for warning dialog.
     * @param errorMSG warning message.
     * @param e an <code>Exception</code> value
     */
    public static void reportWarning(String errorTitle, String errorMSG,
				     Exception e) {
        ErrorDialog.showMessageDialog(PatchEdit.getInstance()/*phil@muqus.com*/,
				      errorMSG, errorTitle,
				      JOptionPane.WARNING_MESSAGE);
	if (debug > 1)
	    System.out.println("WRN> '" + errorMSG + "' reported.");
	if (debug > 0) {
	    System.out.println("WRN> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
	if (debug > 2)
	    Thread.dumpStack();
    }

    /**
     * Report a debug message when <code>debug &gt 1</code>.
     *
     * @param msg a <code>String</code> value
     */
    public static void reportStatus(String msg) {
	if (debug > 1)
	    System.out.println("DBG>" + msg);
    }

    /**
     * Report an <code>Exception</code> information and the stack
     * trace when <code>debug &gt 0</code>.
     *
     * @param e an <code>Exception</code> value
     */
    public static void reportStatus(Exception e) {
	if (debug > 0) {
	    System.out.println("DBG> [Exception] " + e.getMessage());
	    e.printStackTrace(System.out);
	}
    }

    //----- Start phil@muqus.com

    /**
     * Output byte array as a pretty printed hex dump when <code>debug
     * &gt 1</code>.
     *
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(byte[] data) {
	reportStatus(null, data);
    }

    /**
     * Output a debug message and byte array as a pretty printed hex
     * dump when <code>debug &gt 1</code>.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     */
    public static void reportStatus(String sMsg, byte[] data) {
	if (debug < 2)
	    return;

	if (sMsg != null)
	    reportStatus(sMsg);

	Utility.hexDump(data, 0, data.length, 20);
    }

    /**
     * Output a debug message and byte array as a pretty printed hex
     * dump when <code>debug &gt 1</code>.
     *
     * @param sMsg a debug message.
     * @param data a <code>byte</code> array.
     * @param offset an index of <code>data</code> from which hex dump
     * starts.
     * @param len the length of hex dump.
     */
    public static void reportStatus(String sMsg,
				    byte[] data, int offset, int len) {
	Utility.hexDump(data, offset, len, 20);
    }
    //----- End phil@muqus.com
}
