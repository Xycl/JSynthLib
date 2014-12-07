package org.jsynthlib.utils.ctrlr.builder.component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.ctrlr.panel.ModulatorType;
import org.ctrlr.panel.PanelType;

public abstract class GroupBuilderBase<T extends Object> extends
CtrlrComponentBuilderBase<T> implements
Iterable<CtrlrComponentBuilderBase<?>> {

    private final List<CtrlrComponentBuilderBase<?>> builderList;

    public GroupBuilderBase() {
        builderList = new ArrayList<CtrlrComponentBuilderBase<?>>();
        setLabelVisible(false);
    }

    @Override
    public ModulatorType createModulator(PanelType panel, ModulatorType group,
            int vstIndex) {
        ModulatorType component = super.createModulator(panel, group, -1);
        for (CtrlrComponentBuilderBase<?> builder : builderList) {
            builder.createModulator(panel, component, vstIndex);
        }
        return component;
    }

    @Override
    protected String getModulatorName() {
        return "";
    }

    public boolean add(CtrlrComponentBuilderBase<?> e) {
        return builderList.add(e);
    }

    public boolean addAll(Collection<? extends CtrlrComponentBuilderBase<?>> c) {
        return builderList.addAll(c);
    }

    @Override
    public Iterator<CtrlrComponentBuilderBase<?>> iterator() {
        return builderList.iterator();
    }

}
