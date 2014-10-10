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
package org.jsynthlib.test.adapter;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;

import org.jemmy.fx.SceneDock;
import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.xmldevice.CombinedIntPatchParam;
import org.jsynthlib.xmldevice.IntParamSpec;

/**
 * @author Pascal Collberg
 */
public class JFXComboBoxAdapter extends AbstractJFXWidgetAdapter {

    private final ComboBox<String> comboBox;

    JFXComboBoxAdapter(SceneDock scene, ComboBox<String> comboBox,
            int max, int min) {
        super(scene, comboBox);
        this.comboBox = comboBox;
        setValue(comboBox.getSelectionModel().getSelectedIndex());
        setMax(max);
        setMin(min);
        setType(Type.COMBOBOX);
    }

    /**
     * @param scene
     * @param xmlObject
     * @param control
     */
    public JFXComboBoxAdapter(SceneDock scene, ComboBox<String> comboBox,
            IntParamSpec xmlObject) {
        this(scene, comboBox, xmlObject.getMax(), xmlObject.getMin());
    }

    /**
     * @param sceneDock
     * @param comboBox2
     * @param combinedIntPatchParam
     */
    public JFXComboBoxAdapter(SceneDock sceneDock, ComboBox<String> comboBox2,
            CombinedIntPatchParam combinedIntPatchParam) {
        this(sceneDock, comboBox2, combinedIntPatchParam.getMax(),
                combinedIntPatchParam.getMin());
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.test.adapter.WidgetAdapter#getValueSetter()
     */
    @Override
    public IValueSetter getValueSetter() {
        return new IValueSetter() {

            @Override
            public void setValue(int value) {
                final String string = comboBox.getItems().get(value);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        comboBox.valueProperty().set(string);
                    }
                });
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        };
    }

}
