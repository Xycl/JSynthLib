/*
 * Copyright 2014 Pascal Collberg
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */
package org.jsynthlib.utils.ctrlr.controller.lua;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public class AssignBankController extends BankPatchControllerBase implements Observer {

    private static final String LOADED_DATA = "loadedData";

    public interface Factory {
        AssignBankController newAssignBankController();
    }

    @Inject
    private XmlDriverDefinition driverDef;

    private final DriverModel model;
    private final String prefix;
    private final String bankDataVar;

    private int singlePatchSize = -1;

    @Inject
    public AssignBankController(@Named("prefix") String prefix,
            DriverModel model) {
        super(model.getAssignBankMethodName());
        this.model = model;
        bankDataVar = model.getBankDataVarName();

        this.prefix = prefix;
        model.addObserver(this);

    }

    @Override
    protected void writeLuaMethodCode() {

        String patchNameTableVar = prefix + "PatchNameTable";

        String patchDataVar = "patchData";
        String loopIndex = "i";

        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method stores the param values from all modulators ")
        .append(newLine());
        code.append(indent(indent))
        .append("-- and stores them in a specified patch location of a bank")
        .append(newLine());

        code.append(indent(indent.getAndIncrement())).append(
                getMethodDecl(LOADED_DATA));
        code.append(getPanelInitCheck(indent)).append(newLine());

        code.append(indent(indent)).append(bankDataVar)
        .append(" = MemoryBlock(").append(LOADED_DATA)
        .append(":getSize(), false)").append(newLine());

        code.append(indent(indent)).append(bankDataVar).append(":copyFrom(")
        .append(LOADED_DATA).append(", 0, ").append(LOADED_DATA)
        .append(":getSize())").append(newLine());

        code.append(indent(indent)).append(patchNameTableVar).append(" = {}")
        .append(newLine());

        code.append(indent(indent)).append("local numPatches = ")
        .append(driverDef.getPatchNumbers().getStringArray().length)
        .append(newLine());

        code.append(indent(indent.getAndIncrement())).append("for ")
        .append(loopIndex).append(" = 1, (numPatches) do")
        .append(newLine());

        code.append(indent(indent)).append("local ").append(patchDataVar)
                .append(" = MemoryBlock(").append(singlePatchSize)
        .append(", false)").append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(model.getGetPatchMethodName(),
                patchDataVar, loopIndex)).append(newLine());

        code.append(indent(indent.getAndIncrement())).append("if ")
        .append(loopIndex).append(" == 1 then").append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(model.getAssignValuesMethodName(),
                patchDataVar, "true")).append(newLine());
        code.append(indent(indent)).append("getModulatorByName(")
        .append(model.getPatchSelectName()).append("):setValue(0)")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (singlePatchSize == -1) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void initialize() {
        super.initialize();
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        singlePatchSize  = model.getSinglePatchSize();
        init();
    }

}
