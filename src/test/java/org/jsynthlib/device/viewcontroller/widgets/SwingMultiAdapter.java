package org.jsynthlib.device.viewcontroller.widgets;

import org.fest.swing.core.Robot;
import org.fest.swing.fixture.JPanelFixture;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.synthdrivers.QuasimidiQuasar.MultiWidget;

public class SwingMultiAdapter extends AbstractSwingWidgetAdapter {

    public SwingMultiAdapter(MultiWidget widget, Robot robot) {
        super(widget);
        JPanelFixture fixture = new JPanelFixture(robot, widget);
        setType(Type.MULTI);
        LOG.warn("Unhandled MultiWidget");

    }

    @Override
    public IValueSetter getValueSetter() {
        // TODO Auto-generated method stub
        return null;
    }

}
