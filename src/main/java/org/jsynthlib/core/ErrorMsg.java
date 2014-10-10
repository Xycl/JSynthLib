package org.jsynthlib.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.jsynthlib.patch.model.impl.PatchEdit;

/**
 * This class provides utility methods for error, warning and status messages.
 */
public class ErrorMsg extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Show a message in an error dialog.
     * @param title
     *            the error dialog title
     * @param msg
     *            the error message
     */
    public static void reportError(final String title, final String msg) {
        JOptionPane.showMessageDialog(PatchEdit.getInstance(), msg, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a message in an error dialog.
     * @param title
     *            the error dialog title
     * @param msg
     *            the error message
     * @param exception
     *            the error exception
     */
    public static void reportError(final String title, final String msg,
            final Exception exception) {
        String details = stackTraceToString(exception);
        ErrorMsg dialog = new ErrorMsg(PatchEdit.getInstance(), title, msg, details);
        dialog.setVisible(true);
    }

    /**
     * The details button.
     */
    private JButton detailsButton;
    /**
     * The details scroll pane.
     */
    private JScrollPane detailsPane;
    /**
     * Whether the details are visible.
     */
    private boolean detailsVisible;
    /**
     * Whether the details have been visible.
     */
    private boolean detailsBeenVisible;

    /**
     * Create an instance of the error dialog.
     * @param parent
     *            the parent window
     * @param title
     *            the title
     * @param message
     *            the message
     * @param details
     *            the details
     */
    public ErrorMsg(final Window parent, final String title,
            final String message, final String details) {
        super(parent, DEFAULT_MODALITY_TYPE);

        createInterface(parent, title, message, details);
    }

    /**
     * Create the interface.
     * @param parent
     *            the parent window
     * @param title
     *            the title
     * @param message
     *            the message
     * @param details
     *            the details
     */
    private void createInterface(final Window parent, final String title,
            final String message, final String details) {
        setTitle(title);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                close();
            }
        });

        JLabel messageLabel =
                new JLabel(message, UIManager.getIcon("OptionPane.errorIcon"),
                        JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        getContentPane().add(messageLabel, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(details, 8, 0);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        detailsPane = new JScrollPane(textArea);
        detailsPane.setBorder(BorderFactory.createLoweredBevelBorder());

        JPanel buttonPanel = new JPanel();
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        detailsButton = new JButton("Details >>");
        detailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                toggleDetails();
            }
        });
        buttonPanel.add(detailsButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                close();
            }
        });
        buttonPanel.add(closeButton);

        pack();

        setLocationRelativeTo(parent);
    }

    /**
     * Toggle display of the details.
     */
    private void toggleDetails() {
        Dimension dialogSize = getSize();

        if (detailsVisible) {
            detailsButton.setText("Details >>");
            getContentPane().remove(detailsPane);
            Dimension detailsPaneSize = detailsPane.getSize();
            dialogSize.height -= detailsPaneSize.height;
        } else {
            detailsButton.setText("Details <<");
            getContentPane().add(detailsPane, BorderLayout.CENTER);
            Dimension detailsPaneSize;
            if (detailsBeenVisible) {
                detailsPaneSize = detailsPane.getSize();
            } else {
                detailsBeenVisible = true;
                detailsPaneSize = detailsPane.getPreferredSize();
            }
            dialogSize.height += detailsPaneSize.height;
        }

        detailsVisible = !detailsVisible;

        setSize(dialogSize);
        invalidate();
        validate();
    }

    /**
     * Close the dialog.
     */
    private void close() {
        setVisible(false);
        dispose();
    }

    /**
     * Get the stack trace of an exception as a string.
     * @param exception
     *            the exception
     * @return the stack trace as a string
     */
    private static String stackTraceToString(final Exception exception) {
        StringBuilder buf = new StringBuilder();

        for (Throwable cause = exception; cause != null; cause =
                cause.getCause()) {
            buf.append("Caused by: ");
            buf.append(cause.getMessage());
            buf.append('\n');
            for (StackTraceElement element : cause.getStackTrace()) {
                buf.append(element.toString());
                buf.append('\n');
            }
            buf.append('\n');
        }

        return buf.toString();
    }

}
