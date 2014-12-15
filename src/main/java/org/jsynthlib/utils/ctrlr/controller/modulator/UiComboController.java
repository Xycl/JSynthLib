package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;

import com.google.inject.Inject;

public class UiComboController extends MidiModulatorControllerBase {

    public interface Factory {
        UiComboController newUiComboController(SliderSpecWrapper sliderSpec);
    }

    @Inject
    public UiComboController(SliderSpecWrapper sliderSpec) {
        super(sliderSpec);
    }


    // @Override
    // protected String getName() {
    // return getObject().getName();
    // }

    @Override
    public void init() {
        super.init();
        getComponent().setUiComboArrowColour("ffffffff");
        getComponent().setUiComboOutlineColour("8f000000");
        getComponent().setUiComboTextColour("0xff000000");
        getComponent().setUiComboTextJustification("centred");
        getComponent().setUiComboFont("<Sans-Serif>;14;0;0;0;0;1");
        getComponent().setUiComboMenuFont("<Sans-Serif>;16;0;0;0;0;1");
        getComponent().setUiComboButtonColour("ffb7b7b7");
        getComponent().setUiComboBgColour("0xffffffff");
        getComponent().setUiComboMenuBackgroundColour("0xfff0f0f0");
        getComponent().setUiComboMenuFontColour("0xff000000");
        getComponent().setUiComboMenuHighlightColour("ffadd8e6");
        getComponent().setUiComboMenuFontHighlightedColour("0xff232323");
        getComponent().setUiComboContent("OFF(0\nPULSE(1\nWAVE(2\nBOTH(3");
        getComponent().setUiComboMenuBackgroundRibbed(1);
        getComponent().setUiComboButtonGradient(0);
        getComponent().setUiComboButtonGradientColour1("ff0000ff");
        getComponent().setUiComboButtonGradientColour2("ff00008b");
        getComponent().setUiComboButtonWidthOverride(0);
        getComponent().setUiComboButtonWidth(16);
        getComponent().setUiComboDynamicContent(0);
        getComponent().setUiComboSelectedId(-1);
        getComponent().setUiComboSelectedIndex(-1);
        getComponent().setUiType("uiCombo");
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(64, 73);
        super.setRect(rect);
    }
}
