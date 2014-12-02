package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;

public class UiComboBuilder extends CtrlrMidiComponentBuilder {

    @Override
    protected void setComponentAttributes(ComponentType component) {
        super.setComponentAttributes(component);

        component.setUiComboArrowColour("ffffffff");
        component.setUiComboOutlineColour("8f000000");
        component.setUiComboTextColour("0xff000000");
        component.setUiComboTextJustification("centred");
        component.setUiComboFont("<Sans-Serif>;14;0;0;0;0;1");
        component.setUiComboMenuFont("<Sans-Serif>;16;0;0;0;0;1");
        component.setUiComboButtonColour("ffb7b7b7");
        component.setUiComboBgColour("0xffffffff");
        component.setUiComboMenuBackgroundColour("0xfff0f0f0");
        component.setUiComboMenuFontColour("0xff000000");
        component.setUiComboMenuHighlightColour("ffadd8e6");
        component.setUiComboMenuFontHighlightedColour("0xff232323");
        component.setUiComboContent("OFF(0\nPULSE(1\nWAVE(2\nBOTH(3");
        component.setUiComboMenuBackgroundRibbed(1);
        component.setUiComboButtonGradient(0);
        component.setUiComboButtonGradientColour1("ff0000ff");
        component.setUiComboButtonGradientColour2("ff00008b");
        component.setUiComboButtonWidthOverride(0);
        component.setUiComboButtonWidth(16);
        component.setUiComboDynamicContent(0);
        component.setUiComboSelectedId(-1);
        component.setUiComboSelectedIndex(-1);
        component.setUiType("uiCombo");
    }

    @Override
    protected String getName() {
        return getObject().getName();
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(64, 73);
        super.setRect(rect);
    }
}
