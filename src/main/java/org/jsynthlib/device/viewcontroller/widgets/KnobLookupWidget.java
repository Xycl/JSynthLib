package org.jsynthlib.device.viewcontroller.widgets;

import javax.swing.event.ChangeEvent;

import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.patch.model.impl.Patch;

import com.dreamfabric.DKnob;

/**
 * A rotary knob widget with label of values.
 * @version $Id: KnobLookupWidget.java 1182 2011-12-04 22:07:24Z chriswareham $
 * @author denis queffeulou mailto:dqueffeulou@free.fr
 * @see KnobWidget
 */
public class KnobLookupWidget extends KnobWidget {
    private final String[] mValueLabels;

    /**
     * Creates a new <code>KnobLookupWidget</code> instance. <code>min</code> is
     * set to 0.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     */
    public KnobLookupWidget(String label, Patch patch, IParamModel pmodel,
            ISender sender, String[] valueLabels) {
        this(label, patch, 0, pmodel, sender, valueLabels, -1, -1);
    }

    /**
     * Creates a new <code>KnobLookupWidget</code> instance. <code>min</code> is
     * set to 0.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     * @param width
     *            The width of the knob.
     * @param height
     *            The height of the knob.
     */
    public KnobLookupWidget(String label, Patch patch, IParamModel pmodel,
            ISender sender, String[] valueLabels, int width, int height) {
        this(label, patch, 0, pmodel, sender, valueLabels, width, height);
    }

    /**
     * Creates a new <code>KnobLookupWidget</code> instance.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param min
     *            minimum value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     */
    public KnobLookupWidget(String label, Patch patch, int min,
            IParamModel pmodel, ISender sender, String[] valueLabels) {
        this(label, patch, min, pmodel, sender, valueLabels, -1, -1);
    }

    /**
     * Creates a new <code>KnobLookupWidget</code> instance.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param min
     *            minimum value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     * @param width
     *            The width of the knob.
     * @param height
     *            The height of the knob.
     */
    public KnobLookupWidget(String label, Patch patch, int min,
            IParamModel pmodel, ISender sender, String[] valueLabels,
            int width, int height) {
        this(label, patch, min, min + valueLabels.length - 1, pmodel, sender,
                valueLabels, width, height);
    }

    /**
     * Creates a new <code>KnobLookupWidget</code> instance.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param min
     *            minimum value.
     * @param max
     *            maximum value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     */
    public KnobLookupWidget(String label, Patch patch, int min, int max,
            IParamModel pmodel, ISender sender, String[] valueLabels) {
        this(label, patch, min, max, pmodel, sender, valueLabels, -1, -1);
    }

    /**
     * Creates a new <code>KnobLookupWidget</code> instance.
     * @param label
     *            label String.
     * @param patch
     *            a <code>Patch</code> value.
     * @param min
     *            minimum value.
     * @param max
     *            maximum value.
     * @param pmodel
     *            a <code>ParamModel</code> object.
     * @param sender
     *            a <code>SysexSender</code> object.
     * @param valueLabels
     *            an array of labels for the knob values.
     * @param width
     *            The width of the knob.
     * @param height
     *            The height of the knob.
     */
    /*
     * @param max maximum value. => Q: Why this is required? This can be
     * calculated by using valueLabels.length. => A: You may want a knob with
     * less values than in valueLabels!
     */
    public KnobLookupWidget(String label, Patch patch, int min, int max,
            IParamModel pmodel, ISender sender, String[] valueLabels,
            int width, int height) {
        super(label, patch, min, max, 0, pmodel, sender);
        // mBase = 0;

        if (width > 0 && height > 0) {
            mKnob = new DKnob(width, height);
        }

        mValueLabels = valueLabels;
        mKnob.setToolTipText(mValueLabels[getValue()]);
    }

    /** invoked when the knob is moved. */
    @Override
    protected void eventListener(ChangeEvent e) {
        DKnob t = (DKnob) e.getSource();
        int oValue =
                Math.round(t.getValue() * (getValueMax() - getValueMin()))
                        + getValueMin();
        t.setToolTipText(mValueLabels[oValue]);
        t.setValueLabel(mValueLabels[oValue]);
        sendSysex(oValue);
        // ErrorMsg.reportStatus("value = "+(oValue + mBase));
    }
}
