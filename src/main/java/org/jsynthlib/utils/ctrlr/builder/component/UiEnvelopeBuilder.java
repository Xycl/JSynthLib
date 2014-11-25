package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.BuilderFactoryFacade;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiEnvelopeBuilder extends CtrlrComponentBuilderBase<EnvelopeSpec> {

    public interface Factory {
        UiEnvelopeBuilder newUiEnvelopeBuilder(EnvelopeSpec envelopeSpec);
    }

    @Inject
    private BuilderFactoryFacade factory;

    @Inject
    public UiEnvelopeBuilder(@Assisted EnvelopeSpec envelopeSpec) {
        setObject(envelopeSpec);
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        EnvelopeNodeSpec[] envelopeNodeSpecs =
                getObject().getEnvelopeNodeSpecArray();
        Rectangle rectangle =
                new Rectangle((int) rect.getX(), (int) rect.getY() + 20,
                        envelopeNodeSpecs.length * 40, 136);
        ModulatorType envelope =
                createGroup(panel, group, vstIndex, rectangle,
                        envelopeNodeSpecs.length);

        for (int i = 0; i < envelopeNodeSpecs.length; i++) {
            int displayIndex = i + 1;
            EnvelopeNodeSpec envelopeNodeSpec = envelopeNodeSpecs[i];
            if (envelopeNodeSpec.isSetXParam()
                    && isVariable(envelopeNodeSpec.getXParam())) {
                Rectangle xRect = new Rectangle(12 + (34 * i), 19, 33, 47);
                XEnvelopeParamSpec xParam = envelopeNodeSpec.getXParam();
                UiIncDecButtonsBuilder builder =
                        factory.newUiIncDecButtonsBuilder(xParam, displayIndex);
                builder.createComponent(panel, envelope, vstIndex, xRect);
            }
            if (envelopeNodeSpec.isSetYParam()
                    && isVariable(envelopeNodeSpec.getYParam())) {
                Rectangle yRect = new Rectangle(12 + (34 * i), 67, 33, 47);
                YEnvelopeParamSpec yParam = envelopeNodeSpec.getYParam();
                UiIncDecButtonsBuilder builder =
                        factory.newUiIncDecButtonsBuilder(yParam, displayIndex);
                builder.createComponent(panel, envelope, vstIndex, yRect);
            }
        }
        return envelope;
    }

    boolean isVariable(EnvelopeParamSpec paramSpec) {
        return paramSpec.getMin() != paramSpec.getMax();
    }

    ModulatorType createGroup(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect, int numNodes) {
        UiGroupBuilder groupFactory = factory.newUiGroupBuilder("envelope");
        groupFactory.setUiGroupText("");
        ModulatorType modulator =
                groupFactory.createComponent(panel, group, vstIndex, rect);

        Rectangle rateRect = new Rectangle(12 + numNodes * 16, 113, 21, 14);
        UiLabelBuilder rateBuilder = factory.newUiLabelBuilder("rate");
        rateBuilder.createComponent(panel, modulator, vstIndex, rateRect);
        Rectangle levelRect = new Rectangle(12 + numNodes * 16, 7, 21, 14);
        UiLabelBuilder levelBuilder = factory.newUiLabelBuilder("level");
        levelBuilder.createComponent(panel, modulator, vstIndex, levelRect);
        return modulator;
    }

    @Override
    protected String getModulatorName() {
        return "";
    }
}
