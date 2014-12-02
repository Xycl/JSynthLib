package org.jsynthlib.utils.ctrlr.builder;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;

public interface PanelLuaManagerBuilder {

    void createLuaManager(PanelType panel);

    void addDriverDecorator(DriverLuaHandler decorator);
}
