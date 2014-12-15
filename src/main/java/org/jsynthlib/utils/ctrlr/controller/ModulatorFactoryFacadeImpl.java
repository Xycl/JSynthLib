package org.jsynthlib.utils.ctrlr.controller;

import org.jsynthlib.utils.ctrlr.controller.modulator.NameCharSliderController;
import org.jsynthlib.utils.ctrlr.controller.modulator.PatchNameController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiCombinedGroupController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiEnvelopeController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGlobalButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGlobalButtonController.Globalbuttons;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiGroupController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiImageButtonController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiIncDecButtonsController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiKnobController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiLabelController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiSliderController;
import org.jsynthlib.utils.ctrlr.controller.modulator.UiTabController;
import org.jsynthlib.utils.ctrlr.domain.GlobalSliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.CombinedGroup;
import org.jsynthlib.xmldevice.EnvelopeParamSpec;
import org.jsynthlib.xmldevice.EnvelopeSpec;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamGroup;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ModulatorFactoryFacadeImpl implements ModulatorFactoryFacade {

    @Inject
    private PatchNameController.Factory patchNameFactory;

    @Inject
    private UiImageButtonController.Factory uiImageButtonFactory;

    @Inject
    private UiButtonController.Factory uiButtonFactory;

    @Inject
    private UiKnobController.Factory uiKnobFactory;

    @Inject
    private UiGroupController.Factory uiGroupFactory;

    @Inject
    private UiTabController.Factory uiTabFactory;

    @Inject
    private UiEnvelopeController.Factory uiEnvelopeFactory;

    @Inject
    private UiCombinedGroupController.Factory uiCombinedGroupFactory;

    @Inject
    private UiIncDecButtonsController.Factory uiIncDecButtonsFactory;

    @Inject
    private UiLabelController.Factory uiLabelFactory;

    @Inject
    private UiSliderController.Factory uiSliderFactory;

    @Inject
    private NameCharSliderController.Factory nameCharSliderFactory;

    @Inject
    private UiGlobalButtonController.Factory globalButtonFactory;

    @Override
    public PatchNameController newPatchNameController(StringParamSpec paramSpec) {
        PatchNameController controller =
                patchNameFactory.newPatchNameController(paramSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiButtonController newUiButtonController(IntParamSpec paramSpec) {
        UiButtonController controller =
                uiButtonFactory.newUiButtonController(paramSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiButtonController newUiButtonController(SliderSpecWrapper wrapper) {
        UiButtonController controller =
                uiButtonFactory.newUiButtonController(wrapper);
        controller.init();
        return controller;
    }

    @Override
    public UiCombinedGroupController newUiCombinedGroupController(
            CombinedGroup combGroup) {
        UiCombinedGroupController controller =
                uiCombinedGroupFactory.newUiCombinedGroupController(combGroup);
        controller.init();
        return controller;
    }

    @Override
    public UiEnvelopeController newUiEnvelopeController(
            EnvelopeSpec envelopeSpec) {
        UiEnvelopeController controller =
                uiEnvelopeFactory.newUiEnvelopeController(envelopeSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiGroupController newUiGroupController(PatchParamGroup group) {
        UiGroupController controller =
                uiGroupFactory.newUiGroupController(group);
        controller.init();
        return controller;
    }

    @Override
    public UiGroupController newUiGroupController(String name) {
        UiGroupController controller =
                uiGroupFactory.newUiGroupController(name);
        controller.init();
        return controller;
    }

    @Override
    public UiImageButtonController newUiImageButtonController(
            IntParamSpec paramSpec) {
        UiImageButtonController controller =
                uiImageButtonFactory.newUiImageButtonController(paramSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiIncDecButtonsController newUiIncDecButtonsController(
            EnvelopeParamSpec object, int index) {
        UiIncDecButtonsController controller =
                uiIncDecButtonsFactory.newUiIncDecButtonsController(object,
                        index);
        controller.init();
        return controller;
    }

    @Override
    public UiKnobController newUiKnobController(IntParamSpec paramSpec) {
        UiKnobController controller =
                uiKnobFactory.newUiKnobController(paramSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiLabelController newUiLabelController(String name) {
        UiLabelController controller =
                uiLabelFactory.newUiLabelController(name);
        controller.init();
        return controller;
    }

    @Override
    public UiLabelController newUiLabelController(StringParamSpec spec) {
        UiLabelController controller =
                uiLabelFactory.newUiLabelController(spec);
        controller.init();
        return controller;
    }

    @Override
    public UiSliderController newUiSliderController(IntParamSpec paramSpec) {
        UiSliderController controller =
                uiSliderFactory.newUiSliderController(paramSpec);
        controller.init();
        return controller;
    }

    @Override
    public UiSliderController newUiSliderController(SliderSpecWrapper wrapper) {
        UiSliderController controller =
                uiSliderFactory.newUiSliderController(wrapper);
        controller.init();
        return controller;
    }

    @Override
    public UiTabController newUiTabController(PatchParamGroup[] groups) {
        UiTabController controller = uiTabFactory.newUiTabController(groups);
        controller.init();
        return controller;
    }

    @Override
    public NameCharSliderController newNameCharSliderController(
            GlobalSliderSpecWrapper wrapper) {
        NameCharSliderController controller =
                nameCharSliderFactory.newNameCharSliderController(wrapper);
        controller.init();
        return controller;
    }

    @Override
    public UiGlobalButtonController newUiGlobalButtonController(
            Globalbuttons button) {
        UiGlobalButtonController controller =
                globalButtonFactory.newUiGlobalButtonController(button);
        controller.init();
        return controller;
    }
}
