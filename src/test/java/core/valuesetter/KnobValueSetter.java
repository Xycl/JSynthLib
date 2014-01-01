package core.valuesetter;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;

import core.KnobWidget;

public class KnobValueSetter implements IValueSetter {

    private KnobWidget widget;

    public KnobValueSetter(KnobWidget widget) {
        super();
        this.widget = widget;
    }

    @Override
    public void setValue(final int value) {
        GuiActionRunner.execute(new GuiQuery<Object>() {
            public Object executeInEDT() {
                widget.setValue(value);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                return null;
            }
        });
    }

}
