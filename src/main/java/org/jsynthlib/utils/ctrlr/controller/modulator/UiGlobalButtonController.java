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
package org.jsynthlib.utils.ctrlr.controller.modulator;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.jsynthlib.utils.ctrlr.domain.DriverModel;
import org.jsynthlib.utils.ctrlr.domain.GlobalSliderSpecWrapper;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Pascal Collberg
 *
 */
public class UiGlobalButtonController extends UiButtonController implements
Observer {

    public interface Factory {
        UiGlobalButtonController newUiGlobalButtonController(
                Globalbuttons button);
    }

    public enum Globalbuttons {

        RECEIVE("Receive"), LOAD("Load"), SAVE("Save"), WRITE("Write");

        private final String name;

        private Globalbuttons(String name) {
            this.name = name;
        }
    }

    private final Globalbuttons button;

    @Inject
    public UiGlobalButtonController(@Assisted Globalbuttons button,
            DriverModel model) {
        super(new GlobalSliderSpecWrapper(button.name));
        this.button = button;
        model.addObserver(this);
    }

    @Override
    public void init() {
        super.init();
        ArrayList<String> contents = new ArrayList<String>();
        contents.add(button.name);
        setContents(contents);
        setButtonColorOff(getComponent().getUiButtonColourOn());
        setMidiMessageCtrlrValue(1);
        setMuteOnStart(true);
        setExcludeFromSnapshot(true);
        setLabelVisible(false);
        setMidiMessageSysExFormula("");
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof DriverModel) {
            DriverModel model = (DriverModel) o;
            switch (button) {
            case RECEIVE:
                setLuaModulatorValueChange(model.getReceiveMenuName());
                break;
            case WRITE:
                setLuaModulatorValueChange(model.getWriteMenuName());
                break;
            case LOAD:
                setLuaModulatorValueChange(model.getLoadMenuName());
                break;
            case SAVE:
                setLuaModulatorValueChange(model.getSaveMenuName());
                break;
            default:
                throw new IllegalStateException("Bad global button");
            }
        }
    }
}
