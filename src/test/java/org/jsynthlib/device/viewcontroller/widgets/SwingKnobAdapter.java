package org.jsynthlib.device.viewcontroller.widgets;

import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingKnobValueSetter;

public class SwingKnobAdapter extends AbstractSwingWidgetAdapter {

    private final KnobWidget widget;

    public SwingKnobAdapter(KnobWidget widget) {
        super(widget);
        this.widget = widget;
        setType(Type.KNOB);
        setValue(widget.getValue());
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingKnobValueSetter(widget);
    }

}
