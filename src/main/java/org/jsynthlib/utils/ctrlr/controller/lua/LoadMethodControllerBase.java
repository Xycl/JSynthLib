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

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public abstract class LoadMethodControllerBase extends
EditorLuaMethodControllerBase {

    @Inject
    private DriverModel model;
    protected static final String loadedDataVar = "loadedData";

    public LoadMethodControllerBase(String methodName) {
        super(methodName);
    }

    protected String getLoadMethodStart(AtomicInteger indent,
            String fileDialogName) {
        StringBuilder code = new StringBuilder();
        code.append(indent(indent.getAndIncrement())).append(
                getMethodDecl("modulator", "newValue"));
        code.append(getPanelInitCheck(indent)).append(newLine());

        code.append(
                getSaveCurrentWorkPrompt(model.getBankDataVarName(), indent))
                .append(
                        newLine());

        code.append(indent(indent)).append("f = utils.openFileWindow (\"")
        .append(fileDialogName)
        .append("\", File(\"\"), \"*.syx\", true)").append(newLine());
        code.append(indent(indent.getAndIncrement()))
        .append("if f:existsAsFile() then").append(newLine());
        code.append(indent(indent)).append("local ").append(loadedDataVar)
                .append(" = MemoryBlock()").append(newLine());
        code.append(indent(indent)).append("f:loadFileAsData(")
        .append(loadedDataVar).append(")").append(newLine());
        return code.toString();
    }
}
