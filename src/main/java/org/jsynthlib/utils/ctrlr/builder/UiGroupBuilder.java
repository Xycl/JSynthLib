package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.xmldevice.PatchParamGroup;

public class UiGroupBuilder extends CtrlrComponentBuilder<String> {

    private String uiGroupText;

    public UiGroupBuilder(PatchParamGroup object) {
        this(object.getName());
    }

    public UiGroupBuilder(String object) {
        super(object);
        uiGroupText = object;
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel, object);
        ComponentType component = modulator.addNewComponent();
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
        component.setComponentLayerUid(panel.getUiPanelEditor()
                .getUiPanelCanvasLayer().getUiPanelCanvasLayerUid());
        setGroupAttributes(component, group);
        setComponentRectangle(component, rect);
        return modulator;
    }

    public String getUiGroupText() {
        return uiGroupText;
    }

    public void setUiGroupText(String uiGroupText) {
        this.uiGroupText = uiGroupText;
    }

}
