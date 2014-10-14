package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import org.jsynthlib.device.model.IPatchStringSender;
import org.jsynthlib.device.model.PatchNameSender;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * SysexWidget for patch name.
 * @version $Id: PatchNameWidget.java 992 2005-03-17 19:49:47Z ribrdb $
 */
public class PatchNameWidget extends SysexWidget {
    /** JTextField object */
    protected JTextField name;
    protected int patchNameSize;
    private final IPatchStringSender sender;

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     * @param label
     *            a label text.
     * @param patch
     *            a <code>Patch</code>, which is edited.
     */
    public PatchNameWidget(String label, Patch patch) {
        this(label, patch, patch.getNameSize(), new PatchNameSender(patch));
    }

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     * @param label
     *            a label text.
     * @param patch
     *            a <code>Patch</code>, which is edited.
     */
    public PatchNameWidget(String label, Patch patch, IPatchStringSender sender) {
        this(label, patch, patch.getNameSize(), sender);
    }

    /**
     * Creates a new <code>PatchNameWidget</code> instance.
     * @param label
     *            a label text.
     * @param patch
     *            a <code>Patch</code>, which is edited.
     * @param patchNameSize
     *            maximum length of patch name
     */
    public PatchNameWidget(String label, Patch patch, int patchNameSize, IPatchStringSender sender) {
        super(label, patch, null, null);

        this.sender = sender;
        this.patchNameSize = patchNameSize;
        createWidgets();
        layoutWidgets();
    }

    @Override
    protected void createWidgets() {

        if (getDriver() != null) {
            name = new JTextField(getPatch().getName(), patchNameSize);
        } else {
            name = new JTextField("Patch Name", 0);
        }
        name.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            // No system exclusive messages is sent.
            @Override
            public void focusLost(FocusEvent e) {
                JTextField source = (JTextField) e.getSource();
                sender.send(source.getText());
            }
        });
    }

    /** Adds an <code>FocusListener</code> to the JTextField. */
    public void addEventListener(FocusListener l) {
        name.addFocusListener(l);
    }

    @Override
    protected void layoutWidgets() {
        setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);
        add(name, BorderLayout.EAST);
    }

    @Override
    public void setEnabled(boolean e) {
        name.setEnabled(e);
    }
}
