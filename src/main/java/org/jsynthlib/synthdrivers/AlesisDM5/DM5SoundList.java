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

package org.jsynthlib.synthdrivers.AlesisDM5;

/**
 * Alesis DM5 sound list by bank
 * @author Jeff Weber
 */
public class DM5SoundList {
    static final String[][] DRUM_NAME = {
            // Bank 0--Kick
            {
                    "0--Arena     ", "1--Producer  ", "2--Pwr Rock  ",
                    "3--Fat Head  ", "4--Dark Fat  ", "5--Passion   ",
                    "6--Holo      ", "7--WarmKick  ", "8--SpeedMtl  ",
                    "9--Plastine  ", "10--Back Mic ", "11--FrontMic ",
                    "12--Lite     ", "13--RubbrBtr ", "14--Simple   ",
                    "15--Basic    ", "16--Slammin' ", "17--Foot     ",
                    "18--Bch Ball ", "19--LowSolid ", "20--Feels Gd ",
                    "21--Pillow   ", "22--Fusion   ", "23--Reggae   ",
                    "24--Kinetica ", "25--Brt Ambi ", "26--Hi Gate  ",
                    "27--Med Room ", "28--Lrg Room ", "29--Forum    ",
                    "30--Punchy   ", "31--InTheKik ", "32--Big One  ",
                    "33--Bonk     ", "34--RockClub ", "35--MyTribe  ",
                    "36--RoundAmb ", "37--RoundAtk ", "38--HardAttk ",
                    "39--Blitz    ", "40--9oh9Kik1 ", "41--9oh9Kik2 ",
                    "42--9oh9Kik3 ", "43--Native   ", "44--AnaKick  ",
                    "45--Mangler  ", "46--SuprRave ", "47--Spud     ",
                    "48--Rap Wave ", "49--Beat Box ", "50--WeR Borg ",
                    "51--Indscpln ", "52--SonarWav ", "53--60Cycles ",
                    "54--Motor    ", "55--Stages   ", "56--Cybrwave ",
                    "57--Cybo     ", "58--BrainEtr ", "59--Squish   ",
                    "60--Crunch   ", "61--Thump    ", "62--CrnchHed ",
                    "63--CrnchFlp ", "64--Pwr Down ", "65--Hardware ",
                    "66--JunkDrwr ", "67--Junk Man ", "68--LooseLug ",
                    "69--Carpet   ", "70--Smoke    ", "71--Aggresor ",
                    "72--BadBreth ", "73--King     ", "74--Xpando   ",
                    "75--Deep IIx ", "76--Dry IIx  ", "77--Hex Kick ",
                    "78--Fat Boy  ", "79--Techtik  ", "80--Skool    ",
                    "81--KidStuff ", "82--Scratchr ", "83--Afro     ",
                    "84--Cuban    ", "85--Tribal   ", "86--Steak    ",
                    "87--Hazey    ", "88--Koosh    ", "89--Bowels   ",
                    "90--Obergeil ", "91--HiEnergy ", "92--Undrwrld ",
                    "93--Cruiser  ", "94--Plumbing " },
            // Bank 1--Snare
            {
                    "0--Get Real  ", "1--Big Rim   ", "2--Woodclif  ",
                    "3--Hip Hop   ", "4--Heartlnd  ", "5--PwrBalld  ",
                    "6--Session   ", "7--Funky     ", "8--Choked    ",
                    "9--Crome     ", "10--ChromRng ", "11--ChromeHi ",
                    "12--Beauty   ", "13--Piccolo  ", "14--Fat Picc ",
                    "15--Hi Ambi  ", "16--MicroPic ", "17--PiccRoom ",
                    "18--Low Picc ", "19--NicePicc ", "20--Gun Picc ",
                    "21--Dyn Picc ", "22--Velo>Rim ", "23--Tiny E   ",
                    "24--Crisp    ", "25--Clean    ", "26--Cadence  ",
                    "27--DryShell ", "28--TopBrass ", "29--UltraThn ",
                    "30--Kamko    ", "31--Hawaii   ", "32--BluSprkl ",
                    "33--Bronze   ", "34--Hard Rim ", "35--Vintage  ",
                    "36--Weasel   ", "37--WetWeasl ", "38--Has Edge ",
                    "39--WithClap ", "40--Raunchy  ", "41--DeepRoom ",
                    "42--SlapRoom ", "43--WarmRoom ", "44--AnaKick  ",
                    "45--LongTail ", "46--ExtraLrg ", "47--Big Hall ",
                    "48--BigPlate ", "49--Compresd ", "50--Solar    ",
                    "51--Far Away ", "52--Postmdrn ", "53--Loose    ",
                    "54--Grinder  ", "55--Freaky   ", "56--Woody    ",
                    "57--ThinSkin ", "58--Crank It ", "59--Snareo   ",
                    "60--TightLug ", "61--Ibid     ", "62--Beefrank ",
                    "63--SlowFunk ", "64--Low Ring ", "65--FreakRim ",
                    "66--MetlHarm ", "67--Groovy   ", "68--Splat    ",
                    "69--RatlWood ", "70--Trashier ", "71--8oh8 Snr ",
                    "72--8oh8 Rim ", "73--8oh8 Tin ", "74--Krafty   ",
                    "75--MetlPipe ", "76--9oh9 Snr ", "77--9oh9 Rim ",
                    "78--Release  ", "79--City     ", "80--U Bahn   ",
                    "81--Gritty   ", "82--Fat Grit ", "83--Rank     ",
                    "84--BrikHaus ", "85--Overtone ", "86--DingoBoy ",
                    "87--Wonk     ", "88--HexSnare ", "89--IIxSnare ",
                    "90--70'sFunk ", "91--Ol Skool ", "92--Stutter  ",
                    "93--ThikGate ", "94--MetalGat ", "95--Face Beat",
                    "96--Thrasher ", "97--Shred    ", "98--Pipe Bomb",
                    "99--Clanker  ", "100--Blast   ", "101--Assault ",
                    "102--Speck   ", "103--Spectral", "104--OrchRoom",
                    "105--OrchHall", "106--OrchRoll", "107--BrushFat",
                    "108--BrushThn", "109--BrushRim", "110--Jazz Hit",
                    "111--Stik>Snr", "112--DryStick", "113--LiveStik",
                    "114--DeepStik", "115--StikRoom", "116--AmbiStik" },
            // Bank 2--Tom
            {
                    "0--Hero Hi   ", "1--Hero Mid  ", "2--Hero Low  ",
                    "3--Hero Flr  ", "4--Open Hi   ", "5--Open Mid  ",
                    "6--Open Low  ", "7--PinstrpH  ", "8--PinstrpM  ",
                    "9--PinstrpL  ", "10--StudioHi ", "11--StudioMd ",
                    "12--StudioLo ", "13--Big O Hi ", "14--Big O Lo ",
                    "15--Girth Hi ", "16--Girth Lo ", "17--InsideHi ",
                    "18--InsideMd ", "19--InsideLo ", "20--Jazz Hi  ",
                    "21--Jazz Low ", "22--Hall Hi  ", "23--Hall Mid ",
                    "24--Hall Low ", "25--Hall Flr ", "26--Psilo Hi ",
                    "27--PsiloMid ", "28--PsiloLow ", "29--PsiloFlr ",
                    "30--CannonHi ", "31--CannonMd ", "32--CannonLo ",
                    "33--CannonFl ", "34--CanFlngH ", "35--CanFlngM ",
                    "36--CanFlngL ", "37--Ballo Hi ", "38--BalloLow ",
                    "39--MakRakHi ", "40--MakRakMd ", "41--MakRakLo ",
                    "42--MakRakFl ", "43--Omega Hi ", "44--Omega Md ",
                    "45--Omega Lo ", "46--Omega Fl ", "47--Salvo Hi ",
                    "48--Salvo Md ", "49--Salvo Lo ", "50--Hex Hi   ",
                    "51--Hex Mid  ", "52--Hex Low  ", "53--HexFloor ",
                    "54--ClascHex ", "55--Noise Hi ", "56--Noise Lo ",
                    "57--Exo Hi   ", "58--Exo Mid  ", "59--Exo Low  ",
                    "60--OilCanHi ", "61--OilCanLo ", "62--8oh8 Hi  ",
                    "63--8oh8 Mid ", "64--8oh8 Low ", "65--Bit TomH ",
                    "66--Bit TomL ", "67--BombTomH ", "68--BombTomM ",
                    "69--BombTomL ", "70--Mad Roto " },
            // Bank 3--Hi-Hat
            {
                    "0--BrtTite1  ", "1--BrtTite2  ", "2--Brt Clsd  ",
                    "3--Brt Half  ", "4--BrtLoose  ", "5--BrtLoosr  ",
                    "6--DynBrt 1  ", "7--DynBrt 2  ", "8--Brt Open  ",
                    "9--Brt Foot  ", "10--SR Clsd  ", "11--SR Half  ",
                    "12--SR Open  ", "13--LiteClsd ", "14--Lite Dyn ",
                    "15--LiteHalf ", "16--LiteOpen ", "17--FlngClsd ",
                    "18--FlngHalf ", "19--FlngOpen ", "20--Rok Clsd ",
                    "21--RokLoose ", "22--RokSlosh ", "23--Rok Open ",
                    "24--Rok Foot ", "25--8oh8Clsd ", "26--8oh8Open ",
                    "27--Rap Clsd ", "28--Rap Half ", "29--Rap Open ",
                    "30--Zip Clsd ", "31--Zip Open ", "32--Zap Clsd ",
                    "33--Zap Open " },
            // Bank 4--Cymbal
            {
                    "0--Ride Cym  ", "1--VeloRide  ", "2--PingRide  ",
                    "3--Exotic    ", "4--RideBell  ", "5--TransBel  ",
                    "6--El Bell   ", "7--Avantia   ", "8--CymParts  ",
                    "9--BrtCrash  ", "10--Ster Brt ", "11--DrkCrash ",
                    "12--SterDark ", "13--LR Crsh1 ", "14--LR Crsh2 ",
                    "15--IceCrash ", "16--ZootMute ", "17--DrtyMute ",
                    "18--Splash   ", "19--MicroCym ", "20--8 Splash ",
                    "21--China    ", "22--SterChna ", "23--Woo Han  ",
                    "24--Doppler  ", "25--TipShank ", "26--SterPhaz ",
                    "27--Hammered ", "28--EastWest ", "29--Orch Cym ",
                    "30--8oh8Crsh ", "31--8CrashFl ", "32--Syn Pang ",
                    "33--SynCrash ", "34--BlastCym ", "35--Noiz Cym " },
            // Bank 5--Percussion
            {
                    "0--Agogo Hi  ", "1--Agogo Lo  ", "2--AgoPitch  ",
                    "3--Noggin    ", "4--Reco Hi   ", "5--Reco Lo   ",
                    "6--Clay Pot  ", "7--Triangle  ", "8--Tri Mute  ",
                    "9--TriPitch  ", "10--DrumStix ", "11--Cowbell  ",
                    "12--Tambrine ", "13--TamPitch ", "14--Sleighbl ",
                    "15--Snowjob  ", "16--Cabasa   ", "17--SharpShk ",
                    "18--TikTak   ", "19--Maracas  ", "20--ShakerHi ",
                    "21--ShakerLo ", "22--Bead Pot ", "23--BeadShk1 ",
                    "24--BeadShk2 ", "25--BeadShk3 ", "26--SynShkr1 ",
                    "27--SynShkr2 ", "28--SynShkrD ", "29--Rattle   ",
                    "30--CrashrHd ", "31--CrashrSf ", "32--Rainshak ",
                    "33--RainStik ", "34--Gravel   ", "35--RatlBwap ",
                    "36--Bongo Hi ", "37--BngHiSlp ", "38--Bongo Lo ",
                    "39--BngLoSlp ", "40--Conga Hi ", "41--Conga Lo ",
                    "42--CongaSlp ", "43--Slap Dyn ", "44--Cuica Hi ",
                    "45--Cuica Lo ", "46--AmIndian ", "47--Tatonka  ",
                    "48--WarPaint ", "49--BoLanGoo ", "50--BoLanDyn ",
                    "51--BreketaH ", "52--BreketaL ", "53--BrktaDyn ",
                    "54--Elephant ", "55--GhatamHi ", "56--GhatamLo ",
                    "57--Udu      ", "58--Ethnika  ", "59--Amazon   ",
                    "60--Nagara   ", "61--Oobla Hi ", "62--Oobla Lo ",
                    "63--OoblaDyn ", "64--Paah     ", "65--Ethno    ",
                    "66--EasternV ", "67--TalkngHi ", "68--TalkngLo ",
                    "69--HandDrum ", "70--Tavil Hi ", "71--Tavil Lo ",
                    "72--Monastic ", "73--Tavasa   ", "74--Tabla    ",
                    "75--TblaDyn1 ", "76--TblaDyn2 ", "77--Ghatabla ",
                    "78--Tablchrd ", "79--Haji     ", "80--TimbleHi ",
                    "81--TimbleLo ", "82--8cwPitch ", "83--8oh8 Cow ",
                    "84--8oh8 Rim ", "85--CongaRap ", "86--8oh8Clap ",
                    "87--9oh9Clap ", "88--Big Clap ", "89--LiteSnap ",
                    "90--ClscSnap ", "91--Pwr Snap ", "92--Clave    ",
                    "93--ClveKord ", "94--Castanet ", "95--CastRoll ",
                    "96--CastDyn1 ", "97--CastDyn2 ", "98--Wood Hi  ",
                    "99--Wood Lo  ", "100--Block Hi", "101--Block Lo",
                    "102--TempleHi", "103--TempleLo", "104--Vibrslap",
                    "105--Oil Can ", "106--OilPitch", "107--MetalTik",
                    "108--Plucky  ", "109--PopCheek", "110--Rappotab",
                    "111--I'm Clay", "112--BigoBrek", "113--SpacePrc" },
            // Bank 6--Effects
            {
                    "0--Anvil     ", "1--BallPeen  ", "2--BattyBel  ",
                    "3--4 Star    ", "4--Meddle    ", "5--Blksmith  ",
                    "6--Clank     ", "7--Tank Hit  ", "8--SunBurst  ",
                    "9--Industry  ", "10--Big Shot ", "11--Metal    ",
                    "12--WhtNoiz1 ", "13--WhtNoiz2 ", "14--Spectre1 ",
                    "15--Spectre2 ", "16--Tesla    ", "17--Machine  ",
                    "18--PinkZap1 ", "19--PinkZap2 ", "20--PnkBlst1 ",
                    "21--PnkBlst2 ", "22--Zap 1    ", "23--Zap 2    ",
                    "24--Zap 3    ", "25--Wood Zap ", "26--Dyn Zap  ",
                    "27--Dual Zap ", "28--Residue  ", "29--WhipCrak ",
                    "30--Kung Fu  ", "31--WhipNoiz ", "32--Vinyl 1  ",
                    "33--Vinyl 2  ", "34--DynVinyl ", "35--PwrGtrHi ",
                    "36--PwrGtrLo ", "37--Gtr Hit  ", "38--FlngGtrH ",
                    "39--FlngGtrL ", "40--Guitrbot ", "41--Slippery ",
                    "42--Danger!  ", "43--Screech  ", "44--FlScreeH ",
                    "45--FlScreeL ", "46--Mercury  ", "47--Technoid ",
                    "48--Bucket   ", "49--Grab Bag ", "50--Alloys 1 ",
                    "51--Alloys 2 ", "52--Velopede ", "53--Static   ",
                    "54--Pole     ", "55--Froggy   ", "56--Sun City ",
                    "57--InduHit  ", "58--JetBeads ", "59--Plonk    ",
                    "60--Klonk    ", "61--Pop      ", "62--Knock    ",
                    "63--Metronom ", "64--Silence  " },
            // Bank 7--Random
            {
                    "0--BrtHatC1  ", "1--BrtHatC2  ", "2--RokHatCl  ",
                    "3--Real Snr  ", "4--LooseSnr  ", "5--TinSnare  ",
                    "6--ValleySn  ", "7--FreakSnr  ", "8--Aliens    ",
                    "9--Zapalog   ", "10--Blasters ", "11--Metalize ",
                    "12--ShknBake ", "13--Triblism ", "14--CngoBngo ",
                    "15--RagaBabl " } };
}
