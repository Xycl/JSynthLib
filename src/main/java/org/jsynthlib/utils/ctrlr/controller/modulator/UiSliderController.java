package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.IntParamSpec;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UiSliderController extends MidiModulatorControllerBase {

    public interface Factory {
        UiSliderController newUiSliderController(IntParamSpec paramSpec);

        UiSliderController newUiSliderController(SliderSpecWrapper wrapper);
    }

    private int width;
    private int height;

    @AssistedInject
    public UiSliderController(@Assisted SliderSpecWrapper wrapper) {
        super(wrapper);
        this.width = 31;
        this.height = 73;
    }

    @AssistedInject
    public UiSliderController(@Assisted IntParamSpec paramSpec) {
        this(SliderSpecWrapper.Factory.newWrapper(paramSpec));
    }

    @Override
    public void init() {
        super.init();
        setSliderValuePosition(4);
        setUiSliderStyle("LinearVertical");

        getComponent().setUiSliderInterval(1);
        getComponent().setUiSliderDoubleClickEnabled(1);
        getComponent().setUiSliderDoubleClickValue(0);
        getComponent().setUiSliderValueHeight(12);
        getComponent().setUiSliderValueWidth(64);
        getComponent().setUiSliderTrackCornerSize(5);
        getComponent().setUiSliderThumbCornerSize(3);
        getComponent().setUiSliderThumbWidth(0);
        getComponent().setUiSliderThumbHeight(0);
        getComponent().setUiSliderThumbFlatOnLeft(0);
        getComponent().setUiSliderThumbFlatOnRight(0);
        getComponent().setUiSliderThumbFlatOnTop(0);
        getComponent().setUiSliderThumbFlatOnBottom(0);
        getComponent().setUiSliderValueTextColour("0xff000000");
        getComponent().setUiSliderValueBgColour("ffffff");
        getComponent().setUiSliderRotaryOutlineColour("ff000000");
        getComponent().setUiSliderRotaryFillColour("ff000000");
        getComponent().setUiSliderThumbColour("0xffff0000");
        getComponent().setUiSliderValueHighlightColour("0xff0000ff");
        getComponent().setUiSliderValueOutlineColour("0xffffffff");
        getComponent().setUiSliderTrackColour("0xff0f0f0f");
        getComponent().setUiSliderIncDecButtonColour("0xff0000ff");
        getComponent().setUiSliderIncDecTextColour("0xffffffff");
        getComponent().setUiSliderValueFont("<Sans-Serif>;12;1;0;0;0;1");
        getComponent().setUiSliderValueTextJustification("centred");
        getComponent().setUiSliderVelocitySensitivity(1);
        getComponent().setUiSliderVelocityThreshold(1);
        getComponent().setUiSliderVelocityOffset(0);
        getComponent().setUiSliderVelocityMode(0);
        getComponent().setUiSliderVelocityModeKeyTrigger(1);
        getComponent().setUiSliderSpringMode(0);
        getComponent().setUiSliderSpringValue(0);
        getComponent().setUiSliderMouseWheelInterval(1);
        getComponent().setUiSliderPopupBubble(0);
        getComponent().setUiType("uiSlider");
    }

    @Override
    public void setMax(int max) {
        getComponent().setUiSliderMax(max);
        super.setMax(max);
    }

    @Override
    public void setMin(int min) {
        getComponent().setUiSliderMin(min);
        super.setMin(min);
    }

    public final void setUiSliderStyle(String uiSliderStyle) {
        getComponent().setUiSliderStyle(uiSliderStyle);
    }

    protected void setSliderValuePosition(int sliderValuePosition) {
        getComponent().setUiSliderValuePosition(sliderValuePosition);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(width, height);
        super.setRect(rect);
    }
}
