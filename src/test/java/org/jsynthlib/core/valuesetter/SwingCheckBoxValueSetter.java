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
package org.jsynthlib.core.valuesetter;

import org.fest.swing.fixture.JCheckBoxFixture;

public class SwingCheckBoxValueSetter implements IValueSetter {

    private JCheckBoxFixture fixture;
    private int min;

    public SwingCheckBoxValueSetter(JCheckBoxFixture fixture, int min) {
        super();
        this.fixture = fixture;
        this.min = min;
    }

    @Override
    public void setValue(int value) {
        if (value == min) {
            fixture.uncheck();
        } else {
            fixture.check();
        }
    }

}
