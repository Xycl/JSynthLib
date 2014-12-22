package org.jsynthlib.utils.ctrlr.controller.modulator;

import org.jsynthlib.xmldevice.StringParamSpec;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.inject.name.Named;

public class UiLabelController extends ModulatorControllerBase {

    public interface Factory {
        UiLabelController newUiLabelController(String name);

        UiLabelController newUiLabelController(StringParamSpec spec);
    }

    @Inject
    @Named("prefix")
    private String prefix;
    private final String name;
    private int length;

    @AssistedInject
    public UiLabelController(@Assisted String name) {
        this.name = name;
        length = 1024;
    }

    @AssistedInject
    public UiLabelController(@Assisted StringParamSpec spec) {
        this(spec.getName());
        length = spec.getLength();
    }

    @Override
    public void init() {
        super.init();
        setLabelBgColor("0x00000000");
        setUiLabelChangedCbk("-- None");
        setEditOnSingleClick(false);
        setLength(length);
        setModulatorName(prefix + getUniqueName(name));

        setUiLabelText(name);
        getComponent().setComponentLabelVisible(0);
        setUiLabelJustification("centred");
        getComponent().setUiLabelTextColour("ffff8fd8");
        getComponent().setUiLabelOutline(0);

        getComponent().setUiLabelFitFont(0);
        setUiLabelFontSize(11);
        getComponent().setUiLabelDisplaysAllValues(0);
        getComponent().setUiLabelDisplayFormat("%n(%N) ( %v(%h)");
        getComponent().setUiLabelInputHighlightTextColour("0xffffffff");
        getComponent().setUiLabelInputHighlightColour("0xff0000ff");
        getComponent().setUiLabelEditOnDoubleClick(0);
        getComponent().setUiLabelEditFocusDiscardsChanges(1);
        getComponent().setUiLabelInputAllowedChars("");
        getComponent().setUiType("uiLabel");
    }

    public final void setUiLabelFontSize(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append("Arial;").append(size).append(";0;0;0;0;1");
        getComponent().setUiLabelFont(sb.toString());
    }

    public final void setUiLabelJustification(String justification) {
        getComponent().setUiLabelJustification(justification);
    }

    public final void setLength(int length) {
        this.length = length;
        getComponent().setUiLabelInputMaxLength(length);
    }

    public final void setEditOnSingleClick(boolean editOnSingleClick) {
        getComponent().setUiLabelEditOnSingleClick(editOnSingleClick ? 1 : 0);
    }

    public final void setUiLabelChangedCbk(String uiLabelChangedCbk) {
        getComponent().setUiLabelChangedCbk(uiLabelChangedCbk);
    }

    public final void setRawModulatorName(String modulatorName) {
        setModulatorName(modulatorName);
    }

    public final void setUiLabelText(String uiLabelText) {
        getComponent().setUiLabelText(uiLabelText);
    }

    public final void setLabelBgColor(String labelBgColor) {
        getComponent().setUiLabelBgColour(labelBgColor);
        getComponent().setUiLabelOutlineColour(labelBgColor);
    }
}
