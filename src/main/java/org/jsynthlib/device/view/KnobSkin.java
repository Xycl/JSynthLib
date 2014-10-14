package org.jsynthlib.device.view;

import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;

/**
 * A simple knob skin for slider
 *
 * @author Jasper Potts
 */
public class KnobSkin implements Skin<Slider> {

    private Slider control;
    private double dragStartX, dragStartY;

    public KnobSkin(Slider slider) {
        super();
        this.control = slider;
    }

    /**
     * @param position
     *            The position of mouse in 0=min to 1=max range
     */
    public void knobRelease(MouseEvent e, double position) {
        final Slider slider = getSkinnable();
        slider.setValueChanging(false);
        // detect click rather than drag
        if (Math.abs(e.getX() - dragStartX) < 3
                && Math.abs(e.getY() - dragStartY) < 3) {
            slider.adjustValue((position + slider.getMin())
                    * (slider.getMax() - slider.getMin()));
        }
    }

    /**
     * @param position
     *            The position of mouse in 0=min to 1=max range
     */
    public void knobPressed(MouseEvent e, double position) {
        // If not already focused, request focus
        final Slider slider = getSkinnable();
        if (!slider.isFocused()) {
            slider.requestFocus();
        }
        slider.setValueChanging(true);
        dragStartX = e.getX();
        dragStartY = e.getY();
    }

    /**
     * @param position
     *            The position of mouse in 0=min to 1=max range
     */
    public void knobDragged(MouseEvent e, double position) {
        final Slider slider = getSkinnable();
        if (slider.getMin() < 0) {
            slider.adjustValue((position
                    * (slider.getMax() - slider.getMin())) + slider.getMin());
        } else {
            slider.adjustValue((position + slider.getMin())
                    * (slider.getMax() - slider.getMin()));
        }
    }

    void home() {
        final Slider slider = getSkinnable();
        slider.adjustValue(slider.getMin());
    }

    void decrementValue() {
        getSkinnable().decrement();
    }

    void end() {
        final Slider slider = getSkinnable();
        slider.adjustValue(slider.getMax());
    }

    void incrementValue() {
        getSkinnable().increment();
    }

    @Override
    public Slider getSkinnable() {
        return control;
    }

    @Override
    public Node getNode() {
        return null;
    }

    @Override
    public void dispose() {
        control = null;
    }

}

