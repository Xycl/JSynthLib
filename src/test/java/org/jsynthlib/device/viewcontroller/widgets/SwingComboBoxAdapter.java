package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JComboBoxFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingComboBoxValueSetter;

public class SwingComboBoxAdapter extends AbstractSwingWidgetAdapter {

    private final JComboBoxFixture fixture;
    private final ComboBoxWidget widget;

    public SwingComboBoxAdapter(ComboBoxWidget widget, Robot robot) {
        super(widget);
        this.widget = widget;
        fixture =
                new JComboBoxFixture(robot, widget.cb);
        setValue(fixture.target.getSelectedIndex());
        setType(Type.COMBOBOX);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingComboBoxValueSetter(fixture,
                widget.getValueMin());
    }
}
