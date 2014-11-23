package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.PatchParamGroup;

import com.google.inject.Inject;

public class UiGroupBuilder extends CtrlrComponentBuilderBase<String> {

    private String uiGroupText;

    @Inject
    public UiGroupBuilder(DriverContext context) {
        super(context);
    }

    public void setPatchParamGroup(PatchParamGroup group) {
        setObject(group.getName());
    }

    @Override
    public void setObject(String object) {
        super.setObject(object);
        uiGroupText = object;
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel);
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

    @Override
    protected String getModulatorName() {
        return getUniqueName(getObject());
    }

}
