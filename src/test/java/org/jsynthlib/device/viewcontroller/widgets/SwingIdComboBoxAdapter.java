package org.jsynthlib.device.viewcontroller.widgets;

import javax.swing.JComboBox;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JComboBoxFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.core.valuesetter.SwingComboBoxValueSetter;
import org.jsynthlib.synthdrivers.YamahaUB99.IdComboWidget;

public class SwingIdComboBoxAdapter extends AbstractSwingWidgetAdapter {

    private JComboBoxFixture fixture;
    private final IdComboWidget widget;

    @SuppressWarnings("rawtypes")
    public SwingIdComboBoxAdapter(IdComboWidget widget, Robot robot) {
        super(widget);
        this.widget = widget;
        try {
            JComboBox cb = getField("cb", JComboBox.class, widget);
            fixture =
                    new JComboBoxFixture(robot, cb);
            setValue(fixture.target.getSelectedIndex());
            setType(Type.ID_COMBOBOX);
        } catch (NoSuchFieldException e) {
            LOG.warn(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.warn(e.getMessage(), e);
        }

    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingComboBoxValueSetter(
                fixture, widget.getValueMin());
    }


}
