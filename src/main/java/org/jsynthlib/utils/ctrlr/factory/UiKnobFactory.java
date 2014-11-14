package org.jsynthlib.utils.ctrlr.factory;

import org.jsynthlib.xmldevice.IntParamSpec;

public class UiKnobFactory extends UiSliderFactory {

    public UiKnobFactory(IntParamSpec object) {
        super(object, 92, 73);
    }

    @Override
    protected String getUiSliderStyle() {
        return "RotaryVerticalDrag";
    }
}
