package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.utils.ctrlr.builder.component.GlobalSliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.builder.component.NameCharSliderBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.PatchNameBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiCombinedGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiEnvelopeBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiGroupBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiImageButtonBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiIncDecButtonsBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiKnobBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiLabelBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiSliderBuilder;
import org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BuilderFactoryFacadeImpl implements BuilderFactoryFacade {

    @Inject
    private PatchNameBuilder.Factory patchNameBuilderFactory;

    @Inject
    private UiImageButtonBuilder.Factory uiImageButtonBuilderFactory;

    @Inject
    private UiButtonBuilder.Factory uiButtonBuilderFactory;

    @Inject
    private UiKnobBuilder.Factory uiKnobBuilderFactory;

    @Inject
    private UiGroupBuilder.Factory uiGroupBuilderFactory;

    @Inject
    private UiTabBuilder.Factory uiTabBuilderFactory;

    @Inject
    private UiEnvelopeBuilder.Factory uiEnvelopeBuilderFactory;

    @Inject
    private UiCombinedGroupBuilder.Factory uiCombinedGroupBuilderFactory;

    @Inject
    private UiIncDecButtonsBuilder.Factory uiIncDecButtonsBuilderFactory;

    @Inject
    private UiLabelBuilder.Factory uiLabelBuilderFactory;

    @Inject
    private UiSliderBuilder.Factory uiSliderBuilderFactory;

    @Inject
    private NameCharSliderBuilder.Factory nameCharSliderBuilderFactory;

    @Override
    public PatchNameBuilder newPatchNameBuilder(StringParamSpec paramSpec) {
        return patchNameBuilderFactory.newPatchNameBuilder(paramSpec);
    }

    @Override
    public UiButtonBuilder newUiButtonBuilder(IntParamSpec paramSpec) {
        return uiButtonBuilderFactory.newUiButtonBuilder(paramSpec);
    }

    @Override
    public UiButtonBuilder newUiButtonBuilder(SliderSpecWrapper wrapper) {
        return uiButtonBuilderFactory.newUiButtonBuilder(wrapper);
    }

    @Override
    public UiCombinedGroupBuilder newUiCombinedGroupBuilder(
            CombinedGroup combGroup) {
        return uiCombinedGroupBuilderFactory
                .newUiCombinedGroupBuilder(combGroup);
    }

    @Override
    public UiEnvelopeBuilder newUiEnvelopeBuilder(EnvelopeSpec envelopeSpec) {
        return uiEnvelopeBuilderFactory.newUiEnvelopeBuilder(envelopeSpec);
    }

    @Override
    public UiGroupBuilder newUiGroupBuilder(PatchParamGroup group) {
        return uiGroupBuilderFactory.newUiGroupBuilder(group);
    }

    @Override
    public UiGroupBuilder newUiGroupBuilder(String name) {
        return uiGroupBuilderFactory.newUiGroupBuilder(name);
    }

    @Override
    public UiImageButtonBuilder newUiImageButtonBuilder(IntParamSpec paramSpec) {
        return uiImageButtonBuilderFactory.newUiImageButtonBuilder(paramSpec);
    }

    @Override
    public UiIncDecButtonsBuilder newUiIncDecButtonsBuilder(
            EnvelopeParamSpec object, int index) {
        return uiIncDecButtonsBuilderFactory.newUiIncDecButtonsBuilder(object,
                index);
    }

    @Override
    public UiKnobBuilder newUiKnobBuilder(IntParamSpec paramSpec) {
        return uiKnobBuilderFactory.newUiKnobBuilder(paramSpec);
    }

    @Override
    public UiLabelBuilder newUiLabelBuilder(String name) {
        return uiLabelBuilderFactory.newUiLabelBuilder(name);
    }

    @Override
    public UiLabelBuilder newUiLabelBuilder(StringParamSpec spec) {
        return uiLabelBuilderFactory.newUiLabelBuilder(spec);
    }

    @Override
    public UiSliderBuilder newUiSliderBuilder(IntParamSpec paramSpec) {
        return uiSliderBuilderFactory.newUiSliderBuilder(paramSpec);
    }

    @Override
    public UiSliderBuilder newUiSliderBuilder(SliderSpecWrapper wrapper) {
        return uiSliderBuilderFactory.newUiSliderBuilder(wrapper);
    }

    @Override
    public UiTabBuilder newUiTabBuilder(PatchParamGroup[] groups) {
        return uiTabBuilderFactory.newUiTabBuilder(groups);
    }

    @Override
    public NameCharSliderBuilder newNameCharSliderBuilder(
            GlobalSliderSpecWrapper wrapper) {
        return nameCharSliderBuilderFactory.newNameCharSliderBuilder(wrapper);
    }
}
