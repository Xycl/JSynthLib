package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiEnvelopeBuilder extends UiGroupBuilder {

    public interface Factory {
        UiEnvelopeBuilder newUiEnvelopeBuilder(EnvelopeSpec envelopeSpec);
    }

    private final EnvelopeNodeSpec[] envelopeNodeSpecs;

    @Inject
    public UiEnvelopeBuilder(@Assisted EnvelopeSpec envelopeSpec,
            BuilderFactoryFacade factory) {
        super("envelope");
        envelopeNodeSpecs = envelopeSpec.getEnvelopeNodeSpecArray();
        setUiGroupText("");

        int numNodes = envelopeNodeSpecs.length;
        Rectangle rateRect = new Rectangle(12 + numNodes * 16, 113, 21, 14);
        UiLabelBuilder rateBuilder = factory.newUiLabelBuilder("rate");
        rateBuilder.setRect(rateRect);
        add(rateBuilder);
        Rectangle levelRect = new Rectangle(12 + numNodes * 16, 7, 21, 14);
        UiLabelBuilder levelBuilder = factory.newUiLabelBuilder("level");
        levelBuilder.setRect(levelRect);
        add(levelBuilder);

        for (int i = 0; i < envelopeNodeSpecs.length; i++) {
            int displayIndex = i + 1;
            EnvelopeNodeSpec envelopeNodeSpec = envelopeNodeSpecs[i];
            if (envelopeNodeSpec.isSetXParam()
                    && isVariable(envelopeNodeSpec.getXParam())) {
                Rectangle xRect = new Rectangle(12 + (34 * i), 19, 33, 47);
                XEnvelopeParamSpec xParam = envelopeNodeSpec.getXParam();
                UiIncDecButtonsBuilder builder =
                        factory.newUiIncDecButtonsBuilder(xParam, displayIndex);
                builder.setRect(xRect);
                add(builder);
            }
            if (envelopeNodeSpec.isSetYParam()
                    && isVariable(envelopeNodeSpec.getYParam())) {
                Rectangle yRect = new Rectangle(12 + (34 * i), 67, 33, 47);
                YEnvelopeParamSpec yParam = envelopeNodeSpec.getYParam();
                UiIncDecButtonsBuilder builder =
                        factory.newUiIncDecButtonsBuilder(yParam, displayIndex);
                builder.setRect(yRect);
                add(builder);
            }
        }
    }

    boolean isVariable(EnvelopeParamSpec paramSpec) {
        return paramSpec.getMin() != paramSpec.getMax();
    }

    @Override
    protected String getModulatorName() {
        return getUniqueName("envelope");
    }

    @Override
    public void setRect(Rectangle rect) {
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        envelopeNodeSpecs.length * 40, 136);
        super.setRect(rectangle);
    }

}
