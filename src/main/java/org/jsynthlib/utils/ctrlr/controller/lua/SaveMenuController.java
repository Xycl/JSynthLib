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

import java.util.Observable;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.service.LuaMethodProvider;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
@Singleton
public class SaveMenuController extends MenuMethodControllerBase {

    public interface Factory {
        SaveMenuController newSaveMenuController();
    }

    @Inject
    private DriverModel model;

    @Inject
    public SaveMenuController(@Named("prefix") String prefix,
            @Named("editor") LuaMethodProvider luaProvider) {
        super(prefix + "_SaveMenu", "Save Single Patch or Bank(s) to file",
                luaProvider);
    }

    @Override
    public void update(Observable o, Object arg) {
        setList(getLuaMethodProvider().getSaveMenuOptions());
        init();
    }

    @Override
    protected void setMethodNameToModel() {
        model.setSaveMenuName(getMethodName());
    }

}
