/*
 * JSynthlib - "Performance" Bank Driver for Yamaha DX7 MK1
 * ========================================================
 * @version $Id: YamahaDX7PerformanceBankDriver.java 662 2004-08-13 03:08:21Z hayashi $
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.jsynthlib.synthdrivers.YamahaDX7;

import java.io.UnsupportedEncodingException;

import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.viewcontroller.desktop.JSLFrame;
import org.jsynthlib.device.model.XMLBankDriver;
import org.jsynthlib.patch.model.impl.BankPatch;
import org.jsynthlib.patch.model.impl.Patch;
import org.jsynthlib.synthdrivers.YamahaDX7.common.DX7FamilyStrings;
import org.jsynthlib.xmldevice.XmlBankDriverDefinitionDocument.XmlBankDriverDefinition;

public class YamahaDX7PerformanceBankDriver extends XMLBankDriver {

    private boolean spbp;
    private boolean swOffMemProt;
    private boolean tipsMsg;

    private int dxSysexHeaderSize;
    private int dxPatchNameOffset;
    private int dxSinglePackedSize;

    public YamahaDX7PerformanceBankDriver(XmlBankDriverDefinition driverSpec) {
        super(driverSpec);
    }

    /* Only dummy methods, because the DX7 doesn't support Performance banks */
    @Override
    public void storePatch(Patch p, int bankNum, int patchNum) {
        if (isTipsMsg()) {
            // show Information
            YamahaDX7Strings.dxShowInformation(toString(),
                    YamahaDX7Strings.PERFORMANCE_STRING);
        }
    }

    @Override
    public void requestPatchDump(int bankNum, int patchNum) {
        if (isTipsMsg()) {
            // show Information
            YamahaDX7Strings.dxShowInformation(toString(),
                    YamahaDX7Strings.PERFORMANCE_STRING);
        }
    }

    @Override
    public JSLFrame editPatch(Patch p) {
        if (isTipsMsg()) {
            // show Information
            YamahaDX7Strings.dxShowInformation(toString(),
                    YamahaDX7Strings.PERFORMANCE_STRING);
        }

        return super.editPatch(p);
    }

    public int getPatchStart(int patchNum) {
        return (dxSinglePackedSize * patchNum) + dxSysexHeaderSize;
    }

    public int getPatchNameStart(int patchNum) {
        return getPatchStart(patchNum) + dxPatchNameOffset;
    }

    @Override
    public String getPatchName(Patch p, int patchNum) {
        int nameStart = getPatchNameStart(patchNum);

        try {
            StringBuffer s =
                    new StringBuffer(new String(p.sysex, nameStart,
                            getPatchNameSize(), "US-ASCII"));

            return s.toString();
        } catch (UnsupportedEncodingException ex) {
            return "-";
        }
    }

    @Override
    public void setPatchName(Patch p, int patchNum, String name) {
        int nameStart = getPatchNameStart(patchNum);

        while (name.length() < getPatchNameSize()) {
            name = name + " ";
        }
        byte[] namebytes = new byte[getPatchNameSize()];

        try {
            namebytes = name.getBytes("US-ASCII");
            for (int i = 0; i < getPatchNameSize(); i++) {
                p.sysex[nameStart + i] = namebytes[i];
            }
        } catch (UnsupportedEncodingException ex) {
            return;
        }
    }

    @Override
    public void putPatch(Patch bank, Patch p, int patchNum) // puts a patch into
                                                            // the bank,
                                                            // converting it as
                                                            // needed
    {
        if (!canHoldPatch(p)) {
            DX7FamilyStrings.dxShowError(toString(),
                    "This type of patch does not fit in to this type of bank.");
            return;
        }

        // Transform Voice Data to Bulk Dump Packed Format

        // ***** Voice A *****
        bank.sysex[getPatchStart(patchNum) + 0] =
                (byte) (((p.sysex[6 + 2] & 1) * 64) // Poly/Mono
                                                    // .......................(0-
                                                    // 1)
                + (patchNum & 63)); // UNDOCUMENTED! related voice# ....(0-63)
        bank.sysex[getPatchStart(patchNum) + 1] =
                (byte) (((p.sysex[6 + 4] & 7) * 16) // Pitch Bend Step
                                                    // (Low)............(0-12)
                + (p.sysex[6 + 3] & 15)); // Pitch Bend Range
                                          // ................(0-12)
        bank.sysex[getPatchStart(patchNum) + 2] =
                (byte) ((p.sysex[6 + 5] & 127)); // Portamento Time
                                                 // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 3] =
                (byte) (((p.sysex[6 + 1] & 15) * 8) // Source Select
                                                    // ...................(0-15)
                        + ((p.sysex[6 + 8] & 1) * 4) // Portamento
                                                     // Pedal and Knob
                                                     // Assign (0- 1)
                        + ((p.sysex[6 + 7] & 1) * 2) // Portamento
                                                     // Mode
                                                     // .................(0-
                                                     // 1)
                + (p.sysex[6 + 6] & 1)); // Portamento/Glissando
                                         // ............(0- 1)
        bank.sysex[getPatchStart(patchNum) + 4] =
                (byte) (((p.sysex[6 + 10] & 7) * 16) // Modulation
                                                     // Wheel Assign
                                                     // .........(0-
                                                     // 7)
                + (p.sysex[6 + 9] & 15)); // Modulation Wheel
                                          // Sensitivity ....(0-15)
        bank.sysex[getPatchStart(patchNum) + 5] =
                (byte) (((p.sysex[6 + 12] & 7) * 16) // Foot Control
                                                     // Assign
                                                     // .............(0-
                                                     // 7)
                + (p.sysex[6 + 11] & 15)); // Foot Control Sensitivity
                                           // ........(0-15)
        bank.sysex[getPatchStart(patchNum) + 6] =
                (byte) (((p.sysex[6 + 14] & 7) * 16) // Aftertouch
                                                     // Assign
                                                     // ...............(0-
                                                     // 7)
                + (p.sysex[6 + 13] & 15)); // Aftertouch Sensitivity
                                           // ..........(0-15)
        bank.sysex[getPatchStart(patchNum) + 7] =
                (byte) (((p.sysex[6 + 16] & 7) * 16) // Breath Control
                                                     // Assign
                                                     // ...........(0-
                                                     // 7)
                + (p.sysex[6 + 15] & 15)); // Breath Control
                                           // Sensitivity ......(0-15)
        bank.sysex[getPatchStart(patchNum) + 8] = (byte) (0); // not
                                                              // used?
        bank.sysex[getPatchStart(patchNum) + 9] = (byte) (0); // not
                                                              // used?
        bank.sysex[getPatchStart(patchNum) + 10] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 11] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 12] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 13] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 14] =
                (byte) ((p.sysex[6 + 26] & 7)); // Attenuation
                                                // .....................(0-
                                                // 7)
        bank.sysex[getPatchStart(patchNum) + 15] =
                (byte) ((((p.sysex[6 + 4] & 15) >> 3) * 64) // Pitch
                                                            // Bend
                                                            // Step
                                                            // (High)
                                                            // ..........(0-12)
                + (p.sysex[6 + 29] & 63)); // Performance Key Shift
                                           // ...........(0-48)
        // ***** Voice B *****
        bank.sysex[getPatchStart(patchNum) + 16] =
                (byte) (((p.sysex[6 + 32] & 1) * 64) // Poly/Mono
                                                     // .......................(0-
                                                     // 1)
                + (patchNum & 63)); // UNDOCUMENTED! matching voice# ...(0-63)
        bank.sysex[getPatchStart(patchNum) + 17] =
                (byte) (((p.sysex[6 + 34] & 7) * 16) // Pitch Bend
                                                     // Step
                                                     // (Low)............(0-12)
                + (p.sysex[6 + 33] & 15)); // Pitch Bend Range
                                           // ................(0-12)
        bank.sysex[getPatchStart(patchNum) + 18] =
                (byte) ((p.sysex[6 + 35] & 127)); // Portamento Time
                                                  // .................(0-99)
        bank.sysex[getPatchStart(patchNum) + 19] =
                (byte) (((p.sysex[6 + 31] & 15) * 8) // Source Select
                                                     // ...................(0-15)
                        + ((p.sysex[6 + 38] & 1) * 4) // Portamento
                                                      // Pedal and
                                                      // Knob Assign
                                                      // (0- 1)
                        + ((p.sysex[6 + 37] & 1) * 2) // Portamento
                                                      // Mode
                                                      // .................(0-
                                                      // 1)
                + (p.sysex[6 + 36] & 1)); // Portamento/Glissando
                                          // ............(0- 1)
        bank.sysex[getPatchStart(patchNum) + 20] =
                (byte) (((p.sysex[6 + 40] & 7) * 16) // Modulation
                                                     // Wheel Assign
                                                     // .........(0-
                                                     // 7)
                + (p.sysex[6 + 39] & 15)); // Modulation Wheel
                                           // Sensitivity ....(0-15)
        bank.sysex[getPatchStart(patchNum) + 21] =
                (byte) (((p.sysex[6 + 42] & 7) * 16) // Foot Control
                                                     // Assign
                                                     // .............(0-
                                                     // 7)
                + (p.sysex[6 + 41] & 15)); // Foot Control Sensitivity
                                           // ........(0-15)
        bank.sysex[getPatchStart(patchNum) + 22] =
                (byte) (((p.sysex[6 + 44] & 7) * 16) // Aftertouch
                                                     // Assign
                                                     // ...............(0-
                                                     // 7)
                + (p.sysex[6 + 43] & 15)); // Aftertouch Sensitivity
                                           // ..........(0-15)
        bank.sysex[getPatchStart(patchNum) + 23] =
                (byte) (((p.sysex[6 + 46] & 7) * 16) // Breath Control
                                                     // Assign
                                                     // ...........(0-
                                                     // 7)
                + (p.sysex[6 + 45] & 15)); // Breath Control
                                           // Sensitivity ......(0-15)
        bank.sysex[getPatchStart(patchNum) + 24] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 25] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 26] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 27] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 28] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 29] = (byte) (0); // not
                                                               // used?
        bank.sysex[getPatchStart(patchNum) + 30] =
                (byte) ((p.sysex[6 + 56] & 7)); // Attenuation
                                                // .....................(0-
                                                // 7)
        bank.sysex[getPatchStart(patchNum) + 31] =
                (byte) ((((p.sysex[6 + 34] & 15) >> 3) * 64) // Pitch
                                                             // Bend
                                                             // Step
                                                             // (High)
                                                             // ..........(0-12)
                + (p.sysex[6 + 59] & 63)); // Performance Key Shift
                                           // ...........(0-48)

        // ***** Common *****
        bank.sysex[getPatchStart(patchNum) + 32] =
                (byte) (((p.sysex[6 + 62] & 15) * 8) // Dual Mode
                                                     // Detune
                                                     // ................(0-15)
                        + ((p.sysex[6 + 61] & 1) * 4) // Voice Memory
                                                      // Select
                                                      // .............(0-
                                                      // 1)
                + (p.sysex[6 + 60] & 3)); // Key Assign Mode
                                          // .................(0- 2)
        bank.sysex[getPatchStart(patchNum) + 33] = (p.sysex[6 + 63]); // Split
                                                                      // Point
                                                                      // .....................(0-99)
        bank.sysex[getPatchStart(patchNum) + 34] = (p.sysex[6 + 64]); // Performance
                                                                      // name 1
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 35] = (p.sysex[6 + 65]); // Performance
                                                                      // name 2
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 36] = (p.sysex[6 + 66]); // Performance
                                                                      // name 3
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 37] = (p.sysex[6 + 67]); // Performance
                                                                      // name 4
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 38] = (p.sysex[6 + 68]); // Performance
                                                                      // name 5
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 39] = (p.sysex[6 + 69]); // Performance
                                                                      // name 6
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 40] = (p.sysex[6 + 70]); // Performance
                                                                      // name 7
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 41] = (p.sysex[6 + 71]); // Performance
                                                                      // name 8
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 42] = (p.sysex[6 + 72]); // Performance
                                                                      // name 9
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 43] = (p.sysex[6 + 73]); // Performance
                                                                      // name 10
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 44] = (p.sysex[6 + 74]); // Performance
                                                                      // name 11
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 45] = (p.sysex[6 + 75]); // Performance
                                                                      // name 12
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 46] = (p.sysex[6 + 76]); // Performance
                                                                      // name 13
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 47] = (p.sysex[6 + 77]); // Performance
                                                                      // name 14
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 48] = (p.sysex[6 + 78]); // Performance
                                                                      // name 15
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 49] = (p.sysex[6 + 79]); // Performance
                                                                      // name 16
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 50] = (p.sysex[6 + 80]); // Performance
                                                                      // name 17
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 51] = (p.sysex[6 + 81]); // Performance
                                                                      // name 18
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 52] = (p.sysex[6 + 82]); // Performance
                                                                      // name 19
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 53] = (p.sysex[6 + 83]); // Performance
                                                                      // name 20
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 54] = (p.sysex[6 + 84]); // Performance
                                                                      // name 21
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 55] = (p.sysex[6 + 85]); // Performance
                                                                      // name 22
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 56] = (p.sysex[6 + 86]); // Performance
                                                                      // name 23
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 57] = (p.sysex[6 + 87]); // Performance
                                                                      // name 24
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 58] = (p.sysex[6 + 88]); // Performance
                                                                      // name 25
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 59] = (p.sysex[6 + 89]); // Performance
                                                                      // name 26
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 60] = (p.sysex[6 + 90]); // Performance
                                                                      // name 27
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 61] = (p.sysex[6 + 91]); // Performance
                                                                      // name 28
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 62] = (p.sysex[6 + 92]); // Performance
                                                                      // name 29
                                                                      // ..............ASCII
        bank.sysex[getPatchStart(patchNum) + 63] = (p.sysex[6 + 93]); // Performance
                                                                      // name 30
                                                                      // ..............ASCII

        calculateChecksum(bank);
    }

    /*
     * Gets a patch from the bank, converting it as needed
     */
    @Override
    public Patch getPatch(Patch bank, int patchNum) {
        try {
            byte[] sysex = new byte[singleSize];

            // transform bulk-dump-packed-format to voice data
            sysex[0] = (byte) 0xF0;
            sysex[1] = (byte) 0x43;
            sysex[2] = (byte) 0x00;
            sysex[3] = (byte) 0x01;
            sysex[4] = (byte) 0x00;
            sysex[5] = (byte) 0x5E;

            // ***** Voice A *****
            // sysex[6+ 0]=(byte)((bank.sysex[getPatchStart(patchNum)+ 0]& 63));
            // // UNDOCUMENTED! matching voice# ...(0-63)
            sysex[6 + 0] = (byte) (0); // UNDOCUMENTED! matching voice#
                                       // ...(0-63) - fixed to 0!
            // Source Select ...................(0-15)
            sysex[6 + 1] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 3] & 120) / 8);
            // Poly/Mono .......................(0-1)
            sysex[6 + 2] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 0] & 64) / 64);
            // Pitch Bend Range ................(0-12)
            sysex[6 + 3] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 1] & 15));
            // Pitch Bend Step (Low) ...........(0-12)
            // Pitch Bend Step (High) ..........(0-12)
            sysex[6 + 4] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 1] & 112)
                            / 16 + (bank.sysex[getPatchStart(patchNum) + 15] & 64) / 8);
            sysex[6 + 5] = ((bank.sysex[getPatchStart(patchNum) + 2])); // Portamento
                                                                        // Time
                                                                        // .................(0-99)
            sysex[6 + 6] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 3] & 1)); // Portamento/Glissando
                                                                            // ............(0-
                                                                            // 1)
            // Portamento Mode .................(0-1)
            sysex[6 + 7] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 3] & 2) / 2);
            sysex[6 + 8] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 3] & 4) / 4); // Portamento
                                                                                // Pedal
                                                                                // and
                                                                                // Knob
                                                                                // Assign
                                                                                // (0-
                                                                                // 1)
            sysex[6 + 9] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 4] & 15)); // Modulation
                                                                             // Wheel
                                                                             // Sensitivity
                                                                             // ....(0-15)
            sysex[6 + 10] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 4] & 112) / 16); // Modulation
                                                                                   // Wheel
                                                                                   // Assign
                                                                                   // .........(0-
                                                                                   // 7)
            sysex[6 + 11] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 5] & 15)); // Foot
                                                                             // Controller
                                                                             // Sensitivity
                                                                             // .....(0-15)
            sysex[6 + 12] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 5] & 112) / 16); // Foot
                                                                                   // Controller
                                                                                   // Assign
                                                                                   // ..........(0-
                                                                                   // 7)
            sysex[6 + 13] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6] & 15)); // After
                                                                             // Touch
                                                                             // Sensitivity
                                                                             // .........(0-15)
            // After Touch Assign ..............(0-7)
            sysex[6 + 14] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 6] & 112) / 16);
            sysex[6 + 15] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 7] & 15)); // Breath
                                                                             // Controller
                                                                             // Sensitivity
                                                                             // ...(0-15)
            sysex[6 + 16] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 7] & 112) / 16); // Breath
                                                                                   // Controller
                                                                                   // Assign
                                                                                   // ........(0-
                                                                                   // 7)
            sysex[6 + 17] = (byte) (0); // KIAT Sensitivity
                                        // ................(0-15)
            sysex[6 + 18] = (byte) (0); // KIAT - OP1 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 19] = (byte) (0); // KIAT - OP2 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 20] = (byte) (0); // KIAT - OP3 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 21] = (byte) (0); // KIAT - OP4 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 22] = (byte) (0); // KIAT - OP5 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 23] = (byte) (0); // KIAT - OP6 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 24] = (byte) (0); // KIAT - Decay Rate
                                        // ...............(0-99)
            sysex[6 + 25] = (byte) (0); // KIAT - Release Rate
                                        // .............(0-99)
            sysex[6 + 26] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 14] & 7)); // Audio
                                                                             // Output
                                                                             // Level
                                                                             // Attenuator
                                                                             // ...(0-
                                                                             // 7)
            sysex[6 + 27] = (byte) (0); // Program Output ..................(0-
                                        // 1)
            sysex[6 + 28] = (byte) (0); // Sustain Pedal ...................(0-
                                        // 1)
            sysex[6 + 29] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 15] & 63)); // Performance
                                                                              // Key
                                                                              // Shift
                                                                              // ...........(0-48)

            // ***** Voice B *****
            // sysex[6+30]=(byte)((bank.sysex[getPatchStart(patchNum)+16]& 63));
            // // UNDOCUMENTED! matching voice# ...(0-63)
            sysex[6 + 30] = (byte) (0); // UNDOCUMENTED! matching voice#
                                        // ...(0-63) - fixed to 0!
            // Source Select ...................(0-15)
            sysex[6 + 31] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 19] & 120) / 8);
            // Poly/Mono .......................(0-1)
            sysex[6 + 32] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 16] & 64) / 64);
            // Pitch Bend Range ................(0-12)
            sysex[6 + 33] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 17] & 15));
            // Pitch Bend Step (Low) ...........(0-12)
         // Pitch Bend Step (High) ..........(0-12)
            sysex[6 + 34] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 17] & 112) / 16 + (bank.sysex[getPatchStart(patchNum) + 31] & 64) / 8);
            sysex[6 + 35] = ((bank.sysex[getPatchStart(patchNum) + 18])); // Portamento
                                                                          // Time
                                                                          // .................(0-99)
            sysex[6 + 36] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 19] & 1)); // Portamento/Glissando
                                                                             // ............(0-
                                                                             // 1)
            // Portamento Mode .................(0-1)
            sysex[6 + 37] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 19] & 2) / 2);
            sysex[6 + 38] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 19] & 4) / 4); // Portamento
                                                                                 // Pedal
                                                                                 // and
                                                                                 // Knob
                                                                                 // Assign
                                                                                 // (0-
                                                                                 // 1)
            sysex[6 + 39] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 20] & 15)); // Modulation
                                                                              // Wheel
                                                                              // Sensitivity
                                                                              // ....(0-15)
            sysex[6 + 40] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 20] & 112) / 16); // Modulation
                                                                                    // Wheel
                                                                                    // Assign
                                                                                    // .........(0-
                                                                                    // 7)
            sysex[6 + 41] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 21] & 15)); // Foot
                                                                              // Controller
                                                                              // Sensitivity
                                                                              // .....(0-15)
            sysex[6 + 42] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 21] & 112) / 16); // Foot
                                                                                    // Controller
                                                                                    // Assign
                                                                                    // ..........(0-
                                                                                    // 7)
            sysex[6 + 43] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 22] & 15)); // After
                                                                              // Touch
                                                                              // Sensitivity
                                                                              // .........(0-15)
            // After Touch Assign ..............(0-7)
            sysex[6 + 44] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 22] & 112) / 16);
            sysex[6 + 45] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 23] & 15)); // Breath
                                                                              // Controller
                                                                              // Sensitivity
                                                                              // ...(0-15)
            sysex[6 + 46] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 23] & 112) / 16); // Breath
                                                                                    // Controller
                                                                                    // Assign
                                                                                    // ........(0-
                                                                                    // 7)
            sysex[6 + 47] = (byte) (0); // KIAT Sensitivity
                                        // ................(0-15)
            sysex[6 + 48] = (byte) (0); // KIAT - OP1 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 49] = (byte) (0); // KIAT - OP2 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 50] = (byte) (0); // KIAT - OP3 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 51] = (byte) (0); // KIAT - OP4 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 52] = (byte) (0); // KIAT - OP5 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 53] = (byte) (0); // KIAT - OP6 Sensitivity
                                        // ..........(0-15)
            sysex[6 + 54] = (byte) (0); // KIAT - Decay Rate
                                        // ...............(0-99)
            sysex[6 + 55] = (byte) (0); // KIAT - Release Rate
                                        // .............(0-99)
            sysex[6 + 56] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 30] & 7)); // Audio
                                                                             // Output
                                                                             // Level
                                                                             // Attenuator
                                                                             // ...(0-
                                                                             // 7)
            sysex[6 + 57] = (byte) (0); // Program Output ..................(0-
                                        // 1)
            sysex[6 + 58] = (byte) (0); // Sustain Pedal ...................(0-
                                        // 1)
            sysex[6 + 59] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 31] & 63)); // Performance
                                                                              // Key
                                                                              // Shift
                                                                              // ...........(0-48)

            // ***** Common *****
            sysex[6 + 60] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 32] & 3)); // Key
                                                                             // Assign
                                                                             // Mode
                                                                             // .................(0-
                                                                             // 2)
            sysex[6 + 61] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 32] & 4) / 4); // Voice
                                                                                 // Memory
                                                                                 // Select
                                                                                 // Flag
                                                                                 // ........(0-
                                                                                 // 1)
            sysex[6 + 62] =
                    (byte) ((bank.sysex[getPatchStart(patchNum) + 32] & 120) / 8); // Dual
                                                                                   // Mode
                                                                                   // Detune
                                                                                   // ................(0-15)
            sysex[6 + 63] = ((bank.sysex[getPatchStart(patchNum) + 33])); // Split
                                                                          // Point
                                                                          // .....................(0-99)
            sysex[6 + 64] = ((bank.sysex[getPatchStart(patchNum) + 34])); // Performance
                                                                          // name
                                                                          // 1
                                                                          // ..............ASCII
            sysex[6 + 65] = ((bank.sysex[getPatchStart(patchNum) + 35])); // Performance
                                                                          // name
                                                                          // 2
                                                                          // ..............ASCII
            sysex[6 + 66] = ((bank.sysex[getPatchStart(patchNum) + 36])); // Performance
                                                                          // name
                                                                          // 3
                                                                          // ..............ASCII
            sysex[6 + 67] = ((bank.sysex[getPatchStart(patchNum) + 37])); // Performance
                                                                          // name
                                                                          // 4
                                                                          // ..............ASCII
            sysex[6 + 68] = ((bank.sysex[getPatchStart(patchNum) + 38])); // Performance
                                                                          // name
                                                                          // 5
                                                                          // ..............ASCII
            sysex[6 + 69] = ((bank.sysex[getPatchStart(patchNum) + 39])); // Performance
                                                                          // name
                                                                          // 6
                                                                          // ..............ASCII
            sysex[6 + 70] = ((bank.sysex[getPatchStart(patchNum) + 40])); // Performance
                                                                          // name
                                                                          // 7
                                                                          // ..............ASCII
            sysex[6 + 71] = ((bank.sysex[getPatchStart(patchNum) + 41])); // Performance
                                                                          // name
                                                                          // 8
                                                                          // ..............ASCII
            sysex[6 + 72] = ((bank.sysex[getPatchStart(patchNum) + 42])); // Performance
                                                                          // name
                                                                          // 9
                                                                          // ..............ASCII
            sysex[6 + 73] = ((bank.sysex[getPatchStart(patchNum) + 43])); // Performance
                                                                          // name
                                                                          // 10
                                                                          // ..............ASCII
            sysex[6 + 74] = ((bank.sysex[getPatchStart(patchNum) + 44])); // Performance
                                                                          // name
                                                                          // 11
                                                                          // ..............ASCII
            sysex[6 + 75] = ((bank.sysex[getPatchStart(patchNum) + 45])); // Performance
                                                                          // name
                                                                          // 12
                                                                          // ..............ASCII
            sysex[6 + 76] = ((bank.sysex[getPatchStart(patchNum) + 46])); // Performance
                                                                          // name
                                                                          // 13
                                                                          // ..............ASCII
            sysex[6 + 77] = ((bank.sysex[getPatchStart(patchNum) + 47])); // Performance
                                                                          // name
                                                                          // 14
                                                                          // ..............ASCII
            sysex[6 + 78] = ((bank.sysex[getPatchStart(patchNum) + 48])); // Performance
                                                                          // name
                                                                          // 15
                                                                          // ..............ASCII
            sysex[6 + 79] = ((bank.sysex[getPatchStart(patchNum) + 49])); // Performance
                                                                          // name
                                                                          // 16
                                                                          // ..............ASCII
            sysex[6 + 80] = ((bank.sysex[getPatchStart(patchNum) + 50])); // Performance
                                                                          // name
                                                                          // 17
                                                                          // ..............ASCII
            sysex[6 + 81] = ((bank.sysex[getPatchStart(patchNum) + 51])); // Performance
                                                                          // name
                                                                          // 18
                                                                          // ..............ASCII
            sysex[6 + 82] = ((bank.sysex[getPatchStart(patchNum) + 52])); // Performance
                                                                          // name
                                                                          // 19
                                                                          // ..............ASCII
            sysex[6 + 83] = ((bank.sysex[getPatchStart(patchNum) + 53])); // Performance
                                                                          // name
                                                                          // 20
                                                                          // ..............ASCII
            sysex[6 + 84] = ((bank.sysex[getPatchStart(patchNum) + 54])); // Performance
                                                                          // name
                                                                          // 21
                                                                          // ..............ASCII
            sysex[6 + 85] = ((bank.sysex[getPatchStart(patchNum) + 55])); // Performance
                                                                          // name
                                                                          // 22
                                                                          // ..............ASCII
            sysex[6 + 86] = ((bank.sysex[getPatchStart(patchNum) + 56])); // Performance
                                                                          // name
                                                                          // 23
                                                                          // ..............ASCII
            sysex[6 + 87] = ((bank.sysex[getPatchStart(patchNum) + 57])); // Performance
                                                                          // name
                                                                          // 24
                                                                          // ..............ASCII
            sysex[6 + 88] = ((bank.sysex[getPatchStart(patchNum) + 58])); // Performance
                                                                          // name
                                                                          // 25
                                                                          // ..............ASCII
            sysex[6 + 89] = ((bank.sysex[getPatchStart(patchNum) + 59])); // Performance
                                                                          // name
                                                                          // 26
                                                                          // ..............ASCII
            sysex[6 + 90] = ((bank.sysex[getPatchStart(patchNum) + 60])); // Performance
                                                                          // name
                                                                          // 27
                                                                          // ..............ASCII
            sysex[6 + 91] = ((bank.sysex[getPatchStart(patchNum) + 61])); // Performance
                                                                          // name
                                                                          // 28
                                                                          // ..............ASCII
            sysex[6 + 92] = ((bank.sysex[getPatchStart(patchNum) + 62])); // Performance
                                                                          // name
                                                                          // 29
                                                                          // ..............ASCII
            sysex[6 + 93] = ((bank.sysex[getPatchStart(patchNum) + 63])); // Performance
                                                                          // name
                                                                          // 30
                                                                          // ..............ASCII

            sysex[singleSize - 1] = (byte) 0xF7;

            Patch p = getPatchFactory().createNewPatch(sysex, getDevice()); // single
                                                                            // sysex
            p.calculateChecksum();

            return p;
        } catch (Exception e) {
            ErrorMsg.reportError(getManufacturerName() + " " + getModelName(),
                    "Error in " + toString(), e);
            return null;
        }
    }

    /*
     * create a bank with 64 " YAMAHA TX7 FUNCTION DATA  " patches
     */
    @Override
    public BankPatch createNewPatch() {
        return super.createNewPatch();
    }

    public int getDxSysexHeaderSize() {
        return dxSysexHeaderSize;
    }

    public void setDxSysexHeaderSize(int dxSysexHeaderSize) {
        this.dxSysexHeaderSize = dxSysexHeaderSize;
    }

    public int getDxPatchNameOffset() {
        return dxPatchNameOffset;
    }

    public void setDxPatchNameOffset(int dxPatchNameOffset) {
        this.dxPatchNameOffset = dxPatchNameOffset;
    }

    public int getDxSinglePackedSize() {
        return dxSinglePackedSize;
    }

    public void setDxSinglePackedSize(int dxSinglePackedSize) {
        this.dxSinglePackedSize = dxSinglePackedSize;
    }

    public boolean isSpbp() {
        return spbp;
    }

    public void setSpbp(boolean spbp) {
        this.spbp = spbp;
    }

    public boolean isSwOffMemProt() {
        return swOffMemProt;
    }

    public void setSwOffMemProt(boolean swOffMemProt) {
        this.swOffMemProt = swOffMemProt;
    }

    public boolean isTipsMsg() {
        return tipsMsg;
    }

    public void setTipsMsg(boolean tipsMsg) {
        this.tipsMsg = tipsMsg;
    }

}
