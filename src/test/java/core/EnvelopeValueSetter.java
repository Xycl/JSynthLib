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
package core;

import org.fest.swing.edt.GuiActionRunner;
import org.fest.swing.edt.GuiQuery;

import core.EnvelopeWidget;
import core.valuesetter.IValueSetter;

public class EnvelopeValueSetter implements IValueSetter {

    private EnvelopeWidget widget;
    private int faderOffset;

    public EnvelopeValueSetter(EnvelopeWidget widget, int faderOffset) {
        super();
        this.widget = widget;
        this.faderOffset = faderOffset;
    }

    @Override
    public void setValue(final int value) {
        GuiActionRunner.execute(new GuiQuery<Object>() {
            public Object executeInEDT() {
                widget.setFaderValue(faderOffset, value);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                return null;
            }
        });
    }

}
