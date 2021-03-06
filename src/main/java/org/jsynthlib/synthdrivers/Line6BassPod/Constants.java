/*
 * Copyright 2004 Jeff Weber
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

package org.jsynthlib.synthdrivers.Line6BassPod;

/**
 * Constants class for Line6BassPod
 * @author Jeff Weber
 */
final class Constants {

    /** Manufacturer of device */
    static final String MANUFACTURER_NAME = "Line6";
    /** Name of device */
    static final String DEVICE_NAME = "Bass POD";
    /** Name of Converter for device */
    static final String CONVERTER_NAME = "Bass POD All Dump Converter";
    /** Line6 Bass POD Universal Device Inquiry */
    static final String INQUIRY_ID = "F07E..060200010C02000000........F7";
    /** Text displayed in the synth driver device details window in preferences */
    static final String INFO_TEXT = "Device for Line6 Bass POD.";
    /** Author of this Driver */
    static final String AUTHOR = "Jeff Weber";

    /** Program Dump Header Size */
    static final int PDMP_HDR_SIZE = 9;
    /** Edit Buffer Dump Header Size */
    static final int EDMP_HDR_SIZE = 8;
    /** Bank Dump Header Size */
    static final int BDMP_HDR_SIZE = 8;
    /** Single Patch size (not including header and stop byte) */
    static final int SIGL_SIZE = 160;
    /** the number of single patches in an actual bank patch. */
    static final int PATCHES_PER_BANK = 36;
    /**
     * Offset of the patch name in the sysex record--offset value does not
     * include the sysex header.
     */
    static final int PATCH_NAME_START = 64;
    /** Patch Name--Size in bytes */
    static final int PATCH_NAME_SIZE = 16;
    /** Offset of the device ID in the sysex record--Not used by Pod */
    static final int DEVICE_ID_OFFSET = 0;

    /** List of virtual bank numbers for single driver */
    static final String PRGM_BANK_LIST[] = new String[] {
            "1", "2", "3", "4", "5", "6", "7", "8", "9" };
    /** List of patch single driver */
    static final String PRGM_PATCH_LIST[] = new String[] {
            "A", "B", "C", "D" };

    /** List of virtual bank numbers for edit buffer driver */
    static final String EDIT_BANK_LIST[] = new String[] {
        "1" };
    /** List of patch edit buffer driver */
    static final String EDIT_PATCH_LIST[] = new String[] {
        "Edit" };

    /** List of virtual bank numbers for bank driver */
    static final String BANK_BANK_LIST[] = new String[] {
        "Pod Patches" };
    /** List of patch Bank driver */
    static final String BANK_PATCH_LIST[] = new String[] {
            "1-A", "1-B", "1-C", "1-D", "2-A", "2-B", "2-C", "2-D", "3-A",
            "3-B", "3-C", "3-D", "4-A", "4-B", "4-C", "4-D", "5-A", "5-B",
            "5-C", "5-D", "6-A", "6-B", "6-C", "6-D", "7-A", "7-B", "7-C",
            "7-D", "8-A", "8-B", "8-C", "8-D", "9-A", "9-B", "9-C", "9-D" };

    /** Converter Match ID--Used to match a patch to a Line6BassPodConverter */
    static final String CONV_SYSEX_MATCH_ID = "F000010C0201****";

    /** Single Dump Request ID--Sent to POD for a single dump request. */
    static final String SIGL_DUMP_REQ_ID = "F0 00 01 0C 02 00 00 *progNum* F7";
    /** Single Dump Patch Type String */
    static final String SIGL_PATCH_TYP_STR = "Single";
    /**
     * Single Dump Match ID--Used to match a patch to a Line6BassPodSingleDriver
     */
    static final String SIGL_SYSEX_MATCH_ID = "F000010C020100****";
    /** Single Dump Header Bytes--Bytes in a single dump header */
    static final byte[] SIGL_DUMP_HDR_BYTES = {
            (byte) 0xF0, (byte) 0x00, (byte) 0x01, (byte) 0x0C, (byte) 0x02,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01 };
    /**
     * First 7 bytes of Edit Patch Header--Used by Line6BassPodSingleDriver to
     * decide if a patch is an edit buffer patch and then convert it to a
     * program patch.
     */
    static final String EDIT_BUFR_PATCH = "F000010C020101";

    /** Edit Buffer Dump Request ID--Sent to POD for edit buffer dump request. */
    static final String EDIT_DUMP_REQ_ID = "F0 00 01 0C 02 00 01 F7";
    /** Edit Buffer Dump Patch Type String */
    static final String EDIT_PATCH_TYP_STR = "Edit Buffer";
    /** Single Dump Match ID--Used to match a patch to a Line6BassPodEdBufDriver */
    static final String EDIT_SYSEX_MATCH_ID = "F000010C020101**";
    /** Edit Buffer Dump Header Bytes--Bytes in an edit buffer dump header */
    static final byte[] EDIT_DUMP_HDR_BYTES = {
            (byte) 0xF0, (byte) 0x00, (byte) 0x01, (byte) 0x0C, (byte) 0x02,
            (byte) 0x01, (byte) 0x01, (byte) 0x01 };

    /** Bank Dump Request ID--Sent to POD for bank dump request. */
    static final String BANK_DUMP_REQ_ID = "F0 00 01 0C 02 00 02 F7";
    /** Bank Dump Patch Type String */
    static final String BANK_PATCH_TYP_STR = "Bank";
    /** Bank Dump Match ID--Used to match a patch to a Line6BassPodBankDriver */
    static final String BANK_SYSEX_MATCH_ID = "F000010C020102**";
    /** Bank Dump Header Bytes--Bytes in an edit buffer dump header */
    static final byte[] BANK_DUMP_HDR_BYTES = {
            (byte) 0xF0, (byte) 0x00, (byte) 0x01, (byte) 0x0C, (byte) 0x02,
            (byte) 0x01, (byte) 0x02, (byte) 0x01 };

    /** Message displayed when the Play command is invoked */
    static final String PLAY_CMD_MSG = "Play your bass to hear the patch.";

    /**
     * Delay of pauses in milliseconds between each patch when sending a whole
     * bank of patches. Delay is needed so POD can keep up.
     */
    static final int PATCH_SEND_INTERVAL = 500;

    /** Sysex program dump byte array representing a new program patch */
    static final byte NEW_SYSEX[] = {
            (byte) 0xF0, (byte) 0x00, (byte) 0x01, (byte) 0x0C, (byte) 0x02,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x03, (byte) 0x0F, (byte) 0x01,
            (byte) 0x06, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x0F, (byte) 0x02, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x01, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x07,
            (byte) 0x0F, (byte) 0x00, (byte) 0x00, (byte) 0x07, (byte) 0x0F,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x07, (byte) 0x00, (byte) 0x03, (byte) 0x09,
            (byte) 0x05, (byte) 0x03, (byte) 0x02, (byte) 0x06, (byte) 0x03,
            (byte) 0x04, (byte) 0x00, (byte) 0x0B, (byte) 0x03, (byte) 0x0F,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0A, (byte) 0x02,
            (byte) 0x07, (byte) 0x02, (byte) 0x0A, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x0A, (byte) 0x03,
            (byte) 0x0F, (byte) 0x01, (byte) 0x0C, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x02, (byte) 0x00, (byte) 0x02, (byte) 0x00, (byte) 0xF7 };
}
