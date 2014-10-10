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

import java.util.Iterator;

import org.jsynthlib.core.valuesetter.IValueSetter;
import org.jsynthlib.device.model.AbstractEnvelopeParam;
import org.jsynthlib.device.model.EnvelopeModel;
import org.jsynthlib.device.model.EnvelopeNode;
import org.jsynthlib.device.view.Envelope;

/**
 * @author Pascal Collberg
 *
 */
public class JFXEnvelopeValueSetter implements IValueSetter {


    private AbstractEnvelopeParam param;

    public JFXEnvelopeValueSetter(Envelope widget, int faderOffset) {
        EnvelopeModel model = widget.getModel();
        Iterator<EnvelopeNode> iterator = model.iterator();
        while (iterator.hasNext()) {
            EnvelopeNode envelopeNode = iterator.next();
            if (envelopeNode.isVariableX() && faderOffset == envelopeNode.getFaderNumX()) {
                this.param = envelopeNode.getxParam();
                break;
            } else if (envelopeNode.isVariableY() && faderOffset == envelopeNode.getFaderNumY()) {
                this.param = envelopeNode.getyParam();
                break;
            }
        }
        if (this.param == null) {
            throw new IllegalStateException("Could not find corresponding fader.");
        }
    }

    /* (non-Javadoc)
     * @see org.jsynthlib.core.valuesetter.IValueSetter#setValue(int)
     */
    @Override
    public void setValue(int value) {
        param.valueProperty().set(value);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
    }

}
