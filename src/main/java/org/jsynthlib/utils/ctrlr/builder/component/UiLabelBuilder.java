package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class UiLabelBuilder extends CtrlrComponentBuilderBase<String> {

    public interface Factory {
        UiLabelBuilder newUiLabelBuilder(String name);
    }

    @Inject
    @Named("prefix")
    private String prefix;

    private int length;

    @Inject
    public UiLabelBuilder(@Assisted String name) {
        this.length = 1024;
        setObject(name);
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel);
        ComponentType component = modulator.addNewComponent();
        setDefaultComponentFields(component, group, "", panel);

        component.setComponentLabelVisible(0);
        component.setUiLabelBgColour("0x00000000");
        component.setUiLabelTextColour("ffff8fd8");
        component.setUiLabelOutline(0);
        component.setUiLabelOutlineColour("0x00000000");
        component.setUiLabelJustification("centred");
        component.setUiLabelFitFont(0);
        component.setUiLabelFont("Arial;11;0;0;0;0;1");
        component.setUiLabelText(getObject());
        component.setUiLabelDisplaysAllValues(0);
        component.setUiLabelDisplayFormat("%n(%N) ( %v(%h)");
        component.setUiLabelInputHighlightTextColour("0xffffffff");
        component.setUiLabelInputHighlightColour("0xff0000ff");
        component.setUiLabelEditOnSingleClick(0);
        component.setUiLabelEditOnDoubleClick(0);
        component.setUiLabelEditFocusDiscardsChanges(1);
        component.setUiLabelInputAllowedChars("");
        component.setUiLabelInputMaxLength(length);
        component.setUiLabelChangedCbk("-- None");
        component.setUiType("uiLabel");
        setComponentRectangle(component, rect);
        return modulator;
    }

    @Override
    protected String getModulatorName() {
        return prefix + getUniqueName(getObject());
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }

}
