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

import java.util.Map;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 *
 */
public class EditorLuaMethodProvider extends LuaMethodProvider {

    public interface Factory {
        EditorLuaMethodProvider newLuaMethodProvider(LuaMethodGroupType group);
    }

    private final LuaMethodGroupType group;

    @Inject
    public EditorLuaMethodProvider(@Assisted LuaMethodGroupType group) {
        this.group = group;
        Map<String, LuaMethodType> methods = getMethodMap();
        LuaMethodType[] luaMethods = group.getLuaMethodArray();
        for (LuaMethodType method : luaMethods) {
            if (!methods.containsKey(method.getLuaMethodName())) {
                methods.put(method.getLuaMethodName(), method);
            }
        }
    }

    @Override
    public LuaMethodType createLuaMethod() {
        return group.addNewLuaMethod();
    }

    @Override
    public LuaMethodGroupType createLuaMethodGroup() {
        throw new UnsupportedOperationException(
                "Editor method providers can not create sub groups.");
    }

}
