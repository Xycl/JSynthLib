package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;

public class UiLabelBuilder extends CtrlrComponentBuilder<String> {

    public UiLabelBuilder(String object) {
        super(object);
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel, getUniqueName(object));
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, "", panel);

        component.setComponentLabelVisible(0);
        component.setUiLabelBgColour("0x00000000");
        component.setUiLabelTextColour("ffff8fd8");
        component.setUiLabelOutline(0);
        component.setUiLabelOutlineColour("0x00000000");
        component.setUiLabelJustification("centred");
        component.setUiLabelFitFont(0);
        component.setUiLabelFont("Arial;11;0;0;0;0;1");
        component.setUiLabelText(object);
        component.setUiLabelDisplaysAllValues(0);
        component.setUiLabelDisplayFormat("%n(%N) ( %v(%h)");
        component.setUiLabelInputHighlightTextColour("0xffffffff");
        component.setUiLabelInputHighlightColour("0xff0000ff");
        component.setUiLabelEditOnSingleClick(0);
        component.setUiLabelEditOnDoubleClick(0);
        component.setUiLabelEditFocusDiscardsChanges(1);
        component.setUiLabelInputAllowedChars("");
        component.setUiLabelInputMaxLength(1024);
        component.setUiLabelChangedCbk("-- None");
        component.setUiType("uiLabel");
        setComponentRectangle(component, rect);
        return modulator;
    }

}
