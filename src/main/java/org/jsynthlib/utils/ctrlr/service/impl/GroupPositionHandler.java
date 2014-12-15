package org.jsynthlib.utils.ctrlr.service.impl;

import java.awt.Rectangle;

import org.apache.log4j.Logger;
import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;

public class GroupPositionHandler {

    private final transient Logger log = Logger.getLogger(getClass());

    private int currTop;
    private int currLeft;
    private int maxComponentHeight;
    private final int maxWidth;

    public GroupPositionHandler(int maxWidth) {
        currTop = 0;
        currLeft = 0;
        maxComponentHeight = 0;
        this.maxWidth = maxWidth;
    }

    public Rectangle getNextRectangle() {
        return new Rectangle(currLeft, currTop, 0, 0);
    }

    public void addNewModulator(ModulatorType modulator) {
        currLeft += getModulatorRight(modulator);

        if (getModulatorBottom(modulator) > maxComponentHeight) {
            maxComponentHeight = getModulatorBottom(modulator);
            log.info("New max height " + maxComponentHeight);
        }

        if (currLeft > maxWidth) {
            currLeft = 0;
            currTop += maxComponentHeight;
            maxComponentHeight = 0;
            log.info("New row: " + currTop);
        }
    }

    int getModulatorBottom(ModulatorType modulator) {
        Rectangle bounds = getModulatorBounds(modulator);
        return (int) bounds.getHeight();
    }

    int getModulatorRight(ModulatorType modulator) {
        Rectangle bounds = getModulatorBounds(modulator);
        return (int) bounds.getWidth();
    }

    Rectangle getModulatorBounds(ModulatorType modulator) {
        ComponentType component = modulator.getComponent();
        String componentRectangle = component.getComponentRectangle();
        if (componentRectangle == null) {
            return new Rectangle(0, 0, 0, 0);
        } else {
            String[] split = componentRectangle.split(" ");
            int left = Integer.parseInt(split[0]);
            int top = Integer.parseInt(split[1]);
            int width = Integer.parseInt(split[2]);
            int height = Integer.parseInt(split[3]);
            return new Rectangle(left, top, width, height);
        }
    }
}
