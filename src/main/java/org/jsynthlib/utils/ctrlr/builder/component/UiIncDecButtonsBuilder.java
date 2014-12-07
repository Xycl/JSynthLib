package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiIncDecButtonsBuilder extends UiSliderBuilder {

    public interface Factory {
        UiIncDecButtonsBuilder newUiIncDecButtonsBuilder(
                EnvelopeParamSpec object, int index);
    }

    @Inject
    public UiIncDecButtonsBuilder(@Assisted EnvelopeParamSpec object,
            @Assisted int index) {
        super(SliderSpecWrapper.Factory.newWrapper(object, index));
        setWidth(33);
        setHeight(47);
        if (object instanceof XEnvelopeParamSpec) {
            setLabelPosition(ComponentLabelPositionType.BOTTOM);
            setSliderValuePosition(3);
        }
    }

    @Override
    protected String getUiSliderStyle() {
        return "IncDecButtons";
    }
}
