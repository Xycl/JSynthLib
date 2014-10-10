package org.jsynthlib.device.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget;

public abstract class AbstractEnvelopeParam {

    private final int min;
    private final int max;

    private final String name;

    /** can be moved in X direction or not */
    private final boolean variable;

    /** fader number for variable value. */
    private int faderNum;
    /** coordinates on the canvas. */
    private int pos;
    private final IntegerProperty valueProperty;
    private final List<ChangeListener<AbstractEnvelopeParam>> listenerList;

    /**
     * Construcutor for a <code>Node</code>.
     * <p>
     * Using <code>null</code>s for the Models and Senders and setting min to
     * max means that the node is stationary on that axis and has no related
     * parameter.
     * <p>
     * @param minx
     *            The minimum value permitted by the synth parameter which rides
     *            the X axis of the node.
     * @param maxx
     *            The maximum value permitted by the synth parameter which rides
     *            the X axis of the node.
     * @param miny
     *            The minimum value permitted by the synth parameter which rides
     *            the Y axis of the node. Using <code>Node.SAME</code> for
     *            <code>miny</code> means that the height remains at whatever
     *            the previous node was at.
     * @param maxy
     *            The maximum value permitted by the synth parameter which rides
     *            the Y axis of the node. When <code>Node.SAME</code> is used
     *            for <code>miny</code>, this parameter is ignored.
     * @param namex
     *            The names of the X-axis parameters riding each access.
     * @param namey
     *            The names of the Y-axis parameters riding each access.
     * @see EnvelopeWidget
     */
    public AbstractEnvelopeParam(int min, int max, String name) {
        super();
        this.min = min;
        this.max = max;
        this.name = name;
        this.variable = min != max;
        valueProperty = new SimpleIntegerProperty();
        valueProperty.addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                    Number arg1, Number arg2) {
                fireChangeEvent();
            }
        });
        listenerList = new ArrayList<ChangeListener<AbstractEnvelopeParam>>();
    }

    public IntegerProperty valueProperty() {
        return valueProperty;
    }

    public int getFaderNum() {
        return faderNum;
    }

    public void setFaderNum(int faderNum) {
        this.faderNum = faderNum;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    public boolean isVariable() {
        return variable;
    }


    /**
     * Fire a notification that the knob value has changed.
     * @param node
     */
    void fireChangeEvent() {
        for (ChangeListener<AbstractEnvelopeParam> listener : listenerList) {
            listener.changed(null, this, this);
        }
    }

    public void addChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        listenerList.add(l);
    }

    public void removeChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        listenerList.remove(l);
    }

}