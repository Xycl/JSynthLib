package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JTreeFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;

public class SwingTreeAdapter extends AbstractSwingWidgetAdapter {

    public SwingTreeAdapter(TreeWidget widget, Robot robot) {
        super(widget);
        JTreeFixture fixture = new JTreeFixture(robot, widget.tree);
        setType(Type.TREE);
        LOG.warn("Unhandled TreeWidget");
    }

    @Override
    public IValueSetter getValueSetter() {
        // TODO Auto-generated method stub
        return null;
    }

}
