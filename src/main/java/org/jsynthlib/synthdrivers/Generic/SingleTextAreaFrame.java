package org.jsynthlib.synthdrivers.Generic;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;

/**
 * Created by IntelliJ IDEA. User: jemenake Date: May 10, 2005 Time: 1:35:53 AM
 */
public class SingleTextAreaFrame extends JSLFrame {

    Font monospaced = new Font("Courier", Font.PLAIN, 12);
    int rows = 20, cols = 80;
    private JTextArea textArea;

    public SingleTextAreaFrame(String title, int cols) {
        this(title);
        this.cols = cols;
    }

    SingleTextAreaFrame(String title) {
        super(title);

        textArea = new JTextArea(rows, cols);
        textArea.setEditable(false);
        textArea.setFont(monospaced);

        // Get the width of a single character (we'd better have a fixed-width
        // font) and use it
        // to calculate the needed pixel width of the textArea. Then, resize...
        // int charwidth = textArea.getFontMetrics(f).stringWidth(" ");
        // int pixelwidth = cols * charwidth;
        // textArea.setPreferredSize(new Dimension(pixelwidth + 50,200));

        JScrollPane sp = new JScrollPane(textArea);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(sp, BorderLayout.CENTER);

        this.pack();
        this.setVisible(true);

    }

    public void append(String str) {
        textArea.append(str);
    }
}
