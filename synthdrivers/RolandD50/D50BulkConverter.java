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

import java.util.ArrayList;
import java.util.Arrays;

import core.Converter;
import core.Patch;



public class D50BulkConverter extends Converter {




    public D50BulkConverter() {
        super("Bulk Dump Converter", "Pascal Collberg");
        patchSize = 36048;
        sysexID = "F041**1412";
    }



    @Override
    public Patch[] extractPatch(Patch p) {
        byte[] sysex = p.getByteArray();
        ArrayList<byte[]> patchList = new ArrayList<byte[]>();
        int partialCounter = 0;
        int pos = 0;
        while(pos < sysex.length) {
            if((sysex[pos] & 0xFF) == 0xF0) {
                pos += D50Constants.SYSEX_HEADER.length;
                for(int i = 0; i < D50Constants.PARTIALS_PER_MESSAGE; i++) {
                    if(partialCounter == 0) {
                        System.out.println("New patch");
                        // New patch in message -> Add new byte array
                        byte[] buf = new byte[D50Constants.SYSEX_HEADER.length + D50Constants.PARTIALS_SIZE
                                              + D50Constants.SYSEX_FOOTER.length];
                        System.arraycopy(D50Constants.SYSEX_HEADER, 0, buf, 0, D50Constants.SYSEX_HEADER.length);
                        System.arraycopy(D50Constants.SYSEX_FOOTER, 0, buf, buf.length
                                                                            - D50Constants.SYSEX_FOOTER.length,
                                         D50Constants.SYSEX_FOOTER.length);
                        patchList.add(buf);
                    }

                    // Get last array
                    byte[] bs = patchList.get(patchList.size() - 1);

                    // Copy message data to patch array
                    System.arraycopy(sysex, pos, bs, D50Constants.SYSEX_HEADER.length + partialCounter
                                                     * D50Constants.PARTIAL_SIZE, D50Constants.PARTIAL_SIZE);

                    pos += D50Constants.PARTIAL_SIZE;

                    partialCounter++;
                    if(partialCounter == D50Constants.PARTIALS_PER_PATCH) {
                        // Reset for next patch
                        partialCounter = 0;
                    }
                }
                pos += D50Constants.SYSEX_FOOTER.length;

                // Break when all patches have been collected.
                if(partialCounter == 0 && patchList.size() == 64) {
                    break;
                }
            }
            else {
                throw new IllegalArgumentException("Bad patch bank data: " + Arrays.toString(sysex));
            }
        }

        // Create the patches from Sysex array
        Patch[] patches = new Patch[patchList.size()];
        for(int i = 0; i < patchList.size(); i++) {
            byte[] bs = patchList.get(i);
            patches[i] = new Patch(bs);
        }
        return patches;
    }
}
