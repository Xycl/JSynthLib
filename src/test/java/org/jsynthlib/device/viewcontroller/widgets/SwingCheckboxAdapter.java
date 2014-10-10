package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingCheckBoxValueSetter;

public class SwingCheckboxAdapter extends AbstractSwingWidgetAdapter {

    private final JCheckBoxFixture fixture;
    private final CheckBoxWidget widget;

    public SwingCheckboxAdapter(CheckBoxWidget widget, Robot robot) {
        super(widget);
        fixture = new JCheckBoxFixture(robot, widget.cb);
        this.widget = widget;
        if (fixture.target.isSelected()) {
            setValue(widget.getValueMax());
        } else {
            setValue(widget.getValueMin());
        }

        setType(Type.CHECKBOX);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingCheckBoxValueSetter(fixture, widget.getValueMin());
    }
}
