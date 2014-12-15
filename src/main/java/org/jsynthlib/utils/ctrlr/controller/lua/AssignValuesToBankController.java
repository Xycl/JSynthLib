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

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.BankToPatchRelationBean;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public class AssignValuesToBankController extends BankPatchControllerBase
implements Observer {

    public interface Factory {
        AssignValuesToBankController newAssignValuesToBankController(
                List<BankToPatchRelationBean> putPatchData);
    }

    private String bankDataVar;
    private final DriverModel model;

    @Inject
    public AssignValuesToBankController(
            @Assisted List<BankToPatchRelationBean> putPatchData,
            @Named("prefix") String prefix, DriverModel model) {
        super(prefix + "_AssignValuesToBank", putPatchData);
        model.setAssignValuesToBankMethodName(getMethodName());
        this.model = model;
        model.addObserver(this);
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (bankDataVar == null) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method stores the param values from all modulators ")
        .append(newLine());
        code.append(indent(indent))
        .append("-- and stores them in a specified patch location of a bank")
        .append(newLine());
        code.append(getMethodBegin(indent)).append(newLine());

        code.append(indent(indent)).append("local trimmedPatchData = ")
        .append(PATCH_DATA_VAR)
        .append(":getRange(patchDataOffset, patchDataLength)")
        .append(newLine());
        code.append(indent(indent))
                .append(bankDataVar)
                .append(":copyFrom(trimmedPatchData, bankOffset, patchDataLength)")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
        model.deleteObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        bankDataVar = model.getBankDataVarName();
        init();
    }

}
