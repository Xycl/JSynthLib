package core;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;

import core.EnvelopeWidget;
import core.valuesetter.IValueSetter;

public class EnvelopeValueSetter implements IValueSetter {

    private EnvelopeWidget widget;
    private int faderOffset;

    public EnvelopeValueSetter(EnvelopeWidget widget, int faderOffset) {
        super();
        this.widget = widget;
        this.faderOffset = faderOffset;
    }

    @Override
    public void setValue(final int value) {
        GuiActionRunner.execute(new GuiQuery<Object>() {
            public Object executeInEDT() {
                widget.setFaderValue(faderOffset, value);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                return null;
            }
        });
    }

}
