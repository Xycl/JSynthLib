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

package org.jsynthlib.midi.service;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

/**
 * Class to format MIDI messages into Strings.
 * @author Pascal Collberg
 */
public final class MidiMessageFormatter {

    private static final String SYSEX_MESSAGE = "SysEX:length=%d\n%s";

    private MidiMessageFormatter() {
    }

    // ----- Start Joe Emenaker
    /**
     * convert a byte array into a hexa-dump string, with or without spaces
     * between the bytes.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of bytes per line. If equal or less than 0, no newlines
     *            are inserted.
     * @param wantspaces
     *            whether or not to insert spaces between bytes
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes,
            boolean wantspaces) {
        StringBuilder buf = new StringBuilder();
        if (len == -1 || offset + len > d.length) {
            len = d.length - offset;
        }
        for (int i = 0; i < len; i++) {
            int c = (d[offset + i] & 0xff);
            if (c < 0x10) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(c));
            if (bytes > 0 && (i % bytes == bytes - 1 && i != len - 1)) {
                buf.append("\n");
            } else if (i != len - 1 && wantspaces) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }

    public static String hexDump(byte[] d, int offset, int len, int bytes,
            int addressLength, boolean characters) {
        String output = "";
        if (offset > d.length) {
            return ("offset out of bounds of byte array");
        }
        if (offset + len > d.length) {
            len = d.length - offset;
        }

        if (addressLength > 0) {
            String addressstring = Integer.toHexString(offset);
            while (addressstring.length() < addressLength) {
                addressstring = "0" + addressstring;
            }
            output += addressstring + " - ";
        }
        String hexdigits = hexDump(d, offset, len, bytes, true);
        // If hexdigits is too short, pad with spaces so that the last line
        // lines up with the others.
        if (characters) {
            while (hexdigits.length() < 3 * bytes - 1) {
                hexdigits += "   ";
            }
        }
        output += hexdigits;
        if (characters) {
            output += " - ";
            for (int i = offset; i < offset + len; i++) {
                if (d[i] >= 32 && d[i] <= 126) {
                    output += new Character((char) d[i]).toString();
                } else {
                    output += ".";
                }
            }
        }
        return (output);
    }

    /**
     * convert a byte array into a string of hex values, optionally including
     * their character equivalents and/or the address/offset of the beginning of
     * the line. With character equivalents, unprintable characters are replaced
     * by spaces. For example: 65 78 61 6D 70 6C 65 01 EF 31 38 F3 F1 -
     * EXAMPLE..18.. If <code>addresses</code> is true, then the hex
     * representation of the offset will be put at the beginning of each line.
     * For example: 013A0 - 65 78 61 6D 70 6C 65 01 - EXAMPLE. 013A8 - EF 31 38
     * F3 F1 65 64 63 - .18..EDC 013B0 - 38 34 36 37 38 31 37 37 - 84678177 If
     * <code>len</code> is positive non-zero, only <code>len</code> bytes will
     * be represented on each line.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of columns (bytes) to put in each line. If less than or
     *            equal to zero, no line-breaks are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes,
            boolean addresses, boolean characters) {
        // Is offset beyond the end of array?
        if (offset >= d.length) {
            return ("");
        }
        // Is offset+len beyond the end of the array?
        if (offset + len >= d.length || len < 0) {
            len = d.length - offset; // Set len to get remaining bytes
        }
        // If bytes <=0, set it to len so that we get the remaining bytes on one
        // line
        if (bytes <= 0) {
            bytes = len;
        }

        // How many digits to use for address?
        int addresslen = 0;
        // Keep adding 2 digits until we can repesent the highest number we need
        if (addresses) {
            int maxnumber = 1;
            while (maxnumber < offset + len) {
                addresslen += 2;
                maxnumber *= 256;
            }
        }

        String output = "";
        if (len <= bytes) {
            output = hexDump(d, offset, len, bytes, addresslen, characters);
        } else {
            // The bytes won't fit. Split them up and call hexDump for each one
            for (int i = 0; i < len; i += bytes) {
                output =
                        output
                                + hexDump(d, offset + i, bytes, bytes,
                                        addresslen, characters) + "\n";
            }
        }
        return (output);
    }

    // ----- End Joe Emenaker

    // ----- Start Hiroo Hayashi
    //
    // convert MidiMessage or byte array into String
    //
    /**
     * convert a byte array into a hexa-dump string.
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of bytes per line. If equal or less than 0, no newlines
     *            are inserted.
     * @return hexa-dump string.
     */
    public static String hexDump(byte[] d, int offset, int len, int bytes) {
        return hexDump(d, offset, len, bytes, true);
    }

    /**
     * convert a byte array into a one-line hexa-dump string. If it's longer
     * than <code>len</code>, it will have the inner portion removed and
     * replaced with dots, for example: "00 01 03 04 05 .. 7d 7e 7f"
     * @param d
     *            a <code>byte[]</code> array to be converted.
     * @param offset
     *            array index from which dump starts.
     * @param len
     *            number of bytes to be dumped. If -1, dumps to the end of the
     *            array.
     * @param bytes
     *            number of columns (bytes) to put in the one-line string.
     * @return hexa-dump string.
     */
    public static String hexDumpOneLine(byte[] d, int offset, int len, int bytes) {
        if (len == -1 || len > d.length - offset) {
            len = d.length - offset;
        }

        if (len <= bytes || len < 8) {
            return hexDump(d, offset, len, 0);
        }

        return (hexDump(d, offset, bytes - 4, 0) + ".." + hexDump(d, offset
                + len - 3, 3, 0));
    }

    /**
     * Convert <code>SysexMessage</code> into a hexa-dump string.
     * @param m
     *            a <code>SysexMessage</code> value
     * @param bytes
     *            number of bytes per line. If equal or less than 0, no newlines
     *            are inserted.
     * @return a <code>String</code> value
     */
    public static String sysexMessageToString(SysexMessage m, int bytes) {
        byte[] d = m.getMessage();
        return hexDump(d, 0, -1, bytes);
    }

    /**
     * Convert <code>SysexMessage</code> into a hexa-dump string. If the length
     * is longer than 16bytes, bytes of middle of the array are not ignored.
     * @param m
     *            a <code>SysexMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException
     *                if an error occurs
     */
    public static String sysexMessageToString(SysexMessage m)
            throws InvalidMidiDataException {
        byte[] d = m.getMessage();
        return hexDumpOneLine(d, 0, -1, 16);
    }

    static String hex(int c) {
        String s = Integer.toHexString(c);
        return s.length() == 1 ? "0" + s : s;
    }

    /**
     * Convert <code>ShortMessage</code> into a hexa-dump string.
     * @param m
     *            a <code>ShortMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException
     *                if an error occurs
     */
    public static String shortMessageToString(ShortMessage m)
            throws InvalidMidiDataException {
        int c = m.getStatus();
        if (c < 0xf0) {
            c = m.getStatus() & 0xf0;
        }
        switch (c) {
        case 0x80:
        case 0x90:
        case 0xa0:
        case 0xb0:
        case 0xe0:
        case 0xf2:
            return (hex(c) + " " + hex(m.getData1()) + " " + hex(m.getData2()));
        case 0xc0:
        case 0xd0:
        case 0xf1:
        case 0xf3:
            return (hex(c) + " " + hex(m.getData1()));
        case 0xf4:
        case 0xf5:
        case 0xf6:
        case 0xf7:
        case 0xf8:
        case 0xf9:
        case 0xfa:
        case 0xfb:
        case 0xfc:
        case 0xfd:
        case 0xfe:
        case 0xff:
            return (hex(c));
        default:
            throw new InvalidMidiDataException();
        }
    }

    /**
     * Return a <code>String</code> of the name of status byte of a
     * <code>MidiMessage</code>.
     * @param m
     *            a <code>MidiMessage</code> value
     * @return a <code>String</code> value
     * @exception InvalidMidiDataException
     *                if an error occurs
     */
    public static String statusString(MidiMessage m)
            throws InvalidMidiDataException {
        int c = m.getStatus();
        if (c < 0xf0) {
            c = m.getStatus() & 0xf0;
        }
        switch (c) {
        case 0x80:
            return "Note Off";
        case 0x90:
            return "Note On";
        case 0xa0:
            return "Poly Pressure";
        case 0xb0:
            return "Control Change";
        case 0xc0:
            return "Program Change";
        case 0xd0:
            return "Channel Pressure";
        case 0xe0:
            return "Pitch Bend";
        case 0xf0:
            return "System Exclusive";
        case 0xf1:
            return "MIDI Time Code";
        case 0xf2:
            return "Song Position Pointer";
        case 0xf3:
            return "Song Select";
        case 0xf4:
            return "Undefined";
        case 0xf5:
            return "Undefined";
        case 0xf6:
            return "Tune Request";
            // case 0xf7: return "End of System Exclusive";
        case 0xf7:
            return "Special System Exclusive";
        case 0xf8:
            return "Timing Clock";
        case 0xf9:
            return "Undefined";
        case 0xfa:
            return "Start";
        case 0xfb:
            return "Continue";
        case 0xfc:
            return "Stop";
        case 0xfd:
            return "Undefined";
        case 0xfe:
            return "Active Sensing";
        case 0xff:
            return "System Reset";
        default:
            throw new InvalidMidiDataException();
        }
    }

    /**
     * convert <code>MidiMessage</code> into a string.
     * @param m
     *            a <code>MidiMessage</code> value
     * @return a <code>String</code> value
     */
    public static String midiMessageToString(MidiMessage m,
            boolean completeMessage) throws InvalidMidiDataException {
        if (m instanceof ShortMessage) {
            return (statusString(m) + "\n  " + shortMessageToString((ShortMessage) m));
        } else if (m instanceof SysexMessage) {
            String messageString = null;
            if (completeMessage) {
                messageString = sysexMessageToString((SysexMessage) m, 16);
            } else {
                messageString = sysexMessageToString((SysexMessage) m);
            }
            return String.format(SYSEX_MESSAGE, m.getLength(), messageString);
        } else {
            throw new InvalidMidiDataException();
        }
    }

}
