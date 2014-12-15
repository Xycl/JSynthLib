package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class LoadPatchMethodController extends LoadMethodControllerBase
implements Observer {

    public interface Factory {
        LoadPatchMethodController newLoadPatchMethodController();
    }

    private final DriverModel model;
    private String assignValuesMethodName;

    @Inject
    public LoadPatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_LoadPatch");
        this.model = model;
        model.addObserver(this);
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        super.checkPreconditions();
        if (assignValuesMethodName == null) {
            throw new PreConditionsNotMetException();
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
        if (model.getLoadMenuName() == null) {
            model.setLoadMenuName(getMethodName());
        }
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method loads a single patch from file")
        .append(newLine());
        code.append(indent(indent))
        .append("-- and assigns its data to the panel modulators")
        .append(newLine());
        code.append(getLoadMethodStart(indent, "Open Patch"))
        .append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(assignValuesMethodName, loadedDataVar,
                "true")).append(newLine());

        code.append(indent(indent)).append(getBankDataVar()).append(" = nil")
                .append(newLine());

        // Display Patch Loaded
        // codeBuilder.append(getInfoMessageCall("Patch Loaded", indent));
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }

    @Override
    public void update(Observable o, Object arg) {
        assignValuesMethodName = model.getAssignValuesMethodName();
        setBankDataVar(model.getBankDataVarName());
        init();
    }
}
