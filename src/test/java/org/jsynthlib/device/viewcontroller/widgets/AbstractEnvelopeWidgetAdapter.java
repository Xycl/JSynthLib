package org.jsynthlib.device.viewcontroller.widgets;

import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.test.adapter.WidgetAdapter;

public abstract class AbstractEnvelopeWidgetAdapter extends WidgetAdapter {

    private int fader;

    public abstract int getSliderNum();

    public int getFader() {
        return fader;
    }

    public void setFader(int fader) {
        this.fader = fader;
    }

    public abstract EnvelopeNode[] getNodes();
    public abstract IParamModel getParamModel(EnvelopeNode node, boolean xModel);
}
