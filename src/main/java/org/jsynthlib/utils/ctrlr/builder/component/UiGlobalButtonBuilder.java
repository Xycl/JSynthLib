package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.ArrayList;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;

public class UiGlobalButtonBuilder extends UiButtonBuilder {

    public UiGlobalButtonBuilder(String name, Rectangle rect) {
        super(new GlobalSliderSpecWrapper(name));
        ArrayList<String> contents = new ArrayList<String>();
        contents.add(name);
        setContents(contents);
        setButtonColorOff(getButtonColorOn());
        setMidiMessageCtrlrValue(1);
        setMuteOnStart(true);
        setExcludeFromSnapshot(true);
        setWidth((int) rect.getWidth());
        setHeight((int) rect.getHeight());
        setRect(rect);
        setLabelVisible(false);
    }

    @Override
    public ModulatorType createModulator(PanelType panel, ModulatorType group,
            int vstIndex) {
        return super.createModulator(panel, group, -1);
    }

    @Override
    protected void createMidiElement(ModulatorType modulator) {
        createMidiElement(modulator, "");
    }

    @Override
    protected String getModulatorName() {
        return getObject().getName();
    }

    void setMethodName(String name) {
        setLuaModulatorValueChange(name);
    }
}
