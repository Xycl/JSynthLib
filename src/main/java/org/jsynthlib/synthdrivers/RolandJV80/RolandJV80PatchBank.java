/*
 * Copyright 2004 Sander Brandenburg
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
package org.jsynthlib.synthdrivers.RolandJV80;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Copied the wavegroup data names from the jvtool project at sourceforge.net
 */

// if only this version of eclipse would support enums :/

/**
 * @author Sander Brandenburg
 * @version $Id: RolandJV80PatchBank.java 851 2005-01-27 07:36:36Z jbackhaus $
 */
public class RolandJV80PatchBank {
    protected final static List bankList = new ArrayList();
    protected final static Map bankMap = new HashMap();

    private final String name;

    private final int offset;

    private final String[] names;

    RolandJV80PatchBank(String name, int offset, String[] names) {
        this.name = name;
        this.offset = offset;
        this.names = names;

        bankList.add(name);
        bankMap.put(name, this);
    }

    public int getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public String[] getPatches() {
        return names;
    }

    public static RolandJV80PatchBank getBank(String bankName) {
        return (RolandJV80PatchBank) bankMap.get(bankName);
    }

    public static RolandJV80PatchBank getBankByPatchNumber(int patchnr) {
        Iterator i = bankList.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            RolandJV80PatchBank pb = getBank(s);
            if (pb.getOffset() + 64 > patchnr)
                return pb;
        }
        return null;
    }
}
