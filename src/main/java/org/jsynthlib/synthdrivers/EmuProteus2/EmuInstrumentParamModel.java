package org.jsynthlib.synthdrivers.EmuProteus2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsynthlib.patch.model.impl.Patch;

public class EmuInstrumentParamModel extends EmuParamModel {

    private static final Map<String, String> instrumentMap =
            new LinkedHashMap<String, String>();
    private static final List<String> instrumentList = new ArrayList<String>();

    static {
        instrumentMap.put("None", "2-0");
        instrumentMap.put("Arco Basses", "2-1");
        instrumentMap.put("Arco Celli", "2-2");
        instrumentMap.put("Arco Violas", "2-3");
        instrumentMap.put("ArcoViolins", "2-4");
        instrumentMap.put("Dark Basses", "2-5");
        instrumentMap.put("Dark Celli", "2-6");
        instrumentMap.put("Dark Violas", "2-7");
        instrumentMap.put("DarkViolins", "2-8");
        instrumentMap.put("Low Tremolo", "2-9");
        instrumentMap.put("HighTremolo", "2-10");
        instrumentMap.put("Tremolande", "2-13");
        instrumentMap.put("Strings 1", "2-11");
        instrumentMap.put("Strings 2", "2-12");
        instrumentMap.put("Strings 3", "2-14");
        instrumentMap.put("Solo Cello", "4-1");
        instrumentMap.put("Solo Viola", "4-2");
        instrumentMap.put("Solo Violin", "4-3");
        instrumentMap.put("Quartet 1", "4-5");
        instrumentMap.put("Quartet 2", "4-6");
        instrumentMap.put("Quartet 3", "4-7");
        instrumentMap.put("Quartet 4", "4-8");
        instrumentMap.put("Gambambo", "4-4");
        instrumentMap.put("Pizz Basses", "4-9");
        instrumentMap.put("Pizz Celli", "4-10");
        instrumentMap.put("Pizz Violas", "4-11");
        instrumentMap.put("Pizz Violin", "4-12");
        instrumentMap.put("Pizzicombo", "4-13");
        instrumentMap.put("Flute w/Vib", "2-15");
        instrumentMap.put("Flute noVib", "2-63");
        instrumentMap.put("Alt. Flute", "2-65");
        instrumentMap.put("Piccolo", "2-16");
        instrumentMap.put("Bass Clar.", "4-14");
        instrumentMap.put("Clarinet", "4-15");
        instrumentMap.put("B.Clar/Clar", "4-16");
        instrumentMap.put("Cntrbassoon", "4-17");
        instrumentMap.put("Bassoon", "4-18");
        instrumentMap.put("EnglishHorn", "4-19");
        instrumentMap.put("Oboe w/Vib", "4-20");
        instrumentMap.put("Oboe noVib", "4-30");
        instrumentMap.put("Alt. Oboe", "4-79");
        instrumentMap.put("Woodwinds", "4-21");
        instrumentMap.put("Hi Trombone", "2-17");
        instrumentMap.put("Lo Trombone", "2-64");
        instrumentMap.put("mf Trumpet", "2-18");
        instrumentMap.put("ff Trumpet", "2-19");
        instrumentMap.put("Harmon Mute", "4-22");
        instrumentMap.put("mf Fr. Horn", "2-20");
        instrumentMap.put("ff Fr. Horn", "2-21");
        instrumentMap.put("Tuba", "2-22");
        instrumentMap.put("ff Brass", "2-23");
        instrumentMap.put("mf Brass", "2-24");
        instrumentMap.put("Harp", "2-25");
        instrumentMap.put("Xylophone", "2-26");
        instrumentMap.put("Celesta", "2-27");
        instrumentMap.put("Triangle", "2-28");
        instrumentMap.put("Bass Drum", "2-29");
        instrumentMap.put("Snare Drum+", "2-30");
        instrumentMap.put("Piatti", "2-31");
        instrumentMap.put("TempleBlock", "2-32");
        instrumentMap.put("Glocknspiel", "2-33");
        instrumentMap.put("Percussion1", "2-34");
        instrumentMap.put("Percussion2", "2-35");
        instrumentMap.put("Low Perc 2", "2-36");
        instrumentMap.put("High Perc 2", "2-37");
        instrumentMap.put("TubularBell", "4-23");
        instrumentMap.put("Timpani", "4-24");
        instrumentMap.put("Timp/T.Bell", "4-25");
        instrumentMap.put("Tamborine", "4-26");
        instrumentMap.put("Tam Tam", "4-27");
        instrumentMap.put("Percussion3", "4-28");
        instrumentMap.put("Special FX", "4-29");
        instrumentMap.put("Oct 1 Sine", "2-38");
        instrumentMap.put("Oct 2 All", "2-39");
        instrumentMap.put("Oct 3 All", "2-40");
        instrumentMap.put("Oct 4 All", "2-41");
        instrumentMap.put("Oct 5 All", "2-42");
        instrumentMap.put("Oct 6 All", "2-43");
        instrumentMap.put("Oct 7 All", "2-44");
        instrumentMap.put("Oct 2 Odd", "2-45");
        instrumentMap.put("Oct 3 Odd", "2-46");
        instrumentMap.put("Oct 4 Odd", "2-47");
        instrumentMap.put("Oct 5 Odd", "2-48");
        instrumentMap.put("Oct 6 Odd", "2-49");
        instrumentMap.put("Oct 7 Odd", "2-50");
        instrumentMap.put("Oct 2 Even", "2-51");
        instrumentMap.put("Oct 3 Even", "2-52");
        instrumentMap.put("Oct 4 Even", "2-53");
        instrumentMap.put("Oct 5 Even", "2-54");
        instrumentMap.put("Oct 6 Even", "2-55");
        instrumentMap.put("Oct 7 Even", "2-56");
        instrumentMap.put("Low Odds", "2-57");
        instrumentMap.put("Low Evens", "2-58");
        instrumentMap.put("FourOctaves", "2-59");
        instrumentMap.put("Sine Wave", "4-32");
        instrumentMap.put("Tri Wave", "4-33");
        instrumentMap.put("Square Wave", "4-34");
        instrumentMap.put("Pulse 33%", "4-35");
        instrumentMap.put("Pulse 25%", "4-36");
        instrumentMap.put("Pulse 10%", "4-37");
        instrumentMap.put("Sawtooth", "4-38");
        instrumentMap.put("SawOddGone", "4-39");
        instrumentMap.put("Ramp", "4-40");
        instrumentMap.put("RampEveOnly", "4-41");
        instrumentMap.put("Vio Essence", "4-42");
        instrumentMap.put("Buzzoon", "4-43");
        instrumentMap.put("Brassy Wave", "4-44");
        instrumentMap.put("Reedy Buzz", "4-45");
        instrumentMap.put("Growl Wave", "4-46");
        instrumentMap.put("HarpsiWave", "4-47");
        instrumentMap.put("Fuzzy Gruzz", "4-48");
        instrumentMap.put("Power 5ths", "4-49");
        instrumentMap.put("Filt Saw", "4-50");
        instrumentMap.put("Ice Bell", "4-51");
        instrumentMap.put("Bronze Age", "4-52");
        instrumentMap.put("Iron Plate", "4-53");
        instrumentMap.put("Aluminum", "4-54");
        instrumentMap.put("Lead Beam", "4-55");
        instrumentMap.put("SteelXtract", "4-56");
        instrumentMap.put("WinterGlass", "4-57");
        instrumentMap.put("TwnBellWash", "4-58");
        instrumentMap.put("Orch Bells", "4-59");
        instrumentMap.put("Tubular SE", "4-60");
        instrumentMap.put("SoftBellWav", "4-61");
        instrumentMap.put("Swirly", "4-62");
        instrumentMap.put("Tack Attack", "4-63");
        instrumentMap.put("ShimmerWave", "4-64");
        instrumentMap.put("Moog Lead", "4-65");
        instrumentMap.put("B3 SE", "4-66");
        instrumentMap.put("Mild Tone", "4-67");
        instrumentMap.put("Piper", "4-68");
        instrumentMap.put("Ah Wave", "4-69");
        instrumentMap.put("Vocal Wave", "4-70");
        instrumentMap.put("Fuzzy Clav", "4-71");
        instrumentMap.put("Electrhode", "4-72");
        instrumentMap.put("Whine 1", "4-73");
        instrumentMap.put("Whine 2", "4-74");
        instrumentMap.put("Metal Drone", "4-75");
        instrumentMap.put("Silver Race", "4-76");
        instrumentMap.put("MetalAttack", "4-77");
        instrumentMap.put("Filter Bass", "4-78");
        instrumentMap.put("UprightPizz", "4-31");
        instrumentMap.put("NylonPluck1", "2-60");
        instrumentMap.put("NylonPluck2", "2-61");
        instrumentMap.put("PluckedBass", "2-62");
        instrumentList.addAll(instrumentMap.keySet());
    }

    public static String[] getInstruments() {
        return instrumentList.toArray(new String[instrumentList.size()]);
    }

    private int value;



    @Override
    public void setPatch(Patch patch) {
        String instrumentKey = patch.sysex[offset + 1] + "-" + patch.sysex[offset];
        Iterator<Entry<String, String>> iterator =
                instrumentMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(instrumentKey)) {
                value = instrumentList.indexOf(entry.getKey());
                break;
            }
        }
        super.setPatch(patch);
    }

    @Override
    public void set(int i) {
        this.value = i;
        setInstrumentBytes(i, offset, patch.sysex);
    }

    static byte[] setInstrumentBytes(int value, int offset, byte[] buffer) {
        String instrumentKey = instrumentMap.get(instrumentList.get(value));
        String[] split = instrumentKey.split("-");

        buffer[offset + 1] = (byte) Integer.parseInt(split[0]);
        buffer[offset] = (byte) Integer.parseInt(split[1]);
        return buffer;
    }

    @Override
    public int get() {
        return value;
    }

}
