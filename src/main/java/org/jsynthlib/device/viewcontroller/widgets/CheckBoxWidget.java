package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.patch.model.IPatch;

/**
 * A SysexWidget class features JCheckBox widget.
 * @version $Id: CheckBoxWidget.java 696 2004-09-10 20:34:19Z hayashi $
 */
public class CheckBoxWidget extends SysexWidget {
    /** JCheckBox object. */
    protected JCheckBox cb;

    /**
     * Creates a new <code>CheckBoxWidget</code> instance.
     * @param label
     *            a label text for JCheckBox widget..
     * @param patch
     *            a <code>Patch</code>, which is edited.
     * @param pmodel
     *            a <code>IParamModel</code> instance.
     * @param sender
     *            a <code>SysexSender</code> instance.
     * @see SysexWidget
     */
    public CheckBoxWidget(String label, IPatch patch, IParamModel pmodel,
            ISender sender) {
        super(label, patch, 0, 1, pmodel, sender);

        createWidgets();
        layoutWidgets();
    }

    @Override
    protected void createWidgets() {
        if (getValue() == 0) {
            cb = new JCheckBox(getLabel(), false);
        } else {
            cb = new JCheckBox(getLabel(), true);
        }
        cb.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                eventListener(e);
            }
        });
    }

    /** invoked when the check box is toggled. */
    protected void eventListener(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            sendSysex(1);
        } else {
            sendSysex(0);
        }
    }

    /** Adds an ItemListener to the CheckBox. */
    public void addEventListener(ItemListener l) {
        cb.addItemListener(l);
    }

    @Override
    protected void layoutWidgets() {
        setLayout(new BorderLayout());
        add(cb);
    }

    @Override
    public void setValue(int v) {
        super.setValue(v);
        // cb.doClick();
        cb.setSelected(v != 0);
    }

    public void setValue(boolean v) {
        super.setValue(v ? 1 : 0);
        cb.setSelected(v);
    }

    @Override
    public void setLabel(String l) {
        _setLabel(l);
        cb.setText(l);
    }

    @Override
    public void setEnabled(boolean e) {
        cb.setEnabled(e);
    }
}
