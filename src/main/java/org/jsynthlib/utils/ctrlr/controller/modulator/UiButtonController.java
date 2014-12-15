package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import org.jsynthlib.utils.ctrlr.domain.SliderSpecWrapper;
import org.jsynthlib.xmldevice.IntParamSpec;
import org.jsynthlib.xmldevice.PatchParamValues;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class UiButtonController extends MidiModulatorControllerBase {

    public interface Factory {
        UiButtonController newUiButtonController(IntParamSpec paramSpec);

        UiButtonController newUiButtonController(SliderSpecWrapper wrapper);
    }

    private int height;
    private int width;
    private List<String> contents;
    private boolean toggle;

    @AssistedInject
    public UiButtonController(@Assisted SliderSpecWrapper sliderSpec) {
        super(sliderSpec);
        contents = new ArrayList<String>();
        contents.add("OFF");
        contents.add("ON");
        width = 57;
        height = 44;
        toggle = true;
    }

    @AssistedInject
    public UiButtonController(@Assisted IntParamSpec object) {
        this(SliderSpecWrapper.Factory.newWrapper(object));
        if (object.isSetPatchParamValues()) {
            contents = new ArrayList<String>();
            PatchParamValues paramValues = object.getPatchParamValues();
            String[] valueArray = paramValues.getPatchParamValueArray();
            for (String string : valueArray) {
                contents.add(string);
            }
            toggle = false;
        }
    }

    @Override
    public void init() {
        super.init();
        setContents(contents);
        setModulatorName(getSliderSpec().getName());
        getComponent().setUiButtonTrueValue(1);
        getComponent().setUiButtonFalseValue(0);
        getComponent().setUiButtonIsToggle(toggle ? 1 : 0);
        setButtonColorOn("ff00ff68");
        setButtonColorOff("ff89a997");
        getComponent().setUiButtonTextColourOn("0xff000000");
        getComponent().setUiButtonTextColourOff("0xff454545");
        getComponent().setUiButtonConnectedLeft(0);
        getComponent().setUiButtonConnectedRight(0);
        getComponent().setUiButtonConnectedTop(0);
        getComponent().setUiButtonConnectedBottom(0);
        getComponent().setUiButtonRepeat(0);
        getComponent().setUiButtonRepeatRate(100);
        getComponent().setUiButtonTriggerOnMouseDown(0);

        getComponent().setUiType("uiButton");
    }

    public void setContents(List<String> contents) {
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
        getComponent().setUiButtonContent(contentBuilder.toString());
    }

    public void setButtonColorOn(String buttonColorOn) {
        getComponent().setUiButtonColourOn(buttonColorOn);
    }

    public void setButtonColorOff(String buttonColorOff) {
        getComponent().setUiButtonColourOff(buttonColorOff);
    }

    public void setHeight(int height) {
        this.height = height;
        if (getRect() != null) {
            setRect(getRect());
        }
    }

    public void setWidth(int width) {
        this.width = width;
        if (getRect() != null) {
            setRect(getRect());
        }
    }

    @Override
    public void setRect(Rectangle rect) {
        rect.setSize(width, height);
        super.setRect(rect);
    }

}
