package core.valuesetter;

import org.fest.swing.fixture.JComboBoxFixture;

public class ComboBoxValueSetter implements IValueSetter {

    private JComboBoxFixture fixture;

    public ComboBoxValueSetter(JComboBoxFixture fixture) {
        super();
        this.fixture = fixture;
    }

    @Override
    public void setValue(int value) {
        fixture.selectItem(value);
    }

}
