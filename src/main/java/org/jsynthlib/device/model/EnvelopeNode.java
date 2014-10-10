package org.jsynthlib.device.model;

import javafx.beans.value.ChangeListener;

/**
 * A data type used by EnvelopeWidget which stores information about a
 * single node (point) in the Widget.
 * <p>
 * Each <code>Node</code> is one of the movable squares on the envelope.
 * Some of these nodes are stationary, some contain two parameters on the
 * synth and can be moved vertically and horizontally, and others contain
 * only one parameter and can therefore be moved in only one direction.
 * @see EnvelopeWidget
 */
public class EnvelopeNode {

    /**
     * When this value is used for <code>miny</code>, Y axis value remains
     * at whatever the Y axis value of the previous node was.
     */
    public static final int SAME = 5000;

    private final EnvelopeXParam xParam;
    private final EnvelopeYParam yParam;

    public EnvelopeNode(EnvelopeXParam xParam, EnvelopeYParam yParam) {
        this.xParam = xParam;
        this.yParam = yParam;
    }

    public int getFaderNumX() {
        return xParam.getFaderNum();
    }

    public void setFaderNumX(int faderNumX) {
        this.xParam.setFaderNum(faderNumX);
    }

    public int getFaderNumY() {
        return yParam.getFaderNum();
    }

    public void setFaderNumY(int faderNumY) {
        this.yParam.setFaderNum(faderNumY);
    }

    public int getPosX() {
        return xParam.getPos();
    }

    public void setPosX(int posX) {
        this.xParam.setPos(posX);
    }

    public int getPosY() {
        return yParam.getPos();
    }

    public void setPosY(int posY) {
        this.yParam.setPos(posY);
    }

    public int getMinX() {
        return xParam.getMin();
    }

    public int getMinY() {
        return yParam.getMin();
    }

    public int getMaxX() {
        return xParam.getMax();
    }

    public int getMaxY() {
        return yParam.getMax();
    }

    public boolean isInvertX() {
        return xParam.isInvert();
    }

    public int getBaseY() {
        return yParam.getBase();
    }

    public String getNameX() {
        return xParam.getName();
    }

    public String getNameY() {
        return yParam.getName();
    }

    public boolean isVariableX() {
        return xParam.isVariable();
    }

    public boolean isVariableY() {
        return yParam.isVariable();
    }

    public void setXValue(int value) {
        xParam.valueProperty().set(value);
    }

    public int getXValue() {
        return xParam.valueProperty().get();
    }

    public void setYValue(int value) {
        yParam.valueProperty().set(value);
    }

    public int getYValue() {
        return yParam.valueProperty().get();
    }

    public void addXChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        xParam.addChangeListener(l);
    }

    public void removeXChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        xParam.removeChangeListener(l);
    }

    public void addYChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        yParam.addChangeListener(l);
    }

    public void removeYChangeListener(ChangeListener<AbstractEnvelopeParam> l) {
        yParam.removeChangeListener(l);
    }

    public EnvelopeXParam getxParam() {
        return xParam;
    }

    public EnvelopeYParam getyParam() {
        return yParam;
    }

}