package org.jsynthlib.utils.ctrlr.controller.modulator;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiIncDecButtonsController extends UiSliderController {

    public interface Factory {
        UiIncDecButtonsController newUiIncDecButtonsController(
                EnvelopeParamSpec object, int index);
    }

    private final EnvelopeParamSpec object;

    @Inject
    public UiIncDecButtonsController(@Assisted EnvelopeParamSpec object,
            @Assisted int index) {
        super(SliderSpecWrapper.Factory.newWrapper(object, index));
        this.object = object;
    }

    @Override
    public void init() {
        super.init();
        setWidth(33);
        setHeight(47);
        if (object instanceof XEnvelopeParamSpec) {
            setLabelPosition(ComponentLabelPositionType.BOTTOM);
            setSliderValuePosition(3);
        }
        setUiSliderStyle("IncDecButtons");
    }

}
