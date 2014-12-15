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

import java.util.Collection;

import org.jsynthlib.utils.ctrlr.controller.modulator.ModulatorControllerBase;

/**
 * @author Pascal Collberg
 *
 */
public interface GroupController extends Iterable<ModulatorControllerBase> {

    boolean add(ModulatorControllerBase e);

    boolean addAll(Collection<? extends ModulatorControllerBase> c);
}
