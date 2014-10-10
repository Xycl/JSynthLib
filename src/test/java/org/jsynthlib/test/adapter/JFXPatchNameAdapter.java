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

import javafx.scene.control.TextField;

import org.jemmy.fx.SceneDock;
import org.jemmy.fx.control.TextInputControlDock;
import org.jemmy.interfaces.Keyboard.KeyboardButtons;
import org.jsynthlib.core.valuesetter.IPatchNameValueSetter;
import org.jsynthlib.core.valuesetter.IValueSetter;

/**
 * @author Pascal Collberg
 */
public class JFXPatchNameAdapter extends AbstractJFXWidgetAdapter {

    private final TextField textField;
    private final SceneDock scene;

    JFXPatchNameAdapter(SceneDock scene, TextField comboBox) {
        super(scene, comboBox);
        this.scene = scene;
        this.textField = comboBox;
        setType(Type.PATCH_NAME);
        setMin(Integer.MIN_VALUE);
        setMax(Integer.MAX_VALUE);
    }

    /*
     * (non-Javadoc)
     * @see org.jsynthlib.test.adapter.WidgetAdapter#getValueSetter()
     */
    @Override
    public IValueSetter getValueSetter() {
        return new IPatchNameValueSetter() {

            /*
             * (non-Javadoc)
             * @see org.jsynthlib.core.valuesetter.IValueSetter#setValue(int)
             */
            @Override
            public void setValue(int value) {
                TextInputControlDock textDock =
                        new TextInputControlDock(scene.asParent(),
                                textField.getId());
                textDock.asSelectionText().clear();
                textDock.type(VALUES[value]);

                textField.textProperty().set(VALUES[value]);

                scene.keyboard().pressKey(KeyboardButtons.TAB);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
        };
    }
}
