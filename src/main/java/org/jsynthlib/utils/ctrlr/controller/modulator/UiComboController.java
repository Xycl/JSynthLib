package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiComboController extends MidiModulatorControllerBase {

    public interface Factory {
        UiComboController newUiComboController(SliderSpecWrapper sliderSpec);
    }

    private int width = 64;
    private int height = 73;

    @Inject
    public UiComboController(@Assisted SliderSpecWrapper sliderSpec) {
        super(sliderSpec);
    }

    @Override
    public void init() {
        super.init();
        getComponent().setUiComboArrowColour("ffffffff");
        getComponent().setUiComboOutlineColour("8f000000");
        getComponent().setUiComboTextColour("0xff000000");
        getComponent().setUiComboTextJustification("centred");
        getComponent().setUiComboFont("<Sans-Serif>;14;0;0;0;0;1");
        getComponent().setUiComboMenuFont("<Sans-Serif>;14;0;0;0;0;1");
        getComponent().setUiComboButtonColour("ffb7b7b7");
        getComponent().setUiComboBgColour("0xffffffff");
        getComponent().setUiComboMenuBackgroundColour("0xfff0f0f0");
        getComponent().setUiComboMenuFontColour("0xff000000");
        getComponent().setUiComboMenuHighlightColour("ffadd8e6");
        getComponent().setUiComboMenuFontHighlightedColour("0xff232323");
        getComponent().setUiComboMenuBackgroundRibbed(1);
        getComponent().setUiComboButtonGradient(0);
        getComponent().setUiComboButtonGradientColour1("ff0000ff");
        getComponent().setUiComboButtonGradientColour2("ff00008b");
        getComponent().setUiComboButtonWidthOverride(0);
        getComponent().setUiComboButtonWidth(16);
        getComponent().setUiComboDynamicContent(0);
        getComponent().setUiComboSelectedId(-1);
        getComponent().setUiComboSelectedIndex(0);
        getComponent().setUiType("uiCombo");
        setUiComboContent("OFF(0\nPULSE(1\nWAVE(2\nBOTH(3");

        getComponent().setComponentBubbleValueFont("<Sans-Serif>;14;0;0;0;0;1");
        getComponent().setComponentLabelFont("<Sans-Serif>;12;0;0;0;0;1");
    }

    public void setUiComboContent(String content) {
        getComponent().setUiComboContent(content);
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(width, height);
        super.setRect(rect);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
