/*
 * @(#) ErrorDialog.java
 *
 * Copyright (c) 2011 Chris Wareham <chris@chriswareham.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 */

package core;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;

/**
 * This class provides an error dialog.
 *
 * @author Chris Wareham
 */
public class ErrorDialog extends JDialog {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create an instance of the error dialog. Does not return to the caller
     * until the dialog is dismissed.
     *
     * @param title the title
     * @param exception the exception to extract the message and details from
     */
    public static void showDialog(final String title, final Exception exception) {
        ErrorDialog dialog = new ErrorDialog(title, exception);
        dialog.setVisible(true);
    }

    /**
     * Create an instance of the error dialog. Does not return to the caller
     * until the dialog is dismissed.
     *
     * @param parent the parent window
     * @param title the title
     * @param exception the exception to extract the message and details from
     */
    public static void showDialog(final Window parent, final String title, final Exception exception) {
        ErrorDialog dialog = new ErrorDialog(parent, title, exception);
        dialog.setVisible(true);
    }

    /**
     * Create an instance of the error dialog. Does not return to the caller
     * until the dialog is dismissed.
     *
     * @param parent the parent window
     * @param title the title
     * @param message the message
     * @param exception the exception to extract the message and details from
     */
    public static void showDialog(final Window parent, final String title, final String message, final Exception exception) {
        ErrorDialog dialog = new ErrorDialog(parent, title, message, exception);
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
     *
     * @param title the title
     * @param exception the exception to extract the message and details from
     */
    public ErrorDialog(final String title, final Exception exception) {
        this(null, title, exception.getMessage(), stackTraceToString(exception));
    }

    /**
     * Create an instance of the error dialog.
     *
     * @param parent the parent window
     * @param title the title
     * @param exception the exception to extract the message and details from
     */
    public ErrorDialog(final Window parent, final String title, final Exception exception) {
        this(parent, title, exception.getMessage(), stackTraceToString(exception));
    }

    /**
     * Create an instance of the error dialog.
     *
     * @param parent the parent window
     * @param title the title
     * @param message the message
     * @param exception the exception to extract the message and details from
     */
    public ErrorDialog(final Window parent, final String title, final String message, final Exception exception) {
        this(parent, title, message, stackTraceToString(exception));
    }

    /**
     * Create an instance of the error dialog.
     *
     * @param parent the parent window
     * @param title the title
     * @param message the message
     * @param details the details
     */
    public ErrorDialog(final Window parent, final String title, final String message, final String details) {
        super(parent, DEFAULT_MODALITY_TYPE);

        createInterface(parent, title, message, details);
    }

    /**
     * Create the interface.
     *
     * @param parent the parent window
     * @param title the title
     * @param message the message
     * @param details the details
     */
    private void createInterface(final Window parent, final String title, final String message, final String details) {
        setTitle(title);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(final WindowEvent event) {
                close();
            }
        });

        JLabel messageLabel = new JLabel(message, UIManager.getIcon("OptionPane.errorIcon"), JLabel.CENTER);
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
            @Override public void actionPerformed(final ActionEvent event) {
                toggleDetails();
            }
        });
        buttonPanel.add(detailsButton);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(final ActionEvent event) {
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
     *
     * @param exception the exception
     * @return the stack trace as a string
     */
    private static String stackTraceToString(final Exception exception) {
        StringBuilder buf = new StringBuilder();

        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
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
