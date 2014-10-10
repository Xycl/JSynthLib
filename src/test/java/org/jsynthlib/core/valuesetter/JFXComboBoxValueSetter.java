package org.jsynthlib.core.valuesetter;

import java.util.List;

import org.jemmy.fx.control.ComboBoxDock;
import org.jemmy.interfaces.Selectable;

public class JFXComboBoxValueSetter implements IValueSetter {

    private final ComboBoxDock dock;

    public JFXComboBoxValueSetter(ComboBoxDock dock) {
        this.dock = dock;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(int value) {
        Selectable<String> selectable = dock.asSelectable();
        List<String> states = selectable.getStates();
        selectable.selector().select(states.get(value));

    }

}
