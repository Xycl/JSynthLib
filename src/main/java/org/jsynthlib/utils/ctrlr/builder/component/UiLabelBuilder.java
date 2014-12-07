package org.jsynthlib.utils.ctrlr.builder.component;

import org.ctrlr.panel.ComponentType;
import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

public class UiLabelBuilder extends CtrlrComponentBuilderBase<String> {

    public interface Factory {
        UiLabelBuilder newUiLabelBuilder(String name);

        UiLabelBuilder newUiLabelBuilder(StringParamSpec spec);
    }

    @Inject
    @Named("prefix")
    private String prefix;

    private int length = 1024;

    private boolean editOnSingleClick = false;

    private String uiLabelChangedCbk = "-- None";

    private String uiLabelText;

    private String labelBgColor = "0x00000000";

    private String modulatorName;

    @AssistedInject
    public UiLabelBuilder(@Assisted String name) {
        setObject(name);
        uiLabelText = name;
    }

    @AssistedInject
    public UiLabelBuilder(@Assisted StringParamSpec spec) {
        this(spec.getName());
        this.length = spec.getLength();
    }

    @Override
    protected void setComponentAttributes(ComponentType component) {
        super.setComponentAttributes(component);

        component.setComponentLabelVisible(0);

        component.setUiLabelBgColour(labelBgColor);
        component.setUiLabelTextColour("ffff8fd8");
        component.setUiLabelOutline(0);
        component.setUiLabelOutlineColour(labelBgColor);
        component.setUiLabelJustification("centred");
        component.setUiLabelFitFont(0);
        component.setUiLabelFont("Arial;11;0;0;0;0;1");
        component.setUiLabelText(uiLabelText);
        component.setUiLabelDisplaysAllValues(0);
        component.setUiLabelDisplayFormat("%n(%N) ( %v(%h)");
        component.setUiLabelInputHighlightTextColour("0xffffffff");
        component.setUiLabelInputHighlightColour("0xff0000ff");
        component.setUiLabelEditOnSingleClick(editOnSingleClick ? 1 : 0);
        component.setUiLabelEditOnDoubleClick(0);
        component.setUiLabelEditFocusDiscardsChanges(1);
        component.setUiLabelInputAllowedChars("");
        component.setUiLabelInputMaxLength(length);
        component.setUiLabelChangedCbk(uiLabelChangedCbk);
        component.setUiType("uiLabel");
    }

    @Override
    protected String getModulatorName() {
        if (modulatorName == null) {
            return prefix + getUniqueName(getObject());
        } else {
            return modulatorName;
        }
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public boolean isEditOnSingleClick() {
        return editOnSingleClick;
    }

    public void setEditOnSingleClick(boolean editOnSingleClick) {
        this.editOnSingleClick = editOnSingleClick;
    }

    public String getUiLabelChangedCbk() {
        return uiLabelChangedCbk;
    }

    public void setUiLabelChangedCbk(String uiLabelChangedCbk) {
        this.uiLabelChangedCbk = uiLabelChangedCbk;
    }

    public void setModulatorName(String modulatorName) {
        this.modulatorName = modulatorName;
    }

    public String getUiLabelText() {
        return uiLabelText;
    }

    public void setUiLabelText(String uiLabelText) {
        this.uiLabelText = uiLabelText;
    }

    public String getLabelBgColor() {
        return labelBgColor;
    }

    public void setLabelBgColor(String labelBgColor) {
        this.labelBgColor = labelBgColor;
    }
}
