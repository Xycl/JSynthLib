package org.jsynthlib.device.viewcontroller.widgets;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import org.apache.log4j.Logger;
import org.jsynthlib.device.model.AbstractEnvelopeParam;
import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;

/** Actual canvas for the envelope lines. */
public class Envelope extends JPanel {

    private static final long serialVersionUID = 1L;

    private final transient Logger log = Logger.getLogger(getClass());

    /** Array of Nodes provided by constructor */
    private EnvelopeModel model;
    /** Array of Params (parameter/variable). */
    protected Param[] params;

    private final List<ChangeListener<AbstractEnvelopeParam>> changeListeners;

    private float scaleX;
    private float scaleY;
    private int xorigin;
    private int canvasHeight;

    private boolean enabled = true; // for setEnabled()
    private static final int DELTA = 6;
    private final EnvelopeChangeListener changeListener;
    private final JPanel envelopeCanvas;
    private JPanel valuePane;

    private final int xpadding;

    private final int ypadding;

    private int maxX;
    private int maxY;

    public Envelope(final EnvelopeModel model, int xpadding, int ypadding) {
        super();

        this.changeListeners = new ArrayList<ChangeListener<AbstractEnvelopeParam>>();
        this.xpadding = xpadding;
        this.ypadding = ypadding;
        changeListener = new EnvelopeChangeListener();
        setModel(model);


        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 3;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        envelopeCanvas = new EnvelopeCanvas();
        add(envelopeCanvas, constraints);
        createValuePane();
    }

    public Envelope() {
        this(new DefaultEnvelopeModel(new EnvelopeNode[] {
                new EnvelopeNode(new EnvelopeXParam(0, 0, "Time 1", false),
                        new EnvelopeYParam(0, 100, "Level 1", 0)),
                new EnvelopeNode(new EnvelopeXParam(0, 100, "Time 2", false),
                        new EnvelopeYParam(0, 100, "Level 2", 0)),
                new EnvelopeNode(new EnvelopeXParam(0, 100, "Time 3", false),
                        new EnvelopeYParam(0, 100, "Level 3", 0)),
                new EnvelopeNode(new EnvelopeXParam(0, 100, "Time 4", false),
                        new EnvelopeYParam(0, 100, "Level 4", 0)) }), 2, 2);
    }

    void createValuePane() {
        if (valuePane != null) {
            remove(valuePane);
        }
        valuePane = new JPanel();
        valuePane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.WEST;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(ypadding, xpadding, ypadding, xpadding);

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        for (int j = 0; j < params.length; j++) {
            gbc.gridy = j;
            // name of he X/Y axis parameters
            gbc.gridx = 0;
            valuePane.add(params[j].label, gbc);
            // X/Y axis value
            gbc.gridx = 1;
            valuePane.add(params[j].textField, gbc);
        }

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 3;
        constraints.weighty = 1;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;

        constraints.weightx = 1;
        add(valuePane, constraints);
    }

    /** Convert X value to X position in the canvas. */
    int xPos(int value) {
        return xorigin + (int) (value * scaleX);
    }

    /** Convert X value for node[i] to X position in the canvas. */
    int xPos(int i, int value) {
        EnvelopeNode node = model.getNode(i);
        if (i == 0) {
            if (node.isInvertX()) {
                return xPos(node.getMaxX() - value);
            } else {
                return xPos(value);
            }
        } else {
            if (node.isInvertX()) {
                return model.getNode(i - 1).getPosX()
                        + (int) ((node.getMaxX() - value) * scaleX);
            } else {
                return model.getNode(i - 1).getPosX() + (int) (value * scaleX);
            }
        }
    }

    /** Convert Y value to Y position in the canvas. */
    int yPos(int value) {
        return canvasHeight - (int) (value * scaleY);
    }



    @Override
    protected void paintComponent(Graphics g) {
        envelopeCanvas.repaint();
        super.paintComponent(g);
    }



    private class MyListener extends MouseInputAdapter {
        /** dragging node number */
        private int dragNodeIdx = -1;
        private boolean toggle = false;

        @Override
        public void mousePressed(MouseEvent e) {
            if (!enabled) {
                dragNodeIdx = -1;
                return;
            }

            int x = e.getX();
            int y = e.getY();
            // Select the first close node.
            // Change search order every time for the case when
            // multiple nodes overlap each other.
            if (toggle) {
                toggle = false;
                for (int i = 0; i < model.getNumNodes(); i++) {
                    if (isClose(model.getNode(i), x, y)) {
                        dragNodeIdx = i;
                        return;
                    }
                }
            } else {
                toggle = true;
                for (int i = model.getNumNodes() - 1; i >= 0; i--) {
                    if (isClose(model.getNode(i), x, y)) {
                        dragNodeIdx = i;
                        return;
                    }
                }
            }
            // not found
            dragNodeIdx = -1;
        }

        private boolean isClose(EnvelopeNode node, int x, int y) {
            // ignore static node
            return ((node.isVariableX() || node.isVariableY())
                    && (Math.abs(x - node.getPosX()) < DELTA) && (Math.abs(y
                    - node.getPosY()) < DELTA));
        }

        /** last mouse position clocked or dragged. */
        private int oldx, oldy;

        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragNodeIdx == -1) {
                return;
            }

            int x = e.getX();
            int y = e.getY();
            if ((x == oldx) && (y == oldy)) {
                return;
            }

            EnvelopeNode node = model.getNode(dragNodeIdx);
            // move the selected node one dot by one dot.
            // and send Sysex Message (added Jan. 7, 2005)
            if (node.isVariableX() && x != oldx) {
                int xVal = node.getXValue();
                if (node.isInvertX()) {
                    if (x > oldx) { // right
                        while (x - node.getPosX() > DELTA
                                && xVal > node.getMinX()) {
                            node.setPosX(xPos(dragNodeIdx, --xVal));
                        }
                    } else { // left
                        while (x - node.getPosX() < -DELTA
                                && xVal < node.getMaxX()) {
                            node.setPosX(xPos(dragNodeIdx, ++xVal));
                        }
                    }
                } else {
                    if (x > oldx) { // right
                        while (x - node.getPosX() > DELTA
                                && xVal < node.getMaxX()) {
                            node.setPosX(xPos(dragNodeIdx, ++xVal));
                        }
                    } else { // left
                        while (x - node.getPosX() < -DELTA
                                && xVal > node.getMinX()) {
                            node.setPosX(xPos(dragNodeIdx, --xVal));
                        }
                    }
                }
                node.setXValue(xVal);
                params[node.getFaderNumX()].textField.setText(String
                        .valueOf(xVal));
                oldx = x;
            }
            if (node.isVariableY() && y != oldy) {
                int yVal = node.getYValue();
                if (y < oldy) { // up
                    while (y - node.getPosY() < -DELTA && yVal < node.getMaxY()) {
                        node.setPosY(yPos(node.getBaseY() + ++yVal));
                    }
                } else { // down
                    while (y - node.getPosY() > DELTA && yVal > node.getMinY()) {
                        node.setPosY(yPos(node.getBaseY() + --yVal));
                    }
                }
                node.setYValue(yVal);
                params[node.getFaderNumY()].textField.setText(String
                        .valueOf(yVal));
                oldy = y;
            }
        }
    }

    /** Data structure for Paramaters. */
    static class Param {
        /**
         * JLabel widgets which show the names of the X/Y axis parameters riding
         * each access.
         */
        private final JLabel label;
        /** JTextField which show the X/Y axis value. */
        private final JTextField textField;
        /** array index of 'nodes' for the corresponding Node. */
        private final int node;
        /** true for X value, false for Y value. */
        private final boolean isX;

        Param(JLabel label, JTextField textField, int node,
                boolean isX) {
            this.label = label;
            this.textField = textField;
            this.node = node;
            this.isX = isX;
        }
    }

    /**
     * Add a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to add
     */
    public void addChangeListener(final ChangeListener<AbstractEnvelopeParam> listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to remove
     */
    public void removeChangeListener(final ChangeListener<AbstractEnvelopeParam> listener) {
        changeListeners.remove(listener);
    }

    public EnvelopeModel getModel() {
        return model;
    }

    public void setModel(EnvelopeModel model) {
        if (this.model != null) {
            for (EnvelopeNode node : this.model) {
                node.removeXChangeListener(changeListener);
                node.removeYChangeListener(changeListener);
            }
        }

        this.model = model;
        int numFaders = model.getNumFaders();
        params = new Param[numFaders];

        // calculate maximum total x/y value
        int x = 0;
        int y = 0;

        int j = 0;
        int i = 0;
        for (EnvelopeNode node : model) {
            if (node.isVariableX()) {
                params[j] =
                        new Param(new JLabel(node.getNameX()), new JTextField(
                                String.valueOf(node.getXValue()), 4), i, true);
                params[j].textField.setEditable(false);
                node.setFaderNumX(j);
                j++;
            }
            if (node.isVariableY()) {
                params[j] =
                        new Param(new JLabel(node.getNameY()), new JTextField(
                                String.valueOf(node.getYValue()), 4), i, false);
                params[j].textField.setEditable(false);
                node.setFaderNumY(j);
                j++;
            }
            node.addXChangeListener(changeListener);
            node.addYChangeListener(changeListener);
            i++;

            x += node.getMaxX();
            if ((node.getMaxY() + node.getBaseY() > y)
                    && node.getMinY() != EnvelopeNode.SAME) {
                y = node.getMaxY() + node.getBaseY();
            }
        }
        maxX = x;
        maxY = y;

        createValuePane();
        repaint();
    }

    class EnvelopeChangeListener implements ChangeListener<AbstractEnvelopeParam> {

        @Override
        public void changed(
                ObservableValue<? extends AbstractEnvelopeParam> arg0,
                AbstractEnvelopeParam arg1, AbstractEnvelopeParam arg2) {
          repaint(); // invokes paintComponent()
        }
    }

    class EnvelopeCanvas extends JPanel {

        private static final long serialVersionUID = 1L;

        public EnvelopeCanvas() {
            super();

            MyListener myListener = new MyListener();
            addMouseListener(myListener);
            addMouseMotionListener(myListener);

            setMinimumSize(new Dimension(300, 50));
            setPreferredSize(getMinimumSize());

            // recalculate canvas size dependent parameters every time size is
            // changed including the time the canvas is created.
            addComponentListener(new ComponentListener() {
                @Override
                public void componentResized(ComponentEvent e) {
                    // scale by using actual canvas size
                    Insets insets = getInsets();
                    int canvasWidth =
                            getWidth() - insets.left - insets.right - DELTA * 2;
                    canvasHeight =
                            getHeight() - insets.top - insets.bottom - DELTA;
                    scaleX = canvasWidth / (float) maxX;
                    scaleY = (canvasHeight - DELTA) / (float) maxY;
                    xorigin = insets.left + DELTA;

                    int sumX = 0;
                    for (int i = 0; i < model.getNumNodes(); i++) {
                        // calculate coordinates on the canvas
                        sumX += getX(i);
                        model.getNode(i).setPosX(xPos(sumX));
                        model.getNode(i).setPosY(yPos(getY(i)));
                    }
                }

                @Override
                public void componentMoved(ComponentEvent e) {
                }

                @Override
                public void componentShown(ComponentEvent e) {
                }

                @Override
                public void componentHidden(ComponentEvent e) {
                }
            });

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int sumX = 0;
            for (int i = 0; i < model.getNumNodes(); i++) {
                // calculate coordinates on the canvas
                sumX += getX(i);
                model.getNode(i).setPosX(xPos(sumX));
                model.getNode(i).setPosY(yPos(getY(i)));

                // draw a rectangle and a line. Filled box is used for a node
                // for variable.
                if ((model.getNode(i).isVariableX() || model.getNode(i)
                        .isVariableY()) && enabled) {
                    g.fillRect(model.getNode(i).getPosX() - DELTA / 2, model
                            .getNode(i).getPosY() - DELTA / 2, DELTA, DELTA);
                } else {
                    g.drawRect(model.getNode(i).getPosX() - DELTA / 2, model
                            .getNode(i).getPosY() - DELTA / 2, DELTA, DELTA);
                }
                if (i > 0) {
                    g.drawLine(model.getNode(i).getPosX(), model.getNode(i)
                            .getPosY(), model.getNode(i - 1).getPosX(), model
                            .getNode(i - 1).getPosY());
                }
            }
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            Envelope.this.enabled = enabled;
            repaint();
        }

        /** Return X axis value of <code>node[i]</code>. */
        int getX(int i) {
            if (!model.getNode(i).isVariableX()) {
                return model.getNode(i).getMinX();
            } else if (model.getNode(i).isInvertX()) {
                return (model.getNode(i).getMaxX() - model.getNode(i)
                        .getXValue());
            } else {
                return (model.getNode(i).getXValue());
            }
        }

        /** Return Y axis value of <code>node[i]</code>. */
        int getY(int i) {
            if (model.getNode(i).getMinY() == EnvelopeNode.SAME) {
                return getY(i - 1);
            } else if (!model.getNode(i).isVariableY()) {
                return model.getNode(i).getBaseY() + model.getNode(i).getMinY();
            } else {
                return model.getNode(i).getBaseY()
                        + model.getNode(i).getYValue();
            }
        }

    }

    JPanel getEnvelopeCanvas() {
        return envelopeCanvas;
    }

    float getScaleX() {
        return scaleX;
    }

    float getScaleY() {
        return scaleY;
    }
}
