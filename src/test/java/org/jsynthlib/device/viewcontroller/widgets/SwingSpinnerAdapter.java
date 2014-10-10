package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JSpinnerFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingSpinnerValueSetter;

public class SwingSpinnerAdapter extends AbstractSwingWidgetAdapter {

    private final JSpinnerFixture fixture;

    public SwingSpinnerAdapter(SpinnerWidget widget, Robot robot) {
        super(widget);
        fixture = new JSpinnerFixture(robot, widget.spinner);
        setValue((Integer) fixture.target.getValue());
        setType(Type.SPINNER);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingSpinnerValueSetter(fixture);
    }

}
