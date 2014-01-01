package core.valuesetter;

import org.fest.swing.fixture.JSpinnerFixture;

public class SpinnerValueSetter implements IValueSetter {

    private JSpinnerFixture fixture;

    public SpinnerValueSetter(JSpinnerFixture fixture) {
        super();
        this.fixture = fixture;
    }

    @Override
    public void setValue(int value) {
        fixture.select(new Integer(value));
    }

}
