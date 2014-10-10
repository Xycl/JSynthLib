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

import org.jsynthlib.device.model.SysexHandler;

/**
 * @author Sander Brandenburg
 * @version $Id: JV80Constants.java 851 2005-01-27 07:36:36Z jbackhaus $
 */
public class JV80Constants {

    final static int PATCHCOMMONLEN = 45;
    final static int PATCHTONELEN = 127;
    final static int TONEOFFS[] = {
            PATCHCOMMONLEN, PATCHCOMMONLEN + PATCHTONELEN,
            PATCHCOMMONLEN + 2 * PATCHTONELEN,
            PATCHCOMMONLEN + 3 * PATCHTONELEN, };

    final static int PATCHOFFS[] = {
            0, PATCHCOMMONLEN, PATCHCOMMONLEN + PATCHTONELEN,
            PATCHCOMMONLEN + 2 * PATCHTONELEN,
            PATCHCOMMONLEN + 3 * PATCHTONELEN, };

    final static int NUM_TONES = 4;
    final static int PATCH_SIZE = PATCHCOMMONLEN + NUM_TONES * PATCHTONELEN;
    final static int DEVICEIDOFFSET = 2;
    final static int BANKIDX = 5;
    final static int PATCHIDX = 6;
    final static int TONEIDX = 7;
    final static int SIZELSB = 12;

    final static int PATCH_NAME_START = 9;
    final static int PATCH_NAME_SIZE = 12;

    final static int CHECKSUM_START = 5;
    final static int CHECKSUM_OFFSET_END = -1; // offset from EOX

    final static int ADDR1_IDX = 5;
    final static int ADDR2_IDX = 6;
    final static int ADDR3_IDX = 7;
    final static int ADDR4_IDX = 8;
    final static int SIZEL_IDX = 12;

    final static int PATCHCOMMON_DATA_LEN = 0x22;
    final static int PATCHTONE_DATA_LEN = 0x74;
    final static String SYSEXID = "F041**4612";

    final static int SYSREQDATALEN = 4;
    final static SysexHandler sysexRequestDump =
            new SysexHandler(
                    "F0 41 @@ 46 11 *addr1* *addr2* *addr3* 00 00 00 00 *sizeL* *checksum F7");

    final static String NOPATCHES[] = new String[] {};
    final static String BANKS[] = new String[] {
            "Internal", "Card" };
}
