/*
 * ErrorDialog.java
 *
 */

package core;

import javax.swing.*;
import java.io.*;
import java.awt.datatransfer.*;

/** A better implementation to display errors with the chance
 *  to look at the stack trace of the causing exception.
 * @author Gerrit Gehnen
 * @version $Id$
 */
public class ErrorDialog extends javax.swing.JDialog {

    private boolean informationIsVisible;
    java.awt.Dimension miniSize;
    Clipboard clipbrd=getToolkit().getSystemClipboard();

    /** Creates new form ErrorDialog
     * @param parent
     * @param modal
     */
    public ErrorDialog(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        initComponents();
        informationPanel.setVisible(false);
        informationIsVisible=false;
        miniSize=this.getSize();
        pack();
        Utility.centerDialog(this);
        getRootPane().setDefaultButton(okButton);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        showButton = new javax.swing.JToggleButton();
        informationPanel = new javax.swing.JPanel();
        informationPane = new javax.swing.JScrollPane();
        informationTextArea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        copyButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        messagePanel = new javax.swing.JPanel();
        iconLabel = new javax.swing.JLabel();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        okButton.setMnemonic('O');
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel1.add(okButton);

        showButton.setMnemonic('S');
        showButton.setText("Show more Information");
        showButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showButtonActionPerformed(evt);
            }
        });

        jPanel1.add(showButton);

        getContentPane().add(jPanel1, java.awt.BorderLayout.SOUTH);

        informationPanel.setLayout(new java.awt.BorderLayout(0, 10));

        informationPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        informationPane.setMaximumSize(new java.awt.Dimension(218, 218));
        informationPane.setMinimumSize(new java.awt.Dimension(218, 218));
        informationPane.setPreferredSize(new java.awt.Dimension(218, 218));
        informationTextArea.setEditable(false);
        informationPane.setViewportView(informationTextArea);

        informationPanel.add(informationPane, java.awt.BorderLayout.CENTER);

        copyButton.setMnemonic('C');
        copyButton.setText("Copy to Clipboard");
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        jPanel2.add(copyButton);

        informationPanel.add(jPanel2, java.awt.BorderLayout.SOUTH);

        getContentPane().add(informationPanel, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jPanel3.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        messagePanel.setLayout(new javax.swing.BoxLayout(messagePanel, javax.swing.BoxLayout.Y_AXIS));

        messagePanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jPanel3.add(messagePanel, java.awt.BorderLayout.CENTER);

		// I put this in a try/catch block because, if the image was missing, it threw an exception - emenaker 3/12/2003
		try {
	        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Error.gif")));
		} catch (NullPointerException e) {}
        jPanel3.add(iconLabel, java.awt.BorderLayout.WEST);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        pack();
    }//GEN-END:initComponents

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
        // Add your handling code here:

          StringSelection text=new StringSelection(informationTextArea.getText());
        clipbrd.setContents(text,text);
    }//GEN-LAST:event_copyButtonActionPerformed

    private void showButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showButtonActionPerformed
        // Add your handling code here:
        this.setVisible(false);
        informationIsVisible=!informationIsVisible;
        informationPanel.setVisible(informationIsVisible);

        pack();
        Utility.centerDialog(this);
        informationTextArea.setText(exceptionText);
        this.setVisible(true);

    }//GEN-LAST:event_showButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Add your handling code here:
        setVisible(false);
        dispose();

    }//GEN-LAST:event_okButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    public static void showMessageDialog(java.awt.Component parentComponent,
    Object message,
    String title,
    int messageType
    ) {
        ErrorDialog dialog= new ErrorDialog((java.awt.Frame)parentComponent,true);
        dialog.setTitle(title);
        dialog.setMessage((String)message);
        dialog.showButton.setVisible(false);
	try {
	    Thread.sleep(30);
	    dialog.show();
	    Thread.sleep(30);
	} catch (InterruptedException ex) {}
    }

    /**
     * @param parentComponent
     * @param message
     * @param title The title string of the box
     * @param messageType
     * @param e The causing exception, which can be displayed on the screen
     */
    public static void showMessageDialog(java.awt.Component parentComponent,
    Object message,
    String title,
    int messageType,
    Exception e
    ) {
        ErrorDialog dialog= new ErrorDialog((java.awt.Frame)parentComponent,true);
        dialog.setTitle(title);
        dialog.setMessage((String)message);
        // Convert the text of the exception to a String
        ByteArrayOutputStream bas=new ByteArrayOutputStream();
        /*
         * If we were given a null instead of a real exception, we should notice that
         * and say so rather than causing a NullPointerException to get displayed
         * in the dialog, which could be misleading - emenaker 2003.03.18
         */
         PrintStream ps = new PrintStream(bas);
		if(e!=null) {
			ps.println("The error was:");
			ps.println("  " + e.getMessage());
			ps.println("The exception text is:");
	        e.printStackTrace(ps);
		} else {
			ps.println("There were no details provided.");
		}
		// and fill it into the box
		dialog.exceptionText=bas.toString();
		// TODO: Make the informationPane scroll to the top
        dialog.showButton.setVisible(true);
	try {
	    Thread.sleep(30);
	    dialog.show();
	    Thread.sleep(30);
	} catch (InterruptedException ex) {}
    }

    /** Getter for property message.
     * @return Value of property message.
     *
     */
    public String getMessage() {
        return this.message;
    }

    /** Setter for property message.
     * @param message New value of property message.
     *
     */
    public void setMessage(String message) {
        String line;
        java.io.BufferedReader br = new java.io.BufferedReader(new java.io.StringReader(message));
        this.message = message;
        try {
            while ((line = br.readLine()) != null) {
                JLabel jl=new JLabel(line);
                messagePanel.add(jl);
            }
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
        }
        pack();

    }

    /** Getter for property exceptionText.
     * @return Value of property exceptionText.
     *
     */
    public String getExceptionText() {
        return this.exceptionText;
    }

    /** Setter for property exceptionText.
     * @param exceptionText New value of property exceptionText.
     *
     */
    public void setExceptionText(String exceptionText) {
        this.exceptionText = exceptionText;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea informationTextArea;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton copyButton;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel informationPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel messagePanel;
    private javax.swing.JScrollPane informationPane;
    private javax.swing.JToggleButton showButton;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property message. */
    private String message;

    /** Holds value of property exceptionText. */
    private String exceptionText;

}
