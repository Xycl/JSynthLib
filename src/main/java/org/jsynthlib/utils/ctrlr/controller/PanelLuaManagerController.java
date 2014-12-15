package org.jsynthlib.utils.ctrlr.controller;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaManagerType;
import org.ctrlr.panel.PanelType;
import org.jsynthlib.utils.ctrlr.controller.lua.MidiReceivedController;
import org.jsynthlib.utils.ctrlr.controller.lua.PanelLoadedController;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;
import org.jsynthlib.utils.ctrlr.service.impl.RootLuaMethodProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

@Singleton
public class PanelLuaManagerController extends ElementControllerBase {

    public interface Factory {
        PanelLuaManagerController newPanelLuaManagerController(PanelType panel);
    }

    @Inject
    private MidiReceivedController midiReceivedController;

    @Inject
    private PanelLoadedController panelCreatedController;

    @Inject
    @Named("root")
    private LuaMethodProvider luaMethodProvider;

    private final PanelType panel;

    @Inject
    public PanelLuaManagerController(@Assisted PanelType panel) {
        this.panel = panel;
    }

    @Override
    public void init() {
        LuaManagerType luaManager = panel.addNewLuaManager();
        LuaManagerMethodsType methods = luaManager.addNewLuaManagerMethods();
        ((RootLuaMethodProvider) luaMethodProvider).setMethods(methods);
        midiReceivedController.init();
        panelCreatedController.init();
    }
}
