/* $Id: CrossBreedDialog.java 1182 2011-12-04 22:07:24Z chriswareham $ */

package org.jsynthlib.patch.viewcontroller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jsynthlib.core.ColumnLayout;
import org.jsynthlib.core.CrossBreeder;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.impl.PopupHandlerProvider;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.patch.model.impl.Patch;

public class CrossBreedDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JLabel l1;
    private final CrossBreeder crossBreeder;

    public CrossBreedDialog(JFrame parent) {
        super(parent, "Patch Cross-Breeder", false);
        crossBreeder = new CrossBreeder();
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        JPanel p4 = new JPanel();
        p4.setLayout(new ColumnLayout());
        l1 = new JLabel("Patch Type: ");

        JPanel buttonPanel = new JPanel();
        JPanel buttonPanel2 = new JPanel();

        JButton gen = new JButton("Generate");
        gen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generatePressed();
            }
        });
        buttonPanel2.add(gen);

        JButton play = new JButton("Play");
        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                play(crossBreeder.getCurrentPatch());
            }
        });
        buttonPanel2.add(play);

        JButton keep = new JButton("Keep");
        keep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PatchBasket library =
                            (PatchBasket) JSLDesktop.Factory.getDesktop()
                                    .getSelectedFrame();
                    Patch q = crossBreeder.getCurrentPatch();
                    library.pastePatch(q);
                } catch (Exception ex) {
                    PopupHandlerProvider.get().showMessage(null,
                            "Destination Library Must be Focused", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonPanel2.add(keep);

        JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OKPressed();
            }
        });
        buttonPanel.add(ok);
        getRootPane().setDefaultButton(ok);

        p4.add(l1);
        container.add(p4, BorderLayout.NORTH);
        container.add(buttonPanel2, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(container);
        // setSize(400,300);
        pack();
        Utility.centerWindow(this);
    }

    void OKPressed() {
        this.setVisible(false);
    }

    void generatePressed() {
        crossBreeder.generateNewPatch((PatchBasket) JSLDesktop.Factory.getDesktop()
                .getSelectedFrame());
        Patch p = crossBreeder.getCurrentPatch();
        l1.setText("Patch Type: " + p.getDevice().getManufacturerName() + " "
                + p.getDevice().getModelName() + " " + p.getType());
        play(p);
    }

    private void play(Patch p) {
        if (p.isSinglePatch()) {
            p.send();
            p.play();
        }
    }
}
