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

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.MethodDescriptionPair;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author Pascal Collberg
 */
@Singleton
public class LoadMenuController extends MenuMethodControllerBase implements
Observer {

    public interface Factory {
        LoadMenuController newLoadMenuController();
    }

    private final DriverModel model;

    @Inject
    public LoadMenuController(@Named("prefix") String prefix, DriverModel model) {
        super(prefix + "_LoadMenu", "Load data from file");
        model.addObserver(this);
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof List<?>) {
            if (((List<?>) arg).get(0) instanceof MethodDescriptionPair) {
                setList(model.getLoadMenuOptions());
                init();
            }
        }
    }

    @Override
    protected void notifyModel() {
        model.deleteObserver(this);
        model.setLoadMenuName(getMethodName());
    }
}
