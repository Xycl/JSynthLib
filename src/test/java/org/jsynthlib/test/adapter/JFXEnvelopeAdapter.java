package org.jsynthlib.test.adapter;

import java.util.ArrayList;
import java.util.Iterator;

import org.jemmy.fx.SceneDock;
import org.jsynthlib.core.TitleFinder.FrameWrapper;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.device.model.EnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.IParamModel;
import org.jsynthlib.device.view.Envelope;
import org.jsynthlib.device.viewcontroller.widgets.AbstractEnvelopeWidgetAdapter;
import org.jsynthlib.test.adapter.AbstractJFXWidgetAdapter.ControlDisplayer;

public class JFXEnvelopeAdapter extends AbstractEnvelopeWidgetAdapter {

    private final Envelope envelope;
    private final SceneDock sceneDock;

    public JFXEnvelopeAdapter(SceneDock scene, Envelope envelope) {
        this.envelope = envelope;
        this.sceneDock = scene;
        setType(Type.ENVELOPE);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.test.adapter.WidgetAdapter#getValueSetter()
     */
    @Override
    public IValueSetter getValueSetter() {
        return new JFXEnvelopeValueSetter(envelope, getFader());
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.test.adapter.WidgetAdapter#isShowing()
     */
    @Override
    public boolean isShowing() {
        return envelope.isVisible();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.test.adapter.WidgetAdapter#getUniqueName(org.jsynthlib.
     * core.TitleFinder.FrameWrapper)
     */
    @Override
    public String getUniqueName(FrameWrapper frame) {
        return getUniqueNameAndDisplayWidget(envelope, sceneDock);
    }

    public static String getUniqueNameAndDisplayWidget(Envelope envelope,
            SceneDock sceneDock) {
        ControlDisplayer controlDisplayer =
                new ControlDisplayer(sceneDock, envelope);
        controlDisplayer.showWidget();
        return "/" + controlDisplayer.getPath() + controlDisplayer.getName();
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.viewcontroller.widgets.AbstractEnvelopeWidgetAdapter
     * #getSliderNum()
     */
    @Override
    public int getSliderNum() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.viewcontroller.widgets.AbstractEnvelopeWidgetAdapter
     * #getNodes()
     */
    @Override
    public EnvelopeNode[] getNodes() {
        EnvelopeModel model = envelope.getModel();
        int numNodes = model.getNumNodes();
        ArrayList<EnvelopeNode> list = new ArrayList<EnvelopeNode>();
        for (int i = 0; i < numNodes; i++) {
            list.add(model.getNode(i));
        }
        return list.toArray(new EnvelopeNode[list.size()]);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.jsynthlib.device.viewcontroller.widgets.AbstractEnvelopeWidgetAdapter
     * #getParamModel(org.jsynthlib.device.model.EnvelopeNode, boolean)
     */
    @Override
    public IParamModel getParamModel(EnvelopeNode node, boolean xModel) {
        /*
         * Should just not return null if node is variable.
         */
        if (xModel && node.isVariableX()) {
            return new IParamModel() {

                @Override
                public void set(int value) {
                }

                @Override
                public int get() {
                    return 0;
                }
            };
        } else if (!xModel && node.isVariableY()) {
            return new IParamModel() {

                @Override
                public void set(int value) {
                }

                @Override
                public int get() {
                    return 0;
                }
            };
        }
        return null;
    }

    @Override
    public int getMin() {
        Iterator<EnvelopeNode> iterator = envelope.getModel().iterator();
        while (iterator.hasNext()) {
            EnvelopeNode node = iterator.next();
            if (node.isVariableX() && node.getFaderNumX() == getFader()) {
                return node.getMinX();
            } else if (node.isVariableY() && node.getFaderNumY() == getFader()) {
                return node.getMinY();
            }
        }
        throw new IllegalStateException("Could not find min for fader " + getFader());
    }

    @Override
    public int getMax() {
        Iterator<EnvelopeNode> iterator = envelope.getModel().iterator();
        while (iterator.hasNext()) {
            EnvelopeNode node = iterator.next();
            if (node.isVariableX() && node.getFaderNumX() == getFader()) {
                return node.getMaxX();
            } else if (node.isVariableY() && node.getFaderNumY() == getFader()) {
                return node.getMaxY();
            }
        }
        throw new IllegalStateException("Could not find max for fader " + getFader());
    }

    @Override
    public int getValue() {
        Iterator<EnvelopeNode> iterator = envelope.getModel().iterator();
        while (iterator.hasNext()) {
            EnvelopeNode node = iterator.next();
            if (node.isVariableX() && node.getFaderNumX() == getFader()) {
                return node.getXValue();
            } else if (node.isVariableY() && node.getFaderNumY() == getFader()) {
                return node.getYValue();
            }
        }
        throw new IllegalStateException("Could not find value for fader " + getFader());
    }
}
