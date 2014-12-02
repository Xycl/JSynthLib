package org.jsynthlib.utils.ctrlr.lua;

import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.fillMethodData;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.indent;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newLine;
import static org.jsynthlib.utils.ctrlr.lua.LuaMethodUtils.newMethodGroup;

import java.util.ArrayList;
import java.util.List;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaManagerType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.builder.PanelLuaManagerBuilder;
import org.jsynthlib.utils.ctrlr.lua.decorator.DriverLuaHandler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class PanelLuaManagerBuilderImpl implements PanelLuaManagerBuilder {

    @Inject
    @Named("midiReceivedMethodName")
    private String midiReceivedMethodName;

    private final List<DriverLuaHandler> driverDecorators;

    public PanelLuaManagerBuilderImpl() {
        driverDecorators = new ArrayList<DriverLuaHandler>();
    }

    @Override
    public void createLuaManager(PanelType panel) {
        LuaManagerType luaManager = panel.addNewLuaManager();
        LuaManagerMethodsType methods = luaManager.addNewLuaManagerMethods();
        createMidiReceivedMethod(methods);
        createPanelCreated(panel, methods);
        for (DriverLuaHandler handler : driverDecorators) {
            handler.createDriverMethodGroup(methods);
        }
    }

    void createPanelCreated(PanelType panel, LuaManagerMethodsType methods) {
        int indent = 0;
        String methodName = "panelCreated";
        LuaMethodGroupType methodGroup = newMethodGroup(methods, "Panel");
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent)).append("--").append(newLine());
        codeBuilder.append(indent(indent))
                .append("-- Called when a panel loaded ").append(newLine());
        codeBuilder.append(indent(indent++)).append("function ")
        .append(methodName).append("()").append(newLine());
        codeBuilder.append(indent(indent)).append("panel_loaded = 1")
        .append(newLine());
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        fillMethodData(methodGroup, methodName, codeBuilder.toString());
        panel.setLuaPanelLoaded(methodName);
    }

    void createMidiReceivedMethod(LuaManagerMethodsType methods) {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent)).append("--").append(newLine());
        codeBuilder.append(indent(indent)).append(
                "-- Called when a panel receives a midi message ");
        codeBuilder.append(indent(indent))
        .append("(does not need to match any modulator mask)")
        .append(newLine());
        codeBuilder
        .append(indent(indent))
        .append("-- @midi   http://ctrlr.org/api/class_ctrlr_midi_message.html")
        .append(newLine());
        codeBuilder.append(indent(indent++))
        .append("midiReceived = function(midi)").append(newLine());
        codeBuilder.append(indent(indent))
        .append("midiSize = midi:getData():getSize()")
        .append(newLine());
        for (DriverLuaHandler handler : driverDecorators) {
            codeBuilder.append(handler.getMidiReceivedPart());
        }
        codeBuilder.append(indent(--indent)).append("end").append(newLine());
        fillMethodData(methods, "midiReceived", codeBuilder.toString());
    }

    @Override
    public void addDriverDecorator(DriverLuaHandler decorator) {
        driverDecorators.add(decorator);
    }
}
