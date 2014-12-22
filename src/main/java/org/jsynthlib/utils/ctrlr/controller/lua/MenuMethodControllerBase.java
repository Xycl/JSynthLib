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

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsynthlib.utils.ctrlr.domain.MethodDescriptionPair;
import org.jsynthlib.utils.ctrlr.domain.PreConditionsNotMetException;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

/**
 * @author Pascal Collberg
 */
public abstract class MenuMethodControllerBase extends
EditorLuaMethodControllerBase implements Observer {

    private final String menuHeader;
    private List<MethodDescriptionPair> list;
    private final LuaMethodProvider luaProvider;

    public MenuMethodControllerBase(String methodName, String menuHeader,
            LuaMethodProvider luaProvider) {
        super(methodName);
        luaProvider.addObserver(this);
        this.luaProvider = luaProvider;
        this.menuHeader = menuHeader;
        this.list = new ArrayList<MethodDescriptionPair>();
    }

    @Override
    protected void checkPreconditions() throws PreConditionsNotMetException {
        if (list.size() <= 1) {
            throw new PreConditionsNotMetException();
        }
        super.checkPreconditions();
    }

    @Override
    protected void writeLuaMethodCode() {
        AtomicInteger indent = new AtomicInteger(0);
        StringBuilder code = new StringBuilder();
        code.append(indent(indent.getAndIncrement())).append(getMethodDecl());
        code.append(getPanelInitCheck(indent)).append(newLine());

        code.append(indent(indent))
        .append("menu = PopupMenu()    -- Main Menu").append(newLine());

        code.append(indent(indent)).append("menu:addSectionHeader (\"")
        .append(menuHeader).append("\")").append(newLine());

        for (int i = 0; i < list.size(); i++) {
            MethodDescriptionPair menuOption = list.get(i);
            code.append(indent(indent)).append("menu:addItem(").append(i + 1)
            .append(", \"").append(menuOption.getDescription())
            .append("\", true, false, Image())").append(newLine());
        }

        code.append(indent(indent)).append("ret = menu:show(0,0,0,0)")
        .append(newLine());

        code.append(indent(indent.getAndIncrement()))
        .append("if ret == 0 then").append(newLine());
        code.append(indent(indent)).append("return").append(newLine());
        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        for (int i = 0; i < list.size(); i++) {
            MethodDescriptionPair menuOption = list.get(i);
            code.append(indent(indent.getAndIncrement())).append("if ret == ")
            .append(i + 1).append(" then").append(newLine());
            code.append(indent(indent)).append(menuOption.getMethodName())
            .append("()").append(newLine());
            code.append(indent(indent.decrementAndGet())).append("end")
            .append(newLine());
        }

        code.append(indent(indent.decrementAndGet())).append("end")
        .append(newLine());

        setLuaMethodCode(code.toString());
    }

    public void setList(List<MethodDescriptionPair> list) {
        this.list = list;
    }

    protected LuaMethodProvider getLuaProvider() {
        return luaProvider;
    }
}
