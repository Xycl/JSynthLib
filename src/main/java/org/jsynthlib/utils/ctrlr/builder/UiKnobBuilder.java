package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.IntParamSpec;

public class UiKnobBuilder extends UiSliderBuilder {

    public UiKnobBuilder(IntParamSpec object, SysexFormulaParser formulaParser) {
        super(SliderSpecWrapper.Factory.newWrapper(object), 92, 73,
                formulaParser);
    }

    @Override
    protected String getUiSliderStyle() {
        return "RotaryVerticalDrag";
    }
}
