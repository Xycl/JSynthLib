package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Bounds;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiTabsTabType;
import org.jsynthlib.utils.ctrlr.controller.ElementControllerBase;

import com.google.inject.Inject;

public abstract class ModulatorControllerBase extends ElementControllerBase {
    private static final Set<String> NAME_CACHE = new HashSet<String>();

    private Bounds parentAbsoluteBounds;

    private ModulatorType modulator;

    private ComponentType component;

    private Rectangle rect;

    @Inject
    private PanelType panel;

    private int vstIndex;

    @Override
    public void init() {
        modulator = panel.addNewModulator();

        setMuteOnStart(false);
        setExcludeFromSnapshot(false);
        setLuaModulatorValueChange("-- None");
        setValueExpression("modulatorValue");

        modulator.setModulatorCustomIndex(0);
        modulator.setModulatorCustomIndexGroup(0);
        modulator.setModulatorValue(0);
        modulator.setModulatorGlobalVariable(-1);

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
        modulator.setModulatorCustomName("");
        modulator.setModulatorCustomNameGroup("");

        component = modulator.addNewComponent();

        component.setComponentLabelJustification("center");
        component.setComponentLabelHeight(14);
        component.setComponentLabelWidth(0);
        component.setComponentLabelAlwaysOnTop(1);
        component.setComponentSentBack(0);
        component.setComponentLabelColour("0xff000000");
        component.setComponentLabelFont("<Sans-Serif>;12;0;0;0;0;1");
        component.setComponentMouseCursor(2);
        component.setComponentSnapSize(0);
        component.setComponentIsLocked(0);
        component.setComponentDisabled(0);
        component.setComponentRadioGroupId(0);
        component.setComponentRadioGroupNotifyMidi(1);
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

        setLabelVisible(true);
        setComponentVisible(true);
        setLabelPosition(ComponentLabelPositionType.TOP);
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

    public final void setModulatorName(String name) {
        modulator.setName(name);
    }

    public final void setComponentVisibleName(String name) {
        component.setComponentVisibleName(name);
    }

    public final void setGroupAttributes(ModulatorType group) {
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

    public Bounds getParentAbsoluteBounds() {
        return parentAbsoluteBounds;
    }

    public final void setParentAbsoluteBounds(Bounds parentAbsoluteBounds) {
        this.parentAbsoluteBounds = parentAbsoluteBounds;
    }

    public final void setLabelPosition(
            ComponentLabelPositionType.Enum labelPosition) {
        component.setComponentLabelPosition(labelPosition);
    }

    public final void setValueExpression(String valueExpression) {
        modulator.setModulatorValueExpression(valueExpression);
    }

    public final void setLabelVisible(boolean labelVisible) {
        component.setComponentLabelVisible(labelVisible ? 1 : 0);
    }

    public void setMax(int max) {
        modulator.setModulatorMax(max);
    }

    public void setMin(int min) {
        modulator.setModulatorMin(min);
    }

    public final void setLuaModulatorValueChange(String luaModulatorValueChange) {
        modulator.setLuaModulatorValueChange(luaModulatorValueChange);
    }

    public final void setMuteOnStart(boolean muteOnStart) {
        modulator.setModulatorMuteOnStart(muteOnStart ? 1 : 0);
    }

    public final void setExcludeFromSnapshot(boolean excludeFromSnapshot) {
        modulator.setModulatorExcludeFromSnapshot(excludeFromSnapshot ? 1 : 0);
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
        StringBuilder sb = new StringBuilder();
        sb.append((int) rect.getX()).append(" ").append((int) rect.getY())
        .append(" ").append((int) rect.getWidth()).append(" ")
        .append((int) rect.getHeight());
        component.setComponentRectangle(sb.toString());
    }

    public Rectangle getRect() {
        return rect;
    }

    public final void setRect(Bounds bounds) {
        int x = (int) bounds.getMinX();
        int y = (int) bounds.getMinY();
        if (parentAbsoluteBounds != null) {
            x = (int) (bounds.getMinX() - parentAbsoluteBounds.getMinX());
            y = (int) (bounds.getMinY() - parentAbsoluteBounds.getMinY());

        }
        setRect(new Rectangle(x, y, (int) bounds.getWidth(),
                (int) bounds.getHeight()));
    }

    public final void setComponentVisible(boolean componentVisible) {
        component.setComponentVisibility(componentVisible ? 1 : 0);
    }

    public final void setVstIndex(int vstIndex) {
        this.vstIndex = vstIndex;
        if (vstIndex < 0) {
            modulator.setModulatorVstExported(0);
            modulator.setModulatorIsStatic(1);
        } else {
            modulator.setModulatorVstExported(1);
            modulator.setVstIndex(vstIndex++);
            modulator.setModulatorIsStatic(0);
        }
    }

    protected final int getVstIndex() {
        return vstIndex;
    }

    public ModulatorType getModulator() {
        return modulator;
    }

    public ComponentType getComponent() {
        return component;
    }

    public PanelType getPanel() {
        return panel;
    }

    public void setPanel(PanelType panel) {
        this.panel = panel;
    }
}
