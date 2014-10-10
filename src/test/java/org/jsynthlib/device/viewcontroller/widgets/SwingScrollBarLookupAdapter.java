package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JSliderFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingSliderValueSetter;

public class SwingScrollBarLookupAdapter extends AbstractSwingWidgetAdapter {

    private final JSliderFixture fixture;

    public SwingScrollBarLookupAdapter(ScrollBarLookupWidget widget, Robot robot) {
        super(widget);
        fixture = new JSliderFixture(robot, widget.slider);
        setValue(fixture.target.getValue());
        setType(Type.SCROLLBAR_LOOKUP);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingSliderValueSetter(fixture);
    }

}
