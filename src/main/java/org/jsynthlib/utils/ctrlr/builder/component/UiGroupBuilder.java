package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.ComponentType;
import org.jsynthlib.xmldevice.PatchParamGroup;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UiGroupBuilder extends GroupBuilderBase<String> {

    public interface Factory {
        UiGroupBuilder newUiGroupBuilder(PatchParamGroup group);

        UiGroupBuilder newUiGroupBuilder(String name);
    }

    private String uiGroupText;

    @AssistedInject
    public UiGroupBuilder(@Assisted PatchParamGroup group) {
        super();
        setObject(group.getName());
        uiGroupText = group.getName();
    }

    @AssistedInject
    public UiGroupBuilder(@Assisted String object) {
        super();
        setObject(object);
        uiGroupText = object;
    }

    @Override
    protected void setComponentAttributes(ComponentType component) {
        super.setComponentAttributes(component);
        component.setUiGroupText(uiGroupText);
        component.setUiGroupTextColour("6c000000");
        component.setUiGroupOutlineColour1("8c000000");
        component.setUiGroupOutlineColour2("ff262626");
        component.setUiGroupBackgroundColour1("a3a3a3");
        component.setUiGroupBackgroundColour2("ffffff");
        component.setUiGroupOutlineGradientType(1);
        component.setUiGroupBackgroundGradientType(1);
        component.setUiGroupBackgroundImage("");
        component.setUiGroupBackgroundImageLayout(36);
        component.setUiGroupBackgroundImageAlpha(255);
        component.setUiGroupTextPlacement("topRight");
        component.setUiGroupTextFont("<Sans-Serif>;22;1;1;0;0;1");
        component.setUiGroupOutlineThickness(2);
        component.setUiGroupOutlineRoundAngle(8);
        component.setUiGroupTextMargin(0);
        component.setUiType("uiGroup");
    }

    public String getUiGroupText() {
        return uiGroupText;
    }

    public void setUiGroupText(String uiGroupText) {
        this.uiGroupText = uiGroupText;
    }

    @Override
    protected String getModulatorName() {
        return getUniqueName(getObject());
    }
}
