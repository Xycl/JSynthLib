package org.jsynthlib.utils.ctrlr.controller.modulator;

import org.jsynthlib.xmldevice.IntParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiKnobController extends UiSliderController {

    public interface Factory {
        UiKnobController newUiKnobController(IntParamSpec paramSpec);
    }

    @Inject
    public UiKnobController(@Assisted IntParamSpec paramSpec) {
        super(paramSpec);
    }

    @Override
    public void init() {
        super.init();
        setWidth(92);
        setHeight(73);
        setUiSliderStyle("RotaryVerticalDrag");
    }

}
