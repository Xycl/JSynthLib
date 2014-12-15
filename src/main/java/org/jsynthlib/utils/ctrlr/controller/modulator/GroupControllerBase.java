package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.jsynthlib.utils.ctrlr.controller.GroupController;

public abstract class GroupControllerBase extends ModulatorControllerBase
        implements
GroupController {

    private final List<ModulatorControllerBase> builderList;

    public GroupControllerBase() {
        builderList = new ArrayList<ModulatorControllerBase>();
    }

    @Override
    public void init() {
        super.init();
        setLabelVisible(false);
        setModulatorName("");
        setVstIndex(-1);
    }

    @Override
    public boolean add(ModulatorControllerBase e) {
        e.setGroupAttributes(getModulator());
        return builderList.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends ModulatorControllerBase> c) {
        for (ModulatorControllerBase mod : c) {
            mod.setGroupAttributes(getModulator());
        }
        return builderList.addAll(c);
    }

    @Override
    public Iterator<ModulatorControllerBase> iterator() {
        return builderList.iterator();
    }

}
