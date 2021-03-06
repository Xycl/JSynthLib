/* $Id: SpinnerWidget.java 696 2004-09-10 20:34:19Z hayashi $ */
package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.BorderLayout;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.patch.model.impl.Patch;

/**
 * Spinner SysexWidget.
 * @version $Id: SpinnerWidget.java 696 2004-09-10 20:34:19Z hayashi $
 */
public class SpinnerWidget extends SysexWidget {
    protected int base;
    protected JTextField text;
    protected JSpinner spinner;

    /**
     * Constructor for setting up the SpinnerWidget.
     * @param label
     *            Label for the Widget.
     * @param patch
     *            The patch, which is edited.
     * @param min
     *            Minimum value.
     * @param max
     *            Maximum value.
     * @param base
     *            base value. This value is added to the actual value for
     *            display purposes.
     * @param pmodel
     *            a <code>ParamModel</code> instance.
     * @param sender
     *            sysexSender for transmitting the value at editing the
     *            parameter
     */
    public SpinnerWidget(String label, Patch patch, int min, int max,
            int base, IParamModel pmodel, ISender sender) {
        super(label, patch, min, max, pmodel, sender);
        this.base = base;

        createWidgets();
        layoutWidgets();
    }

    /*
     * public SpinnerWidget(String l, Patch p, int min, int max, int b,
     * ParamModel ofs, SysexSender s, int valueInit) { this(l, p, min, max, b,
     * ofs, s); setValue(valueInit); }
     */

    @Override
    protected void createWidgets() {
        SpinnerNumberModel model =
                new SpinnerNumberModel(getValue() + base, getValueMin() + base,
                        getValueMax() + base, 1);
        spinner = new JSpinner(model);
        spinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                eventListener(e);
            }
        });
    }

    /** invoked when the value is changed. */
    protected void eventListener(ChangeEvent e) {
        // Maybe the displayed value differ from sysex value for 'base'.
        sendSysex(((Integer) spinner.getValue()).intValue() - base);
    }

    /** Adds a <code>ChangeListener</code> to the spinner. */
    public void addEventListener(ChangeListener l) {
        spinner.addChangeListener(l);
    }

    @Override
    protected void layoutWidgets() {
        setLayout(new BorderLayout());
        add(getJLabel(), BorderLayout.WEST);
        add(spinner, BorderLayout.CENTER);
    }

    @Override
    public void setValue(int v) {
        super.setValue(v);
        // Maybe the displayed value differ from sysex value for 'base'.
        spinner.setValue(new Integer(v + base));
    }

    @Override
    public void setMin(int min) {
        super.setMin(min);
        ((SpinnerNumberModel) (spinner.getModel()))
                .setMinimum(new Integer(min));
        spinner.setValue(new Integer(getValue()));
    }

    @Override
    public void setMax(int max) {
        super.setMax(max);
        ((SpinnerNumberModel) (spinner.getModel()))
                .setMaximum(new Integer(max));
        spinner.setValue(new Integer(getValue()));
    }

    @Override
    public void setEnabled(boolean e) {
        spinner.setEnabled(e);
    }
}
