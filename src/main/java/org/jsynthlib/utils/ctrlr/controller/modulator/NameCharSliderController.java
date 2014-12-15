package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;

import org.jsynthlib.utils.ctrlr.domain.GlobalSliderSpecWrapper;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

public class NameCharSliderController extends UiSliderController {

    public interface Factory {
        NameCharSliderController newNameCharSliderController(
                GlobalSliderSpecWrapper wrapper);
    }

    @Inject
    @Named("prefix")
    private String prefix;
    private final GlobalSliderSpecWrapper wrapper;

    @Inject
    public NameCharSliderController(@Assisted GlobalSliderSpecWrapper wrapper) {
        super(wrapper);
        this.wrapper = wrapper;
    }

    @Override
    public void init() {
        super.init();
        setRect(new Rectangle());
        setMuteOnStart(true);
        setComponentVisible(false);
        setVstIndex(-1);
        setMidiMessageSysExFormula("");
        setModulatorName(prefix + wrapper.getOffset());
    }

    public void setPatchCharMax(int max) {
        setMax(max);
    }

}
