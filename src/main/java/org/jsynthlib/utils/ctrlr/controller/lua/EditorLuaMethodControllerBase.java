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

import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
public abstract class EditorLuaMethodControllerBase extends
LuaMethodControllerBase {

    private static final String PATCH_WARN =
            "This action will overwrite your existing patch. Are you sure you want to continue?";
    private static final String BANK_WARN =
            "You have loaded a bank. The current action will overwrite your existing bank. Are you sure you want to continue?";
    @Inject
    @Named("editor")
    private LuaMethodProvider luaMethodProvider;

    public EditorLuaMethodControllerBase(String methodName) {
        super(methodName);
    }

    @Override
    protected LuaMethodProvider getLuaMethodProvider() {
        return luaMethodProvider;
    }

    protected String getSaveCurrentWorkPrompt(String bankDataVar,
            AtomicInteger indent) {
        StringBuilder code = new StringBuilder();
        code.append(indent(indent.getAndIncrement())).append("if ")
        .append(bankDataVar).append(" ~= nil then").append(newLine());
        code.append(indent(indent)).append("-- Prompt user to save bank")
        .append(newLine());
        code.append(indent(indent))
        .append("ret = ")
        .append(getInfoOkCancelMessageCall("Overwrite bank?", BANK_WARN))
        .append(newLine());
        code.append(indent(indent)).append("if ret == false then return end")
        .append(newLine());

        code.append(indent(indent.decrementAndGet())).append("else")
        .append(newLine());
        code.append(indent(indent.incrementAndGet()))
        .append("-- Prompt user to save patch").append(newLine());
        code.append(indent(indent))
        .append("ret = ")
        .append(getInfoOkCancelMessageCall("Overwrite patch?",
                PATCH_WARN));
        code.append(indent(indent)).append("if ret == false then return end")
        .append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());
        return code.toString();
    }
}
