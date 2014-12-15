package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SavePatchMethodController extends SaveMethodControllerBase
implements Observer {

    public interface Factory {
        SavePatchMethodController newSavePatchMethodController();
    }

    private final DriverModel model;
    private String assembleValuesMethodName;

    @Inject
    public SavePatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_SavePatch", "patch");
        this.model = model;
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        assembleValuesMethodName = model.getAssembleValuesMethodName();
        init();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (assembleValuesMethodName == null) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
        if (model.getSaveMenuName() == null) {
            model.setSaveMenuName(getMethodName());
        }
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();

        code.append(indent(indent))
        .append("-- This method saves the current patch to file")
        .append(newLine());

        code.append(getMethodBegin(indent, false)).append(newLine());

        code.append(indent(indent))
        .append("local PatchDataCurrent = MemoryBlock()")
        .append(newLine());
        code.append(indent(indent))
        .append(getMethodCall(assembleValuesMethodName,
                "PatchDataCurrent")).append(newLine());

        code.append(getMethodEnd(indent)).append(newLine());

        setLuaMethodCode(code.toString());
    }
}
