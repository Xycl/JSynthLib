package org.jsynthlib.utils.ctrlr.builder;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

public class UiIncDecButtonsBuilder extends UiSliderBuilder {

    public UiIncDecButtonsBuilder(EnvelopeParamSpec object,
            SysexFormulaParser formulaParser, int index) {
        super(SliderSpecWrapper.Factory.newWrapper(object, index), 33, 47,
                formulaParser);
        if (object instanceof YEnvelopeParamSpec) {
            setLabelPosition(ComponentLabelPositionType.BOTTOM);
            setSliderValuePosition(3);
        }
    }

    @Override
    protected String getUiSliderStyle() {
        return "IncDecButtons";
    }
}
