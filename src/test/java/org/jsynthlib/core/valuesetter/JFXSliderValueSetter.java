package org.jsynthlib.core.valuesetter;

import org.jemmy.fx.control.SliderDock;

public class JFXSliderValueSetter implements IValueSetter {

    private final SliderDock dock;

    public JFXSliderValueSetter(SliderDock dock) {
        this.dock = dock;
    }

    @Override
    public void setValue(int value) {
        dock.asScroll().to(value);
    }

}
