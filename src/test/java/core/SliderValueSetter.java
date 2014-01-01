package core;

import org.fest.swing.fixture.JSliderFixture;

import core.valuesetter.IValueSetter;

public class SliderValueSetter implements IValueSetter {

    private JSliderFixture fixture;

    public SliderValueSetter(JSliderFixture fixture) {
        super();
        this.fixture = fixture;
    }

    @Override
    public void setValue(int value) {
        fixture.slideTo(value);
    }

}
