package org.jsynthlib.patch.viewcontroller;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.jsynthlib.core.viewcontroller.preferences.ConfigPanel;
import org.jsynthlib.core.viewcontroller.preferences.PrefsDialog;

/**
 * ConfigPanel for MIDI patch download repository.
 * @author Brian Klock
 * @author Hiroo Hayashi
 * @version $Id: RepositoryConfigPanel.java 950 2005-03-06 03:58:11Z hayashi $
 */
public class RepositoryConfigPanel extends ConfigPanel {
    {
        panelName = "Repository";
        nameSpace = "repository";
    }

    private final JTextField t1 = new JTextField(null, 20);
    private final JTextField t2 = new JTextField(null, 20);
    private final JPasswordField t3 = new JPasswordField(null, 20);

    public RepositoryConfigPanel(PrefsDialog parent) {
        super(parent);
        setLayout(new BorderLayout());
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 0, 0, 0);

        JLabel l = new JLabel("Patch Repository:");
        l.setToolTipText("Before uploading patches to a repository, You need to make an account.");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        p.add(l, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Repository Site:"), c);
        c.gridx = 1;
        p.add(t1, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("User Name:"), c);
        c.gridx = 1;
        p.add(t2, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Password:"), c);
        c.gridx = 1;
        p.add(t3, c);

        add(p, BorderLayout.CENTER);

        CaretListener cl = new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                setModified(true);
            }
        };
        t1.addCaretListener(cl);
        t2.addCaretListener(cl);
        t3.addCaretListener(cl);
    }

    protected void init() {
        t1.setText(getAppConfig().getRepositoryURL());
        t2.setText(getAppConfig().getRepositoryUser());
        t3.setText(getAppConfig().getRepositoryPass());
    }

    public void commitSettings() {
        getAppConfig().setRepositoryURL(t1.getText());
        getAppConfig().setRepositoryUser(t2.getText());
        getAppConfig().setRepositoryPass(new String(t3.getPassword()));
        setModified(false);
    }
}
