package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.IntParamSpec;

public class UiSliderBuilder extends CtrlrMidiComponentBuilder {

    private final int width;
    private final int height;
    private int sliderValuePosition;

    public UiSliderBuilder(IntParamSpec object, SysexFormulaParser formulaParser) {
        this(SliderSpecWrapper.Factory.newWrapper(object), 31, 73,
                formulaParser);
    }

    protected UiSliderBuilder(SliderSpecWrapper object, int width, int height,
            SysexFormulaParser formulaParser) {
        super(object, formulaParser);
        this.width = width;
        this.height = height;
        this.sliderValuePosition = 4;
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator =
                createModulator(panel, vstIndex,
                        getUniqueName(object.getName()), object.getMin(),
                        object.getMax());

        createMidiElement(modulator);
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, object.getName(), panel);

        component.setUiSliderStyle(getUiSliderStyle());
        component.setUiSliderMin(object.getMin());
        component.setUiSliderMax(object.getMax());
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
}
