package org.jsynthlib.utils.ctrlr.builder;

import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.utils.ctrlr.lua.generator.MidiReceivedPartGenerator;

public interface PanelLuaManagerBuilder {

    void createLuaManager(PanelType panel);

    void addMidiReceivedPartGenerator(MidiReceivedPartGenerator generator);

    DriverLuaBean getDriverLuaBean(String prefix);
}
