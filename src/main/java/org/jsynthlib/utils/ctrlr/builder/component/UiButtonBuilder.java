package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamValues;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UiButtonBuilder extends CtrlrMidiComponentBuilder {

    public interface Factory {
        UiButtonBuilder newUiButtonBuilder(IntParamSpec paramSpec);

        UiButtonBuilder newUiButtonBuilder(SliderSpecWrapper wrapper);
    }

    private List<String> contents;
    private boolean toggle;
    private String buttonColorOn;
    private String buttonColorOff;
    private int height;
    private int width;

    @AssistedInject
    public UiButtonBuilder(@Assisted SliderSpecWrapper wrapper) {
        setObject(wrapper);
        contents = new ArrayList<String>();
        contents.add("OFF");
        contents.add("ON");
        toggle = true;
        buttonColorOn = "ff00ff68";
        buttonColorOff = "ff89a997";
        width = 57;
        height = 44;
    }

    @AssistedInject
    public UiButtonBuilder(@Assisted IntParamSpec object) {
        this(SliderSpecWrapper.Factory.newWrapper(object));
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
        ModulatorType modulator = createModulator(panel, vstIndex);

        createMidiElement(modulator);
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, getObject().getName(),
                panel);

        component.setUiButtonTrueValue(1);
        component.setUiButtonFalseValue(0);
        component.setUiButtonIsToggle(toggle ? 1 : 0);
        component.setUiButtonColourOn(buttonColorOn);
        component.setUiButtonColourOff(buttonColorOff);
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
        rect.setSize(width, height);
        setComponentRectangle(component, rect);

        return modulator;
    }

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public String getButtonColorOn() {
        return buttonColorOn;
    }

    public void setButtonColorOn(String buttonColorOn) {
        this.buttonColorOn = buttonColorOn;
    }

    public String getButtonColorOff() {
        return buttonColorOff;
    }

    public void setButtonColorOff(String buttonColorOff) {
        this.buttonColorOff = buttonColorOff;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

}
