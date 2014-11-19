package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.IntParamSpec;

public class UiComboBuilder extends CtrlrMidiComponentBuilder {

    public UiComboBuilder(IntParamSpec object, SysexFormulaParser formulaParser) {
        super(SliderSpecWrapper.Factory.newWrapper(object), formulaParser);
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
        rect.setSize(64, 73);
        setComponentRectangle(component, rect);

        return modulator;
    }

}
