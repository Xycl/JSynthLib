package org.jsynthlib.utils.ctrlr.builder.component;

import org.jsynthlib.utils.ctrlr.builder.DefaultSliderSpecWrapper;
import org.jsynthlib.utils.ctrlr.builder.SliderSpecWrapper;

public class GlobalSliderSpecWrapper extends DefaultSliderSpecWrapper implements
        SliderSpecWrapper {

    private int offset;

    public GlobalSliderSpecWrapper(String name) {
        this(name, 1);
    }

    public GlobalSliderSpecWrapper(String name, int max) {
        super();
        setName(name);
        setMax(max);
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
