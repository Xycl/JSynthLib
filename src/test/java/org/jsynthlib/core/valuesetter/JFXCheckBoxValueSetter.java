package org.jsynthlib.core.valuesetter;

import org.jemmy.fx.control.CheckBoxDock;
import org.jemmy.fx.control.CheckBoxWrap.State;
import org.jemmy.interfaces.Selector;

public class JFXCheckBoxValueSetter implements IValueSetter {

    private final CheckBoxDock dock;
    private final int min;

    public JFXCheckBoxValueSetter(CheckBoxDock dock, int min) {
        this.dock = dock;
        this.min = min;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(int value) {
        State state = null;
        if (value == min) {
            state = State.UNCHECKED;
        } else {
            state = State.CHECKED;
        }
        Selector<State> selector = dock.asSelectable().selector();
        selector.select(state);
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
        }
    }

}
