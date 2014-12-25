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

import javax.inject.Named;

import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public class PanelLoadedCallbackController extends LuaMethodControllerBase {

    public static final String METHOD_NAME = "panelLoadedCallback";

    @Inject
    @Named("root")
    private LuaMethodProvider methodProvider;

    @Inject
    public PanelLoadedCallbackController() {
        super(METHOD_NAME);
    }

    @Override
    protected LuaMethodProvider getLuaMethodProvider() {
        return methodProvider;
    }

    @Override
    protected void writeLuaMethodCode() {
        int indent = 0;
        StringBuilder code = new StringBuilder();
        code.append(indent(indent)).append("--").append(newLine());
        code.append(indent(indent)).append("-- Called when a panel loaded ")
        .append(newLine());
        code.append(indent(indent++)).append("function ")
        .append(getMethodName()).append("()").append(newLine());

        code.append(indent(indent)).append("panel_loaded = 1")
        .append(newLine());
        code.append(indent(indent)).append("timer:stopTimer(33)")
                .append(newLine());

        code.append(indent(--indent)).append("end").append(newLine());

        setLuaMethodCode(code.toString());

    }

}
