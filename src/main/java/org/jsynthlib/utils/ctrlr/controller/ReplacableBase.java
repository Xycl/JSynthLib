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
package org.jsynthlib.utils.ctrlr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Pascal Collberg
 *
 */
public abstract class ReplacableBase<T extends Observable> implements
Replaceable, Observer {

    private final List<ReplacableListener> listeners;
    private final T t;

    public ReplacableBase(T t) {
        this.t = t;
        this.listeners = new ArrayList<ReplacableListener>();
        t.addObserver(this);
    }

    @Override
    public void addListener(ReplacableListener l) {
        listeners.add(l);
    }

    @Override
    public void removeListener(ReplacableListener l) {
        listeners.remove(l);
    }

    @Override
    public void update(Observable o, Object arg) {
        String update = getUpdate(t);
        if (o.equals(t) && update != null) {
            ArrayList<ReplacableListener> temp =
                    new ArrayList<ReplacableListener>();
            temp.addAll(listeners);
            for (ReplacableListener listener : temp) {
                listener.onReplace(this, update);
            }
        }
    }

    protected abstract String getUpdate(T t);
}
