package org.jsynthlib.device.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class Knob extends Slider {

    private static final int SIZE = 40;
    private double knobRadius;

    private final double minAngle = -140;
    private final double maxAngle = 140;
    private double dragOffset;

    private StackPane knob;
    private StackPane knobOverlay;
    private StackPane knobDot;

    public Knob() {
        super();
        setSkin(new KnobSkin(this));
        initialize();

        valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                if (isValueChanging()) {
                    return;
                } else {
                    rotateKnob();
                }
            }
        });
    }

    private void initialize() {
        knob = new StackPane() {
            @Override
            protected void layoutChildren() {
                knobDot.autosize();
                knobDot.setLayoutX((knob.getWidth() - knobDot.getWidth()) / 2);
                knobDot.setLayoutY(5 + (knobDot.getHeight() / 2));
            }

        };
        knob.getStyleClass().setAll("knob");
        knobOverlay = new StackPane();
        knobOverlay.getStyleClass().setAll("knobOverlay");
        knobDot = new StackPane();
        knobDot.getStyleClass().setAll("knobDot");

        getChildren().setAll(knob, knobOverlay);
        knob.getChildren().add(knobDot);

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                double dragStart = mouseToValue(me.getX(), me.getY());
                double zeroOneValue =
                        (getValue() - getMin()) / (getMax() - getMin());
                dragOffset = zeroOneValue - dragStart;
                ((KnobSkin) getSkin()).knobPressed(me, dragStart);
            }
        });
        setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                ((KnobSkin) getSkin()).knobRelease(me,
                        mouseToValue(me.getX(), me.getY()));
            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent me) {
                ((KnobSkin) getSkin()).knobDragged(me,
                        mouseToValue(me.getX(), me.getY()) + dragOffset);
                rotateKnob();
            }
        });
    }

    private double mouseToValue(double mouseX, double mouseY) {
        double cx = getWidth() / 2;
        double cy = getHeight() / 2;
        double mouseAngle =
                Math.toDegrees(Math.atan((mouseY - cy) / (mouseX - cx)));
        double topZeroAngle;
        if (mouseX < cx) {
            topZeroAngle = 90 - mouseAngle;
        } else {
            topZeroAngle = -(90 + mouseAngle);
        }
        double value = 1 - ((topZeroAngle - minAngle) / (maxAngle - minAngle));
        return value;
    }

    void rotateKnob() {
        double zeroOneValue = (getValue() - getMin()) / (getMax() - getMin());
        double angle = minAngle + ((maxAngle - minAngle) * zeroOneValue);
        knob.setRotate(angle);
    }

    @Override
    protected void layoutChildren() {
        // calculate the available space
        double x = getInsets().getLeft();
        double y = getInsets().getTop();
        double w =
                getWidth() - (getInsets().getLeft() + getInsets().getRight());
        double h =
                getHeight() - (getInsets().getTop() + getInsets().getBottom());
        double cx = x + (w / 2);
        double cy = y + (h / 2);

        // resize thumb to preferred size
        double knobWidth = SIZE; // knob.prefWidth(-1);
        double knobHeight = SIZE; // knob.prefHeight(-1);
        knobRadius = Math.max(knobWidth, knobHeight) / 2;
        knob.resize(knobWidth, knobHeight);
        knob.setLayoutX(cx - knobRadius);
        knob.setLayoutY(cy - knobRadius);
        knobOverlay.resize(knobWidth, knobHeight);
        knobOverlay.setLayoutX(cx - knobRadius);
        knobOverlay.setLayoutY(cy - knobRadius);
        rotateKnob();
    }

    @Override
    protected double computeMinWidth(double height) {
        return SIZE; // (getInsets().getLeft() + knob.minWidth(-1) +
                     // getInsets().getRight());
    }

    @Override
    protected double computeMinHeight(double width) {
        return SIZE; // (getInsets().getTop() + knob.minHeight(-1) +
                     // getInsets().getBottom());
    }

    @Override
    protected double computePrefWidth(double height) {
        return SIZE; // (getInsets().getLeft() + knob.prefWidth(-1) +
                     // getInsets().getRight());
    }

    @Override
    protected double computePrefHeight(double width) {
        return SIZE; // (getInsets().getTop() + knob.prefHeight(-1) +
                     // getInsets().getBottom());
    }

    @Override
    protected double computeMaxWidth(double height) {
        return Double.MAX_VALUE;
    }

    @Override
    protected double computeMaxHeight(double width) {
        return Double.MAX_VALUE;
    }

}
