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
package org.jsynthlib.patch.model.impl;

/**
 * @author Pascal Collberg
 */
public enum LibraryColumns {
    /**
     * Synth column.
     */
    SYNTH("Synth"),

    /**
     * Type column.
     */
    TYPE("Type"),

    /**
     * Patch name column.
     */
    PATCH_NAME("Patch Name"),

    /**
     * Field 1 column.
     */
    FIELD_1("Field 1"),

    /**
     * Field 2 column.
     */
    FIELD_2("Field 2"),

    /**
     * Comment column.
     */
    COMMENT("Comment");

    private final String name;

    private LibraryColumns(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
