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

import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.inject.Named;

import org.jsynthlib.utils.ctrlr.domain.CtrlrPanelModel;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;

/**
 * @author Pascal Collberg
 */
public class PanelLoadedController extends LuaMethodControllerBase implements
Observer {

    @Inject
    @Named("root")
    private LuaMethodProvider methodProvider;

    private final CtrlrPanelModel model;

    private Set<Entry<String, String>> globalVariableEntries;

    @Inject
    public PanelLoadedController(CtrlrPanelModel model) {
        super("panelCreated");
        this.model = model;
        model.putGlobalVariable("panel_loaded", "1");
        model.setPanelLoadedName(getMethodName());
        globalVariableEntries = model.getGlobalVariableEntries();
        model.addObserver(this);
    }

    @Override
    protected void writeLuaMethodCode() {
        int indent = 0;
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(indent(indent)).append("--").append(newLine());
        codeBuilder.append(indent(indent))
        .append("-- Called when a panel loaded ").append(newLine());
        codeBuilder.append(indent(indent++)).append("function ")
        .append(getMethodName()).append("()").append(newLine());

        for (Entry<String, String> entry : globalVariableEntries) {
            codeBuilder.append(indent(indent)).append(entry.getKey())
            .append(" = ").append(entry.getValue()).append(newLine());
        }
        codeBuilder.append(indent(--indent)).append("end").append(newLine());

        setLuaMethodCode(codeBuilder.toString());
    }

    @Override
    protected LuaMethodProvider getLuaMethodProvider() {
        return methodProvider;
    }

    @Override
    public void update(Observable o, Object arg) {
        globalVariableEntries = model.getGlobalVariableEntries();
        writeLuaMethodCode();
    }

}
