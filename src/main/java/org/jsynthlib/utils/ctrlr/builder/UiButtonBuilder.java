package org.jsynthlib.utils.ctrlr.builder;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.SliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.SysexFormulaParser;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamValues;

public class UiButtonBuilder extends CtrlrMidiComponentBuilder {

    private List<String> contents;
    private boolean toggle;

    public UiButtonBuilder(SliderSpecWrapper object,
            SysexFormulaParser formulaParser) {
        super(object, formulaParser);
        contents = new ArrayList<String>();
        contents.add("OFF");
        contents.add("ON");
        toggle = true;
    }

    public UiButtonBuilder(IntParamSpec object, SysexFormulaParser formulaParser) {
        this(SliderSpecWrapper.Factory.newWrapper(object), formulaParser);

        if (object.isSetPatchParamValues()) {
            contents.clear();
            PatchParamValues paramValues = object.getPatchParamValues();
            String[] valueArray = paramValues.getPatchParamValueArray();
            for (String string : valueArray) {
                contents.add(string);
            }
            toggle = false;
        }
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

        component.setUiButtonTrueValue(1);
        component.setUiButtonFalseValue(0);
        component.setUiButtonIsToggle(toggle ? 1 : 0);
        component.setUiButtonColourOn("ff00ff68");
        component.setUiButtonColourOff("ff89a997");
        component.setUiButtonTextColourOn("0xff000000");
        component.setUiButtonTextColourOff("0xff454545");
        StringBuilder contentBuilder = new StringBuilder();
        boolean first = true;
        for (String string : contents) {
            if (first) {
                first = false;
            } else {
                contentBuilder.append("\n");
            }
            contentBuilder.append(string);
        }
        component.setUiButtonContent(contentBuilder.toString());
        component.setUiButtonConnectedLeft(0);
        component.setUiButtonConnectedRight(0);
        component.setUiButtonConnectedTop(0);
        component.setUiButtonConnectedBottom(0);
        component.setUiButtonRepeat(0);
        component.setUiButtonRepeatRate(100);
        component.setUiButtonTriggerOnMouseDown(0);

        component.setUiType("uiButton");
        rect.setSize(57, 44);
        setComponentRectangle(component, rect);

        return modulator;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

}
