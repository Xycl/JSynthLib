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

import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.xmldevice.XmlDriverDefinition;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public class SelectPatchMethodController extends EditorLuaMethodControllerBase {

    public interface Factory {
        SelectPatchMethodController newSelectPatchMethodController();
    }

    @Inject
    private XmlDriverDefinition driverDef;

    private final DriverModel model;

    @Inject
    public SelectPatchMethodController(@Named("prefix") String prefix,
            DriverModel model) {
        super(model.getPatchSelectMethodName());
        this.model = model;
    }

    @Override
    protected void writeLuaMethodCode() {
        StringBuilder code = new StringBuilder();

        String patchDataVar = "patchData";

        AtomicInteger indent = new AtomicInteger(0);
        code.append(indent(indent))
        .append("-- This method assigns the selected patch to the panel modulators ")
        .append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append(getMethodDecl("mod", "newValue")).append(newLine());
        code.append(getPanelInitCheck(indent)).append(newLine());

        code.append(indent(indent.getAndIncrement())).append("if ")
        .append(model.getBankDataVarName()).append(" == nil then")
        .append(newLine());
        code.append(indent(indent))
                .append("mod:getComponent():setProperty(\"componentDisabled\", 1, false)")
        .append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        // code.append(indent(indent)).append("local ").append(patchDataVar)
        // .append(" = MemoryBlock(").append(driverDef.getPatchSize())
        // .append(", false)").append(newLine());

        code.append(indent(indent))
        .append("local ")
        .append(patchDataVar)
        .append(" = ")
        .append(getMethodCall(model.getGetPatchMethodName(), "newValue"))
        .append(newLine());

        code.append(indent(indent))
        .append(getMethodCall(model.getAssignValuesMethodName(),
                patchDataVar, "true")).append(newLine());

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }

}
