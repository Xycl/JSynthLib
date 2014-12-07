package org.jsynthlib.utils.ctrlr.lua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaManagerType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.lua.generator.MethodGenerator;
import org.jsynthlib.utils.ctrlr.lua.generator.MidiReceivedPartGenerator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class PanelLuaManagerBuilderImpl implements PanelLuaManagerBuilder {

    @Inject
    @Named("midiReceivedMethodName")
    private String midiReceivedMethodName;

    @Inject
    private LuaMethodUtils utils;

    private final List<MidiReceivedPartGenerator> midiReceivedList;

    private final HashMap<String, DriverLuaBean> driverValuesMap;

    @Inject
    Set<MethodGenerator> methodGenerators;

    public PanelLuaManagerBuilderImpl() {
        driverValuesMap = new HashMap<String, DriverLuaBean>();
        midiReceivedList = new ArrayList<MidiReceivedPartGenerator>();
    }

    @Override
    public void createLuaManager(PanelType panel) {
        LuaManagerType luaManager = panel.addNewLuaManager();
        LuaManagerMethodsType methods = luaManager.addNewLuaManagerMethods();
        createMidiReceivedMethod(methods);
        createPanelCreated(panel, methods);

        for (Entry<String, DriverLuaBean> entry : driverValuesMap
                .entrySet()) {
            LuaMethodGroupType group =
                    utils.newMethodGroup(methods, entry.getKey());
            for (MethodGenerator generator : methodGenerators) {
                generator.generateMethod(group, entry.getValue());
            }
        }
    }

    void createPanelCreated(PanelType panel, LuaManagerMethodsType methods) {
        int indent = 0;
        String methodName = "panelCreated";
        LuaMethodGroupType methodGroup = utils.newMethodGroup(methods, "Panel");
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(utils.indent(indent)).append("--")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent))
        .append("-- Called when a panel loaded ")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent++)).append("function ")
        .append(methodName).append("()").append(utils.newLine());
        codeBuilder.append(utils.indent(indent)).append("panel_loaded = 1")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(--indent)).append("end")
        .append(utils.newLine());

        utils.fillMethodData(methodGroup, methodName, codeBuilder.toString());
        panel.setLuaPanelLoaded(methodName);
    }

    void createMidiReceivedMethod(LuaManagerMethodsType methods) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(utils.indent(indent)).append("--")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent)).append(
                "-- Called when a panel receives a midi message ");
        codeBuilder.append(utils.indent(indent))
        .append("(does not need to match any modulator mask)")
        .append(utils.newLine());
        codeBuilder
        .append(utils.indent(indent))
        .append("-- @midi   http://ctrlr.org/api/class_ctrlr_midi_message.html")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent++))
        .append("midiReceived = function(midi)")
        .append(utils.newLine());
        codeBuilder.append(utils.indent(indent))
        .append("midiSize = midi:getData():getSize()")
        .append(utils.newLine());
        for (MidiReceivedPartGenerator handler : midiReceivedList) {
            codeBuilder.append(handler.getMidiReceivedPart());
        }
        codeBuilder.append(utils.indent(--indent)).append("end")
        .append(utils.newLine());
        utils.fillMethodData(methods, "midiReceived", codeBuilder.toString());
    }

    @Override
    public void addMidiReceivedPartGenerator(MidiReceivedPartGenerator generator) {
        midiReceivedList.add(generator);
    }

    @Override
    public DriverLuaBean getDriverLuaBean(String prefix) {
        if (driverValuesMap.containsKey(prefix)) {
            return driverValuesMap.get(prefix);
        } else {
            DriverLuaBean luaBean = new DriverLuaBean();
            driverValuesMap.put(prefix, luaBean);
            return luaBean;
        }
    }
}
