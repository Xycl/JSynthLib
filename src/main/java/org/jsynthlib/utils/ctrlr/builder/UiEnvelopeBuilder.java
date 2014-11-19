package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.EnvelopeNodeSpec;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.XEnvelopeParamSpec;
import org.jsynthlib.xmldevice.YEnvelopeParamSpec;

public class UiEnvelopeBuilder extends CtrlrComponentBuilder<EnvelopeSpec> {

    private final SysexFormulaParser formulaParser;

    public UiEnvelopeBuilder(EnvelopeSpec object, SysexFormulaParser sysexParser) {
        super(object);
        this.formulaParser = sysexParser;
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        EnvelopeNodeSpec[] envelopeNodeSpecs =
                object.getEnvelopeNodeSpecArray();
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
                new UiIncDecButtonsBuilder(xParam, formulaParser, displayIndex)
                        .createComponent(panel, envelope, vstIndex, xRect);
            }
            if (envelopeNodeSpec.isSetYParam()
                    && isVariable(envelopeNodeSpec.getYParam())) {
                Rectangle yRect = new Rectangle(12 + (34 * i), 67, 33, 47);
                YEnvelopeParamSpec yParam = envelopeNodeSpec.getYParam();
                new UiIncDecButtonsBuilder(yParam, formulaParser, displayIndex)
                        .createComponent(panel, envelope, vstIndex, yRect);
            }
        }
        return envelope;
    }

    boolean isVariable(EnvelopeParamSpec paramSpec) {
        return paramSpec.getMin() != paramSpec.getMax();
    }

    ModulatorType createGroup(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect, int numNodes) {
        UiGroupBuilder groupFactory = new UiGroupBuilder("envelope");
        groupFactory.setUiGroupText("");
        ModulatorType modulator =
                groupFactory.createComponent(panel, group, vstIndex, rect);

        Rectangle rateRect = new Rectangle(12 + numNodes * 16, 113, 21, 14);
        new UiLabelBuilder("rate").createComponent(panel, modulator, vstIndex,
                rateRect);
        Rectangle levelRect = new Rectangle(12 + numNodes * 16, 7, 21, 14);
        new UiLabelBuilder("level").createComponent(panel, modulator, vstIndex,
                levelRect);
        return modulator;
    }
}
