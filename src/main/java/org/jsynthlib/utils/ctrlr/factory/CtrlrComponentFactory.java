package org.jsynthlib.utils.ctrlr.factory;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Bounds;

import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.MidiType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiTabsTabType;

public abstract class CtrlrComponentFactory<T extends Object> {
    private static final Set<String> NAME_CACHE = new HashSet<String>();

    protected final T object;
    private Bounds parentAbsoluteBounds;

    protected CtrlrComponentFactory(T object) {
        this.object = object;
    }

    public abstract ModulatorType createComponent(PanelType panel,
            ModulatorType group, int vstIndex, Rectangle rect);

    public ModulatorType createComponent(PanelType panel, ModulatorType group,
            int vstIndex, Bounds bounds) {
        int x = (int) bounds.getMinX();
        int y = (int) bounds.getMinY();
        if (parentAbsoluteBounds != null) {
            x = (int) (bounds.getMinX() - parentAbsoluteBounds.getMinX());
            y = (int) (bounds.getMinY() - parentAbsoluteBounds.getMinY());

        }
        Rectangle rect =
                new Rectangle(x, y, (int) bounds.getWidth(),
                        (int) bounds.getHeight());
        return createComponent(panel, group, vstIndex, rect);
    }

    ModulatorType createModulatorBase(PanelType panel, String modName) {
        ModulatorType modulator = panel.addNewModulator();
        modulator.setModulatorCustomIndex(0);
        modulator.setModulatorCustomIndexGroup(0);
        modulator.setName(getUniqueName(modName));
        modulator.setModulatorValue(0);
        return modulator;
    }

    protected ModulatorType createModulator(PanelType panel, String modName) {
        ModulatorType modulator = createModulatorBase(panel, modName);
        modulator.setModulatorVstExported(0);
        modulator.setModulatorIsStatic(1);
        return modulator;
    }

    protected ModulatorType createModulator(PanelType panel, int vstIndex,
            String modName, int min, int max) {
        ModulatorType modulator = createModulatorBase(panel, modName);
        modulator.setModulatorVstExported(1);
        modulator.setModulatorMax(max);
        modulator.setVstIndex(vstIndex);
        modulator.setModulatorIsStatic(0);
        modulator.setModulatorGlobalVariable(-1);
        modulator.setModulatorMuteOnStart(0);
        modulator.setModulatorExcludeFromSnapshot(0);
        modulator.setModulatorValueExpression("setModulatorValue");
        modulator.setModulatorValueExpressionReverse("midiValue");
        modulator.setLuaModulatorGetValueForMIDI("-- None");
        modulator.setLuaModulatorGetValueFromMIDI("-- None");
        modulator.setModulatorLinkedToPanelProperty("-- None");
        modulator.setModulatorLinkedToModulatorProperty("-- None");
        modulator.setModulatorLinkedToModulator("-- None");
        modulator.setModulatorLinkedToModulatorSource(1);
        modulator.setModulatorLinkedToComponent(0);
        modulator.setModulatorBaseValue(0);
        modulator.setModulatorVstNameFormat("%n");
        modulator.setLuaModulatorValueChange("-- None");
        modulator.setModulatorMin(min);
        modulator.setModulatorCustomName("");
        modulator.setModulatorCustomNameGroup("");
        return modulator;
    }

    protected void createMidiElement(ModulatorType modulator) {
        MidiType midiType = modulator.addNewMidi();
        midiType.setMidiMessageType(5);
        midiType.setMidiMessageChannelOverride(0);
        midiType.setMidiMessageChannel(1);
        midiType.setMidiMessageCtrlrNumber(1);
        midiType.setMidiMessageCtrlrValue(0);
        midiType.setMidiMessageMultiList("");
        midiType.setMidiMessageSysExFormula("F0 10 06 06 00 xx F7");
    }

    protected String getUniqueName(String paramName) {
        String cleanName = paramName.replaceAll("[^A-Za-z0-9]", "");
        if (NAME_CACHE.contains(cleanName)) {
            int index = 1;
            while (NAME_CACHE.contains(cleanName)) {
                cleanName = cleanName + index++;
            }
            NAME_CACHE.add(cleanName);
            return cleanName;
        } else {
            NAME_CACHE.add(cleanName);
            return cleanName;
        }
    }

    protected void setDefaultModulatorFields(ComponentType component,
            ModulatorType group, String name, PanelType panel) {
        component.setComponentLabelPosition("top");
        component.setComponentLabelJustification("center");
        component.setComponentLabelHeight(14);
        component.setComponentLabelWidth(0);
        component.setComponentLabelVisible(1);
        component.setComponentLabelAlwaysOnTop(1);
        component.setComponentSentBack(0);
        component.setComponentLabelColour("0xff000000");
        component.setComponentLabelFont("<Sans-Serif>;12;0;0;0;0;1");
        component.setComponentVisibleName(name);
        component.setComponentMouseCursor(2);
        setGroupAttributes(component, group);
        component.setComponentSnapSize(0);
        component.setComponentIsLocked(0);
        component.setComponentDisabled(0);
        component.setComponentRadioGroupId(0);
        component.setComponentRadioGroupNotifyMidi(1);
        component.setComponentVisibility(1);
        component.setComponentEffect("0");
        component.setComponentEffectRadius(1);
        component.setComponentEffectColour("0xff000000");
        component.setComponentEffectOffsetX(0);
        component.setComponentEffectOffsetY(0);
        component.setComponentExcludedFromLabelDisplay(0);
        component.setComponentBubbleRoundAngle(10);
        component.setComponentBubbleBackgroundColour1("0x9cffffff");
        component.setComponentBubbleBackgroundColour2("0xbab9b9b9");
        component.setComponentBubbleBackgroundGradientType(1);
        component.setComponentBubbleValueColour("0xff000000");
        component.setComponentBubbleValueFont("<Sans-Serif>;14;0;0;0;0;1");
        component.setComponentBubbleValueJustification("centred");
        component.setComponentBubbleNameColour("0xff000000");
        component.setComponentBubbleNameFont("<Sans-Serif>;14;0;0;0;0;1");
        component.setComponentBubbleNameJustification("centred");
        component.setComponentValueDecimalPlaces(0);
        component.setComponentLuaMouseMoved("-- None");

        component.setComponentLayerUid(panel.getUiPanelEditor()
                .getUiPanelCanvasLayer().getUiPanelCanvasLayerUid());
    }

    protected void setGroupAttributes(ComponentType component,
            ModulatorType group) {
        if (group == null) {
            component.setComponentGroupped(0);
        } else if (group.getComponent().getUiType().equals("uiGroup")) {
            component.setComponentGroupName(group.getName());
            component.setComponentGroupped(1);
        } else if (group.getComponent().getUiType().equals("uiTabs")) {
            int currentTab = group.getComponent().getUiTabsCurrentTab();
            UiTabsTabType[] tabArray = group.getComponent().getUiTabsTabArray();
            UiTabsTabType tab = tabArray[currentTab];
            component.setComponentTabName(group.getName());
            component.setComponentTabId(tab.getUiTabsTabIndex());
        }

    }

    protected void setComponentRectangle(ComponentType component, Rectangle rect) {
        StringBuilder sb = new StringBuilder();
        sb.append((int) rect.getX()).append(" ").append((int) rect.getY())
                .append(" ").append((int) rect.getWidth()).append(" ")
                .append((int) rect.getHeight());
        component.setComponentRectangle(sb.toString());
    }

    public Bounds getParentAbsoluteBounds() {
        return parentAbsoluteBounds;
    }

    public void setParentAbsoluteBounds(Bounds parentAbsoluteBounds) {
        this.parentAbsoluteBounds = parentAbsoluteBounds;
    }
}
