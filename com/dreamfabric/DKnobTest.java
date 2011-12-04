/*
 * @(#) DKnobTest.java
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

package com.dreamfabric;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class provides a frame to exercise the knob component.
 *
 * @author Chris Wareham
 */
public class DKnobTest extends JFrame {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Run the test application.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        JFrame frame = new DKnobTest();
        frame.setVisible(true);
    }

    /**
     * The test knob.
     */
    private DKnob knob;
    /**
     * The text field for displaying the knob value.
     */
    private JTextField textField;

    /**
     * Construct an instance of the test application.
     */
    public DKnobTest() {
        setTitle("DKnob Test");

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(final WindowEvent event) {
                close();
            }
        });

        getContentPane().setLayout(new BorderLayout(0, 2));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        JPanel panel = new JPanel(new GridBagLayout());
        getContentPane().add(panel, BorderLayout.CENTER);

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Knob:", JLabel.RIGHT), c);

        c.weightx = 1.0;
        c.gridx++;
        textField = new JTextField(24);
        textField.setEditable(false);
        panel.add(textField, c);

        c.weightx = 0.0;
        c.gridx++;
        knob = new DKnob();
        knob.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(final ChangeEvent event) {
                textField.setText(Float.toString(knob.getValue()));
            }
        });
        panel.add(knob, c);

        pack();

        setLocationRelativeTo(null);
    }

    /**
     * Close the frame.
     */
    private void close() {
        setVisible(false);
        dispose();
    }
}
