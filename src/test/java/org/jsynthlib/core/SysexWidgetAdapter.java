package org.jsynthlib.core;

import java.util.List;

import org.jsynthlib.core.TitleFinder.FrameWrapper;

public class SysexWidgetAdapter {

    public static List<SysexWidgetAdapter> findSysexWidgets(FrameWrapper patchEditor) {
        return null;
    }

    public enum Type {
        CHECKBOX,
        COMBOBOX,
        SLIDER,
        ID_COMBOBOX,
        KNOB,
        ENVELOPE,
        PATCHNAME,
        TREE,
        SLIDER_LOOKUP,
        MULTI
    }

    private Type type;
    private int max;
    private int min;
    private String uniqueName;
}
