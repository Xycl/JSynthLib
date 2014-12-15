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
package org.jsynthlib.utils.ctrlr.service.impl;

import org.ctrlr.panel.LuaManagerMethodsType;
import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Singleton;

/**
 * @author Pascal Collberg
 *
 */
@Singleton
public class RootLuaMethodProvider extends LuaMethodProvider {

    private LuaManagerMethodsType methods;


    public LuaManagerMethodsType getMethods() {
        return methods;
    }

    public void setMethods(LuaManagerMethodsType methods) {
        this.methods = methods;
    }

    @Override
    public LuaMethodType createLuaMethod() {
        return methods.addNewLuaMethod();
    }

    @Override
    public LuaMethodGroupType createLuaMethodGroup() {
        return methods.addNewLuaMethodGroup();
    }

}
