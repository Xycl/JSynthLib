package org.jsynthlib.test.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.valuesetter.IValueSetter;

public abstract class WidgetAdapter {
    
    protected static final List<String> UNIQUE_NAMES = new ArrayList<String>();

    protected final Logger LOG = Logger.getLogger(getClass());

    public enum Type {
        /** ENVELOPE */
        ENVELOPE,
        /** LABEL */
        LABEL,
        /** CHECKBOX */
        CHECKBOX,
        /** COMBOBOX */
        COMBOBOX,
        /** ID_COMBOBOX */
        ID_COMBOBOX,
        /** KNOB */
        KNOB,
        /** PATCH_NAME */
        PATCH_NAME,
        /** SCROLLBAR */
        SCROLLBAR,
        /** SPINNER */
        SPINNER,
        /** TREE */
        TREE,
        /** SCROLLBAR_LOOKUP */
        SCROLLBAR_LOOKUP,
        /** MULTI */
        MULTI
    }

    private boolean enabled;
    private IValueSetter valueSetter;
    private int min;
    private int max;
    private int value;
    private Type type;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public abstract IValueSetter getValueSetter();

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public abstract boolean isShowing();
    public abstract String getUniqueName(FrameWrapper frame);

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static void clear() {
        UNIQUE_NAMES.clear();
    }
}
