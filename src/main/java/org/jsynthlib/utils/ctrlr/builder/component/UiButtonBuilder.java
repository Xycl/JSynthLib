package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.ctrlr.panel.ComponentType;
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
    protected void setComponentAttributes(ComponentType component) {
        super.setComponentAttributes(component);
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
    }

    @Override
    protected String getName() {
        return getObject().getName();
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

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(width, height);
        super.setRect(rect);
    }

}
