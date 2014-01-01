package core.valuesetter;

import org.fest.swing.fixture.JCheckBoxFixture;

public class CheckBoxValueSetter implements IValueSetter {

    private JCheckBoxFixture fixture;
    private int min;

    public CheckBoxValueSetter(JCheckBoxFixture fixture, int min) {
        super();
        this.fixture = fixture;
        this.min = min;
    }

    @Override
    public void setValue(int value) {
        if (value == min) {
            fixture.uncheck();
        } else {
            fixture.check();
        }
    }

}
