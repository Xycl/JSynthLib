package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ctrlr.panel.UiTabsTabType;
import org.jsynthlib.utils.ctrlr.controller.GroupController;
import org.jsynthlib.xmldevice.PatchParamGroup;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiTabController extends ModulatorControllerBase {

    public interface Factory {
        UiTabController newUiTabController(PatchParamGroup[] groups);
    }

    private final List<GroupController> tabGroups;
    private final PatchParamGroup[] groups;

    @Inject
    public UiTabController(@Assisted PatchParamGroup[] groups) {
        this.groups = groups;
        tabGroups = new ArrayList<GroupController>();
        for (int i = 0; i < groups.length; i++) {
            tabGroups.add(new TabGroupController(i));
        }
    }

    @Override
    public void init() {
        super.init();
        setLabelVisible(false);

        setVstIndex(-1);

        getComponent().setUiTabsCurrentTabChanged("-- None");
        getComponent().setUiTabsDepth(24);
        getComponent().setUiTabsOutlineThickness(2);
        getComponent().setUiTabsFrontTabOutline(1);
        getComponent().setUiTabsTabOutline(1);
        getComponent().setUiTabsIndentThickness(0);

        getComponent().setUiTabsFrontTabFont("<Sans-Serif>;16;0;0;0;0;1");
        getComponent().setUiTabsTabFont("<Sans-Serif>;16;0;0;0;0;1");
        getComponent().setUiTabsOutlineGlobalColour("0x00000000");
        getComponent().setUiTabsOutlineGlobalBackgroundColour("ffffffff");
        getComponent().setUiTabsOutlineTabColour("0xff303030");
        getComponent().setUiTabsTextTabColour("ff000000");
        getComponent().setUiTabsFrontTabOutlineColour("0xff000000");
        getComponent().setUiTabsFrontTabTextColour("0xff000000");
        getComponent().setUiTabsAddTab(0);
        getComponent().setUiTabsRemoveTab(0);
        getComponent().setUiType("uiTabs");
        getComponent().setUiTabsCurrentTab(0);

        setTabsOrientation(0);
        setModulatorName(getUniqueName("tabs"));

        for (PatchParamGroup ppg : groups) {
            newUiTabsTab(ppg.getName());
        }
    }


    public GroupController getTabGroup(int index) {
        return tabGroups.get(index);
    }

    void newUiTabsTab(String name) {
        UiTabsTabType uiTabsTab = getComponent().addNewUiTabsTab();
        uiTabsTab.setUiTabsTabName(name);
        int index = getComponent().getUiTabsTabArray().length - 1;
        uiTabsTab.setUiTabsTabIndex(index);
        uiTabsTab.setUiTabsTabContentBackgroundColour("ffffffff");
        uiTabsTab.setUiTabsTabBackgroundColour("ffffffff");
        uiTabsTab.setUiTabsTabBackgroundImage("-- None");
        uiTabsTab.setUiTabsTabBackgroundImageLayout(36);
        uiTabsTab.setUiTabsTabBackgroundImageAlpha(255);
        getComponent().setUiTabsCurrentTab(index);
    }

    public void setTabsOrientation(int tabsOrientation) {
        getComponent().setUiTabsOrientation(tabsOrientation);
    }

    class TabGroupController implements GroupController {

        private final List<ModulatorControllerBase> list;
        private final int index;

        public TabGroupController(int index) {
            list = new ArrayList<ModulatorControllerBase>();
            this.index = index;
        }

        @Override
        public Iterator<ModulatorControllerBase> iterator() {
            return list.iterator();
        }

        @Override
        public boolean add(ModulatorControllerBase e) {
            getComponent().setUiTabsCurrentTab(index);
            e.setGroupAttributes(getModulator());
            return list.add(e);
        }

        @Override
        public boolean addAll(Collection<? extends ModulatorControllerBase> c) {
            for (ModulatorControllerBase e : c) {
                getComponent().setUiTabsCurrentTab(index);
                e.setGroupAttributes(getModulator());
            }
            return list.addAll(c);
        }

    }
}
