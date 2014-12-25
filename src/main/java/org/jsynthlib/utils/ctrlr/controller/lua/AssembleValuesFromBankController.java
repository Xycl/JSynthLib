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
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.BankToPatchRelationBean;
import org.jsynthlib.utils.ctrlr.domain.DriverModel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public class AssembleValuesFromBankController extends BankPatchControllerBase {

    public interface Factory {
        AssembleValuesFromBankController newAssembleValuesFromBankController(
                List<BankToPatchRelationBean> putPatchData);
    }

    private final DriverModel model;

    @Inject
    public AssembleValuesFromBankController(
            @Assisted List<BankToPatchRelationBean> putPatchData,
            @Named("prefix") String prefix, DriverModel model) {
        super(model.getGetPatchMethodName());
        this.model = model;
    }

    @Override
    protected void writeLuaMethodCode() {

        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent))
        .append("-- This method assembles the patch param values from ")
        .append(newLine());
        code.append(indent(indent))
        .append("-- a patch location in a bank and stores them in a memory block")
        .append(newLine());
        code.append(getMethodBegin(indent)).append(newLine());

        code.append(indent(indent)).append("local trimmedPatchData = ")
        .append(model.getBankDataVarName()).append(":getRange(")
        .append(BANK_OFFSET_VAR).append(", ")
        .append(PATCH_DATA_LENGTH_VAR).append(")").append(newLine());
        code.append(indent(indent)).append(PATCH_DATA_VAR)
        .append(":copyFrom(trimmedPatchData, ")
        .append(PATCH_DATA_OFFSET_VAR).append(", ")
        .append(PATCH_DATA_LENGTH_VAR).append(")").append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }
}
