package org.jsynthlib.core.valuesetter;

import org.jemmy.fx.control.ControlDock;
import org.jsynthlib.device.view.Knob;

public class JFXKnobValueSetter implements IValueSetter {

    private final ControlDock knobDock;

    public JFXKnobValueSetter(ControlDock knobDock) {
        this.knobDock = knobDock;
    }

    @Override
    public void setValue(int value) {
        Knob knob = (Knob) knobDock.control();
        knob.valueProperty().set(value);
    }

}
