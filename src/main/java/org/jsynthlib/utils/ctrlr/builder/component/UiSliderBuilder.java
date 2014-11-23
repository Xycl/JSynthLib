package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.driverContext.DriverContext;
import org.jsynthlib.xmldevice.IntParamSpec;

import com.google.inject.Inject;

public class UiSliderBuilder extends CtrlrMidiComponentBuilder {

    private int width;
    private int height;
    private int sliderValuePosition;

    @Inject
    public UiSliderBuilder(DriverContext context) {
        super(context);
        this.width = 31;
        this.height = 73;
        this.sliderValuePosition = 4;
    }

    public void setIntParamSpec(IntParamSpec paramSpec) {
        super.setObject(SliderSpecWrapper.Factory.newWrapper(paramSpec));
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel, vstIndex);

        createMidiElement(modulator);
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, getObject().getName(),
                panel);

        component.setUiSliderStyle(getUiSliderStyle());
        component.setUiSliderMin(getObject().getMin());
        component.setUiSliderMax(getObject().getMax());
        component.setUiSliderInterval(1);
        component.setUiSliderDoubleClickEnabled(1);
        component.setUiSliderDoubleClickValue(0);
        component.setUiSliderValuePosition(sliderValuePosition);
        component.setUiSliderValueHeight(12);
        component.setUiSliderValueWidth(64);
        component.setUiSliderTrackCornerSize(5);
        component.setUiSliderThumbCornerSize(3);
        component.setUiSliderThumbWidth(0);
        component.setUiSliderThumbHeight(0);
        component.setUiSliderThumbFlatOnLeft(0);
        component.setUiSliderThumbFlatOnRight(0);
        component.setUiSliderThumbFlatOnTop(0);
        component.setUiSliderThumbFlatOnBottom(0);
        component.setUiSliderValueTextColour("0xff000000");
        component.setUiSliderValueBgColour("ffffff");
        component.setUiSliderRotaryOutlineColour("ff000000");
        component.setUiSliderRotaryFillColour("ff000000");
        component.setUiSliderThumbColour("0xffff0000");
        component.setUiSliderValueHighlightColour("0xff0000ff");
        component.setUiSliderValueOutlineColour("0xffffffff");
        component.setUiSliderTrackColour("0xff0f0f0f");
        component.setUiSliderIncDecButtonColour("0xff0000ff");
        component.setUiSliderIncDecTextColour("0xffffffff");
        component.setUiSliderValueFont("<Sans-Serif>;12;1;0;0;0;1");
        component.setUiSliderValueTextJustification("centred");
        component.setUiSliderVelocitySensitivity(1);
        component.setUiSliderVelocityThreshold(1);
        component.setUiSliderVelocityOffset(0);
        component.setUiSliderVelocityMode(0);
        component.setUiSliderVelocityModeKeyTrigger(1);
        component.setUiSliderSpringMode(0);
        component.setUiSliderSpringValue(0);
        component.setUiSliderMouseWheelInterval(1);
        component.setUiSliderPopupBubble(0);
        component.setUiType("uiSlider");
        rect.setSize(width, height);
        setComponentRectangle(component, rect);

        return modulator;
    }

    protected String getUiSliderStyle() {
        return "LinearVertical";
    }

    protected int getSliderValuePosition() {
        return sliderValuePosition;
    }

    protected void setSliderValuePosition(int sliderValuePosition) {
        this.sliderValuePosition = sliderValuePosition;
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
}
