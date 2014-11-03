package org.jsynthlib.device.viewcontroller.widgets;

import java.util.ArrayList;

import org.jsynthlib.core.SwingEnvelopeValueSetter;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.jsynthlib.device.model.handler.IParamModel;
import org.jsynthlib.device.model.handler.ISender;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget.Node;
import org.jsynthlib.device.viewcontroller.widgets.EnvelopeWidget.Param;

public class SwingEnvelopeAdapter extends AbstractEnvelopeWidgetAdapter {

    private final EnvelopeWidget widget;

    public SwingEnvelopeAdapter(EnvelopeWidget widget) {
        this.widget = widget;
        setType(Type.ENVELOPE);
    }

    @Override
    public EnvelopeNode[] getNodes() {
        ArrayList<EnvelopeNodeAdapter> list = new ArrayList<EnvelopeNodeAdapter>();
        Node[] nodes = widget.nodes;
        for (Node node : nodes) {
            list.add(new EnvelopeNodeAdapter(node));
        }
        return list.toArray(new EnvelopeNode[list.size()]);
    }

    @Override
    public IValueSetter getValueSetter() {
        return new SwingEnvelopeValueSetter(widget,
                getFader());
    }

    @Override
    public int getSliderNum() {
        return widget.getSliderNum();
    }

    @Override
    public int getMin() {
        Param param = widget.getParams()[getFader() - getSliderNum()];
        Node node = widget.getNodes()[param.node];
        if (param.isX) {
            return node.getMinX();
        } else {
            return node.getMinY();
        }
    }

    @Override
    public int getMax() {
        Param param = widget.getParams()[getFader() - getSliderNum()];
        Node node = widget.getNodes()[param.node];
        if (param.isX) {
            return node.getMaxX();
        } else {
            return node.getMaxY();
        }
    }

    @Override
    public int getValue() {
        Param param = widget.getParams()[getFader() - getSliderNum()];
        Node node = widget.getNodes()[param.node];
        if (param.isX) {
            return node.getPmodelX().get();
        } else {
            return node.getPmodelY().get();
        }
    }

    public static class EnvelopeNodeAdapter extends EnvelopeNode {

        private final IParamModel xModel;
        private final IParamModel yModel;
        private final ISender xSender;
        private final ISender ySender;

        public EnvelopeNodeAdapter(Node origin) {
            super(new EnvelopeXParam(origin.getMinX(), origin.getMaxX(),
                    origin.getNameX(), origin.isInvertX()), new EnvelopeYParam(
                    origin.getMinY(), origin.getMaxY(), origin.getNameY(),
                    origin.getBaseY()));
            this.xModel = origin.getPmodelX();
            this.yModel = origin.getPmodelY();
            this.xSender = origin.getSenderX();
            this.ySender = origin.getSenderY();
        }

        public IParamModel getxModel() {
            return xModel;
        }

        public IParamModel getyModel() {
            return yModel;
        }

        public ISender getxSender() {
            return xSender;
        }

        public ISender getySender() {
            return ySender;
        }

    }

    @Override
    public IParamModel getParamModel(EnvelopeNode node, boolean xModel) {
        EnvelopeNodeAdapter adapter = (EnvelopeNodeAdapter) node;
        if (xModel) {
            return adapter.getxModel();
        } else {
            return adapter.getyModel();
        }
    }

    @Override
    public boolean isShowing() {
        return widget.isShowing();
    }

    @Override
    public String getUniqueName(FrameWrapper frame) {
        return AbstractSwingWidgetAdapter.getUniqueName(frame, widget);
    }
}
