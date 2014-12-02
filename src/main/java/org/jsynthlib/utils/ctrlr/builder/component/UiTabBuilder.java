package org.jsynthlib.utils.ctrlr.builder.component;

import java.util.ArrayList;
import java.util.List;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiTabsTabType;
import org.jsynthlib.xmldevice.PatchParamGroup;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class UiTabBuilder extends CtrlrComponentBuilderBase<PatchParamGroup[]> {

    public interface Factory {
        UiTabBuilder newUiTabBuilder(PatchParamGroup[] groups);
    }

    private final List<GroupBuilderBase<?>> tabGroups;

    @Inject
    public UiTabBuilder(@Assisted PatchParamGroup[] groups) {
        setObject(groups);
        setLabelVisible(false);
        tabGroups = new ArrayList<GroupBuilderBase<?>>();
        for (PatchParamGroup patchParamGroup : groups) {
            tabGroups.add(new GroupBuilderBase<Object>() {
            });
        }
    }

    @Override
    public ModulatorType createModulator(PanelType panel, ModulatorType group,
            int vstIndex) {
        ModulatorType modulator = super.createModulator(panel, group, -1);
        for (int i = 0; i < tabGroups.size(); i++) {
            GroupBuilderBase<?> tabGroup = tabGroups.get(i);
            modulator.getComponent().setUiTabsCurrentTab(i);
            for (CtrlrComponentBuilderBase<?> builder : tabGroup) {
                builder.createModulator(panel, modulator, vstIndex);
            }
        }
        return modulator;
    }

    @Override
    protected void setComponentAttributes(ComponentType component) {
        super.setComponentAttributes(component);
        component.setUiTabsCurrentTabChanged("tabChanged");
        component.setUiTabsDepth(24);
        component.setUiTabsOutlineThickness(2);
        component.setUiTabsFrontTabOutline(1);
        component.setUiTabsTabOutline(1);
        component.setUiTabsIndentThickness(0);
        component.setUiTabsOrientation(0);
        component.setUiTabsFrontTabFont("<Sans-Serif>;16;0;0;0;0;1");
        component.setUiTabsTabFont("<Sans-Serif>;16;0;0;0;0;1");
        component.setUiTabsOutlineGlobalColour("0x00000000");
        component.setUiTabsOutlineGlobalBackgroundColour("ffffffff");
        component.setUiTabsOutlineTabColour("0xff303030");
        component.setUiTabsTextTabColour("ff000000");
        component.setUiTabsFrontTabOutlineColour("0xff000000");
        component.setUiTabsFrontTabTextColour("0xff000000");
        component.setUiTabsAddTab(0);
        component.setUiTabsRemoveTab(0);
        component.setUiType("uiTabs");
        component.setUiTabsCurrentTab(0);

        for (PatchParamGroup ppg : getObject()) {
            newUiTabsTab(component, ppg.getName());
        }
    }

    public GroupBuilderBase<?> getTabGroup(int index) {
        return tabGroups.get(index);
    }

    void newUiTabsTab(ComponentType component, String name) {
        UiTabsTabType uiTabsTab = component.addNewUiTabsTab();
        uiTabsTab.setUiTabsTabName(name);
        int index = component.getUiTabsTabArray().length - 1;
        uiTabsTab.setUiTabsTabIndex(index);
        uiTabsTab.setUiTabsTabContentBackgroundColour("ffffffff");
        uiTabsTab.setUiTabsTabBackgroundColour("ffffffff");
        uiTabsTab.setUiTabsTabBackgroundImage("-- None");
        uiTabsTab.setUiTabsTabBackgroundImageLayout(36);
        uiTabsTab.setUiTabsTabBackgroundImageAlpha(255);
        component.setUiTabsCurrentTab(index);
    }

    @Override
    protected String getModulatorName() {
        return getUniqueName("tabs");
    }
}
