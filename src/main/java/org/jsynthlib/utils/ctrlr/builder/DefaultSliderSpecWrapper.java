package org.jsynthlib.utils.ctrlr.builder;

import org.jsynthlib.xmldevice.MidiSenderReference;
import org.jsynthlib.xmldevice.ParamModelReference;

public class DefaultSliderSpecWrapper implements SliderSpecWrapper {

    private String name;
    private int min;
    private int max;
    private MidiSenderReference msRef;
    private ParamModelReference pmRef;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMin() {
        return min;
    }

    @Override
    public int getMax() {
        return max;
    }

    @Override
    public MidiSenderReference getMidiSender() {
        return msRef;
    }

    @Override
    public boolean isSetMidiSender() {
        return msRef != null;
    }

    @Override
    public boolean isSetParamModel() {
        return pmRef != null;
    }

    @Override
    public ParamModelReference getParamModel() {
        return pmRef;
    }

    public MidiSenderReference getMsRef() {
        return msRef;
    }

    public void setMsRef(MidiSenderReference msRef) {
        this.msRef = msRef;
    }

    public ParamModelReference getPmRef() {
        return pmRef;
    }

    public void setPmRef(ParamModelReference pmRef) {
        this.pmRef = pmRef;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
