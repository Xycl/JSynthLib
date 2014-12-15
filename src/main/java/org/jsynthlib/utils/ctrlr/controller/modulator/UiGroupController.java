package org.jsynthlib.utils.ctrlr.controller.modulator;

import org.jsynthlib.xmldevice.PatchParamGroup;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UiGroupController extends GroupControllerBase {


    public interface Factory {
        UiGroupController newUiGroupController(PatchParamGroup group);

        UiGroupController newUiGroupController(String name);
    }

    private final String name;

    @AssistedInject
    public UiGroupController(@Assisted PatchParamGroup group) {
        this(group.getName());
    }

    @AssistedInject
    public UiGroupController(@Assisted String object) {
        this.name = object;
    }

    @Override
    public void init() {
        super.init();
        setUiGroupText(name);
        setComponentVisibleName(name);
        getComponent().setUiGroupTextColour("6c000000");
        getComponent().setUiGroupOutlineColour1("8c000000");
        getComponent().setUiGroupOutlineColour2("ff262626");
        getComponent().setUiGroupBackgroundColour1("a3a3a3");
        getComponent().setUiGroupBackgroundColour2("ffffff");
        getComponent().setUiGroupOutlineGradientType(1);
        getComponent().setUiGroupBackgroundGradientType(1);
        getComponent().setUiGroupBackgroundImage("");
        getComponent().setUiGroupBackgroundImageLayout(36);
        getComponent().setUiGroupBackgroundImageAlpha(255);
        getComponent().setUiGroupTextPlacement("topRight");
        getComponent().setUiGroupTextFont("<Sans-Serif>;22;1;1;0;0;1");
        getComponent().setUiGroupOutlineThickness(2);
        getComponent().setUiGroupOutlineRoundAngle(8);
        getComponent().setUiGroupTextMargin(0);
        getComponent().setUiType("uiGroup");
        setModulatorName(getUniqueName(name));
    }

    public void setUiGroupText(String uiGroupText) {
        getComponent().setUiGroupText(uiGroupText);
        if (uiGroupText != null) {
            setComponentVisibleName(uiGroupText);
        }
    }
}
