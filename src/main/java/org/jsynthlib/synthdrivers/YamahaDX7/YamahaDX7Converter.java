/*
 * JSynthlib - Converter for Yamaha DX7 Mark-I (necessary for SER-7 ROM)
 * =====================================================================
 * @version $Id: YamahaDX7Converter.java 678 2004-08-21 17:08:55Z hayashi $
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7;

import org.jsynthlib.device.model.Converter;
import org.jsynthlib.patch.model.impl.Patch;

public class YamahaDX7Converter extends Converter {

  // Init Single Function patch (" YAMAHA DX7 FUNCTION DATA  ")
  static final byte[] INIT_PERFORMANCE = {
          -16, 67, 0, 1, 0, 94, 0, 0, 0, 7, 0, 0, 0, 0, 1, 8, 1, 8, 0, 8, 0,
          15, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 7, 0, 1, 24, 0, 0, 0, 7, 0, 0,
          0, 0, 1, 8, 1, 8, 0, 8, 0, 15, 0, 0, 0, 0, 0, 0, 0, 0, 99, 99, 7,
          0, 1, 24, 0, 0, 0, 39, 32, 89, 65, 77, 65, 72, 65, 32, 32, 68, 88,
          55, 32, 32, 70, 85, 78, 67, 84, 73, 79, 78, 32, 32, 68, 65, 84, 65,
          32, 32, 9, -9 };
    public YamahaDX7Converter() {
        super("SER7-Converter", "Torsten Tittmann");

        sysexID = "F0430*00011B";
        patchSize = 275;
        /* This doesn't seem to be used */
        // trimSize=275;
    }

    @Override
    public Patch[] extractPatch(Patch p) {
        byte[] sysex = p.getByteArray();
        byte[] sx = new byte[163]; // single voice
        byte[] tx = new byte[102]; // single performance
        byte[] px = new byte[112]; // rest of data

        System.arraycopy(sysex, 0, sx, 0, 163); // Copy single voice data into
                                                // the single voice patch
        System.arraycopy(sysex, 163, px, 0, 112); // Copy remaining data bytes,
                                                  // which are sent by SER-7 ROM
        System.arraycopy(INIT_PERFORMANCE, 0, tx,
                0, 102); // copy init performance patch

        // extract performance data, which are sent by SER-7 ROM per function
        // change messages
        tx[6 + 2] = px[0 * 7 + 5]; // Poly/Mono
        tx[6 + 3] = px[1 * 7 + 5]; // Pitch Bend Range
        tx[6 + 4] = px[2 * 7 + 5]; // Pitch Bend Step
        tx[6 + 7] = px[3 * 7 + 5]; // Portamento Mode
        tx[6 + 6] = px[4 * 7 + 5]; // Portamento Glissando
        tx[6 + 5] = px[5 * 7 + 5]; // Portamento Time
        tx[6 + 9] = px[6 * 7 + 5]; // Mod. Wheel Sensitivity
        tx[6 + 10] = px[7 * 7 + 5]; // Mod. Wheel Assign
        tx[6 + 11] = px[8 * 7 + 5]; // Foot Control Sensitivity
        tx[6 + 12] = px[9 * 7 + 5]; // Foot Control Assign
        tx[6 + 15] = px[10 * 7 + 5]; // Breath Control Sensitivity
        tx[6 + 16] = px[11 * 7 + 5]; // Breath Control Assign
        tx[6 + 13] = px[12 * 7 + 5]; // Aftertouch Sensitivity
        tx[6 + 14] = px[13 * 7 + 5]; // Aftertouch Assign
        // "FC-Volume Off/On" !not! part of performance patch
        // "Aftertouch Off/On" !not! part of performance patch

        // generate proper performance name
        //
        // clear init performance name
        for (int i = 64; i <= 93; i++) {
            tx[6 + i] = 0x20;
        }
        // copy single voice name to performance
        System.arraycopy(sx, 6 + 145, tx, 6 + 64, 10);
        // append string "  Function  Data" to voice name
        byte[] fkt_data_string =
                {
                        32, 32, 70, 117, 110, 99, 116, 105, 111, 110, 32, 32,
                        68, 97, 116, 97 };
        System.arraycopy(fkt_data_string, 0, tx, 6 + 74, 16);

        Patch[] pf = new Patch[2];

        pf[0] = getPatchFactory().createNewPatch(sx); // single voice
        pf[1] = getPatchFactory().createNewPatch(tx); // single performance

        return pf;
    }
}
