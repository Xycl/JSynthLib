package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

import com.google.inject.Inject;

public class UiIncDecButtonsBuilder extends UiSliderBuilder {

    @Inject
    public UiIncDecButtonsBuilder(DriverContext context) {
        super(context);
        setWidth(33);
        setHeight(47);
    }

    @Override
    protected String getUiSliderStyle() {
        return "IncDecButtons";
    }

    public void setEnvelopeParamSpec(EnvelopeParamSpec object, int index) {
        setObject(SliderSpecWrapper.Factory.newWrapper(object, index));
        if (object instanceof YEnvelopeParamSpec) {
            setLabelPosition(ComponentLabelPositionType.BOTTOM);
            setSliderValuePosition(3);
        }
    }
}
