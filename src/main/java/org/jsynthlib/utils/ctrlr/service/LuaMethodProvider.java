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
package org.jsynthlib.utils.ctrlr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.UUID;

import org.ctrlr.panel.LuaMethodGroupType;
import org.ctrlr.panel.LuaMethodType;
import org.jsynthlib.utils.ctrlr.domain.MethodDescriptionPair;

/**
 * @author Pascal Collberg
 */
public abstract class LuaMethodProvider extends Observable {

    private final Map<String, LuaMethodType> methodMap;
    private final Map<String, LuaMethodGroupType> groupMap;

    private final List<MethodDescriptionPair> loadMenuOptions;
    private final List<MethodDescriptionPair> receiveMenuOptions;
    private final List<MethodDescriptionPair> saveMenuOptions;
    private final List<MethodDescriptionPair> writeMenuOptions;

    public LuaMethodProvider() {
        this.loadMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.receiveMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.saveMenuOptions = new ArrayList<MethodDescriptionPair>();
        this.writeMenuOptions = new ArrayList<MethodDescriptionPair>();
        methodMap = new HashMap<String, LuaMethodType>();
        groupMap = new HashMap<String, LuaMethodGroupType>();
    }

    protected String newUuid() {
        return UUID.randomUUID().toString().replace("_", "");
    }

    public LuaMethodType getLuaMethod(String name) {
        if (methodMap.containsKey(name)) {
            return methodMap.get(name);
        } else {
            LuaMethodType method = createLuaMethod();
            method.setLuaMethodName(name);
            method.setUuid(newUuid());
            methodMap.put(name, method);
            return method;
        }
    }

    public LuaMethodGroupType getLuaMethodGroup(String name) {
        if (groupMap.containsKey(name)) {
            return groupMap.get(name);
        } else {
            LuaMethodGroupType group = createLuaMethodGroup();
            group.setUuid(newUuid());
            group.setName(name);
            groupMap.put(name, group);
            return group;
        }

    }

    protected abstract LuaMethodType createLuaMethod();

    protected abstract LuaMethodGroupType createLuaMethodGroup();

    protected Map<String, LuaMethodType> getMethodMap() {
        return methodMap;
    }

    protected Map<String, LuaMethodGroupType> getGroupMap() {
        return groupMap;
    }

    public List<MethodDescriptionPair> getWriteMenuOptions() {
        return writeMenuOptions;
    }

    public List<MethodDescriptionPair> getLoadMenuOptions() {
        return loadMenuOptions;
    }

    public List<MethodDescriptionPair> getReceiveMenuOptions() {
        return receiveMenuOptions;
    }

    public List<MethodDescriptionPair> getSaveMenuOptions() {
        return saveMenuOptions;
    }

    public void addLoadMenuOption(MethodDescriptionPair loadMenuOption) {
        this.loadMenuOptions.add(loadMenuOption);
        setChanged();
        notifyObservers(loadMenuOptions);
    }

    public void addReceiveMenuOption(MethodDescriptionPair receiveMenuOption) {
        this.receiveMenuOptions.add(receiveMenuOption);
        setChanged();
        notifyObservers(receiveMenuOptions);
    }

    public void addSaveMenuOption(MethodDescriptionPair saveMenuOption) {
        this.saveMenuOptions.add(saveMenuOption);
        setChanged();
        notifyObservers(saveMenuOptions);
    }

    public void addWriteMenuOption(MethodDescriptionPair writeMenuOption) {
        this.writeMenuOptions.add(writeMenuOption);
        setChanged();
        notifyObservers(writeMenuOptions);
    }

    public void driverParseComplete() {
        setChanged();
        notifyObservers();
    }
}
