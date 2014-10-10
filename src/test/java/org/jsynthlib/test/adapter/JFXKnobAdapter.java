package org.jsynthlib.test.adapter;

import javafx.application.Platform;

import org.jemmy.fx.SceneDock;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.device.view.Knob;

public class JFXKnobAdapter extends AbstractJFXWidgetAdapter {

    private final Knob knob;

    public JFXKnobAdapter(SceneDock scene, Knob knob) {
        super(scene, knob);
        this.knob = knob;
        setMax((int) knob.getMax());
        setMin((int) knob.getMin());
        setType(Type.KNOB);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new IValueSetter() {

            @Override
            public void setValue(final int value) {
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {
                        knob.valueProperty().set(value);
                    }
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        };
    }
}
