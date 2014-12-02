package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

import javax.inject.Named;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class NameCharSliderBuilder extends UiSliderBuilder {

    public interface Factory {
        NameCharSliderBuilder newNameCharSliderBuilder(
                GlobalSliderSpecWrapper wrapper);
    }

    @Inject
    @Named("prefix")
    private String prefix;
    private final GlobalSliderSpecWrapper globalWrapper;

    @Inject
    public NameCharSliderBuilder(@Assisted GlobalSliderSpecWrapper wrapper) {
        super(wrapper);
        this.globalWrapper = wrapper;
        setRect(new Rectangle());
        setMuteOnStart(true);
        setComponentVisible(false);
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

    public void setPatchCharMax(int max) {
        setMax(max);
    }

    @Override
    protected String getModulatorName() {
        return prefix + globalWrapper.getOffset();
    }

}
