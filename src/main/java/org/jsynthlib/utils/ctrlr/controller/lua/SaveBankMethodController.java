package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class SaveBankMethodController extends SaveMethodControllerBase
implements Observer {

    public interface Factory {
        SaveBankMethodController newSaveBankMethodController();
    }

    private final DriverModel model;
    private String assignValuesToBankMethodName;
    private String assembleValuesMethodName;
    private String bankDataVarName;

    @Inject
    public SaveBankMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(prefix + "_SaveBank", "Bank");
        this.model = model;
        model.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        assignValuesToBankMethodName = model.getAssignValuesToBankMethodName();
        assembleValuesMethodName = model.getAssembleValuesMethodName();
        bankDataVarName = model.getBankDataVarName();
        setBankVarName(bankDataVarName);
        init();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        super.checkPreconditions();
        if (assignValuesToBankMethodName == null
                || assembleValuesMethodName == null || bankDataVarName == null) {
            throw new PreConditionsNotMetException();
        }
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
        .append("-- This method saves the current bank to file")
        .append(newLine());

        code.append(getMethodBegin(indent, true)).append(newLine());

        // Write active patch to its bank location
        // use the global bank variable to save into the file

        code.append(indent(indent))
        .append("local PatchDataCurrent = MemoryBlock()")
        .append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(assembleValuesMethodName,
                "PatchDataCurrent")).append(newLine());

        // TODO: get patch number!
        code.append(indent(indent))
        .append(getMethodCall(assignValuesToBankMethodName,
                "PatchDataCurrent", "1")).append(newLine());

        code.append(getMethodEnd(indent)).append(newLine());

        setLuaMethodCode(code.toString());
    }
}
