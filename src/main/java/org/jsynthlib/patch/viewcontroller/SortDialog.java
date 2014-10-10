package org.jsynthlib.patch.viewcontroller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.log4j.Logger;
import org.jsynthlib.core.ColumnLayout;
import org.jsynthlib.core.Utility;
import org.jsynthlib.core.viewcontroller.desktop.JSLDesktop;
import org.jsynthlib.patch.model.impl.LibraryColumnComparator;
import org.jsynthlib.patch.model.impl.LibraryColumns;
import org.jsynthlib.patch.model.impl.PatchEdit;

public class SortDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final transient Logger log = Logger.getLogger(getClass());

    public SortDialog(JFrame parent) {
        super(parent, "Library Sort", true);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        JLabel myLabel =
                new JLabel("Please select a Field to Sort the Library by.",
                        JLabel.CENTER);
        try {
            container.add(myLabel, BorderLayout.NORTH);
            final ButtonGroup group = new ButtonGroup();
            final JPanel radioPanel = new JPanel();
            radioPanel.setLayout(new ColumnLayout());

            LibraryColumns[] libraryColumns = LibraryColumns.values();
            for (LibraryColumns column : libraryColumns) {
                if (LibraryColumns.COMMENT.equals(column)) {
                    continue;
                }
                JRadioButton button = new JRadioButton(column.getName());
                button.setActionCommand(column.name());
                group.add(button);
                radioPanel.add(button);

                if (LibraryColumns.PATCH_NAME.equals(column)) {
                    button.setSelected(true);
                }
            }

            container.add(radioPanel, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            JButton done = new JButton(" OK ");
            done.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    PatchEdit.showWaitDialog();
                    String command = group.getSelection().getActionCommand();
                    LibraryColumns column =
                            LibraryColumns.valueOf(LibraryColumns.class,
                                    command);
                    ((LibraryFrame) JSLDesktop.Factory.getDesktop()
                            .getSelectedFrame())
                            .sortPatch(new LibraryColumnComparator(column));

                    PatchEdit.hideWaitDialog();
                }
            });
            buttonPanel.add(done);

            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            buttonPanel.add(cancel);

            getRootPane().setDefaultButton(done);

            container.add(buttonPanel, BorderLayout.SOUTH);
            getContentPane().add(container);
            pack();
            Utility.centerWindow(this);
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
    }
}
