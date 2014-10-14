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

import java.util.Comparator;

/**
 * @author Pascal Collberg
 *
 */
public class LibraryColumnComparator implements Comparator<Patch> {
    private final LibraryColumns column;

    public LibraryColumnComparator(LibraryColumns column) {
        this.column = column;
    }

    @Override
    public int compare(Patch a1, Patch a2) {
        String s1;
        String s2;
        switch (column) {
        case PATCH_NAME:
            s1 = a1.getName().toLowerCase();
            s2 = a2.getName().toLowerCase();
            break;
        case FIELD_1:
            s1 = a1.getDate().toLowerCase();
            s2 = a2.getDate().toLowerCase();
            break;
        case FIELD_2:
            s1 = a1.getAuthor().toLowerCase();
            s2 = a2.getAuthor().toLowerCase();
            break;
        case SYNTH:
            s1 = a1.getDevice().getSynthName();
            s2 = a2.getDevice().getSynthName();
            break;
        case TYPE:
        default:
            s1 = a1.getType();
            s2 = a2.getType();
            break;
        }
        return s1.compareTo(s2);
    }
}
