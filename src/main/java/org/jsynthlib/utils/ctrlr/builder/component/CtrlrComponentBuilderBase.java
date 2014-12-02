package org.jsynthlib.utils.ctrlr.builder.component;

import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

import javafx.geometry.Bounds;

import org.ctrlr.panel.ComponentLabelPositionType;
import org.ctrlr.panel.ComponentType;
import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;
import org.ctrlr.panel.UiTabsTabType;

public abstract class CtrlrComponentBuilderBase<T extends Object> {
    private static final Set<String> NAME_CACHE = new HashSet<String>();

    private T object;
    private Bounds parentAbsoluteBounds;
    private ComponentLabelPositionType.Enum labelPosition =
            ComponentLabelPositionType.TOP;
    private String valueExpression = "modulatorValue";
    private boolean labelVisible = true;
    private int max;
    private int min;
    private String luaModulatorValueChange = "-- None";
    private Rectangle rect;
    private boolean muteOnStart = false;

    private boolean excludeFromSnapshot = false;

    private boolean componentVisible = true;

    public ModulatorType createModulator(PanelType panel, ModulatorType group,
            int vstIndex) {
        ModulatorType modulator = panel.addNewModulator();
        modulator.setModulatorCustomIndex(0);
        modulator.setModulatorCustomIndexGroup(0);
        modulator.setName(getModulatorName());
        modulator.setModulatorValue(0);
        if (vstIndex < 0) {
            modulator.setModulatorVstExported(0);
            modulator.setModulatorIsStatic(1);
        } else {
            modulator.setModulatorVstExported(1);
            modulator.setVstIndex(vstIndex++);
            modulator.setModulatorIsStatic(0);
        }

        modulator.setModulatorMax(max);
        modulator.setModulatorGlobalVariable(-1);
        modulator.setModulatorMuteOnStart(muteOnStart ? 1 : 0);
        modulator.setModulatorExcludeFromSnapshot(excludeFromSnapshot ? 1 : 0);
        modulator.setModulatorValueExpression(valueExpression);
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
        modulator.setLuaModulatorValueChange(luaModulatorValueChange);
        modulator.setModulatorMin(min);
        modulator.setModulatorCustomName("");
        modulator.setModulatorCustomNameGroup("");

        createMidiElement(modulator);
        createComponent(modulator, group, panel);
        return modulator;

    }

    protected abstract String getModulatorName();

    protected void createMidiElement(ModulatorType modulator) {
    }

    protected void setComponentAttributes(ComponentType component) {

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

    protected ComponentType createComponent(ModulatorType modulator,
            ModulatorType group, PanelType panel) {
        ComponentType component = modulator.addNewComponent();
        component.setComponentLabelPosition(labelPosition);
        component.setComponentLabelJustification("center");
        component.setComponentLabelHeight(14);
        component.setComponentLabelWidth(0);
        component.setComponentLabelVisible(labelVisible ? 1 : 0);
        component.setComponentLabelAlwaysOnTop(1);
        component.setComponentSentBack(0);
        component.setComponentLabelColour("0xff000000");
        component.setComponentLabelFont("<Sans-Serif>;12;0;0;0;0;1");
        component.setComponentVisibleName(getName());
        component.setComponentMouseCursor(2);
        setGroupAttributes(component, group);
        component.setComponentSnapSize(0);
        component.setComponentIsLocked(0);
        component.setComponentDisabled(0);
        component.setComponentRadioGroupId(0);
        component.setComponentRadioGroupNotifyMidi(1);
        component.setComponentVisibility(componentVisible ? 1 : 0);
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

        setComponentAttributes(component);

        setComponentRectangle(component);
        return component;
    }

    protected String getName() {
        return "";
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

    protected void setComponentRectangle(ComponentType component) {
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

    protected ComponentLabelPositionType.Enum getLabelPosition() {
        return labelPosition;
    }

    protected void setLabelPosition(
            ComponentLabelPositionType.Enum labelPosition) {
        this.labelPosition = labelPosition;
    }

    protected String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    protected boolean isLabelVisible() {
        return labelVisible;
    }

    protected void setLabelVisible(boolean labelVisible) {
        this.labelVisible = labelVisible;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getLuaModulatorValueChange() {
        return luaModulatorValueChange;
    }

    public void setLuaModulatorValueChange(String luaModulatorValueChange) {
        this.luaModulatorValueChange = luaModulatorValueChange;
    }

    public boolean isMuteOnStart() {
        return muteOnStart;
    }

    public void setMuteOnStart(boolean muteOnStart) {
        this.muteOnStart = muteOnStart;
    }

    public boolean isExcludeFromSnapshot() {
        return excludeFromSnapshot;
    }

    public void setExcludeFromSnapshot(boolean excludeFromSnapshot) {
        this.excludeFromSnapshot = excludeFromSnapshot;
    }

    public Rectangle getRect() {
        return rect;
    }

    public void setRect(Rectangle rect) {
        this.rect = rect;
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

    public boolean isComponentVisible() {
        return componentVisible;
    }

    public void setComponentVisible(boolean componentVisible) {
        this.componentVisible = componentVisible;
    }
}
