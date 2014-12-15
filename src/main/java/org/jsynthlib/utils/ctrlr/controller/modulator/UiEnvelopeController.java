package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.controller.ModulatorFactoryFacade;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiEnvelopeController extends UiGroupController {

    public interface Factory {
        UiEnvelopeController newUiEnvelopeController(EnvelopeSpec envelopeSpec);
    }

    private final EnvelopeNodeSpec[] envelopeNodeSpecs;

    @Inject
    private ModulatorFactoryFacade factory;

    @Inject
    public UiEnvelopeController(@Assisted EnvelopeSpec envelopeSpec) {
        super("envelope");
        envelopeNodeSpecs = envelopeSpec.getEnvelopeNodeSpecArray();
    }

    @Override
    public void init() {
        super.init();
        setModulatorName(getUniqueName("envelope"));
        setUiGroupText("");

        int numNodes = envelopeNodeSpecs.length;
        Rectangle rateRect = new Rectangle(12 + numNodes * 16, 113, 21, 14);
        UiLabelController rateBuilder = factory.newUiLabelController("rate");
        rateBuilder.setRect(rateRect);
        add(rateBuilder);
        Rectangle levelRect = new Rectangle(12 + numNodes * 16, 7, 21, 14);
        UiLabelController levelBuilder = factory.newUiLabelController("level");
        levelBuilder.setRect(levelRect);
        add(levelBuilder);

        for (int i = 0; i < envelopeNodeSpecs.length; i++) {
            int displayIndex = i + 1;
            EnvelopeNodeSpec envelopeNodeSpec = envelopeNodeSpecs[i];
            if (envelopeNodeSpec.isSetXParam()
                    && isVariable(envelopeNodeSpec.getXParam())) {
                Rectangle xRect = new Rectangle(12 + 34 * i, 67, 33, 47);
                XEnvelopeParamSpec xParam = envelopeNodeSpec.getXParam();
                UiIncDecButtonsController builder =
                        factory.newUiIncDecButtonsController(xParam, displayIndex);
                builder.setRect(xRect);
                add(builder);
            }
            if (envelopeNodeSpec.isSetYParam()
                    && isVariable(envelopeNodeSpec.getYParam())) {
                Rectangle yRect = new Rectangle(12 + 34 * i, 19, 33, 47);
                YEnvelopeParamSpec yParam = envelopeNodeSpec.getYParam();
                UiIncDecButtonsController builder =
                        factory.newUiIncDecButtonsController(yParam, displayIndex);
                builder.setRect(yRect);
                add(builder);
            }
        }
    }


    boolean isVariable(EnvelopeParamSpec paramSpec) {
        return paramSpec.getMin() != paramSpec.getMax();
    }

    @Override
    public void setRect(Rectangle rect) {
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        envelopeNodeSpecs.length * 40, 136);
        super.setRect(rectangle);
    }

}
