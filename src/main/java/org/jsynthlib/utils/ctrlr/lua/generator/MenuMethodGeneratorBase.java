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
package org.jsynthlib.utils.ctrlr.lua.generator;

import java.util.List;

import org.apache.log4j.Logger;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.lua.DriverLuaBean;
import org.jsynthlib.utils.ctrlr.lua.StringPair;

/**
 * @author Pascal Collberg
 *
 */
public abstract class MenuMethodGeneratorBase extends MethodGenerator {

    private final transient Logger log = Logger.getLogger(getClass());
    private final String menuHeader;

    public MenuMethodGeneratorBase(String menuHeader) {
        this.menuHeader = menuHeader;
    }

    @Override
    public LuaMethodType generateMethod(LuaMethodGroupType group,
            DriverLuaBean values) {
        List<StringPair> list = getList(values);
        if (list.size() <= 1) {
            log.info("Not enough receive menu options.");
            return null;
        } else {
            int indent = 0;
            StringBuilder codeBuilder = new StringBuilder();
            codeBuilder.append(indent(indent++)).append(
                    getMethodDecl(getMenuName(values)));
            codeBuilder
            .append(indent(indent))
            .append("-- This variable stops index issues during panel bootup")
            .append(newLine());
            codeBuilder
            .append(indent(indent++))
            .append("if panel:getRestoreState() == true or panel:getProgramState() == true then")
            .append(newLine());
            codeBuilder.append(indent(indent)).append("return")
            .append(newLine());
            codeBuilder.append(indent(--indent)).append("end")
            .append(newLine());

            codeBuilder.append(indent(indent))
            .append("menu = PopupMenu()    -- Main Menu")
            .append(newLine());

            codeBuilder.append(indent(indent))
            .append("menu:addSectionHeader (\"").append(menuHeader)
            .append("\")").append(newLine());

            for (int i = 0; i < list.size(); i++) {
                StringPair menuOption = list.get(i);
                codeBuilder.append(indent(indent)).append("menu:addItem(")
                .append(i + 1).append(", \"")
                .append(menuOption.getString2())
                .append("\", true, false, Image())").append(newLine());
            }

            codeBuilder.append(indent(indent))
            .append("ret = menu:show(0,0,0,0)").append(newLine());

            codeBuilder.append(indent(indent++)).append("if ret == 0 then")
            .append(newLine());
            codeBuilder.append(indent(indent)).append("return")
            .append(newLine());
            codeBuilder.append(indent(--indent)).append("end")
            .append(newLine());

            for (int i = 0; i < list.size(); i++) {
                StringPair menuOption = list.get(i);
                codeBuilder.append(indent(indent++)).append("if ret == ")
                .append(i + 1).append(" then").append(newLine());
                codeBuilder.append(indent(indent))
                .append(menuOption.getString1()).append("()")
                .append(newLine());
                codeBuilder.append(indent(--indent)).append("end")
                .append(newLine());
            }

            return createMethod(group, getMenuName(values),
                    codeBuilder.toString());
        }
    }

    protected abstract String getMenuName(DriverLuaBean values);

    protected abstract List<StringPair> getList(DriverLuaBean values);
}
