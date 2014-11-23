package org.jsynthlib.utils.ctrlr.builder.component;

import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;

import com.google.inject.Inject;

public class UiKnobBuilder extends UiSliderBuilder {

    @Inject
    public UiKnobBuilder(DriverContext context) {
        super(context);
        setWidth(92);
        setHeight(73);
    }

    @Override
    protected String getUiSliderStyle() {
        return "RotaryVerticalDrag";
    }
}
