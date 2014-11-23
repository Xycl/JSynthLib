package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.utils.ctrlr.builder.component.CtrlrComponentBuilderBase;
import org.jsynthlib.utils.ctrlr.builder.component.PatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiCombinedGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiEnvelopeBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiImageButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiIncDecButtonsBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiKnobBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiLabelBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;

public interface CtrlrComponentBuilderFactory {

    CtrlrComponentBuilderBase<? extends Object> newFactory(Object object);

    UiImageButtonBuilder newUiImageButtonBuilder(IntParamSpec paramSpec);

    UiButtonBuilder newUiButtonBuilder(IntParamSpec paramSpec);

    UiKnobBuilder newUiKnobBuilder(IntParamSpec paramSpec);

    UiGroupBuilder newUiGroupBuilder(PatchParamGroup group);

    UiTabBuilder newUiTabBuilder(PatchParamGroup[] groups);

    UiEnvelopeBuilder newUiEnvelopeBuilder(EnvelopeSpec envelopeSpec);

    UiCombinedGroupBuilder newUiCombinedGroupBuilder(CombinedGroup combGroup);

    UiIncDecButtonsBuilder newUiIncDecButtonsBuilder(EnvelopeParamSpec param,
            int displayIndex);

    UiButtonBuilder newUiButtonBuilder(SliderSpecWrapper newWrapper);

    UiLabelBuilder newUiLabelBuilder(String string);

    UiGroupBuilder newUiGroupBuilder(String string);

    PatchNameBuilder newPatchNameBuilder(StringParamSpec paramSpec);
}
