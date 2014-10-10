package org.jsynthlib.device.view;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import org.jsynthlib.device.model.AbstractEnvelopeParam;
import org.jsynthlib.device.model.DefaultEnvelopeModel;
import org.jsynthlib.device.model.EnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.model.EnvelopeXParam;
import org.jsynthlib.device.model.EnvelopeYParam;
import org.jsynthlib.device.model.IPatchDriver;
import org.jsynthlib.device.model.ISender;
import org.jsynthlib.device.viewcontroller.WidgetDataFormat;

public class Envelope extends VBox {

    private static final double DELTA = 6;

    /** Array of Nodes provided by constructor */
    private EnvelopeModel model;
    /** Array of Params (parameter/variable). */
    private EnvelopeParam[] params;

    private final List<ChangeListener<AbstractEnvelopeParam>> changeListeners;
    private final EnvelopeChangeListener changeListener;

    private GridPane valuePane;

    private EnvelopeCanvas envCanvas;

    private int maxX;
    private int maxY;

    private IPatchDriver driver;

    private int sliderNum;

    /**
     * Creates a new <code>SysexWidget</code> instance.
     * @param label
     *            a label text for the sysexWidget.
     * @param patch
     *            a <code>Patch</code>, which is edited.
     * @param nodes
     *            an array of Node.
     * @param xpadding
     *            space at the right and left border.
     * @param ypadding
     *            space at the top and bottom border.
     */
    public Envelope(final EnvelopeModel model) {
        super();

        this.changeListeners =
                new ArrayList<ChangeListener<AbstractEnvelopeParam>>();
        changeListener = new EnvelopeChangeListener();
        setModel(model);
        setMinHeight(200);
        setPrefSize(300, 200);
        setMaxWidth(300);

        setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                onMousePressed(arg0);
            }
        });

        setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent arg0) {
                onMouseDragged(arg0);
            }
        });
        createValuePane();

        widthProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                    Number arg1, Number arg2) {
                envCanvas.handleWidthChange(arg2);
                valuePane.setMaxWidth(arg2.doubleValue());
            }
        });
        heightProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0,
                    Number arg1, Number arg2) {
                envCanvas.handleHeightChange(arg2.intValue() - 40);
            }
        });
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
                        new EnvelopeYParam(0, 100, "Level 4", 0)) }));
    }

    /**
     * Add a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to add
     */
    public void addChangeListener(
            final ChangeListener<AbstractEnvelopeParam> listener) {
        changeListeners.add(listener);
    }

    /**
     * Remove a listener for notifications that the knob value has changed.
     * @param listener
     *            the listener to remove
     */
    public void removeChangeListener(
            final ChangeListener<AbstractEnvelopeParam> listener) {
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
        params = new EnvelopeParam[numFaders];

        // calculate maximum total x/y value
        int x = 0;
        int y = 0;

        int j = 0;
        int i = 0;
        for (EnvelopeNode node : model) {
            if (node.isVariableX()) {
                params[j] =
                        new EnvelopeParam(
                                new Label(node.getNameX()),
                                new TextField(String.valueOf(node.getXValue())),
                                i, true);
                params[j].textField.setEditable(false);
                params[j].textField.textProperty().bind(
                        Bindings.convert(node.getxParam().valueProperty()));
                node.setFaderNumX(j);
                j++;
            }
            if (node.isVariableY()) {
                params[j] =
                        new EnvelopeParam(
                                new Label(node.getNameY()),
                                new TextField(String.valueOf(node.getYValue())),
                                i, false);
                params[j].textField.setEditable(false);
                WidgetDataFormat format = new WidgetDataFormat(node.getBaseY());
                params[j].textField.textProperty().bindBidirectional(
                        node.getyParam().valueProperty(), format);
                node.setFaderNumY(j);
                j++;
            }
            node.addXChangeListener(changeListener);
            node.addYChangeListener(changeListener);
            i++;

            x += node.getMaxX();
            if ((node.getMaxY() > y)
                    && node.getMinY() != EnvelopeNode.SAME) {
                y = node.getMaxY();
            }

        }

        if (envCanvas != null) {
            getChildren().remove(envCanvas.canvas);
        }
        envCanvas = new EnvelopeCanvas();
        getChildren().add(envCanvas.canvas);

        maxX = x;
        maxY = y;

        createValuePane();
        envCanvas.repaint();
    }

    void createValuePane() {
        if (valuePane != null) {
            getChildren().remove(valuePane);
        }
        valuePane = new GridPane();
        valuePane.setMaxWidth(300);

        for (int j = 0; j < params.length; j++) {
            // name of he X/Y axis parameters
            int x = 0;
            int y = j % 2;
            if (y == 0) {
                x = j;
            } else {
                x = j - 1;
            }
            valuePane.add(params[j].label, x, y);
            // X/Y axis value
            valuePane.add(params[j].textField, x + 1, y);
        }

        getChildren().add(valuePane);
    }

    public void setSliderNum(int num) {
        _setSliderNum(num);
        // setTooltip(new Tooltip("Bank " + ((num - 1) / 16 + 1) + "  Slider "
        // + ((num - 1) % 16 + 1) + "  to  Bank "
        // + ((num + params.length - 2) / 16 + 1) + "  Slider "
        // + ((num + params.length - 2) % 16 + 1)));

        for (int j = 0; j < params.length; j++) {
            String t =
                    ("Bank " + ((num - 1) / 16 + 1) + "  Slider " + ((num + j - 1) % 16 + 1));
            params[j].label.setTooltip(new Tooltip(t));
            params[j].textField.setTooltip(new Tooltip(t));
        }
    }

    /**
     * Set specified by <code>fader</code> and send System Exclusive message to
     * a MIDI port.
     * <p>
     * Called by PatchEditorFrame.faderMoved(byte, byte). This method is used
     * and must be extended by a SysexWidget with multiple prameters (i.e.
     * numFaders != 1, only EnvelopeWidget now).
     * @param fader
     *            fader number.
     * @param value
     *            value to be set. [0-127]
     */
    public void setFaderValue(int fader, int value) {
        fader -= getSliderNum(); // set the 1st fader to '0'.

        EnvelopeNode node = model.getNode(params[fader].node);
        if (params[fader].isX) {
            value =
                    (int) (node.getMinX() + ((float) (value) / 127 * (node
                            .getMaxX() - node.getMinX())));
            // set paramModel and show the value.
            node.setXValue(value);
        } else {
            value =
                    (int) (node.getMinY() + ((float) (value) / 127 * (node
                            .getMaxY() - node.getMinY())));
            // set paramModel and show the value.
            node.setYValue(value);
        }
        params[fader].textField.setText(String.valueOf(value));
        // update canvas
        envCanvas.repaint();
    }

    public void setValue() {
        envCanvas.repaint();
    }

    /**
     * Set value and send MIDI messages for the value to the MIDI port of
     * <code>driver</code>. An extended class calls this when widget state is
     * chagned.
     * <p>
     * This method does not update the widget state nor do min/max range check.
     * It is caller's responsibility to do that.
     */
    // now used by EnvelopeWidget only
    protected void sendSysex(ISender sender, int value) {
        if (sender != null) { // do it only if there is a sysex-sender available
            sender.send(driver, value);
        }
    }

    public void setEnabled(boolean e) {
        envCanvas.setDisable(!e);
    }

    /** Setter of fader slider number. */
    protected void _setSliderNum(int num) {
        sliderNum = num;
    }

    /** Getter of fader slider number. */
    public int getSliderNum() {
        return sliderNum;
    }

    void onMousePressed(MouseEvent arg0) {
        if (!envCanvas.enabled) {
            envCanvas.dragNodeIdx = -1;
            return;
        }

        int x = (int) arg0.getX();
        int y = (int) arg0.getY();
        // Select the first close node.
        // Change search order every time for the case when
        // multiple nodes overlap each other.
        if (envCanvas.toggle) {
            envCanvas.toggle = false;
            for (int i = 0; i < model.getNumNodes(); i++) {
                if (envCanvas.isClose(model.getNode(i), x, y)) {
                    envCanvas.dragNodeIdx = i;
                    return;
                }
            }
        } else {
            envCanvas.toggle = true;
            for (int i = model.getNumNodes() - 1; i >= 0; i--) {
                if (envCanvas.isClose(model.getNode(i), x, y)) {
                    envCanvas.dragNodeIdx = i;
                    return;
                }
            }
        }
        // not found
        envCanvas.dragNodeIdx = -1;
    }

    void onMouseDragged(MouseEvent arg0) {
        if (envCanvas.dragNodeIdx == -1) {
            return;
        }

        int x = (int) arg0.getX();
        int y = (int) arg0.getY();
        if ((x == envCanvas.oldx) && (y == envCanvas.oldy)) {
            return;
        }

        EnvelopeNode node = model.getNode(envCanvas.dragNodeIdx);

        // move the selected node one dot by one dot.
        // and send Sysex Message (added Jan. 7, 2005)
        if (node.isVariableX() && x != envCanvas.oldx) {
            int xVal = node.getXValue();
            if (node.isInvertX()) {
                if (x > envCanvas.oldx) { // right
                    while (x - node.getPosX() > DELTA && xVal > node.getMinX()) {
                        node.setPosX(envCanvas.xPos(envCanvas.dragNodeIdx,
                                --xVal));
                    }
                } else { // left
                    while (x - node.getPosX() < -DELTA && xVal < node.getMaxX()) {
                        node.setPosX(envCanvas.xPos(envCanvas.dragNodeIdx,
                                ++xVal));
                    }
                }
            } else {
                if (x > envCanvas.oldx) { // right
                    while (x - node.getPosX() > DELTA && xVal < node.getMaxX()) {
                        node.setPosX(envCanvas.xPos(envCanvas.dragNodeIdx,
                                ++xVal));
                    }
                } else { // left
                    while (x - node.getPosX() < -DELTA && xVal > node.getMinX()) {
                        node.setPosX(envCanvas.xPos(envCanvas.dragNodeIdx,
                                --xVal));
                    }
                }
            }
            node.setXValue(xVal);
            envCanvas.oldx = x;
        }
        if (node.isVariableY() && y != envCanvas.oldy) {
            int yVal = node.getYValue();
            if (y < envCanvas.oldy) { // up
                while (y - node.getPosY() < -DELTA && yVal < node.getMaxY()) {
                    node.setPosY(envCanvas.yPos(++yVal));
                }
            } else { // down
                while (y - node.getPosY() > DELTA && yVal > node.getMinY()) {
                    node.setPosY(envCanvas.yPos(--yVal));
                }
            }
            node.setYValue(yVal);
            envCanvas.oldy = y;
        }
        envCanvas.repaint(); // invokes paintComponent()
    }

    class EnvelopeCanvas {

        private float scaleX;
        private float scaleY;
        private int xorigin;
        private int canvasHeight;
        private final Canvas canvas;

        private final boolean enabled = true; // for setEnabled()
        private static final int DELTA = 6;

        /** dragging node number */
        private int dragNodeIdx = -1;
        private boolean toggle = false;

        private boolean isClose(EnvelopeNode node, int x, int y) {
            // ignore static node
            return ((node.isVariableX() || node.isVariableY())
                    && (Math.abs(x - node.getPosX()) < DELTA) && (Math.abs(y
                    - node.getPosY()) < DELTA));
        }

        /** last mouse position clocked or dragged. */
        private int oldx, oldy;

        public EnvelopeCanvas() {
            canvas = new Canvas();
            canvas.autosize();

            // calculate maximum total x/y value
            int x = 0;
            int y = 0;
            for (EnvelopeNode node : model) {
                x += node.getMaxX();
                if ((node.getMaxY() > y)
                        && node.getMinY() != EnvelopeNode.SAME) {
                    y = node.getMaxY();
                }
            }
            maxX = x;
            maxY = y;
        }

        void handleWidthChange(Number newWidth) {
            if (canvas.getWidth() == newWidth.doubleValue()) {
                return;
            }
            canvas.setWidth(newWidth.doubleValue());

            // recalculate canvas size dependent parameters every time size is
            // changed including the time the canvas is created.
            // scale by using actual canvas size
            Insets insets = getInsets();
            int canvasWidth =
                    (int) (newWidth.intValue() - insets.getLeft()
                            - insets.getRight() - DELTA * 2);
            scaleX = (float) canvasWidth / (float) maxX;
            xorigin = (int) (insets.getLeft() + DELTA);

            int sumX = 0;
            for (int i = 0; i < model.getNumNodes(); i++) {
                // calculate coordinates on the canvas
                sumX += getX(i);
                model.getNode(i).setPosX(xPos(sumX));
            }

            repaint();
        }

        void handleHeightChange(Number newHeight) {
            if (canvas.getHeight() == newHeight.doubleValue()) {
                return;
            }
            canvas.setHeight(newHeight.doubleValue());
            Insets insets = getInsets();
            canvasHeight =
                    (int) (newHeight.intValue() - insets.getTop()
                            - insets.getBottom() - DELTA);
            scaleY = (float) (canvasHeight - DELTA) / (float) maxY;
            for (int i = 0; i < model.getNumNodes(); i++) {
                // calculate coordinates on the canvas
                model.getNode(i).setPosY(yPos(getY(i)));
            }

            repaint();
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
                    return model.getNode(i - 1).getPosX()
                            + (int) (value * scaleX);
                }
            }
        }

        /** Convert Y value to Y position in the canvas. */
        int yPos(int value) {
            return canvasHeight - (int) (value * scaleY);
        }

        /** Return X axis value of <code>node[i]</code>. */
        private int getX(int i) {
            EnvelopeNode node = model.getNode(i);
            if (!node.isVariableX()) {
                return node.getMinX();
            } else if (node.isInvertX()) {
                return (node.getMaxX() - node.getXValue());
            } else {
                return (node.getXValue());
            }
        }

        /** Return Y axis value of <code>node[i]</code>. */
        private int getY(int i) {
            EnvelopeNode node = model.getNode(i);
            if (node.getMinY() == EnvelopeNode.SAME) {
                return getY(i - 1);
            } else if (!node.isVariableY()) {
                return node.getMinY();
            } else {
                return node.getYValue();
            }
        }

        public void repaint() {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    paintComponent();
                }
            });
        }

        protected void paintComponent() {
            int sumX = 0;

            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            for (int i = 0; i < model.getNumNodes(); i++) {

                EnvelopeNode node = model.getNode(i);
                // calculate coordinates on the canvas
                sumX += getX(i);
                node.setPosX(xPos(sumX));
                node.setPosY(yPos(getY(i)));

                // draw a rectangle and a line. Filled box is used for a node
                // for variable.
                if ((node.isVariableX() || node.isVariableY()) && enabled) {
                    gc.fillRect(node.getPosX() - DELTA / 2, node.getPosY()
                            - DELTA / 2, DELTA, DELTA);
                } else {
                    gc.strokeRect(node.getPosX() - DELTA / 2, node.getPosY()
                            - DELTA / 2, DELTA, DELTA);
                }
                if (i > 0) {
                    gc.setLineWidth(1);
                    gc.strokeLine(node.getPosX(), node.getPosY(), model
                            .getNode(i - 1).getPosX(), model.getNode(i - 1)
                            .getPosY());
                }
            }
        }

        public final void setDisable(boolean arg0) {
            canvas.setDisable(arg0);
        }

        // @Override
        // public void setEnabled(boolean enabled) {
        // // super.setEnabled(enabled);
        // this.enabled = enabled;
        // // repaint();
        // }
    }

    /** Data structure for Paramaters. */
    class EnvelopeParam {
        /**
         * JLabel widgets which show the names of the X/Y axis parameters riding
         * each access.
         */
        protected Label label;
        /** JTextField which show the X/Y axis value. */
        protected TextField textField;
        /** array index of 'nodes' for the corresponding Node. */
        protected int node;
        /** true for X value, false for Y value. */
        protected boolean isX;

        protected EnvelopeParam(Label label, TextField textField, int node,
                boolean isX) {
            this.label = label;
            this.textField = textField;
            this.textField.setPrefWidth(40);
            this.node = node;
            this.isX = isX;
        }
    }

    class EnvelopeChangeListener implements
            ChangeListener<AbstractEnvelopeParam> {

        @Override
        public void changed(
                ObservableValue<? extends AbstractEnvelopeParam> arg0,
                AbstractEnvelopeParam arg1, AbstractEnvelopeParam arg2) {
            envCanvas.repaint(); // invokes paintComponent()
        }
    }

}
