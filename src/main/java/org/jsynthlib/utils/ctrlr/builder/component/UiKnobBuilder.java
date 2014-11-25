package org.jsynthlib.utils.ctrlr.builder.component;

import org.jsynthlib.xmldevice.IntParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiKnobBuilder extends UiSliderBuilder {

    public interface Factory {
        UiKnobBuilder newUiKnobBuilder(IntParamSpec paramSpec);
    }

    @Inject
    public UiKnobBuilder(@Assisted IntParamSpec paramSpec) {
        super(paramSpec);
        setWidth(92);
        setHeight(73);
    }

    @Override
    protected String getUiSliderStyle() {
        return "RotaryVerticalDrag";
    }
}
