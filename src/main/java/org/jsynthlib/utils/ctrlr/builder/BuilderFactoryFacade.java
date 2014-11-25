package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.utils.ctrlr.builder.component.PatchNameBuilder.Factory;

public interface BuilderFactoryFacade
        extends
        Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiButtonBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiCombinedGroupBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiEnvelopeBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiGroupBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiImageButtonBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiIncDecButtonsBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiKnobBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiLabelBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiSliderBuilder.Factory,
        org.jsynthlib.utils.ctrlr.builder.component.UiTabBuilder.Factory {

}
