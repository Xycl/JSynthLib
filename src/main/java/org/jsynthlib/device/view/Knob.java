package org.jsynthlib.device.view;

import javafx.scene.control.Slider;

public class Knob extends Slider {

    public Knob() {
        setSkinClassName(KnobSkin.class.getName());
    }
}
