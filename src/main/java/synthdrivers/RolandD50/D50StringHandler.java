/*
 * Copyright 2013 Pascal Collberg
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
package synthdrivers.RolandD50;

import java.util.Arrays;
import java.util.List;

import core.Patch;

public final class D50StringHandler {

    public static final char[] CHARACTER_SET = new char[64];
    private static final int NUM_LETTERS = 26;
    private static final int UPPER_A_OFFSET = 65;
    private static final int LOWER_A_OFFSET = 97;
    private static final int ZERO_OFFSET = 48;

    static {
        CHARACTER_SET[0] = ' ';
        for (int i = 0; i < NUM_LETTERS; i++) {
            CHARACTER_SET[i + 1] = (char) (i + UPPER_A_OFFSET);
            CHARACTER_SET[i + NUM_LETTERS + 1] = (char) (i + LOWER_A_OFFSET);
        }
        for (int i = 1; i <= 9; i++) {
            CHARACTER_SET[i + NUM_LETTERS * 2] = (char) (i + ZERO_OFFSET);
        }
        CHARACTER_SET[62] = '0';
        CHARACTER_SET[63] = '-';
    }

    private D50StringHandler() {
    }

    public static String getName(Patch patch, int offset, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            int index = (int) patch.sysex[i];
            sb.append(CHARACTER_SET[index]);
        }
        return sb.toString();
    }

    public static void setName(Patch patch, String name, int offset, int length) {
        String characters = new String(CHARACTER_SET);
        for (int i = 0; i < length; i++) {
            if (i < name.length()) {
            char character = name.charAt(i);
            int index = characters.indexOf(character);
            if (index >= 0) {
                patch.sysex[offset + i] = (byte) index;
                }
            } else {
                patch.sysex[offset + i] = (byte) 0;
            }
        }
    }
}
