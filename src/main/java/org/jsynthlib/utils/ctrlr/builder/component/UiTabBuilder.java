package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;

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

    @Inject
    public UiTabBuilder(@Assisted PatchParamGroup[] groups) {
        setObject(groups);
    }

    @Override
    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Rectangle rect) {
        ModulatorType modulator = createModulator(panel);
        ComponentType component = modulator.addNewComponent();
        setLabelVisible(false);
        setDefaultComponentFields(component, group, "", panel);
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
        setComponentRectangle(component, rect);

        for (PatchParamGroup patchParamGroup : getObject()) {
            newUiTabsTab(modulator, patchParamGroup.getName());
        }
        return modulator;
    }

    ModulatorType newUiTabsTab(ModulatorType tabType, String name) {
        ComponentType component = tabType.getComponent();
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
        return tabType;
    }

    @Override
    protected String getModulatorName() {
        return getUniqueName("tabs");
    }
}
