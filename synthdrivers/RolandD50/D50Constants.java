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

public final class D50Constants {

    public static final int PART_WG_PITCH_COARSE       = 0;
    public static final int PART_WG_PITCH_FINE         = 1;
    public static final int PART_WG_PITCH_KEYFOLLOW    = 2;
    public static final int PART_WG_LFO_MODE           = 3;
    public static final int PART_WG_P_ENV_MODE         = 4;
    public static final int PART_WG_BENDER_MODE        = 5;
    public static final int PART_WG_WAVEFORM           = 6;
    public static final int PART_WG_PCM_WAVE_NO        = 7;
    public static final int PART_WG_PULSE_WIDTH        = 8;
    public static final int PART_WG_PW_VELO_RANGE      = 9;
    public static final int PART_WG_PW_LFO_SELECT      = 10;
    public static final int PART_WG_PW_LFO_DEPTH       = 11;
    public static final int PART_WG_PW_AT_RANGE        = 12;

    public static final int PART_TVF_CUTOFF_FREQ       = 13;
    public static final int PART_TVF_RESONANCE         = 14;
    public static final int PART_TVF_KEYFOLLOW         = 15;
    public static final int PART_TVF_BIAS_POINT_DIR    = 16;
    public static final int PART_TVF_BIAS_LEVEL        = 17;
    public static final int PART_TVF_ENV_DEPTH         = 18;
    public static final int PART_TVF_ENV_VELO_RANGE     = 19;
    public static final int PART_TVF_ENV_DEPTH_KEYF    = 20;
    public static final int PART_TVF_ENV_TIME_KEYF     = 21;
    public static final int PART_TVF_ENV_TIME_1        = 22;
    public static final int PART_TVF_ENV_TIME_2        = 23;
    public static final int PART_TVF_ENV_TIME_3        = 24;
    public static final int PART_TVF_ENV_TIME_4        = 25;
    public static final int PART_TVF_ENV_TIME_5        = 26;
    public static final int PART_TVF_ENV_LEVEL_1       = 27;
    public static final int PART_TVF_ENV_LEVEL_2       = 28;
    public static final int PART_TVF_ENV_LEVEL_3       = 29;
    public static final int PART_TVF_ENV_SUSTAIN_LEVEL = 30;
    public static final int PART_TVF_ENV_END_LEVEL     = 31;
    public static final int PART_TVF_LFO_SELECT        = 32;
    public static final int PART_TVF_LFO_DEPTH         = 33;
    public static final int PART_TVF_AT_RANGE          = 34;
    
    public static final int PART_TVA_LEVEL = 35;
    public static final int PART_TVA_VELO_RANGE = 36;
    public static final int PART_TVA_BIAS_POINT = 37;
    public static final int PART_TVA_BIAS_LEVEL = 38;
    public static final int PART_TVA_ENV_TIME_1 = 39;
    public static final int PART_TVA_ENV_TIME_2 = 40;
    public static final int PART_TVA_ENV_TIME_3 = 41;
    public static final int PART_TVA_ENV_TIME_4 = 42;
    public static final int PART_TVA_ENV_TIME_5 = 43;
    public static final int PART_TVA_ENV_LEVEL_1 = 44;
    public static final int PART_TVA_ENV_LEVEL_2 = 45;
    public static final int PART_TVA_ENV_LEVEL_3 = 46;
    public static final int PART_TVA_ENV_SUSTAIN_LEVEL = 47;
    public static final int PART_TVA_ENV_END_LEVEL = 48;
    public static final int PART_TVA_ENV_VELOCITY_KEYF = 49;
    public static final int PART_TVA_ENV_TIME_KEYF = 50;
    public static final int PART_TVA_LFO_SELECT = 51;
    public static final int PART_TVA_LFO_DEPTH = 52;
    public static final int PART_TVA_AT_RANGE = 53;
    
  
    public static final int COMMON_STRUCTURE = 10;
    public static final int COMMON_ENV_VELO_RANGE = 11;
    public static final int COMMON_ENV_TIME_KEYF = 12;
    public static final int COMMON_ENV_TIME_1 = 13;
    public static final int COMMON_ENV_TIME_2 = 14;
    public static final int COMMON_ENV_TIME_3 = 15;
    public static final int COMMON_ENV_TIME_4 = 16;
    public static final int COMMON_ENV_LEVEL_0 = 17;
    public static final int COMMON_ENV_LEVEL_1 = 18;
    public static final int COMMON_ENV_LEVEL_2 = 19;
    public static final int COMMON_ENV_SUSTAIN_LEVEL = 20;
    public static final int COMMON_ENV_END_LEVEL = 21;
    public static final int COMMON_LFO_SELECT = 22;
    public static final int COMMON_LFO_DEPTH = 23;
    public static final int COMMON_AT_RANGE = 24;
    
    public static final int COMMON_LFO_WAVEFORM = 25;
    public static final int COMMON_LFO_RATE = 26;
    public static final int COMMON_LFO_DELAY_TIME = 27;
    public static final int COMMON_LFO_SYNC = 28;
    
    public static final int COMMON_LOW_EQ_FREQ = 37;
    public static final int COMMON_LOW_EQ_GAIN = 38;
    public static final int COMMON_HIGH_EQ_FREQ = 39;
    public static final int COMMON_HIGH_EQ_Q = 40;
    public static final int COMMON_HIGH_EQ_GAIN = 41;
    
    public static final int COMMON_CHORUS_TYPE = 42;
    public static final int COMMON_CHORUS_RATE = 43;
    public static final int COMMON_CHORUS_DEPTH = 44;
    public static final int COMMON_CHORUS_BALANCE = 45;
    
    public static final int COMMON_PART_MUTE = 46;
    public static final int COMMON_PART_BALANCE = 47;
    
    
    public static final int PATCH_KEY_MODE = 18;
    public static final int PATCH_SPLIT_POINT = 19;
    public static final int PATCH_PORTA_MODE = 20;
    public static final int PATCH_HOLD_MODE = 21;
    public static final int PATCH_UTONE_KEYSHIFT = 22;
    public static final int PATCH_LTONE_KEYSHIFT = 23;
    public static final int PATCH_UTONE_FINE = 24;
    public static final int PATCH_LTONE_FINE = 25;
    public static final int PATCH_BENDER_RANGE = 26;
    public static final int PATCH_AT_BEND_RANGE = 27;
    public static final int PATCH_PORTA_TIME = 28;
    public static final int PATCH_OUTPUT_MODE = 29;
    public static final int PATCH_REVERB_TYPE = 30;
    public static final int PATCH_REVERB_BALANCE = 31;
    public static final int PATCH_TOTAL_VOLUME = 32;
    public static final int PATCH_TONE_BALANCE = 33;
    public static final int PATCH_CHASE_MODE = 34;
    public static final int PATCH_CHASE_LEVEL = 35;
    public static final int PATCH_CHASE_TIME = 36;

    public static final int    PARTIAL_SIZE         = 64;
    public static final int    PARTIALS_PER_PATCH   = 7;
    public static final int    PARTIALS_PER_MESSAGE = 4;
    public static final int    PARTIALS_SIZE        = PARTIAL_SIZE * PARTIALS_PER_PATCH;

    // F0 + Roland header + Address
    // TODO: flexible address for each patch
    public static final byte[] SYSEX_HEADER         = new byte[]{
            (byte)0xF0, 0x41, 0x00, 0x14, 0x12, 0x02, 0x00, 0x00
                                                    };
    public static final int SYSEX_HEADER_SIZE = SYSEX_HEADER.length;

    // Checksum + F7
    public static final byte[] SYSEX_FOOTER         = new byte[]{
            0x00, (byte)0xF7
                                                    };
    public static final int SYSEX_FOOTER_SIZE = SYSEX_FOOTER.length;
    public static final int    PATCH_SIZE           = SYSEX_HEADER_SIZE + PARTIAL_SIZE * PARTIALS_PER_PATCH
                                                      + SYSEX_FOOTER_SIZE;
    public static final int    SYSEX_MESSAGE_SIZE   = SYSEX_HEADER_SIZE + PARTIAL_SIZE * PARTIALS_PER_MESSAGE
                                                      + SYSEX_FOOTER_SIZE;
    

    public static final int              PARTIAL_NAME_SIZE = 10;

    public static final int PATCH_NAME_START    = SYSEX_HEADER_SIZE + PARTIAL_SIZE * 6;
    public static final int PATCH_NAME_SIZE     = 18;
    public static final int CHECKSUM_START      = 6;

    private D50Constants() {
    }


}
